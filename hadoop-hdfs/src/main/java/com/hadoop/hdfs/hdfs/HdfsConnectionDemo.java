package com.hadoop.hdfs.hdfs;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;
import org.apache.hadoop.io.IOUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Leo_Chan on 2018/2/9.
 */
public class HdfsConnectionDemo {

    public static void main(String[] args) throws Exception {

        URI uri = new URI("hdfs://leochan:9000");
        Configuration conf = new Configuration();
        conf.addResource("ha/core-site.xml");
        conf.addResource("ha/hdfs-site.xml");
        String s = conf.get("fs.defaultFS");

        FileSystem fileSystem = FileSystem.get(uri, conf);

        ls(fileSystem);

    }


    public static void checkConf(){
        Configuration conf = new Configuration();
        String value = conf.get("dfs.namenode.secondary.http-address");

        System.out.println(value);
    }


    public static void ls(FileSystem fileSystem) throws IOException {
        Path path = new Path("/root/");
        FileStatus[] fileStatuses = fileSystem.listStatus(path);

        for (FileStatus fileStatus : fileStatuses){
            print(fileStatus.toString());
        }
    }


    public static void put(FileSystem fileSystem) throws IOException {
        FSDataOutputStream fsDataOutputStream = fileSystem.create(new Path("/zjfa.txt"));
        FileInputStream fileInputStream = new FileInputStream("/Users/leochan/增肌方案.numbers");
        IOUtils.copyBytes(fileInputStream, fsDataOutputStream, 1024, true);
    }


    public static void get(FileSystem fileSystem) throws IOException {
        FSDataInputStream fsDataInputStream = fileSystem.open(new Path("/xx"));

        File file = new File("/Users/leochan/hadoop_download");
        if (!file.exists()){
            file.mkdirs();
        }
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        IOUtils.copyBytes(fsDataInputStream, fileOutputStream, 1024, true);
    }



    private static void print(String str){
        System.out.println(str);
    }
}
