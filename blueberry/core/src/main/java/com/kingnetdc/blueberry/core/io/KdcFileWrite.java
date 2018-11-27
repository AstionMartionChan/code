package com.kingnetdc.blueberry.core.io;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcFileWrite {

    private static final Logger logger = LoggerFactory.getLogger(KdcFileWrite.class);

    private FileWriter fileWriter;

    private String fileName;

    /**
     * 一个绝对路径的文件
     * @param fileName
     * @throws Exception
     */
    public KdcFileWrite(String fileName) throws Exception {
        this.fileName = fileName;
        fileWriter = new FileWriter(new File(fileName));
    }

    public KdcFileWrite(File file) throws Exception {
        this.fileName = file.getAbsolutePath();
        fileWriter = new FileWriter(file);
    }

    /**
     * 写入并刷到磁盘
     * @param line
     * @return
     */
    public boolean writeAndFlush(String line) {
        return writeAndFlush(line, true);
    }

    /**
     * 写入并刷到磁盘
     * @param line
     * @param lineBreak
     * @return
     */
    public boolean writeAndFlush(String line, boolean lineBreak) {
        return write(line, lineBreak) && flush();
    }

    /**
     * 默认写入带换行符的文件
     * @param line
     * @return
     */
    public boolean write(String line) {
        return write(line, true);
    }

    /**
     * 写入到文件里，需要在写入前注意换行
     * @param line
     * @return
     */
    public boolean write(String line, boolean lineBreak) {
        try {
            fileWriter.write(line + (lineBreak ? "\r\n" : ""));
        } catch (Throwable e) {
            logger.error("write to file error, filename : " + fileName, e);
            return false;
        }
        return true;
    }

    public boolean flush() {
        try {
            fileWriter.flush();
        } catch (Throwable e) {
            logger.error("flush to file error, filename : " + fileName, e);
            return false;
        }
        return true;
    }

    public void close() {
        if (null != fileWriter) {
            try {
                fileWriter.close();
            } catch (Throwable e) {
                logger.error("close file write error.", e);
            }
        }
    }
}
