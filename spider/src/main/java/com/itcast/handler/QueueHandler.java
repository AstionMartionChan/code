package com.itcast.handler;

/**
 * Created by leochan on 2018/2/3.
 */
public interface QueueHandler {

    void add(String url);

    String poll();
}
