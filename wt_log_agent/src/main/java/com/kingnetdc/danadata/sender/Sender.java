package com.kingnetdc.danadata.sender;

import java.util.List;
import java.util.Map;

/**
 * Created by yangjun on 17/3/24.
 */
public abstract class Sender {
	public abstract boolean configure(Map<String, String> params);

	public abstract boolean send(List<String> data, Map<String, String> params);

	public void setSendTopic(String send_topic){}

	public abstract boolean close();
}
