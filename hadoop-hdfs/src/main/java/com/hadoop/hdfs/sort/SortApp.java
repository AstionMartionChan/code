package com.hadoop.hdfs.sort;

import com.hadoop.hdfs.mapreduce.HelloCountAppMap;
import com.hadoop.hdfs.mapreduce.HelloCountAppReduce;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.io.Text;
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
 * Created by leochan on 2018/2/20.
 */
public class SortApp {

    public static void main(String[] args) throws Exception {
        String inputPath = args[0];
        Path outputPath = new Path(args[1]);

        Configuration conf = new Configuration();

        String jobName = SortApp.class.getSimpleName();
        Job job = Job.getInstance(conf, jobName);
        job.setJarByClass(SortApp.class);
        // 方法2：自定义排序
//        job.setSortComparatorClass(CustomSortComparator.class);

        FileInputFormat.setInputPaths(job, inputPath);
        FileOutputFormat.setOutputPath(job, outputPath);

        job.setMapperClass(SortMapper.class);
        job.setMapOutputKeyClass(SortKey.class);
        job.setMapOutputValueClass(NullWritable.class);

        job.setReducerClass(SortReducer.class);
        job.setOutputKeyClass(SortKey.class);
        job.setOutputValueClass(NullWritable.class);

        job.waitForCompletion(true);
    }

    public static class SortMapper extends Mapper<LongWritable, Text, SortKey, NullWritable> {

        private SortKey key2 = new SortKey();
        private NullWritable value2 = NullWritable.get();

        @Override
        protected void map(LongWritable key1, Text value1, Context context) throws IOException, InterruptedException {
            String[] splited = value1.toString().split("\t");
            key2.setFirst(Integer.valueOf(splited[0]));
            key2.setSecond(Integer.valueOf(splited[1]));

            context.write(key2, value2);
        }
    }

    public static class SortReducer extends Reducer<SortKey, NullWritable, SortKey, NullWritable> {

        private NullWritable value3 = NullWritable.get();

        @Override
        protected void reduce(SortKey key2, Iterable<NullWritable> values3, Context context) throws IOException, InterruptedException {
            context.write(key2, value3);
        }
    }

    public static class CustomSortComparator extends WritableComparator {

        public CustomSortComparator() {
            super(SortKey.class, true);
        }

        @Override
        public int compare(WritableComparable a, WritableComparable b) {
            SortKey sortKey1 = (SortKey) a;
            SortKey sortKey2 = (SortKey) b;

            return sortKey1.getFirst().compareTo(sortKey2.getFirst()) == 0 ?
                    sortKey1.getSecond().compareTo(sortKey2.getSecond()) :
                    sortKey1.getFirst().compareTo(sortKey2.getFirst());
        }
    }


    public static class SortKey implements WritableComparable<SortKey> {

        private Integer first;
        private Integer second;

        public SortKey() {
            super();
        }

        @Override
        public int compareTo(SortKey sortKey) {

            return this.first.compareTo(sortKey.getFirst()) == 0 ?
                    this.second.compareTo(sortKey.getSecond()) :
                    this.first.compareTo(sortKey.getFirst());

//            return 0;
        }

        @Override
        public void write(DataOutput out) throws IOException {
            out.writeInt(this.first);
            out.writeInt(this.second);
        }

        @Override
        public void readFields(DataInput in) throws IOException {
            this.first = in.readInt();
            this.second = in.readInt();
        }

        @Override
        public String toString() {
            return first + "\t" + second;
        }

        public Integer getFirst() {
            return first;
        }

        public void setFirst(Integer first) {
            this.first = first;
        }

        public Integer getSecond() {
            return second;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }


    }
}
