package com.kingnetdc.job

import com.google.common.collect.Lists
import com.kingnetdc.UnitSpecs
import com.kingnetdc.blueberry.cache.RedisClusterCache
import com.kingnetdc.blueberry.cache.base.Constants._
import com.kingnetdc.utils.JsonUtils
import com.kingnetdc.watermelon.utils.StringUtils
import redis.clients.jedis.HostAndPort
import scala.collection.JavaConversions._

/**
  * Created by zhouml on 02/06/2018.
  */
class HomePageMixRcmdSuite extends UnitSpecs {

    private val NewsRedisKey = "wutiao:homepage-rcmd:popular-news"
    private val VideoRedisKey = "wutiao:homepage-rcmd:popular-video"

    private val ItemId = "itemid"
    private val Score = "score"

    private var cluster: RedisClusterCache = null

    val fixture = {
        new {
            cluster = new RedisClusterCache(
                Map(REDIS_CLUSTER_CONNECT -> "192.168.77.11:6380,192.168.77.25:6380,192.168.77.26:6380")
            )
        }
    }

    "setItemScore and getItemScore" should "pass check" in {
        import fixture._

        val newsRedisKeyValue = JsonUtils.render(
            List(
                Map(
                    ItemId -> "13131",
                    Score -> 2.34
                )
            )
        )

        val videoRedisKeyValue = JsonUtils.render(
            List(
                Map(
                    ItemId -> "12345",
                    Score -> 4.24
                )
            )
        )

        cluster.set(NewsRedisKey, newsRedisKeyValue, 60)
        cluster.set(VideoRedisKey, videoRedisKeyValue, 60)
    }

    def mixRcmdByRatio(
      newsItemScoreLen: Int, videoItemScoreLen: Int,
      mixInRatio: (Int, Int), maxToFetch: Int
    ) = {
        if (newsItemScoreLen == 0 || videoItemScoreLen == 0) {
            (math.min(newsItemScoreLen, maxToFetch), math.min(videoItemScoreLen, maxToFetch))
        } else {
            val (newsRatio, videoRatio) = mixInRatio

            val maxNewsToFetch = ((newsRatio / (newsRatio + videoRatio).toDouble) * maxToFetch).toInt
            val maxVideoToFetch = ((videoRatio / (newsRatio + videoRatio).toDouble) * maxToFetch).toInt

            if (newsItemScoreLen >= newsRatio * videoItemScoreLen) {
                val newsToFetch = math.min(maxNewsToFetch, newsRatio * videoItemScoreLen)
                val videoToFetch = math.min(maxVideoToFetch, videoItemScoreLen)

                (newsToFetch, videoToFetch)
            } else {
                val newsToFetch = math.min(maxNewsToFetch, newsItemScoreLen)
                val videoToFetch = math.min(maxVideoToFetch, (videoRatio / newsRatio.toDouble) * newsItemScoreLen).toInt

                (newsToFetch, videoToFetch)
            }
        }
    }

    "mixRcmdByRatio" should "pass check" in {
        mixRcmdByRatio(5000, 1000, (4, 1), 5000) shouldBe (4000, 1000)

        mixRcmdByRatio(8100, 2345, (4, 1), 5000) shouldBe (4000, 1000)

        mixRcmdByRatio(1300, 234, (4, 1), 5000) shouldBe (936, 234)

        mixRcmdByRatio(1200, 1, (4, 1), 5000) shouldBe (4, 1)

        mixRcmdByRatio(1200, 0, (4, 1), 5000) shouldBe (1200, 0)
    }

    "Kryo serialization & deserialization" should "pass check" in {
        val samples: java.util.List[(String, Double)] = Lists.newArrayList(("A1" -> 3.2), ("A2" -> 3.34))
        val serializeOne = StringUtils.serializeObject(samples)
        val deserializeOne = StringUtils.deserializeObject[java.util.List[(String, Double)]](serializeOne)
        deserializeOne shouldBe samples
    }

    override def afterAll(): Unit = {
        cluster.close()
    }

}
