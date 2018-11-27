package com.kingnetdc.danadata.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Config {
	
	private static final Logger logger = LoggerFactory.getLogger(Config.class);
	
	private static Properties p = new Properties();
	
	private long commonLastModify = 0;
	private long log4jLastModify = 0;
	
	private String commonConf;
	private String log4jConf;
	
	public Config(String relativeConfPath) {
		commonConf = Common.getAbsolutePath(relativeConfPath) + "/properties.properties";
		log4jConf = Common.getAbsolutePath(relativeConfPath) + "/log4j.properties";
		reloadConfig();
	}
	
	public void reloadConfig() {
		reloadLog4jConfig();
		reloadCommonConfig();
	}
	
	public void reloadCommonConfig() {
		File f = new File(commonConf);
		if(commonLastModify != f.lastModified()) {
			try {
				p.clear();
				p.load(new BufferedInputStream(new FileInputStream(commonConf)));
				logger.info("reload common config data");
			} catch(FileNotFoundException e) {
				logger.error("file is not exists, fileName : " + commonConf, e);
			} catch(IOException e) {
				logger.error("io exception, fileName : " + commonConf, e);
			} catch(Exception e) {
				logger.error("unexception error, fileName : " + commonConf, e);
			}
			commonLastModify = f.lastModified();
		}
	}
	
	public void reloadLog4jConfig() {
		File f = new File(log4jConf);
		if(log4jLastModify != f.lastModified()) {
			PropertyConfigurator.configure(log4jConf);
			logger.info("reload log4j config data");
			log4jLastModify = f.lastModified();
		}
	}
	
	
	public long getCommonLastModify() {
		return commonLastModify;
	}
	
	/**
	 * 设置属性
	 */
	public void set(String key, String value) {
		p.setProperty(key, value);
	}
	
	/**
	 * 获取属性
	 */
	public String get(String key) {
		return p.getProperty(key);
	}

	public String get(String key, String defaultVal) {
		return p.getProperty(key, defaultVal);
	}
}
