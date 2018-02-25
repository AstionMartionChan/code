package com.hadoop.hdfs.mapreduce;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by leochan on 2018/2/23.
 * 解析电信日志文件，统计流量数据
 */
public class TratticStatApp {

    public static void main(String args[]) throws IOException, ClassNotFoundException, InterruptedException {
        // 指定输入输出文件在hdfs上的位置
        String inputDir = args[0];
        Path outputDir = new Path(args[1]);

        // 开启MapReducer Job任务
        Job job = Job.getInstance();

        // 设置输入，输出路径
        FileInputFormat.setInputPaths(job, inputDir);
        FileOutputFormat.setOutputPath(job, outputDir);

        // 设置jar运行类
        job.setJarByClass(TratticStatApp.class);
        // 指定map类
        job.setMapperClass(TratticStatMapper.class);
        // 指定map输出key2，value2的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TratticWritable.class);
        // 指定reducer类
        job.setReducerClass(TratticStatReducer.class);
        // 指定reducer输出key3, value3的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(TratticWritable.class);

        job.waitForCompletion(true);
    }


    public static class TratticStatMapper extends Mapper<LongWritable, Text, Text, TratticWritable> {

        private Text key2 = new Text();
        private TratticWritable value2 = new TratticWritable();

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {

            String[] splited = value1.toString().split("\t");
            key2.set(splited[1]);
            value2.set(splited[6], splited[7], splited[8], splited[9]);

            context.write(key2, value2);
        }
    }


    public static class TratticStatReducer extends Reducer<Text, TratticWritable, Text, TratticWritable> {

        TratticWritable value3 = new TratticWritable();

        @Override
        protected void reduce(Text key2, Iterable<TratticWritable> values2, Context context) throws IOException, InterruptedException {

            Long t1 = 0L;
            Long t2 = 0L;
            Long t3 = 0L;
            Long t4 = 0L;
            for (TratticWritable value2 : values2){
                t1 += value2.getT1();
                t2 += value2.getT2();
                t3 += value2.getT3();
                t4 += value2.getT4();
            }

            value3.set(t1, t2, t3, t4);

            context.write(key2, value3);

        }
    }


    public static class TratticWritable implements Writable {

        private Long t1;
        private Long t2;
        private Long t3;
        private Long t4;

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeLong(t1);
            out.writeLong(t2);
            out.writeLong(t3);
            out.writeLong(t4);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.t1 = in.readLong();
            this.t2 = in.readLong();
            this.t3 = in.readLong();
            this.t4 = in.readLong();
        }

        public void set(Long t1, Long t2, Long t3, Long t4) {
            this.t1 = t1;
            this.t2 = t2;
            this.t3 = t3;
            this.t4 = t4;
        }

        public void set(String t1, String t2, String t3, String t4) {
            set(Long.parseLong(t1), Long.parseLong(t2), Long.parseLong(t3), Long.parseLong(t4));
        }

        public Long getT1() {
            return t1;
        }

        public void setT1(Long t1) {
            this.t1 = t1;
        }

        public Long getT2() {
            return t2;
        }

        public void setT2(Long t2) {
            this.t2 = t2;
        }

        public Long getT3() {
            return t3;
        }

        public void setT3(Long t3) {
            this.t3 = t3;
        }

        public Long getT4() {
            return t4;
        }

        public void setT4(Long t4) {
            this.t4 = t4;
        }

        @Override
        public String toString() {
            return "TratticWritable{" +
                    "t1=" + t1 +
                    ", t2=" + t2 +
                    ", t3=" + t3 +
                    ", t4=" + t4 +
                    '}';
        }
    }

}
