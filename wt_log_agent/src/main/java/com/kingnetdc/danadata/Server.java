package com.kingnetdc.danadata;

import com.kingnetdc.danadata.reader.DanaReader;
import com.kingnetdc.danadata.reader.Reader;
import com.kingnetdc.danadata.util.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sun.misc.Signal;
import sun.misc.SignalHandler;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by yangjun on 17/3/27.
 */
public class Server implements SignalHandler {

    private static final Logger logger = LoggerFactory.getLogger(Server.class);

    public Map<String, DanaReader> readers;

    public static void main(String[] args) {

        Server server = new Server();

        Signal.handle(new Signal("TERM"), server);
        Signal.handle(new Signal("INT"), server);

        server.readers = new HashMap<String, DanaReader>();

        final String relativeConfPath = "./config";
        Config config;
        config = new Config(relativeConfPath);

        String pattern = config.get("pattern");
        String path = config.get("path");

        String serverUrl = config.get("serverUrl");



        String token = config.get("token");
        String key = config.get("key");
        String topic = config.get("topic");
        String requestTimeout = config.get("requestTimeout");
        int maxReadNum = Integer.valueOf(config.get("maxReadNum", "400"));
        String reSendNum = config.get("reSendNum", "3");

        if (maxReadNum > 400) {
            maxReadNum = 400;
        }
        if (pattern == null || pattern.isEmpty()) {
            logger.error("pattern is empty, please check.");
            return;
        }
        Map<String, String> sendParams = new HashMap<String, String>();
        sendParams.put("serverUrl", serverUrl);

        sendParams.put("token", token);
        sendParams.put("key", key);
        sendParams.put("topic", topic);
        sendParams.put("requestTimeout", requestTimeout);
        sendParams.put("maxSend", String.valueOf(maxReadNum));
        sendParams.put("reSendNum", reSendNum);

        DanaReader reader = new DanaReader();
        reader.configure(pattern, path, maxReadNum, sendParams);
        server.readers.put(pattern, reader);
        reader.start();
        logger.info("start read files ...");
    }

    @Override
    public void handle(Signal sn) {
        signalCallBack(sn);
    }

    private void signalCallBack(Signal sn) {
        if (this.readers != null && this.readers.size() > 0) {
            for (String key : this.readers.keySet()) {
                DanaReader reader = this.readers.get(key);
                reader.setStop();
            }
        }
        logger.info("stop read files ...");
    }
}
