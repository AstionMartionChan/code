package com.kingnetdc.blueberry.core.http;

import okhttp3.Headers;
import okhttp3.Protocol;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcHttpResponse {

    private long sentRequestAtMillis;
    private long receivedResponseAtMillis;
    private String body;
    private Headers headers;
    private Protocol protocol;
    private int code;
    private String message;

    public KdcHttpResponse(long sentRequestAtMillis, long receivedResponseAtMillis, String body, Headers headers, Protocol protocol, int code, String message) {
        this.sentRequestAtMillis = sentRequestAtMillis;
        this.receivedResponseAtMillis = receivedResponseAtMillis;
        this.body = body;
        this.headers = headers;
        this.protocol = protocol;
        this.code = code;
        this.message = message;
    }

    public long getSentRequestAtMillis() {
        return sentRequestAtMillis;
    }

    public long getReceivedResponseAtMillis() {
        return receivedResponseAtMillis;
    }

    public String getBody() {
        return body;
    }

    public Headers getHeaders() {
        return headers;
    }

    public Protocol getProtocol() {
        return protocol;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "KdcHttpResponse{" +
                "sentRequestAtMillis=" + sentRequestAtMillis +
                ", receivedResponseAtMillis=" + receivedResponseAtMillis +
                ", body=" + body +
                ", headers=" + headers +
                ", protocol=" + protocol +
                ", code=" + code +
                ", message='" + message + '\'' +
                '}';
    }
}
