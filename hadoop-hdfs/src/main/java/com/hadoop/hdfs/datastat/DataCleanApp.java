package com.hadoop.hdfs.datastat;

import com.alibaba.fastjson.JSONObject;
import com.hadoop.hdfs.topk.TopkApp;
import org.apache.avro.data.Json;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.filecache.DistributedCache;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.mortbay.util.ajax.JSON;

import java.io.IOException;
import java.net.URI;

/**
 * Created by leochan on 2018/3/17.
 */
public class DataCleanApp {

    public static void main(String[] args) throws Exception {
        // 1.执行数据清洗
        String dataCleanInputPath = args[0];
        Path dataCleanOutputPath = new Path(args[1]);

        Configuration conf = new Configuration();
        FileSystem fileSystem = FileSystem.newInstance(new URI("hdfs://leochan:9000"), conf);
        // 输出目录如果存在则删除
        fileSystem.delete(dataCleanOutputPath, true);

        String jobName = DataCleanApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(DataCleanApp.class);

        FileInputFormat.setInputPaths(job, dataCleanInputPath);
        FileOutputFormat.setOutputPath(job, dataCleanOutputPath);

        job.setMapperClass(DataCleanMapper.class);
        job.setMapOutputKeyClass(Text.class);
        job.setMapOutputValueClass(NullWritable.class);
        job.addFileToClassPath(new Path("/jars/fastjson-1.2.41.jar"));

        boolean isSuccess = job.waitForCompletion(true);

        // 2.执行后台接口访问PV量的统计
        if (isSuccess){
            Path dataStatOutputPath = new Path(args[2]);

            conf = new Configuration();
            // 输出目录如果存在则删除
            fileSystem.delete(dataStatOutputPath, true);

            String jobName2 = "DataStatAppJob";
            Job job2 = Job.getInstance(conf, jobName);
            job2.setJarByClass(DataCleanApp.class);

            FileInputFormat.addInputPath(job2, dataCleanOutputPath);
            FileOutputFormat.setOutputPath(job2, dataStatOutputPath);

            job2.setMapperClass(StatPVMapper.class);
            job2.setMapOutputKeyClass(Text.class);
            job2.setMapOutputValueClass(LongWritable.class);

            job2.setReducerClass(StatPVReducer.class);
            job2.setOutputKeyClass(Text.class);
            job2.setOutputValueClass(LongWritable.class);

            job2.waitForCompletion(true);
        }
    }


    public static class DataCleanMapper extends Mapper<LongWritable, Text, Text, NullWritable> {


        private Text key2 = new Text();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String lineData = value.toString();

            if (lineData != null && lineData.contains("c.r.f.l.a.s.impl.AccessLogService")){
                String data = lineData.substring(lineData.indexOf("{"), lineData.lastIndexOf("}") + 1);
                data = JSONObject.parseObject(data, DataBean.class).toString();
                key2.set(data);
                context.write(key2, NullWritable.get());
            }
        }
    }


    public static class StatPVMapper extends Mapper<LongWritable, Text, Text, LongWritable> {

        private Text key2 = new Text();

        private LongWritable value2 = new LongWritable();

        @Override
        protected void map(LongWritable key, Text value, Context context) throws IOException, InterruptedException {
            String[] splited = value.toString().split("\t");
            String url = splited[2];

            key2.set(url);
            value2.set(1L);
            context.write(key2, value2);
        }
    }


    public static class StatPVReducer extends Reducer<Text, LongWritable, Text, LongWritable> {

        private LongWritable value3 = new LongWritable();


        @Override
        protected void reduce(Text key2, Iterable<LongWritable> value2s, Context context) throws IOException, InterruptedException {

            Long sum = 0L;

            for (LongWritable value2 : value2s){
                sum += value2.get();
            }
            value3.set(sum);

            context.write(key2, value3);

        }
    }
}
