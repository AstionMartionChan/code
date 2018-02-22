package com.hadoop.hdfs.mapreduce;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

import java.io.IOException;

/**
 * Created by leochan on 2018/2/20.
 */
public class HelloCountAppReduce extends Reducer<Text, LongWritable, Text, LongWritable> {
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
