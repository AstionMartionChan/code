package com.kingnetdc.model

import com.alibaba.fastjson.JSON
import com.google.common.annotations.VisibleForTesting
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.Logging
import org.apache.kafka.clients.consumer.ConsumerRecord
import scala.collection.JavaConversions._

/*
      1：资讯，2：视频

      有效浏览率：内容有效观看的人数 / 内容的总浏览人数    all_read/all_view
      投票率：对内容点赞的人数 / 内容的总浏览人数         all_likes/all_view
      评论率：对内容评论的人数 / 内容的总浏览人数         all_comment/all_view
      分享率：对内容分享的人数 / 内容的总浏览人数         all_share/all_view

      K1 - 本篇内容浏览率 / 最近30天平台平均有效观看率 K1 = [0.1, 2]
      K2 - 本篇内容投票率 / 最近30天平台平均内容投票率 K2 = [0.1, 2]
      K3 - 本篇内容评论率 / 最近30天平台平均内容评论率 K3 = [0.5, 2]
      K3 - 本篇内容分享率 / 最近30天平台平均内容分享率 K4 = [0.5, 2]

      + 分子或者分母若其中一个为零, 则取最小值
      + K值计算超过了以上范围就取范围最大或最小值, 即超过最大值去最大, 超过最小取最小

      // 收益X = revenuePercentageByType * scoreBasedOnOperation1 / (scoreBasedOnOperation1 + scoreBasedOnOperation2 + ...)
      // 同时段(eventTime), 同类型(itemType)
*/
class WBRevenueLog(
  val itemId: String,
  val itemType: Int,
  val eventTime: Long,

  val allComment: Long,
  val allLikes: Long,
  val allRead: Long,
  val allShare: Long,
  val allView: Long,
  val coin: Double,

  val avgCommentRatio: Double,
  val avgLikeRatio: Double,
  val avgReadRatio: Double,
  val avgShareRatio: Double,

  val contributionsScore: Long,
  val auditScore: Long
) extends Serializable {

  private def ratioBasedOnView(count: Long): Double = {
    if (allView == 0) 0D
    else count / allView.toDouble
  }

  private val readRatio: Double = ratioBasedOnView(allRead)
  private val likeRatio: Double = ratioBasedOnView(allLikes)
  private val commentRatio: Double = ratioBasedOnView(allComment)
  private val shareRatio: Double = ratioBasedOnView(allShare)

  private def KCalculation(numerator: Double, denominator: Double, lowerBound: Double, upperBound: Double) = {
    if (numerator == 0D || denominator == 0D) {
      lowerBound
    } else {
    val originalValue = numerator / denominator
      if (originalValue <= lowerBound) {
        lowerBound
      } else if (originalValue >= upperBound) {
        upperBound
      } else {
        originalValue
      }
    }
  }

  // K1 = [0.1, 2]
  def K1: Double = KCalculation(readRatio, avgReadRatio, 0.1, 2)
  def K2: Double = KCalculation(likeRatio, avgLikeRatio, 0.1, 2)
  def K3: Double = KCalculation(commentRatio, avgCommentRatio, 0.5, 2)
  def K4: Double = KCalculation(shareRatio, avgShareRatio, 0.5, 2)

  // 类型可分得的收益占比 -- Y
  def revenuePercentageByType: Double = {
    if (itemType == 1 || itemType == 2) {
      1900 * 0.15
    } else {
      0D
    }
  }

  // 基于操作和贡献力, 审核力的算分 -- A = (G1+G2+G3...) * (k1+k2+k3+k4)
  def scoreBasedOnOperation: Double = {
    (contributionsScore + auditScore) * List(K1, K2, K3, K4).sum
  }

}

class WBRevenue (
  val itemId: String,
  val itemType: Int,
  val eventTime: Long,
  val revenuePercentageByType: Double, // Y
  val scoreBasedOnOperation: Double, // A
  val coin: Double,
  val uniqueIdentifier: String
) extends Serializable {}

object WBRevenueLog extends Logging {

  @VisibleForTesting
  def parse(jsonStr: String): Option[WBRevenueLog] = {
    try {
      val jsonObject = JSON.parseObject(jsonStr)

      val itemId = jsonObject.getString("item_id")
      val itemType = jsonObject.getInteger("item_type")
      val eventTime = jsonObject.getLong("time") * 1000

      val allComment = jsonObject.getLong("all_comment")
      val allLikes = jsonObject.getLong("all_likes")
      val allRead = jsonObject.getLong("all_read")
      val allShare = jsonObject.getLong("all_share")
      val allView = jsonObject.getLong("all_view")
      val coin = jsonObject.getDouble("coin")

      val avgComment = jsonObject.getDouble("plat_avg_comment_rate")
      val avgLike = jsonObject.getDouble("plat_avg_like_rate")
      val avgRead = jsonObject.getDouble("plat_avg_read_rate")
      val avgShare = jsonObject.getDouble("plat_avg_share_rate")

      val contributionsOpt = Option(jsonObject.getJSONObject("contributions"))
      val contributionScore = contributionsOpt.map { contributions =>
        contributions.getInnerMap.values().map(_.toString.toLong).sum
      }.getOrElse(0L)
      val auditingOpt = Option(jsonObject.getJSONObject("verifys"))
      val auditScore = auditingOpt.map { auditing =>
        auditing.getInnerMap.values().map(_.toString.toLong).sum
      }.getOrElse(0L)

      val wbRevenueLog = new WBRevenueLog(
        itemId, itemType, eventTime,
        allComment, allLikes, allRead, allShare, allView, coin,
        avgComment, avgLike, avgRead, avgShare,
        contributionScore, auditScore
      )
      Some(wbRevenueLog)
    } catch {
      case ex: Exception =>
        logger.error(s"Failed to parse line: ${jsonStr}", ex)
        None
    }
  }

  def convert(consumerRecord: ConsumerRecord[String, String]): Option[WBRevenue] = {
    val jsonStr: String = consumerRecord.value()
    val uniqueIdentifier =
      List(consumerRecord.topic, consumerRecord.partition, consumerRecord.offset).mkString(DASH)
    parse(jsonStr).map { wbRevenueLog =>
      val wbRevenue = new WBRevenue(
        wbRevenueLog.itemId,
        wbRevenueLog.itemType,
        wbRevenueLog.eventTime,
        wbRevenueLog.revenuePercentageByType,
        wbRevenueLog.scoreBasedOnOperation,
        wbRevenueLog.coin,
        uniqueIdentifier
      )
      wbRevenue
    }
  }

}