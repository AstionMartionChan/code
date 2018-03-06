package com.hadoop.hdfs.grouping;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.io.WritableComparator;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/2/26.
 */
public class GroupingApp {

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        Configuration conf = new Configuration();

        String jobName = GroupingApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(GroupingApp.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(GroupingMapper.class);
        job.setMapOutputKeyClass(CustomUserTimeWritable.class);
        job.setMapOutputValueClass(CustomTimePageWritable.class);
        job.setGroupingComparatorClass(CustomGroupingComparator.class);

        job.setReducerClass(GroupingReducer.class);
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(NullWritable.class);

        job.waitForCompletion(true);
    }


    public static class CustomGroupingComparator extends WritableComparator {

        public CustomGroupingComparator() {
            super(CustomUserTimeWritable.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            CustomUserTimeWritable customUserTimeWritable1 = (CustomUserTimeWritable) a;
            CustomUserTimeWritable customUserTimeWritable2 = (CustomUserTimeWritable) b;

            return customUserTimeWritable1.getUser().compareTo(customUserTimeWritable2.getUser());
        }
    }


    public static class GroupingMapper extends Mapper<LongWritable, Text, CustomUserTimeWritable, CustomTimePageWritable> {

        private CustomUserTimeWritable key2 = new CustomUserTimeWritable();
        private CustomTimePageWritable value2 = new CustomTimePageWritable();

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {

            String[] splited = value1.toString().split("\t");
            key2.set(splited[0], Integer.parseInt(splited[1]));
            value2.set(Integer.parseInt(splited[1]), splited[2]);

            context.write(key2, value2);
        }
    }


    public static class GroupingReducer extends Reducer<CustomUserTimeWritable, CustomTimePageWritable, Text, NullWritable> {
        private Text key3 = new Text();

        @Override
        protected void reduce(CustomUserTimeWritable key2, Iterable<CustomTimePageWritable> values2, Context context) throws IOException, InterruptedException {


            Integer startTime = 0;
            Integer endTime = 0;
            Integer totalPages = 0;
            String startPage = null;
            String endPage = null;


            for (CustomTimePageWritable value2 : values2){
                // 第一次进入取 第一次访问时间和访问的页面
                if (totalPages == 0){
                    startTime = value2.getTime();
                    startPage = value2.getPage();
                }
                // 迭代最后一次循环 是最后一次访问时间和访问的页面
                endTime = value2.getTime();
                endPage = value2.getPage();
                totalPages++;
            }

            Integer waitTime = endTime - startTime;
            key3.set(key2.getUser() + "在" + startTime + "的入口是" + startPage + "，在" + endTime + "的出口是" + endPage + "，访问" + totalPages + "个页面，停留时间为" + waitTime + "分钟");

            context.write(key3, NullWritable.get());
        }
    }


    public static class CustomUserTimeWritable implements WritableComparable<CustomUserTimeWritable> {

        private String user;
        private Integer time;

        public void set(String user, Integer time) {
            this.user = user;
            this.time = time;
        }

        @Override
        public int compareTo(CustomUserTimeWritable next) {
            return this.user.equals(next.getUser()) ?
                    this.time.compareTo(next.getTime()) :
                    this.user.compareTo(next.getUser());
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeUTF(this.user);
            out.writeInt(this.time);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.user = in.readUTF();
            this.time = in.readInt();
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }
    }


    public static class CustomTimePageWritable implements Writable {

        private Integer time;
        private String page;

        public void set(Integer time, String page) {
            this.time = time;
            this.page = page;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(this.time);
            out.writeUTF(this.page);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.time = in.readInt();
            this.page = in.readUTF();
        }

        public Integer getTime() {
            return time;
        }

        public void setTime(Integer time) {
            this.time = time;
        }

        public String getPage() {
            return page;
        }

        public void setPage(String page) {
            this.page = page;
        }
    }
}
