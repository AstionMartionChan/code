package com.kingnetdc.blueberry.core.http;

import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class KdcHttpClient {

    private static OkHttpClient client = new OkHttpClient();

    private static Logger logger = LoggerFactory.getLogger(KdcHttpClient.class);

    public KdcHttpClient() {

    }


    /**
     * 处理 get 请求，返回字符串
     * @param url
     * @return
     * @throws IOException
     */
    public static String doGet(String url) throws IOException {
        return doGetResponse(url).getBody();
    }

    public static String doGet(String url, Headers headers) throws IOException {
        return doGetResponse(url, headers).getBody();
    }


    /**
     * 直接返回 response 对象
     * @param url
     * @return
     * @throws IOException
     */
    public static KdcHttpResponse doGetResponse(String url) throws IOException {
        return doGetResponse(url, Headers.of());
    }

    public static KdcHttpResponse doGetResponse(String url, Headers headers) throws IOException {
        Request request = new Request.Builder().url(url).headers(headers).build();
        try (Response response = client.newCall(request).execute()) {
            logger.debug("doGet url:" + url + ", header:" + headers + ", response code:" + response.code());
            return new KdcHttpResponse(response.sentRequestAtMillis(), response.receivedResponseAtMillis(),
                    response.body().string(), response.headers(), response.protocol(), response.code(), response.message());
        }
    }


    /**
     * 返回 http body 的字符串
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    public static String doPost(String url, String body) throws IOException {
        return doPostResponse(url, body).getBody();
    }

    public static String doPost(String url, String body, String mediaTypeString) throws IOException {
        return doPostResponse(url, body, mediaTypeString).getBody();
    }

    public static String doPost(String url, String body, MediaType mediaType) throws IOException {
        return doPostResponse(url, body, mediaType).getBody();
    }

    public static String doPost(String url, String body, MediaType mediaType, Headers headers) throws IOException {
        return doPostResponse(url, body, mediaType, headers).getBody();
    }


    /**
     * 返回 response 对象
     * @param url
     * @param body
     * @return
     * @throws IOException
     */
    public static KdcHttpResponse doPostResponse(String url, String body) throws IOException {
        return doPostResponse(url, body, "application/json; charset=utf-8");
    }

    public static KdcHttpResponse doPostResponse(String url, String body, String mediaTypeString) throws IOException {
        MediaType mediaType = MediaType.get(mediaTypeString);
        return doPostResponse(url, body, mediaType);
    }

    public static KdcHttpResponse doPostResponse(String url, String body, MediaType mediaType) throws IOException {
        return doPostResponse(url, body, mediaType, Headers.of());
    }

    public static KdcHttpResponse doPostResponse(String url, String body, MediaType mediaType, Headers headers) throws IOException {
        RequestBody requestBody = RequestBody.create(mediaType, body);
        Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            logger.debug("doPost url:" + url + ", mediaType:" + mediaType + ", headers:" + headers + ", response code:" + response.code());
            return new KdcHttpResponse(response.sentRequestAtMillis(), response.receivedResponseAtMillis(),
                    response.body().string(), response.headers(), response.protocol(), response.code(), response.message());
        }
    }

    public static KdcHttpResponse doPostResponse(String url, RequestBody requestBody) throws IOException {
        return doPostResponse(url, requestBody, Headers.of());
    }

    public static KdcHttpResponse doPostResponse(String url, RequestBody requestBody, Headers headers) throws IOException {
        Request request = new Request.Builder().url(url).headers(headers).post(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            logger.debug("doPost url:" + url + ", headers:" + headers + ", response code:" + response.code());
            return new KdcHttpResponse(response.sentRequestAtMillis(), response.receivedResponseAtMillis(),
                    response.body().string(), response.headers(), response.protocol(), response.code(), response.message());
        }
    }

    /**
     * doPut 多种的实现
     * @param url
     * @param requestBody
     * @return
     * @throws IOException
     */
    public static String doPut(String url, RequestBody requestBody) throws IOException {
        return doPutResponse(url, requestBody).getBody();
    }

    public static String doPut(String url, RequestBody requestBody, Headers headers) throws IOException {
        return doPutResponse(url, requestBody, headers).getBody();
    }

    public static KdcHttpResponse doPutResponse(String url, RequestBody requestBody) throws IOException {
        return doPutResponse(url, requestBody, Headers.of());
    }

    public static KdcHttpResponse doPutResponse(String url, RequestBody requestBody, Headers headers) throws IOException {
        Request request = new Request.Builder().url(url).headers(headers).put(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            logger.debug("doPutResponse url:" + url + ", headers:" + headers + ", response code:" + response.code());
            return new KdcHttpResponse(response.sentRequestAtMillis(), response.receivedResponseAtMillis(),
                    response.body().string(), response.headers(), response.protocol(), response.code(), response.message());
        }
    }

    /**
     * doDelete 多种的实现
     * @param url
     * @param requestBody
     * @return
     * @throws IOException
     */
    public static String doDelete(String url, RequestBody requestBody) throws IOException {
        return doDeleteResponse(url, requestBody).getBody();
    }

    public static String doDelete(String url, RequestBody requestBody, Headers headers) throws IOException {
        return doDeleteResponse(url, requestBody, headers).getBody();
    }

    public static KdcHttpResponse doDeleteResponse(String url, RequestBody requestBody) throws IOException {
        return doDeleteResponse(url, requestBody, Headers.of());
    }

    public static KdcHttpResponse doDeleteResponse(String url, RequestBody requestBody, Headers headers) throws IOException {
        Request request = new Request.Builder().url(url).headers(headers).put(requestBody).build();
        try (Response response = client.newCall(request).execute()) {
            logger.debug("doDeleteResponse url:" + url + ", headers:" + headers + ", response code:" + response.code());
            return new KdcHttpResponse(response.sentRequestAtMillis(), response.receivedResponseAtMillis(),
                    response.body().string(), response.headers(), response.protocol(), response.code(), response.message());
        }
    }

}
