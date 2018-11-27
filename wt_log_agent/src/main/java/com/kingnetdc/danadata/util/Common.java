package com.kingnetdc.danadata.util;

import java.io.File;

public class Common {

	/**
	 * head string
	 */
	public static final String HEAD_AUTHORIZATION = "Authorization";
	public static final String HEAD_CONTENTTYPE = "Content-Type";
	public static final String HEAD_VERSION = "Version";
	public static final String HEAD_TOPIC = "Topic";
	public static final String HEAD_KEY = "Key";

	/**
	 * User-Agent
	 */
	public static final String HEAD_USER_AGENT = "User-Agent";
	public static final String HEAD_USER_AGENT_INFO = "Dana Java SDK";
	public static final String VERSION = "2";


	//public static final String TOPIC = "dana";
	//public static final String AUTH_KEY = "9c708c7fce87abaa544b221898769baa";

	/**
	 * Content-Type 类型
	 */
	public static final String APPLICATION_X_GZIP_TYPE = "application/x-gzip";
	public static final String APPLICATION_JSON = "application/json";


	public static boolean isBlank(String str) {
		int strLen;
		if (str != null && (strLen = str.length()) != 0) {
			for (int i = 0; i < strLen; ++i) {
				if (!Character.isWhitespace(str.charAt(i))) {
					return false;
				}
			}

			return true;
		} else {
			return true;
		}
	}

	/**
	 * 给定相对目录，返回绝对目录
	 */
	public static String getAbsolutePath(String relativePath) {
		File f = new File("");
		String path = f.getAbsolutePath();
		if("./".equals(relativePath.substring(0, 2))) {
			relativePath = relativePath.substring(2);
		}
		return path + "/" + relativePath;
	}

	public static final String file_match_key = "fileMatchName";
	public static final String file_name_key = "fileName";
	public static final String offset_key = "offset";
	public static final String offset_file_name = "offsetFileName";

}
