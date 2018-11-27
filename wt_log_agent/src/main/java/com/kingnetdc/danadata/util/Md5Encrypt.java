package com.kingnetdc.danadata.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * MD5加密算法
 */
public class Md5Encrypt {
	/**
	 * Used building output as Hex
	 */
	private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

	/**
	 * 对字符串进行MD5加密
	 *
	 * @param text 明文
	 * @return 密文
	 */
	public static String md5(String text) {
		String ret = null;
		try {
			MessageDigest msgDigest = null;
			msgDigest = MessageDigest.getInstance("MD5");
			msgDigest.update(text.getBytes("utf-8"));
			byte[] bytes = msgDigest.digest();
			ret = new String(encodeHex(bytes));
		} catch (NoSuchAlgorithmException e) {
			System.out.println("System doesn't support MD5 algorithm.");
		} catch (UnsupportedEncodingException e) {
			System.out.println("System doesn't support your  EncodingException.");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ret;
	}

	public static char[] encodeHex(byte[] data) {
		int l = data.length;
		char[] out = new char[l << 1];
		// two characters form the hex value.
		for (int i = 0, j = 0; i < l; i++) {
			out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
			out[j++] = DIGITS[0x0F & data[i]];
		}
		return out;
	}

}