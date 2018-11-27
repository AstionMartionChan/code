package com.kingnetdc.blueberry.core.io;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.net.URLDecoder;


/**
 * 获取系统相关的路径
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class Path {

    private static final Logger logger = LoggerFactory.getLogger(Path.class);


    /**
     * 包内资源文件获取路径，获取的是包内的路径，需要使用 new File 把文件打开
     * 不能使用 "/"
     * @param path
     * @return
     */
    public static String getResourceFile(String path) {
        try {
            if (null != path) {
                if ("/".equals(path.substring(0, 1)) && path.length() >= 1) {
                    path = path.substring(1);
                }
                return URLDecoder.decode(Path.class.getClassLoader().getResource(path).getPath(), "UTF-8");
            }
        } catch (Throwable e) {
            logger.error("get resource file error.", e);
        }
        return null;
    }

    /**
     * 根据某个 class 获取某个所在编译后的 jar 文件的地址
     * 只在编译成 Jar 的时候生效
     * @param type
     * @param <T>
     * @return
     */
    public static <T> String getJarLocation(Class<T> type) {
        try {
            return URLDecoder.decode(type.getProtectionDomain().getCodeSource().getLocation().getPath(), "UTF-8");
        } catch (Throwable e) {
            logger.error("get jar location error.", e);
        }
        return null;
    }

    /**
     * 获取启动目录的当前目录
     * @return
     */
    public static String getUserDir() {
        return System.getProperty("user.dir", null);
    }

    /**
     * 获取 class path
     * @return
     */
    public static String getClassPath() {
        return System.getProperty("java.class.path", null);
    }

}
