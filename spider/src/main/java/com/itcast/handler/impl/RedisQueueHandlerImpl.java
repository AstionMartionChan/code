package com.itcast.handler.impl;

import com.itcast.handler.QueueHandler;
import com.itcast.utils.RedisClusterUtil;
import com.itcast.utils.RedisUtil;
import redis.clients.jedis.Jedis;

/**
 * Created by leochan on 2018/2/3.
 */
public class RedisQueueHandlerImpl implements QueueHandler {

    private static Jedis jedis;

    @Override
    public void add(String url) {
        RedisUtil.lpush("jdDetaillUrl", url);
//        RedisClusterUtil.lpush("jdDetaillUrl:list", url);
    }

    @Override
    public String poll() {
        return RedisUtil.rpop("jdDetaillUrl");
//        return RedisClusterUtil.rpop("jdDetaillUrl");
    }

}
