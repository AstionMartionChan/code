package com.kingnetdc.blueberry.core.util;


import com.kingnetdc.blueberry.core.io.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class PropertyLoader {

    private static Logger logger = LoggerFactory.getLogger(PropertyLoader.class);

    private Properties props = new Properties();

    private static volatile PropertyLoader propertyLoader;

    private PropertyLoader() {
    }

    public static PropertyLoader getInstance() {
        if (propertyLoader == null) {
            synchronized (PropertyLoader.class) {
                if (propertyLoader == null) {
                    propertyLoader = new PropertyLoader();
                }
            }
        }
        return propertyLoader;
    }

    public void setProperties(Properties properties) {
        this.props = properties;
    }

    public Long getLong(String key, Long defaultValue) {
        if (props.contains(key)) {
            return Long.parseLong(props.getProperty(key));
        }
        return defaultValue;
    }

    public Long getLong(String key) {
        if (props.contains(key)) {
            return Long.parseLong(props.getProperty(key));
        }
        return null;
    }

    public Integer getInt(String key, Integer defaultValue) {
        if (props.contains(key)) {
            return Integer.parseInt(props.getProperty(key));
        }
        return defaultValue;
    }

    public Integer getInt(String key) {
        if (props.contains(key)) {
            return Integer.parseInt(props.getProperty(key));
        }
        return null;
    }

    public String getValue(String key, String defaultValue) {
        return props.getProperty(key, defaultValue);
    }

    public String getValue(String key) {
        return props.getProperty(key);
    }

    public void loadFromClasspath(String fileName) {
        logger.info("load properties from classpath : " + fileName);
        InputStream is = null;
        try {
            is = new FileInputStream(Path.getResourceFile(fileName));
            props.load(is);
            logger.info("load properties from classpath success.");
        } catch (Throwable e) {
            logger.error("load config error", e);
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Throwable e) {
                logger.error("close input stream error.", e);
            }
        }
    }

    public void loadFromFile(String filePath) {
        logger.info("load properties from filepath : " + filePath);
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
            props.load(is);
            logger.info("load properties from filepath success.");
        } catch (Throwable e) {
            logger.error("load config error", e);
        } finally {
            try {
                if (null != is) {
                    is.close();
                }
            } catch (Throwable e) {
                logger.error("close input stream error.", e);
            }
        }
    }

}
