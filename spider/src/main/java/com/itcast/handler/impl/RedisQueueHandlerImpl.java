package com.itcast.handler.impl;

import com.itcast.handler.QueueHandler;
import com.itcast.po.Spider;
import com.itcast.utils.RedisClusterUtil;
import com.itcast.utils.RedisUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;

/**
 * Created by leochan on 2018/2/3.
 */
public class RedisQueueHandlerImpl implements QueueHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(RedisQueueHandlerImpl.class);

    @Override
    public void add(String url) {
        LOGGER.info("正在存入队列： {}", url);
        RedisUtil.lpush("jd_detail_url", url);
//        RedisClusterUtil.lpush("jdDetaillUrl:list", url);
    }

    @Override
    public String poll() {
        return RedisUtil.rpop("jd_detail_url");
//        return RedisClusterUtil.rpop("jdDetaillUrl");
    }

}
