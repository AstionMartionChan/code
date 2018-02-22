package com.hadoop.hdfs.rpc;

import org.apache.hadoop.ipc.ProtocolSignature;

import java.io.IOException;

/**
 * Created by leochan on 2018/2/11.
 */
public class Biz implements Bizable {
    public String sendHeart(String content) {
        System.out.println("接收到客户端心跳" + content);
        return "继续干活-----";
    }

    public long getProtocolVersion(String protocol, long clientVersion) throws IOException {
        return versionID;
    }

    public ProtocolSignature getProtocolSignature(String protocol, long clientVersion, int clientMethodsHash) throws IOException {
        return new ProtocolSignature();
    }
}
