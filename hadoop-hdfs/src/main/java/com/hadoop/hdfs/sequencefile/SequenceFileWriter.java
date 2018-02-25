package com.hadoop.hdfs.sequencefile;

import org.apache.commons.io.FileUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.SequenceFile;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.hadoop.mapreduce.counters.FileSystemCounterGroup;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by Leo_Chan on 2018/2/24.
 */
public class SequenceFileWriter {

    public static void main(String args[]) throws Exception {

        // 获取指定的输入输出路径
        String inputDir = args[0];
        Path outputDir = new Path(args[1]);
        // 获取指定的压缩类型
        String comType = args[2];

        // 获取dfs
        Configuration configuration = new Configuration();
        FileSystem fileSystem = FileSystem.newInstance(new URI("hdfs://leochan:9000"), configuration);
        // 输出目录如果存在则删除
        fileSystem.delete(outputDir, true);

        // 构造opts数组，有4个元素，第一个是输出路径，第二个是key类型，第三个是value类型，第四个是压缩类
        SequenceFile.CompressionType compressionType = buildCompressionType(comType);
        SequenceFile.Writer.Option[] options = {SequenceFile.Writer.file(outputDir),
                SequenceFile.Writer.keyClass(Text.class),
                SequenceFile.Writer.valueClass(Text.class),
                SequenceFile.Writer.compression(compressionType, new GzipCodec())};

        // 创建一个writer实例
        SequenceFile.Writer writer = SequenceFile.createWriter(configuration, options);

        File inputDirPath = new File(inputDir);
        if (inputDirPath.isDirectory()){
            File[] files = inputDirPath.listFiles();
            for (File file : files){
                String content = FileUtils.readFileToString(file);
                Text key = new Text(file.getName());
                Text value = new Text(content);
                writer.append(key, value);
            }
        }

        writer.close();
    }

    public static SequenceFile.CompressionType buildCompressionType(String comType) {
        if ("none".equals(comType)){
            return SequenceFile.CompressionType.NONE;
        } else if("block".equals(comType)){
            return SequenceFile.CompressionType.BLOCK;
        } else if("record".equals(comType)){
            return SequenceFile.CompressionType.RECORD;
        }
        return null;
    }

}
