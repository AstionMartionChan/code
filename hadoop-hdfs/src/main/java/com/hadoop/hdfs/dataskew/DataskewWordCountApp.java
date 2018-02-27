package com.hadoop.hdfs.dataskew;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.SequenceFileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;

/**
 * Created by Leo_Chan on 2018/2/27.
 */
public class DataskewWordCountApp {

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        // 获取dfs
        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.newInstance(new URI("hdfs://leochan:9000"), conf);
        // 输出目录如果存在则删除
        fileSystem.delete(outputPath, true);

        String jobName = DataskewWordCountApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(DataskewWordCountApp.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        // 设置为sequencefile类型的inoutformat
        job.setInputFormatClass(SequenceFileInputFormat.class);
        // 设置分组
        job.setGroupingComparatorClass(DataSkewGroupingComparator.class);
        // 设置分区
        job.setPartitionerClass(DataSkewPartitioner.class);
        // 设置reduce数量
        job.setNumReduceTasks(8);


        job.setMapperClass(WordCountMapper.class);
        job.setMapOutputKeyClass(DataSkewKey.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(WordCountReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.waitForCompletion(true);
    }


    public static class WordCountMapper extends Mapper<Text, Text, DataSkewKey, LongWritable> {

        private DataSkewKey key2 = new DataSkewKey();
        private LongWritable value2 = new LongWritable();

        @Override
        protected void map(Text key1, Text value1, Context context) throws IOException, InterruptedException {
            String fileName = key1.toString();
            String[] splited = value1.toString().split("\t");

            for (String str : splited){
                key2.set(str, fileName);
                value2.set(1L);
                context.write(key2, value2);
            }
        }
    }


    public static class WordCountReducer extends Reducer<DataSkewKey, LongWritable, Text, LongWritable> {

        private Text key3 = new Text();
        private LongWritable value3 = new LongWritable();

        @Override
        protected void reduce(DataSkewKey key2, Iterable<LongWritable> values2, Context context) throws IOException, InterruptedException {

            Long sum = 0L;
            for (LongWritable value2 : values2){
                sum += value2.get();
            }

            key3.set(key2.getWord());
            value3.set(sum);
            context.write(key3, value3);
        }
    }


    public static class DataSkewKey implements WritableComparable<DataSkewKey> {

        private String word;
        private String fileName;

        public void set(String word, String fileName) {
            this.word = word;
            this.fileName = fileName;
        }

        @Override
        public int compareTo(DataSkewKey next) {
            return this.word.equals(next.getWord()) ?
                    this.fileName.compareTo(next.getFileName()) :
                    this.word.compareTo(next.getWord());
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(word);
            out.writeUTF(fileName);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.word = in.readUTF();
            this.fileName = in.readUTF();
        }

        public String getWord() {
            return word;
        }

        public void setWord(String word) {
            this.word = word;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

    public static class DataSkewPartitioner extends Partitioner<DataSkewKey, LongWritable> {

        @Override
        public int getPartition(DataSkewKey dataSkewKey, LongWritable longWritable, int i) {

            String firstName = dataSkewKey.getFileName().substring(0, 1);
            return Integer.parseInt(firstName) - 2;

        }
    }


    public static class DataSkewGroupingComparator extends WritableComparator {

        public DataSkewGroupingComparator() {
            super(DataSkewKey.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            DataSkewKey current = (DataSkewKey) a;
            DataSkewKey next = (DataSkewKey) b;

            return current.getWord().compareTo(next.getWord());
        }
    }



}
