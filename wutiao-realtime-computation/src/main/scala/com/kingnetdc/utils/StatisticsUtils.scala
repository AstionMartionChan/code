package com.kingnetdc.utils

import com.kingnetdc.sink.SingletonMysqlSink
import java.util.concurrent.TimeUnit
import com.kingnetdc.blueberry.cache.base.Tuple3
import com.kingnetdc.model.{NotificationChannelEnum, KPIRecord}
import org.apache.spark.streaming.Time
import scala.collection.mutable.ListBuffer
import scala.concurrent.duration.Duration
import com.kingnetdc.blueberry.cache.KdcCache
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.watermelon.utils.{MessageAlert, CommonUtils, DateUtils, Logging}
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.SparkConf
import scala.collection.JavaConversions._
import scala.collection.mutable
import scala.util.control.NonFatal
import scala.util.{Failure, Random, Success}
import org.roaringbitmap.{RoaringBitmap => BitMap}

// scalastyle:off
object StatisticsUtils extends Logging {

    val ABNORMAL_UID = "0"

    /**
     * 1：资讯，2：视频
     */
    val ITEM_TYPE = List("1", "2")

    val IS_NEW_USER = "is_new_user"

    val IS_OLD_USER = "is_old_user"

    val BYTE_ONE: Byte = 1

    val BYTE_ZERO: Byte = 0

    val mediaPrefix = "wutiao:media:"

    val itemPrefix = "wutiao:item:"

    val RECEIVERS_KEY = "notification.receivers"

    val DEFAULT_RECEIVERS = "weixin:zhouml@kingnet.com,zhoujiongyu@kingnet.com"

    def bitmapOf(int: Int*): BitMap = {
        val bitmap =  new BitMap()
        bitmap.add(int: _*)
        bitmap
    }

    def initializeBitMap(b: Byte, uid: Int) = {
        if (b == BYTE_ONE) bitmapOf(uid) else bitmapOf()
    }

    def fillBitMap(bitMap: BitMap, b: Byte, uid: Int) = {
        if (b == BYTE_ONE) {
            bitMap.add(uid)
            bitMap
        } else bitMap
    }

    type ValueType = (Int, List[Byte])

    type CombinerType = List[BitMap]

    def createCombiner(value: ValueType): CombinerType = {
        val (id, bytes) = value
        bytes.map { byte => initializeBitMap(byte, id)}
    }

    def mergeValue(combiner: CombinerType, value: ValueType): CombinerType = {
        val (id, bytes) = value

        (combiner zip bytes).map {
            case (combiner, byte) => fillBitMap(combiner, byte, id)
        }
    }

    def mergeCombiner(oneCombiner: CombinerType, otherCombiner: CombinerType): CombinerType = {
        (oneCombiner zip otherCombiner).map {
            case (one, other) =>
                one.or(other)
                one
        }
    }

    implicit def booleanToByte(boolean: Boolean) = {
        if (boolean) BYTE_ONE else BYTE_ZERO
    }

    implicit def booleanOptToByte(booleanOpt: Option[Boolean]): Byte =
        booleanOpt.map(booleanToByte).getOrElse(BYTE_ZERO)

    @Deprecated
    def getInfluxDBConfig(config: Map[String, String]): Map[String, String] = {
        Map(
            INFLUXDB_HOST -> config("influxdb.host").toString,
            INFLUXDB_USERNAME -> config("influxdb.username").toString,
            INFLUXDB_PASSWORD -> config("influxdb.password").toString,
            INFLUXDB_DB -> config("influxdb.db").toString
        )
    }

    def getMysqlConfig(config: Map[String, String]) = {
        Map(
            MYSQL_URL -> config("mysql.url"),
            MYSQL_USER -> config("mysql.user"),
            MYSQL_PASSWORD -> config("mysql.password")
        )
    }

    @Deprecated
    def getUserStatusFilterFieldMap(
        userStatus: Map[Long, Boolean], durations: List[Long]
    ): Map[String, Byte] = {
        require(durations.size == 3, "Currently we only support three calculation duration")

        val one :: two :: three :: Nil = durations

        val userStatusInFirstDuration = userStatus.get(one).getOrElse(false)
        val userStatusInSecondDuration = userStatus.get(two).getOrElse(false)
        val userStatusInThirdDuration = userStatus.get(three).getOrElse(false)

        Map(
            s"${IS_NEW_USER}_${one}" -> userStatusInFirstDuration,
            s"${IS_NEW_USER}_${two}" -> userStatusInSecondDuration,
            s"${IS_NEW_USER}_${three}" -> userStatusInThirdDuration,

            s"${IS_OLD_USER}_${one}" -> !userStatusInFirstDuration,
            s"${IS_OLD_USER}_${two}" -> !userStatusInSecondDuration,
            s"${IS_OLD_USER}_${three}" -> !userStatusInThirdDuration
        )
    }

    def getKafkaParams(
        bootstrapServers: String, topic: String,
        group: String, offSetOpt: Option[String] = None
    ): Map[String, Object] = {
        Map(
            BootstrapServers -> bootstrapServers,
            TOPIC -> topic,
            KAFKA_GROUP -> group,
            "auto.offset.reset" -> offSetOpt.getOrElse("latest"),
            "key.deserializer" -> classOf[StringDeserializer],
            "value.deserializer" -> classOf[StringDeserializer],
            "enable.auto.commit" -> (false: java.lang.Boolean)
        )
    }

    def sparkConfCheck(sparkConf: SparkConf) = {
        require(sparkConf.contains(SPARK_CP_DIR), s"${SPARK_CP_DIR} is missing")
        require(sparkConf.contains(SPARK_STREAMING_DURATION), s"${SPARK_STREAMING_DURATION} is missing")
    }

    @Deprecated
    def statusCheck(
        currentId: String, eventTime: Long,
        activeAt: Map[String, Long], calculationDurations: List[Long]
    ): Map[Long, Boolean] = {
        require(calculationDurations.size > 1, "calculation duration should not be empty")

        (activeAt.get(currentId) match {
            case Some(activeTime) =>
                calculationDurations.map { calculationDuration =>
                    val gap =
                        DateUtils.floor(eventTime, calculationDuration) -
                        DateUtils.floor(activeTime, calculationDuration)

                    if (gap < 0) {
                        logger.warn(s"Event time ${eventTime} is earlier than activeTime ${activeTime}")
                    }

                    calculationDuration -> (gap < calculationDuration)
                }
            case None =>
                calculationDurations.map { calculationDuration =>
                    calculationDuration -> true
                }
        }).toMap
    }

    /**
     *  特殊的5分钟判新逻辑
     *
     * @param currentId
     * @param eventTime
     * @param activeAt
     * @param calculationDurations
     *
     * @return
     */
    def statusCheckFive(
        currentId: String, eventTime: Long,
        activeAt: Map[String, Long], calculationDurations: List[Long]
    ): Map[Long, Boolean] = {
        require(calculationDurations.size > 1, "calculation duration should not be empty")

        (activeAt.get(currentId) match {
            case Some(activeTime) =>
                calculationDurations.map { calculationDuration =>
                    // 严格判断, 判新事件一定这个批次的第一个事件
                    if (calculationDuration == 300000) {
                        calculationDuration -> (activeTime == eventTime)
                    } else {
                        val gap =
                            DateUtils.floor(eventTime, calculationDuration) -
                            DateUtils.floor(activeTime, calculationDuration)

                        if (gap < 0) {
                            logger.warn(s"Event time ${eventTime} is earlier than activeTime ${activeTime}")
                        }

                        calculationDuration -> (gap < calculationDuration)
                    }
                }
            case None =>
                calculationDurations.map { calculationDuration =>
                    if (calculationDuration == 86400000) {
                        calculationDuration -> false
                    } else {
                        calculationDuration -> true
                    }
                }
        }).toMap
    }


    @Deprecated
    def checkNew(
        userKeyValue: Map[String, String], id: String,
        eventTime: Long, batchTime: Long, timeDurations: List[Long]
    ): Map[Long, Boolean] = {
        if (timeDurations.size < 1) {
            throw new Exception("time duration size should be larger than 0 ")
        }
        val minDuration = timeDurations.sorted.get(0)
        val value = userKeyValue.get(id)
        value match {
            case None => {
                timeDurations.map(time => {
                    (time -> true)
                }).toMap
            }
            case Some(timestamp) => {
                if (timestamp == null) {
                    timeDurations.map(time => {
                        (time -> true)
                    }).toMap
                } else if (batchTime - eventTime < minDuration) {
                    val map: mutable.Map[Long, Boolean] = mutable.Map.empty
                    timeDurations.foreach(time => {
                        if (Math.abs(DateUtils.floor(eventTime, time) -
                                DateUtils.floor(timestamp.toLong, time)) < time) {
                            map.put(time, true)
                        } else {
                            map.put(time, false)
                        }
                    })
                    map.toMap
                } else {
                    val map: mutable.Map[Long, Boolean] = mutable.Map.empty
                    timeDurations.foreach(time => {
                        if (Math.abs(DateUtils.floor(eventTime, time) -
                                DateUtils.floor(timestamp.toLong, time)) < time) {
                            map.put(time, true)
                        } else {
                            map.put(time, false)
                        }
                    })
                    map.put(minDuration, false)
                    map.toMap
                }
            }
        }
    }

    def getKeyValue(cacheConfig: String, ids: List[String]): Map[String, String] = {
        val kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(cacheConfig))
        CommonUtils.safeRelease(kdcCache)(kdcCache => {
            kdcCache.multiGet(ids).toMap
        })() match {
            case Success(value) => value.filter( kv => {
                kv._2 != null
            })
            case Failure(e) =>
                logger.error(s"Failed to get key|value from ${cacheConfig}", e)
                Map.empty
        }
    }

    /**
      * 获取redis以及mysql中存储的用户信息
      * | key | value
      * ——————————————————————————
      * | id  | firstTime,channel
     *
     * @param cacheConfig
      * @param ids
     *
     * @return 用户 | 首次活跃或安装时间,渠道
      */
    def getUserInfo(cacheConfig: String, ids: List[String]): Map[String, String] = {
        getKeyValue(cacheConfig, ids)
    }

    /**
      * 获取用户或设备的首次时间
     *
     * @param cacheConfig
      * @param ids
     *
     * @return id | 首次时间
      */
    def getFirstTime(cacheConfig: String, ids: List[String]): Map[String, Long] = {
        getKeyValue(cacheConfig, ids).map(values => {
            values._1 -> values._2.split(",")(0).toLong
        })
    }

    /**
      * 获取redis以及mysql中的自媒体信息
      * | key | value |
      * ———————————————————————————
      * | id  | status,mediaType |
     *
     * @param cacheConfig
      * @param ids
     *
     * @return 自媒体id | 自媒体状态,自媒体类型
      */
    def getMediaInfo(cacheConfig: String, ids: List[String]): Map[String, String] = {
        val preIds = ids.map(mediaPrefix + _)
        getKeyValue(cacheConfig, preIds).map( kv => {
            kv._1.stripPrefix(mediaPrefix) -> kv._2
        })
    }

    /**
      * 获取redis以及mysql中的文章信息
      * | key | value |
      * ————————————————————————————————————
      * | id  | status,createTime,category |
     *
     * @param cacheConfig
      * @param ids
     *
     * @return 文章id | 文章状态,创建时间,垂直类目
      */
    def getItemInfo(cacheConfig: String, ids: List[String]): Map[String, String] = {
        val preIds = ids.map(itemPrefix + _)
        getKeyValue(cacheConfig, preIds).map( kv => {
            kv._1.stripPrefix(itemPrefix) -> kv._2
        })
    }

    def buildAppVersionDimension(appChannel: String, os: String) = {
        List(
            (ABNORMAL_VALUE, appChannel, os),
            (ABNORMAL_VALUE, appChannel, "allos"),
            (ABNORMAL_VALUE, "allchannel",  os),
            (ABNORMAL_VALUE, "allchannel", "allos")
        )
    }

    def buildAppChannelDimension(appVersion: String, os: String) = {
        List(
            (appVersion, ABNORMAL_VALUE, os),
            (appVersion, ABNORMAL_VALUE, "allos"),
            ("allappver", ABNORMAL_VALUE, os),
            ("allappver", ABNORMAL_VALUE, "allos")
        )
    }

    def buildOsDimension(appVersion: String, appChannel: String) = {
        List(
            (appVersion, appChannel, ABNORMAL_VALUE),
            (appVersion, "allchannel", ABNORMAL_VALUE),
            ("allappver", appChannel, ABNORMAL_VALUE),
            ("allappver", "allchannel", ABNORMAL_VALUE)
        )
    }

    // TODO 目前只使用redis, 如果使用reliable storage, 像mysql之类的, 需要考虑只取当天的key
    /**
     *  首先尝试从cache中获取 ouid or did 对应的当日首次出现的各维度的值
     *  + 如果不存在或者是其中某个值为-1, 则尝试使用当前批次的值进行覆盖, 并使用修改后值作为当前批次计算时的维度值
     *  + 直接使用作为当前批次计算时的维度值
     *
     *  最后,如果进行了覆盖, 则需要进行回写; 由于该缓存对应的维度只在当天生效,
     *  所以过期时间应该是 (当天结束 - 当前时间)
     *
     * @param cacheConfigPath
     * @param idAndDimension 用户或者是设备, 当前批次 (第一次出现的时间, appver, channel, os)
     *
     * @return
     *   当前批次应该使用的 id对应的dimension, 以及需要剔除该ID的dimension组合
     */
    def getOrSetFirstDimension(
        cacheConfigPath: String, idAndDimension: Map[String, (Long, String, String, String)], keyPrefix: String
    ): Map[String, (String, String, String, List[(String, String, String)])] = {
        var kdcCache: KdcCache = null
        try {
            kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(cacheConfigPath))

            val prefixedKeys = idAndDimension.keySet.map { id =>
                s"${keyPrefix}${id}"
            }
            val prefixedIdAndDimensionFromCache = kdcCache.multiGet(prefixedKeys).toMap

            val writeBackKeyValueMap: mutable.Map[String, (Long, String)] = mutable.Map.empty

            // 返回当前批次使用值 以及 回写相应的值
            val modifiedIdAndDimension =
                prefixedIdAndDimensionFromCache.map {
                    case keyValue @ (prefixedId, nullableDimension) =>
                        val id = prefixedId.stripPrefix(keyPrefix)
                        val requireFixDimension: ListBuffer[(String, String, String)] = new ListBuffer()

                        try {
                            Option(nullableDimension) match {
                                case Some(dimension) =>
                                    // 如果任意一个为异常值(-1), 则尝试使用当前值进行覆盖
                                    val appVersionInCache :: channelInCache :: osInCache :: Nil =
                                        nullableDimension.split(COMMA).toList

                                    val (firstTime, appVersion, channel, os) = idAndDimension(id)

                                    val modifiedAppVersion =
                                        if (appVersionInCache == ABNORMAL_VALUE && appVersion != ABNORMAL_VALUE) {
                                            // requireFixDimension ++= buildAppVersionDimension(channelInCache, osInCache)
                                            appVersion
                                        } else appVersionInCache

                                    val modifiedChannel =
                                        if (channelInCache == ABNORMAL_VALUE && channel != ABNORMAL_VALUE) {
                                            // requireFixDimension ++= buildAppChannelDimension(appVersionInCache, osInCache)
                                            channel
                                        } else channelInCache

                                    val modifiedOs =
                                        if (osInCache == ABNORMAL_VALUE && os != ABNORMAL_VALUE) {
                                            requireFixDimension ++= buildOsDimension(appVersionInCache, channelInCache)
                                            os
                                        } else osInCache

                                    if (requireFixDimension.nonEmpty) {
                                        writeBackKeyValueMap +=
                                                (s"${keyPrefix}${id}" -> (firstTime, List(modifiedAppVersion, modifiedChannel, modifiedOs).mkString(COMMA)))
                                    }

                                    id -> (modifiedAppVersion, modifiedChannel, modifiedOs, requireFixDimension.toList)
                                case None =>
                                    val (firstTime, appVersion, channel, os) = idAndDimension(id)

                                    // 将当前批次的值进行回写
                                    writeBackKeyValueMap += (s"${keyPrefix}${id}" -> (firstTime, List(appVersion, channel, os).mkString(COMMA)))

                                    id -> (appVersion, channel, os, Nil)
                            }
                        } catch {
                            case e: Exception =>
                                logger.error(s"Failed to get first dimension for ${id}", e)
                                val (_, appVersion, channel, os) = idAndDimension(id)
                                id -> (appVersion, channel, os, Nil)
                        }
                }

            if (logger.isDebugEnabled) {
                logger.debug("WriteBackKeyValueMap: " + writeBackKeyValueMap)
            }

            val writeBackKeyValue =
                writeBackKeyValueMap.map {
                    case (key, (firstTime, value)) =>
                        new Tuple3(key, value, getRemainingSecondsInToday(firstTime))
                }

            kdcCache.multiSet(writeBackKeyValue)

            modifiedIdAndDimension
        } catch {
            case ex: Exception =>
                logger.error("Failed to get or set first dimension", ex)
                Map.empty
        } finally {
            Option(kdcCache).foreach { _.close() }
        }
    }

    /**
     * 获取用户的首次活跃时间|安装时间, 如果没有找到, 则根据是否需要回写, 来将对应的时间作为首次活跃时间
     *
     * @param cacheConfig   KacCache的配置文件名
     * @param activeAt      当前活跃的时间以及渠道
     *
     * @return 用户 | 用户首次活跃或者安装时间,渠道
     */
    def getOrSetUserFirstActiveTime(
        cacheConfig: String, activeAt: Map[String, (Long, String)]
    ): Map[String, Long] = {
        var kdcCache: KdcCache = null
        try {
            kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(cacheConfig))
            val keyValueMap = kdcCache.multiGet(activeAt.keySet).toMap

            val withFirstActiveTimeModified =
                keyValueMap.map(kv => {
                    val key = kv._1
                    val value = kv._2
                    try {
                        val firstActiveTime = if (value == null) activeAt(key)._1 else value.substring(0, 13).toLong
                        key -> firstActiveTime
                    } catch {
                        case e: Exception =>
                            logger.error("get first time error.", e)
                            key -> activeAt(key)._1
                    }
                })

            val writeBackKeyValue = keyValueMap.filter {
                case (key, value) =>
                    value == null
            }.map {
                case (key, value) => {
                    val twoDayInSec = Duration(2, TimeUnit.DAYS).toSeconds.toInt
                    // 随机2 ~ 3天的保留时间
                    new Tuple3(key, s"${activeAt(key)._1.toString},${activeAt(key)._2}",
                        twoDayInSec + new Random().nextInt(86400))
                }
            }
            kdcCache.multiSet(writeBackKeyValue)

            withFirstActiveTimeModified
        } catch {
            case ex: Exception =>
                logger.error("Failed to get or set first active time", ex)
                Map.empty[String, Long]
        } finally {
            Option(kdcCache).foreach { _.close() }
        }
    }

    /**
      * 获取设备的首次活跃时间|安装时间, 如果没有找到, 则根据是否需要回写, 来将对应的时间作为首次活跃时间
      *
      * @param cacheConfig   KacCache的配置文件名
      * @param activeAt      当前活跃的时间
      *
      * @return 设备 | 设备首次活跃或者安装时间
      */
    def getOrSetFirstActiveTime(
        cacheConfig: String, activeAt: Map[String, Long]
    ): Map[String, Long] = {
        var kdcCache: KdcCache = null
        try {
            kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(cacheConfig))

            val keyValueMap = kdcCache.multiGet(activeAt.keySet).toMap

            val withFirstActiveTimeModified =
                keyValueMap.map {
                    case (key, value) =>
                        try {
                            val firstActiveTime = if (value == null) activeAt(key) else value.toLong
                            key -> firstActiveTime
                        } catch {
                            case e: Exception =>
                                logger.error(s"Failed to get or set first active time for ${key}", e)
                                key -> activeAt(key)
                        }

                }

            val writeBackKeyValue = keyValueMap.filter {
                case (key, value) => value == null
            }.map {
                case (key, value) => {
                    val twoDayInSec = Duration(2, TimeUnit.DAYS).toSeconds.toInt
                    new Tuple3(key, activeAt(key).toString, twoDayInSec + new Random().nextInt(86400))
                }
            }

            kdcCache.multiSet(writeBackKeyValue)

            withFirstActiveTimeModified
        } catch {
            case ex: Exception =>
                logger.error("Failed to get or set first active time", ex)
                Map.empty[String, Long]
        } finally {
            Option(kdcCache).foreach { _.close() }
        }
    }

    def getRemainingSecondsInToday(ts: Long) = {
        // 额外加的时间, 作为缓冲时间
        val oneDayInMillis = 1000 * 3600 * 24
        math.round((DateUtils.floor(ts, oneDayInMillis) + oneDayInMillis - ts) / 1000.0).toInt + 60 * 2
    }

    @Deprecated
    def getUserPairs(
        cacheConfigPath: String, userActiveAt: Map[String, Long], needWriteBack: Boolean
    ): Map[String, String] = {
        val kdcCache = KdcCache.builder(getClass.getClassLoader.getResourceAsStream(cacheConfigPath))

        val newUserKeyValueTry = CommonUtils.safeRelease(kdcCache)(kdcCache => {
            val keyValueMap = kdcCache.multiGet(userActiveAt.keySet)
            val newUserKeyValuePairs = keyValueMap.toMap
            val writeBackParis = newUserKeyValuePairs.filter(_._2 == null).map {
                case (key, value) =>
                    new Tuple3(key, userActiveAt.get(key).get.toString, Duration(2, TimeUnit.DAYS).toSeconds.toInt)
            }
            if (needWriteBack) {
                kdcCache.multiSet(writeBackParis)
            }
            newUserKeyValuePairs
        })()

        newUserKeyValueTry match {
            case Success(newUserKeyValuePairs) => newUserKeyValuePairs
            case Failure(e) =>
                logger.error("Fail to get new user ids.", e)
                Map.empty[String, String]
        }
    }

    def saveKPIRecord(
        batchTime: Time, kpiRecords: List[KPIRecord], mysqlSink: SingletonMysqlSink, tableName: String
    ) = {
        if (kpiRecords.nonEmpty) {
            try {
                val columns = kpiRecords.head.columns
                val metricNames = kpiRecords.head.metricNames

                mysqlSink.insertOrUpdate(
                    tableName, columns,
                    kpiRecords.map(_.rowValues).iterator, metricNames
                )
            } catch {
                case ex: Exception =>
                    val time = DateUtils.getYMDHMS.format(batchTime.milliseconds)
                    logger.error(s"Failed to save kpi records to mysql for batch: ${time}", ex)
            }
        }
    }

    def validLogtime(time: Long, timeThresholdOpt: Option[Long]) = {
        timeThresholdOpt.isEmpty || timeThresholdOpt.exists { timeThreshold =>
            time >= timeThreshold
        }
    }

    def alertByChannel(message: String, config: Map[String, String]) = {
        val notificationReceivers =
            config.get(RECEIVERS_KEY).getOrElse(DEFAULT_RECEIVERS)

        try {
            val channel :: receivers :: Nil = notificationReceivers.split(COLON).toList

            if (channel == NotificationChannelEnum.Phone.toString) {
                MessageAlert.sendToPhone(message, receivers)
            } else {
                MessageAlert.sendToWeixin(message, receivers)
            }
        } catch {
            case NonFatal(t) =>
                logger.error(s"Failed to send ${message} to ${notificationReceivers}", t)
        }
    }

}
// scalastyle:on
