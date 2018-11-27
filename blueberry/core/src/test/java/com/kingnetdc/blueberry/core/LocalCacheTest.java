package com.kingnetdc.blueberry.core;

import com.kingnetdc.blueberry.core.cache.KdcMemCache;
import org.junit.Test;

public class LocalCacheTest {

    @Test
    public void LocalCacheBytes() {
        KdcMemCache<Long, byte[]> cache = new KdcMemCache<>(1000);
        cache.set(100L, "download_item".getBytes());
        byte[] item = cache.get(100L);
        for (int i = 0; i < item.length; i ++) {
            System.out.println(" ----> " + item[i]);
        }
        System.out.println(new String(item));
    }

}
