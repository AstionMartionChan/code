package com.kingnetdc.blueberry.core.util;

import com.google.common.base.Strings;
import com.google.common.hash.Hashing;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class Common {

    private static final Logger logger = LoggerFactory.getLogger(Common.class);

    /**
     * 线程 sleep 毫秒值
     * @param millis
     */
    public static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (Throwable e) {
            logger.error("sleep error.", e);
        }
    }

    /**
     * 转换字节到字符串
     * @param bytes
     * @return
     */
    public static String utf8(byte[] bytes) {
        try {
            return new String(bytes, "UTF8");
        } catch (Throwable e) {
            logger.error("byte[] to utf8 string error.", e);
        }
        return null;
    }

    public static byte[] utf8(String string) {
        try {
            return string.getBytes("UTF8");
        } catch (Throwable e) {
            logger.error("utf8 string to byte[] error.", e);
        }
        return null;
    }

    /**
     * 默认使用 UTF-8 字符集
     * @param str
     * @return
     */
    public static String md5(String str) {
        return md5(str, Charset.forName("UTF-8"));
    }

    /**
     * 使用 guava MD5 生成结果
     * @param str
     * @param charset
     * @return
     */
    public static String md5(String str, Charset charset) {
        try {
            return Hashing.md5().hashString(str, charset).toString();
        } catch (Throwable e) {
            logger.error("md5 hashing " + str + " error.", e);
        }
        return null;
    }

    /**
     * 默认使用 utf-8 字符集
     * @param str
     * @return
     */
    public static String fastHashing(String str) {
        return fastHashing(str, Charset.forName("UTF-8"));
    }

    /**
     * 使用快速 Hash 128 位结果
     * @param str
     * @param charset
     * @return
     */
    public static String fastHashing(String str, Charset charset) {
        try {
            return Hashing.murmur3_128().hashString(str, charset).toString();
        } catch (Throwable e) {
            logger.error(" murmur3_128() hashing " + str + " error.", e);
        }
        return null;
    }

    /**
     * 判斷字符串是否為空
     * @param str 字符串
     * @return true：空
     */
    public static boolean isBlank(String str) {
        return Strings.isNullOrEmpty(str);
    }

}
