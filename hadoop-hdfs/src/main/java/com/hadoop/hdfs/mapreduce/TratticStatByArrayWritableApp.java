package com.hadoop.hdfs.mapreduce;

import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.ArrayWritable;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.Writable;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

import java.io.IOException;

/**
 * Created by leochan on 2018/2/23.
 */
public class TratticStatByArrayWritableApp {


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
        job.setMapperClass(TratticStatApp.TratticStatMapper.class);
        // 指定map输出key2，value2的类型
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(TratticStatApp.TratticWritable.class);
        // 指定reducer类
        job.setReducerClass(TratticStatApp.TratticStatReducer.class);
        // 指定reducer输出key3, value3的类型
        job.setOutputKeyClass(Text.class);
        job.setOutputValueClass(TratticStatApp.TratticWritable.class);

        job.waitForCompletion(true);
    }


    public static class TratticStatMapper extends Mapper<LongWritable, Text, Text, LongArrayWritable> {

        private Text key2 = new Text();
        private LongArrayWritable value2 = new LongArrayWritable();

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {

            String[] splited = value1.toString().split("\t");
            key2.set(splited[1]);
            value2.set(splited[6], splited[7], splited[8], splited[9]);

            context.write(key2, value2);
        }
    }


    public static class TratticStatReducer extends Reducer<Text, LongArrayWritable, Text, LongArrayWritable> {

        LongArrayWritable value3 = new LongArrayWritable();

        @Override
        protected void reduce(Text key2, Iterable<LongArrayWritable> values2, Context context) throws IOException, InterruptedException {

            Long t1 = 0L;
            Long t2 = 0L;
            Long t3 = 0L;
            Long t4 = 0L;
            for (LongArrayWritable value2 : values2){
                LongWritable[] longWritable = (LongWritable[]) value2.toArray();

                t1 += longWritable[0].get();
                t2 += longWritable[1].get();
                t3 += longWritable[2].get();
                t4 += longWritable[3].get();
            }

            value3.set(t1, t2, t3, t4);

            context.write(key2, value3);

        }
    }



    public static class LongArrayWritable extends ArrayWritable {

        public LongArrayWritable() {
            super(LongWritable.class);
        }

        public void set(Long t1, Long t2, Long t3, Long t4) {
            Writable[] writables = new Writable[]{new LongWritable(t1), new LongWritable(t2),
                    new LongWritable(t3), new LongWritable(t4)};
            super.set(writables);
        }

        public void set(String t1, String t2, String t3, String t4) {
            set(Long.parseLong(t1), Long.parseLong(t2), Long.parseLong(t3), Long.parseLong(t4));
        }
    }

}
