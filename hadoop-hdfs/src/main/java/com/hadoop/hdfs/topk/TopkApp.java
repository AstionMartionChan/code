package com.hadoop.hdfs.topk;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.net.URI;
import java.util.TreeSet;

/**
 * Created by Leo_Chan on 2018/2/28.
 */
public class TopkApp {

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.newInstance(new URI("hdfs://leochan:9000"), conf);
        // 输出目录如果存在则删除
        fileSystem.delete(outputPath, true);

        String jobName = TopkApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(TopkApp.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(TopkMap.class);
        job.setMapOutputKeyClass(LongWritable.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(TopkReducer.class);
        job.setOutputKeyClass(LongWritable.class);
        job.setOutputValueClass(NullWritable.class);

        job.waitForCompletion(true);
    }


    public static class TopkMap extends Mapper<LongWritable, Text, LongWritable, NullWritable> {

        private TreeSet<Long> topk = new TreeSet<>();
        private LongWritable key2 = new LongWritable();

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {

            topk.add(Long.valueOf(value1.toString()));
            if (topk.size() > 5){
                topk.pollFirst();
            }

        }

        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {

            for (Long num : topk){
                key2.set(num);
                context.write(key2, NullWritable.get());
            }
        }
    }


    public static class TopkReducer extends Reducer<LongWritable, NullWritable, LongWritable, NullWritable> {

        private TreeSet<Long> topk = new TreeSet<>();
        private LongWritable key3 = new LongWritable();

        @Override
        protected void reduce(LongWritable key, Iterable<NullWritable> values, Context context) throws IOException, InterruptedException {

            topk.add(key.get());
            if (topk.size() > 5){
                topk.pollFirst();
            }

        }


        @Override
        protected void cleanup(Context context) throws IOException, InterruptedException {
            for (Long num : topk){
                key3.set(num);
                context.write(key3, NullWritable.get());
            }
        }
    }
}
