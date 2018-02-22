package com.hadoop.hdfs.mapreduce;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by leochan on 2018/2/20.
 */
public class HelloCountApp {

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        Configuration conf = new Configuration();

        String jobName = HelloCountApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(HelloCountApp.class);

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

}
