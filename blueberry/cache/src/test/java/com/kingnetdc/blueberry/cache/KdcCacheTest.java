package com.kingnetdc.blueberry.cache;

import static org.junit.Assert.assertTrue;

import com.kingnetdc.blueberry.cache.base.Tuple3;
import com.kingnetdc.blueberry.core.io.Path;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

/**
 * 测试缓存
 */
public class KdcCacheTest {


    /**
     * 测试内存缓存
     */
    @Test
    public void testMemoryCache() {
        checkItem("kdcCache-memory.yml");
    }


    /**
     * 测试 redis 单实例
     */
    @Test
    public void testRedisCache() {
        checkItem("kdcCache-redis.yml");
    }

    /**
     * 测试 redis 集群
     */
    @Test
    public void testRedisClusterCache() {
        checkItem("kdcCache-redis-cluster.yml");
    }

    /**
     * 测试 mysql 单连接
     */
    @Test
    public void testMysqlCache() {
        checkItem("kdcCache-mysql.yml");
    }

    /**
     * 测试 MySQL 连接池
     */
    @Test
    public void testMysqlDataSourceCache() {
        checkItem("kdcCache-mysql-datasource.yml");
    }

    private void checkItem(String fileName) {
        String testA = null, testB = null, testC = null;
        Map<String, String> result = null;
        Map<String, Boolean> multiExistsRet = null;
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile(fileName))));
            // 清理测试值
            kdcCache.remove("testA");
            kdcCache.remove("testB");
            kdcCache.remove("testC");

            // 检查值是否存在
            kdcCache.set("testA", "testAAA", 1000);

            System.out.println("first get : " + kdcCache.get("testA"));
            System.out.println("first exists : " + kdcCache.exists("testA"));

            // 清理值
            kdcCache.remove("testA");

            // 检查状态
            System.out.println("second get : " + kdcCache.get("testA"));
            System.out.println("second exists : " + kdcCache.exists("testA"));

            // multi get / set

            List<Tuple3> tuples = new ArrayList<>();
            tuples.add(new Tuple3("testB", "testBBB", 1000));
            tuples.add(new Tuple3("testC", "testCCC", 1000));
            kdcCache.multiSet(tuples);

            result = kdcCache.multiGet(Arrays.asList("testB", "testC"));
            multiExistsRet = kdcCache.multiExists(Arrays.asList("testB", "testC"));

            System.out.println("third multi get testB is : " + result.get("testB"));
            System.out.println("third multi get testC is : " + result.get("testC"));
            System.out.println("third multi exists testB is : " + multiExistsRet.get("testB"));
            System.out.println("third multi exists testC is : " + multiExistsRet.get("testC"));

            // 清理值
            kdcCache.remove("testB");
            kdcCache.remove("testC");

            result = kdcCache.multiGet(Arrays.asList("testB", "testC"));
            multiExistsRet = kdcCache.multiExists(Arrays.asList("testB", "testC"));

            System.out.println("four multi get testB is : " + result.get("testB"));
            System.out.println("four multi get testC is : " + result.get("testC"));
            System.out.println("four multi exists testB is : " + multiExistsRet.get("testB"));
            System.out.println("four multi exists testC is : " + multiExistsRet.get("testC"));

            kdcCache.set("testA", "testAAA", 1000);
            List<Tuple3> tupless = new ArrayList<>();
            tupless.add(new Tuple3("testB", "testBBB", 1000));
            tupless.add(new Tuple3("testC", "testCCC", 1000));
            kdcCache.multiSet(tupless);

            testA = kdcCache.get("testA");
            testB = kdcCache.get("testB");
            testC = kdcCache.get("testC");

        } catch (Throwable e) {
            e.printStackTrace();
        }
        assertTrue(("testAAA".equals(testA) && "testBBB".equals(testB) && "testCCC".equals(testC)));
    }

    /**
     * 测试整个流程的数据
     */
    @Test
    public void testAll() {
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile("kdcCache.yml"))));
            for (int i = 1; i <= 100; i ++) {
                System.out.println("now is : " + i);
                kdcCache.set(String.valueOf(i), String.valueOf(i), 1000);
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试回写流程的数据
     */
    @Test
    public void testAllWriteBack() {
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile("kdcCache.yml"))));
            for (int i = 1; i <= 100; i ++) {
                System.out.println("now is : " + i + ", and cache result is : " + kdcCache.get(String.valueOf(i)));
            }

        } catch (Throwable e) {
            e.printStackTrace();
        }
    }



    /**
     * 测试multiGet回写流程的数据
     */
    @Test
    public void testMultiGetRedisCluster() {
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile("kdcCache.yml"))));
            ArrayList<String> numArrList = new ArrayList<>();
            IntStream.range(1, 101).forEach(itemNum -> {
                numArrList.add(String.valueOf(itemNum));
            });
            kdcCache.multiGet(numArrList).forEach((num, val) -> {
                System.out.println("now is : " + num + ", and cache result value is : " + val);
            });
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试回写流程的数据
     */
    @Test
    public void testCleanRedisCluster() {
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile("kdcCache-redis-cluster.yml"))));
            for (int i = 1; i <= 100; i ++) {
                kdcCache.remove(String.valueOf(i));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试回写流程的数据
     */
    @Test
    public void testExistsRedisCluster() {
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile("kdcCache-redis-cluster.yml"))));
            for (int i = 1; i <= 100; i ++) {
                System.out.println("now is : " + i + ", and cache exists is : " + kdcCache.exists(String.valueOf(i)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 测试回写流程的数据
     */
    @Test
    public void testGetRedisCluster() {
        try {

            KdcCache kdcCache = KdcCache.builder(new FileInputStream(new File(Path.getResourceFile("kdcCache-redis-cluster.yml"))));
            for (int i = 1; i <= 100; i ++) {
                System.out.println("now is : " + i + ", and get cluster is : " + kdcCache.get(String.valueOf(i)));
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

}
