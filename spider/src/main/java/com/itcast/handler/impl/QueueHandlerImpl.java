package com.itcast.handler.impl;

import com.itcast.handler.QueueHandler;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * Created by leochan on 2018/2/3.
 */
public class QueueHandlerImpl implements QueueHandler {

    private Queue<String> queue = new ConcurrentLinkedQueue<>();

    @Override
    public void add(String url) {
        queue.add(url);
    }

    @Override
    public String poll() {
        String url = queue.poll();
        return url;
    }
}
