package com.kingnetdc.danadata.sender;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.kingnetdc.danadata.util.Base64Coder;
import com.kingnetdc.danadata.util.Common;
import com.kingnetdc.danadata.util.Md5Encrypt;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.GZIPOutputStream;

/**
 * Created by yangjun on 17/3/25.
 */
public class DanaServerSender extends Sender {

	private final static Logger logger = LoggerFactory.getLogger(DanaServerSender.class);

	private String serverUrl = "";
	private String token = "";
	private String key = "";
	private String topic = "";

	//实际发送topic
	private String real_topic = "";
	//数据是否压缩
	private boolean compressData = true;
	private int _requestTimeout = 10;

	//一次最多发送的数据条数
	private int maxSend = 100;

	//发送失败重试次数
	private int reSendNum = 3;

	//保存读取文件offset信息的文件名
	private String write_offset_info_file_name;

	//打印接口发送时长时间
	private long printTime = System.currentTimeMillis();
	//打印发送时长间隔时长，单位秒
	private int printWaite = 10;

	@Override
	public void setSendTopic(String send_topic){
		this.real_topic = send_topic;
	}

	//初始化配置
	@Override
	public boolean configure(Map<String, String> params){
		serverUrl = params.get("serverUrl");
		token = params.get("token");
		key = params.get("key");
		topic = params.get("topic");
		write_offset_info_file_name = params.get("offsetFileName");
		try {
			compressData = Boolean.valueOf(params.getOrDefault("compressData", "true"));
			_requestTimeout = Integer.valueOf(params.getOrDefault("requestTimeout", "10"));
			maxSend = Integer.valueOf(params.getOrDefault("maxSend", "100"));
			reSendNum = Integer.valueOf(params.getOrDefault("reSendNum", "3"));
		} catch (Exception e) {
			logger.error("参数设置异常", e);
		}
		if(serverUrl.isEmpty() || token.isEmpty() || topic.isEmpty()) {
			logger.warn("参数不足，serverUrl=" + serverUrl + " token=" + token + " key=" + key + " topic=" + topic);
			return false;
		}
		return true;
	}

	//发送消息
	@Override
	public boolean send(List<String> data, Map<String, String> params){
		Long startTime = System.currentTimeMillis();
		List<JSONObject> sendList = new ArrayList<JSONObject>();
		for(int i = 0; i < data.size(); i++) {
			try {
				sendList.add(JSONObject.parseObject(data.get(i)));
			} catch (JSONException e) {
				logger.error("json转换错误，数据为：{}，错误：{}", data.get(i), e);
			}
		}
		String sendStr = JSON.toJSONString(sendList);
		int sendNum = 0;
		while(!sendMsg(sendStr)) {
			sendNum ++;
			logger.error("请求失败，再次重试，请求次数：{}，topic: {}。", sendNum, real_topic);
			try {
				Thread.sleep(1000);
			} catch (Exception e) {
				logger.error("sleep 异常", e);
			}
		}
		//发送完毕写入offset
		writeOffset(params);
		if(this.printTime <= System.currentTimeMillis() - this.printWaite*1000) {
			this.printTime = System.currentTimeMillis();
			long time = System.currentTimeMillis() - startTime ;
			logger.info("发送数据接口消耗时长：{}毫秒，发送数据条数：{}", time, data.size());
		}
		//清除发送topic
		this.real_topic = "";
		return true;
	}

	@Override
	public boolean close(){

		return true;
	}

	//将offset写入文件
	private void writeOffset(Map<String, String> params){
		String fileName = params.get(Common.file_name_key);
		String offset = params.get(Common.offset_key);
		JSONObject json = new JSONObject();
		json.put("fileName", fileName);
		json.put("offset", offset);
		String jsonStr = json.toJSONString();
		OutputStreamWriter osw = null;
		try {
			File f = new File("./conf/" + write_offset_info_file_name);
			FileOutputStream fos = new FileOutputStream(f);
			osw = new OutputStreamWriter(fos);
			osw.write(jsonStr);
		} catch (Exception e) {
			logger.error("写入offset失败，参数：" + jsonStr, e);
		} finally {
			if(null != osw) {
				try {
					osw.close();
				} catch (Exception e) {
					logger.error("关闭流失败", e);
				}
			}
		}
	}

	//发送多条json格式组合字符串
	private boolean sendMsg(final String data){
		CloseableHttpClient httpclient = HttpClients.createDefault();
		CloseableHttpResponse response = null;
		boolean ret = false;
		try {
			response = httpclient.execute(getHttpRequest(data));
			int httpStatusCode = response.getStatusLine().getStatusCode();
			String httpContent = EntityUtils.toString(response.getEntity(), "UTF-8");
			if (httpStatusCode < 200 || httpStatusCode >= 300) {
				logger.error("request fail statusCode=" + httpStatusCode);
			} else if("0".equals(httpContent)){
				ret = true;
			} else {
				logger.error("return code error. code=" + httpContent);
				ret = false;
			}
			Thread.sleep(100);
		} catch (IOException ioerr) {
			logger.error("http request io error.", ioerr);
		} catch (Exception e) {
			logger.error("http request exception error", e);
		} finally {
			try{
				if (null != response) {
					response.close();
				}
			} catch (Exception e) {
				logger.error("close response error", e);
			}
		}
		return ret;
	}

	private HttpUriRequest getHttpRequest(final String data) throws IOException {
		HttpPost httpPost = new HttpPost(this.serverUrl);
		RequestConfig config = RequestConfig.custom()
			.setConnectionRequestTimeout(_requestTimeout)
			.setSocketTimeout(_requestTimeout)
			.setConnectTimeout(_requestTimeout)
			.build();
		httpPost.setConfig(config);
		String message = getHttpEntry(data);
		httpPost.setEntity(new StringEntity(message));

		if(StringUtils.isEmpty(real_topic)) {
			real_topic = this.topic;
		}
		Map<String, String> httpHeaders = structHeaders(compressData, message, token, real_topic, key);
		if (httpHeaders != null) {
			for (Map.Entry<String, String> entry : httpHeaders.entrySet()) {
				httpPost.addHeader(entry.getKey(), entry.getValue());
			}
		}
		return httpPost;
	}

	private String getHttpEntry(final String data) throws IOException {
		byte[] bytes = data.getBytes(Charset.forName("UTF-8"));
		if (compressData) {
			ByteArrayOutputStream os = new ByteArrayOutputStream(bytes.length);
			GZIPOutputStream gos = new GZIPOutputStream(os);
			gos.write(bytes);
			gos.close();
			byte[] compressed = os.toByteArray();
			os.close();
			return new String(Base64Coder.encode(compressed));
		}
		return data;
	}

	/**
	 * 构建http 消息头
	 *
	 * @param compressData
	 * @param body
	 * @return
	 */
	static final Map<String, String> structHeaders(boolean compressData, String body, String token, String topic, String key) {
		Map<String, String> headers = new HashMap<String, String>();
		headers.put(Common.HEAD_VERSION, Common.VERSION);
		headers.put(Common.HEAD_TOPIC, topic);
		headers.put(Common.HEAD_KEY, key);

		headers.put(Common.HEAD_USER_AGENT, Common.HEAD_USER_AGENT_INFO);
		if (compressData) {
			headers.put(Common.HEAD_CONTENTTYPE, Common.APPLICATION_X_GZIP_TYPE);
		} else {
			headers.put(Common.HEAD_CONTENTTYPE, Common.APPLICATION_JSON);
		}

		String hashString = token + topic + Common.VERSION + token + body;
		String authorization = Md5Encrypt.md5(hashString);
		headers.put(Common.HEAD_AUTHORIZATION, authorization);
		return headers;
	}

}
