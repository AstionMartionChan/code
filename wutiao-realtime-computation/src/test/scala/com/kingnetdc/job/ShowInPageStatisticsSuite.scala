package com.kingnetdc.job

import java.nio.charset.Charset

import com.google.common.hash.{PrimitiveSink, Funnel}
import com.kingnetdc.UnitSpecs
import com.kingnetdc.blueberry.cache.KdcRedisCluster
import com.kingnetdc.utils.RedisBasedBloomFilter
import com.kingnetdc.watermelon.utils.AppConstants._

/**
 * Created by zhouml on 14/08/2018.
 */
class ShowInPageStatisticsSuite extends UnitSpecs {

    private var cluster: KdcRedisCluster = null
    private var stringFunnel: Funnel[String] = null

    override def beforeAll(): Unit = {
        cluster = new KdcRedisCluster("192.168.77.11:6380,192.168.77.25:6380,192.168.77.26:6380", "123456")
        stringFunnel = new Funnel[String] {
            override def funnel(from: String, into: PrimitiveSink): Unit = {
                into.putString(from, Charset.forName(UTF8))
            }
        }
    }

    "getMergedBloomFilter" should "pass check" in {
        val redisKey = "wutiao:showinpage:v1.0:2127092"

        cluster.getJedisCluster().del(redisKey)
        val itemList1 = List("278112866959089664", "278090762633732096")

        val initialBloomFilter = ShowInPageStatistics.getMergedBloomFilter(redisKey, itemList1, Map.empty, cluster)

        initialBloomFilter.mightContain("278112866959089664") shouldBe true
        initialBloomFilter.mightContain("278090762633732096") shouldBe true

        RedisBasedBloomFilter.setBloomFilter(redisKey, initialBloomFilter, cluster)

        val itemList2 = List("278292340600906752", "278090822574482432")
        val modifiedBloomFilter = ShowInPageStatistics.getMergedBloomFilter(redisKey, itemList2, Map.empty, cluster)

        modifiedBloomFilter.mightContain("278112866959089664") shouldBe true
        modifiedBloomFilter.mightContain("278090762633732096") shouldBe true
        modifiedBloomFilter.mightContain("278292340600906752") shouldBe true
        modifiedBloomFilter.mightContain("278090822574482432") shouldBe true

        cluster.getJedisCluster().del(redisKey)
    }

    "saveShowInPageToRedis offline -> online" should "pass check" in {
        val didRedisKey = ShowInPageStatistics.buildShowInPageRedisKey("v1.0", "B7EED9C9C9552B78077EBE28DF73BDD4")
        val uidRedisKey = ShowInPageStatistics.buildShowInPageRedisKey("v1.0", "2160962")

        cluster.getJedisCluster().del(didRedisKey)
        cluster.getJedisCluster().del(uidRedisKey)

        val redisConfig = Map(
            "redis.env" -> "v1.0",
            "redis.connect" -> "192.168.77.11:6380,192.168.77.25:6380,192.168.77.26:6380"
        )

        // 最开始未登录
        val itemList1 = List("278112866959089664", "278090762633732096")
        val offlineShowInPage1: List[((Option[String], Option[String]), List[String])] =
            ((None, Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList1) :: Nil

        ShowInPageStatistics.saveShowInPageToRedis(offlineShowInPage1, redisConfig)

        val didBloomFilter =
            RedisBasedBloomFilter.getBloomFilter[String](didRedisKey, stringFunnel, cluster).get

        didBloomFilter.mightContain("278112866959089664") shouldBe true
        didBloomFilter.mightContain("278090762633732096") shouldBe true
        didBloomFilter.approximateElementCount() shouldBe 2

        // 登录
        val itemList2 = List("278080359459534848", "278082963907786752")
        val onlineShowInPage: List[((Option[String], Option[String]), List[String])] =
            ((Some("2160962"), Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList2) :: Nil

        ShowInPageStatistics.saveShowInPageToRedis(onlineShowInPage, redisConfig)

        val didBloomFilterOpt = RedisBasedBloomFilter.getBloomFilter[String](didRedisKey, stringFunnel, cluster)
        didBloomFilterOpt.isEmpty shouldBe true

        val uidBloomFilter = RedisBasedBloomFilter.getBloomFilter[String](uidRedisKey, stringFunnel, cluster).get

        uidBloomFilter.mightContain("278112866959089664") shouldBe true
        uidBloomFilter.mightContain("278090762633732096") shouldBe true
        uidBloomFilter.mightContain("278080359459534848") shouldBe true
        uidBloomFilter.mightContain("278082963907786752") shouldBe true
        uidBloomFilter.approximateElementCount() shouldBe 4

        cluster.getJedisCluster().del(didRedisKey)
        cluster.getJedisCluster().del(uidRedisKey)
    }

    "saveShowInPageToRedis online -> offline" should "pass check" in {
        val didRedisKey = ShowInPageStatistics.buildShowInPageRedisKey("v1.0", "B7EED9C9C9552B78077EBE28DF73BDD4")
        val uidRedisKey = ShowInPageStatistics.buildShowInPageRedisKey("v1.0", "2160962")

        cluster.getJedisCluster().del(didRedisKey)
        cluster.getJedisCluster().del(uidRedisKey)

        val redisConfig = Map(
            "redis.env" -> "v1.0",
            "redis.connect" -> "192.168.77.11:6380,192.168.77.25:6380,192.168.77.26:6380"
        )

        // 登录
        val itemList1 = List("278080359459534848", "278082963907786752")
        val onlineShowInPage: List[((Option[String], Option[String]), List[String])] =
            ((Some("2160962"), Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList1) :: Nil

        ShowInPageStatistics.saveShowInPageToRedis(onlineShowInPage, redisConfig)

        val uidBloomFilter = RedisBasedBloomFilter.getBloomFilter[String](uidRedisKey, stringFunnel, cluster).get
        uidBloomFilter.mightContain("278080359459534848") shouldBe true
        uidBloomFilter.mightContain("278082963907786752") shouldBe true
        uidBloomFilter.approximateElementCount() shouldBe 2

        // 未登录 --> 不影响
        val itemList2 = List("278112866959089664", "278090762633732096")
        val offlineShowInPage1: List[((Option[String], Option[String]), List[String])] =
            ((None, Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList2) :: Nil

        ShowInPageStatistics.saveShowInPageToRedis(offlineShowInPage1, redisConfig)

        val didBloomFilter =
            RedisBasedBloomFilter.getBloomFilter[String](didRedisKey, stringFunnel, cluster).get

        didBloomFilter.mightContain("278112866959089664") shouldBe true
        didBloomFilter.mightContain("278090762633732096") shouldBe true
        didBloomFilter.approximateElementCount() shouldBe 2

        uidBloomFilter.mightContain("278080359459534848") shouldBe true
        uidBloomFilter.mightContain("278082963907786752") shouldBe true
        uidBloomFilter.approximateElementCount() shouldBe 2

        cluster.getJedisCluster().del(didRedisKey)
        cluster.getJedisCluster().del(uidRedisKey)
    }

    "saveShowInPageToRedis online -> offline -> online" should "pass check" in {
        val didRedisKey = ShowInPageStatistics.buildShowInPageRedisKey("v1.0", "B7EED9C9C9552B78077EBE28DF73BDD4")
        val uidRedisKey = ShowInPageStatistics.buildShowInPageRedisKey("v1.0", "2160962")

        val itemList1 = List("278080359459534848", "278082963907786752")
        val itemList2 = List("278112866959089664", "278090762633732096")
        val itemList3 = List("278223355746723840", "278293811824692224")

        cluster.getJedisCluster().del(didRedisKey)
        cluster.getJedisCluster().del(uidRedisKey)

        val redisConfig = Map(
            "redis.env" -> "v1.0",
            "redis.connect" -> "192.168.77.11:6380,192.168.77.25:6380,192.168.77.26:6380"
        )

        // 登录 和 非登录同时出现,  非登录后执行, 当次不会立即合并这个曝光, 在下次才会
        val onlineShowInPage1: List[((Option[String], Option[String]), List[String])] =
            ((Some("2160962"), Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList1) :: Nil
        ShowInPageStatistics.saveShowInPageToRedis(onlineShowInPage1, redisConfig)

        // 未登录 --> 不影响
        val offlineShowInPage1: List[((Option[String], Option[String]), List[String])] =
            ((None, Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList2) :: Nil
        ShowInPageStatistics.saveShowInPageToRedis(offlineShowInPage1, redisConfig)

        val uidBloomFilter1 = RedisBasedBloomFilter.getBloomFilter[String](uidRedisKey, stringFunnel, cluster).get
        uidBloomFilter1.mightContain("278080359459534848") shouldBe true
        uidBloomFilter1.mightContain("278082963907786752") shouldBe true
        uidBloomFilter1.approximateElementCount() shouldBe 2

        val didBloomFilter1 = RedisBasedBloomFilter.getBloomFilter[String](didRedisKey, stringFunnel, cluster).get
        didBloomFilter1.mightContain("278112866959089664") shouldBe true
        didBloomFilter1.mightContain("278090762633732096") shouldBe true
        didBloomFilter1.approximateElementCount() shouldBe 2

        val onlineShowInPage2: List[((Option[String], Option[String]), List[String])] =
            ((Some("2160962"), Some("B7EED9C9C9552B78077EBE28DF73BDD4")), itemList3) :: Nil
        ShowInPageStatistics.saveShowInPageToRedis(onlineShowInPage2, redisConfig)

        val uidBloomFilter2 = RedisBasedBloomFilter.getBloomFilter[String](uidRedisKey, stringFunnel, cluster).get
        (itemList1 ::: itemList2 ::: itemList3).foreach { item =>
            uidBloomFilter2.mightContain(item) shouldBe true
        }
        uidBloomFilter2.approximateElementCount() shouldBe 6

        RedisBasedBloomFilter.getBloomFilter[String](didRedisKey, stringFunnel, cluster).isEmpty shouldBe true

        cluster.getJedisCluster().del(didRedisKey)
        cluster.getJedisCluster().del(uidRedisKey)
    }

    override def afterAll(): Unit = {
        cluster.close()
    }

}
