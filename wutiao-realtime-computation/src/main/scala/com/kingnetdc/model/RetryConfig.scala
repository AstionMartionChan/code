package com.kingnetdc.model

/**
 * Created by zhouml on 20/08/2018.
 */
class RetryConfig(
    var retryIntervalMs: Int,
    var maxRetry: Int,
    var notificationReceivers: String
) extends Serializable {}

object RetryConfig {

    // weixin:receivers || phone:receivers
    private val DEFAULT_RECEIVERS = "weixin:zhouml@kingnet.com,zhoujiongyu@kingnet.com"

    def parse(config: Map[String, String]): RetryConfig = {
        // 1s
        val retryIntervalMs = config.get("retry.interval.ms").map(_.toInt).getOrElse(1000)
        // 3
        val maxRetry = config.get("max.retry").map(_.toInt).getOrElse(3)
        val notificationReceivers = config.get("notification.receivers").getOrElse(DEFAULT_RECEIVERS)
        new RetryConfig(retryIntervalMs, maxRetry, notificationReceivers)
    }

}
