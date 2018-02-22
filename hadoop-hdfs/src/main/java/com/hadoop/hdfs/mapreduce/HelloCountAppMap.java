package com.hadoop.hdfs.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;

import java.io.IOException;

/**
 * Created by leochan on 2018/2/20.
 */
public class HelloCountAppMap extends Mapper<LongWritable, Text, Text, LongWritable> {

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
