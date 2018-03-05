package com.hadoop.hdfs.join;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.input.FileSplit;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo_Chan on 2018/2/28.
 */
public class JoinApp {

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.newInstance(new URI("hdfs://leochan:9000"), conf);
        // 输出目录如果存在则删除
        fileSystem.delete(outputPath, true);

        String jobName = JoinApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(JoinApp.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(JoinMap.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(JoinValue.class);

        job.setReducerClass(JoinReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(Text.class);

        job.waitForCompletion(true);
    }

    public static class JoinMap extends Mapper<LongWritable, Text, LongWritable, JoinValue> {

        private String fileName;
        private LongWritable key2 = new LongWritable();
        private JoinValue value2 = new JoinValue();

        @Override
        protected void setup(Context context) throws IOException, InterruptedException {
            FileSplit fileSplit = (FileSplit) context.getInputSplit();
            fileName = fileSplit.getPath().getName();
        }

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {

            if (fileName.equals("province.txt")){

                String[] splited = value1.toString().split("\t");
                key2.set(Long.parseLong(splited[0]));
                value2.set(splited[1], fileName);
                context.write(key2, value2);

            } else if (fileName.equals("city.txt")){

                String[] splited = value1.toString().split("\t");
                key2.set(Long.parseLong(splited[1]));
                value2.set(splited[0], fileName);
                context.write(key2, value2);

            }

        }
    }


    public static class JoinReducer extends Reducer<LongWritable, JoinValue, Text, Text> {

        private Text key3 = new Text();
        private Text key3null = new Text("");


        @Override
        protected void reduce(LongWritable key2, Iterable<JoinValue> values2, Context context) throws IOException, InterruptedException {

            List<Text> values3 = new ArrayList<>();

            for (JoinValue value2 : values2){
                if (value2.getFileName().equals("province.txt")) {
                    key3.set(value2.getArea());
                } else if (value2.getFileName().equals("city.txt")){
                    Text value3 = new Text();
                    value3.set(value2.getArea());
                    values3.add(value3);
                }
            }

            for (int x = 0; x < values3.size(); x++){
                if (x == 0){
                    context.write(key3, values3.get(x));
                } else {
                    context.write(key3null, values3.get(x));
                }
            }

        }
    }


    public static class JoinValue implements Writable {

        private String area;

        private String fileName;

        public void set(String area, String fileName) {
            this.area = area;
            this.fileName = fileName;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.area);
            out.writeUTF(this.fileName);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.area = in.readUTF();
            this.fileName = in.readUTF();
        }

        public String getArea() {
            return area;
        }

        public void setArea(String area) {
            this.area = area;
        }

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
    }

}
