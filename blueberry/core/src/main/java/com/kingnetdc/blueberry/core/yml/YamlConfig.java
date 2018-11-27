package com.kingnetdc.blueberry.core.yml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;


/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class YamlConfig {

    private static Logger logger = LoggerFactory.getLogger(YamlConfig.class);

    private static final ThreadLocal THREAD_YAML = new ThreadLocal();

    private static Yaml getThreadLocalYaml() {
        Yaml yaml = (Yaml) THREAD_YAML.get();
        THREAD_YAML.remove();
        try {
            if (null == yaml) {
                yaml = new Yaml();
                THREAD_YAML.set(yaml);
            }
        } catch (Throwable e) {
            logger.error("get thread local error.", e);
        }
        return yaml;
    }

    /**
     * 解析配置输入流
     * @param inputStream
     * @param type
     * @param <T>
     * @return
     */
    public static <T> T loadAs(InputStream inputStream, Class<T> type) {
        return getThreadLocalYaml().loadAs(inputStream, type);
    }

    /**
     * 直接解析配置文件，路径使用的绝对路径
     * @param file
     * @param type
     * @param <T>
     * @return
     */
    public static  <T> T loadAs(String file, Class<T> type) {
        return getThreadLocalYaml().loadAs(file, type);
    }
}
