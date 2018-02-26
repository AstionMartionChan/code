package com.hadoop.hdfs.combiner;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Partitioner;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/2/26.
 */
public class CombinerWordCountApp {


    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        Configuration conf = new Configuration();

        String jobName = CombinerWordCountApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(CombinerWordCountApp.class);
        job.setCombinerClass(HelloCountAppReduce.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(HelloCountAppMap.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(LongWritable.class);

        job.setReducerClass(HelloCountAppReduce.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(LongWritable.class);

        job.waitForCompletion(true);
    }


    public static class HelloCountAppMap extends Mapper<LongWritable, Text, Text, LongWritable> {

        private Text key2 = new Text();

        private LongWritable value2 = new LongWritable();

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {
            String[] split = value1.toString().split("\t");

            for (String str : split){
                key2.set(str);
                value2.set(1L);
                context.write(key2, value2);
            }

        }
    }


    public static class HelloCountAppReduce extends Reducer<Text, LongWritable, Text, LongWritable> {
        private LongWritable value3 = new LongWritable();

        @Override
        protected void reduce(Text key2, Iterable<LongWritable> values2, Context context) throws IOException, InterruptedException {

            Long sum = 0L;
            for (LongWritable value2 : values2){
                sum += value2.get();
            }

            value3.set(sum);
            context.write(key2, value3);
        }
    }

    public static class MyPartitioner extends Partitioner<Text, LongWritable> {

        @Override
        public int getPartition(Text text, LongWritable longWritable, int i) {
            if (text.toString().equals("cfy") || text.toString().equals("lym")){
                return 0;
            } else {
                return 1;
            }
        }
    }
}
