package com.kingnetdc.blueberry.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcFileReader {

    private static final Logger logger = LoggerFactory.getLogger(KdcFileReader.class);

    private String fileName;

    private BufferedReader bufferedReader;

    /**
     * 一个绝对路径下文件
     * @param fileName
     * @throws Exception
     */
    public KdcFileReader(String fileName) throws Exception {
        this.fileName = fileName;
        bufferedReader = new BufferedReader(new FileReader(fileName));
    }

    public KdcFileReader(File file) throws Exception {
        this.fileName = file.getAbsolutePath();
        bufferedReader = new BufferedReader(new FileReader(file));
    }

    /**
     * 从文件中读取数据
     * @return
     */
    public String readLine() {
        try {
            return bufferedReader.readLine();
        } catch (Throwable e) {
            logger.error("read file error, file name : " + fileName, e);
        }
        return null;
    }

    /**
     * 如果是忽略行数，则直接调用读取的行数
     * @param num
     * @return
     */
    public long skipLine(long num) {
        long ret = -1;
        try {
            String temp = null;
            for (long i = 0; i < num; i ++) {
                temp = readLine();
                if (null == temp || i == num - 1) {
                    ret = i + 1;
                }
            }
        } catch (Throwable e) {
            logger.error("skip file lines error. file name : " + fileName, e);
        }
        return ret;
    }

    /**
     * 如果 skip 的结果不是 >= 0 的，则是错误的
     * @param num
     * @return
     */
    public long skip(long num) {
        try {
            return bufferedReader.skip(num);
        } catch (Throwable e) {
            logger.error("skip file error. file name : " + fileName, e);
        }
        return -1;
    }

    public void close() {
        try {
            bufferedReader.close();
        } catch (Throwable e) {
            logger.error("close file error, file name : " + fileName, e);
        }
    }

}
