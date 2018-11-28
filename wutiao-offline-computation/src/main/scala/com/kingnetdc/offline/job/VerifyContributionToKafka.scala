package com.kingnetdc.offline.job

import com.alibaba.fastjson.JSONObject
import com.kingnetdc.watermelon.output.KafkaSink
import com.kingnetdc.watermelon.utils.AppConstants.YMD
import com.kingnetdc.watermelon.utils.SparkUtils.touchFile
import com.kingnetdc.watermelon.utils.{ConfigUtils, Logging}
import org.apache.kafka.clients.producer.{Callback, ProducerRecord, RecordMetadata}
import org.apache.kafka.common.serialization.StringSerializer
import org.apache.spark.SparkContext
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.sql.SparkSession
import org.joda.time.DateTime

/**
  * Created by yangjun on 06/06/2018.
  * 用户30天贡献力、审核力更新到kafka
  */
object VerifyContributionToKafka extends Serializable with Logging {

	def getVerifyContributionSql() ={
		val current = new DateTime()
		var sql =
			s"""
				select uid,
					if(contributionpoin=0,500,contributionpoin) as contribution,
			   		if(verify_flag='1',verifypoin,500) as verify,
					registe_time as register_time,
			   		if(inviter is null , '-1', inviter) as inviterId,
			   		inviter_time as inviter_time,
					last_active_time as act_time
				from wutiao.adl_wutiao_contribute_verify_realtime
				where ds='${current.plusDays(-1).toString(YMD)}' and uid is not null and uid <> '-1' and uid <> '0'
			 """
		sql =
			s"""
			  select t1.uid,t2.contribution,t1.verify,t1.register_time,t1.inviterid,t1.inviter_time,t1.act_time
			  from
			  (select uid,
			  	if(verify_flag='1',verifypoin,500) as verify,
			  	registe_time as register_time,
			  	if(inviter is null , '-1', inviter) as inviterId,
			  	inviter_time as inviter_time,
			  	last_active_time as act_time
			  from wutiao.adl_wutiao_contribute_verify_realtime
			  where ds='${current.plusDays(-1).toString(YMD)}' and uid is not null and uid <> '-1' and uid <> '0') t1
			  left join
			  (select uid,sum(day_contributionpoint) as contribution from wutiao.adl_wutiao_contribute_verify_realtime where ds>='${current.plusDays(-7).toString(YMD)}' and ds<='${current.plusDays(-1).toString(YMD)}' and uid is not null and uid <> '-1' and uid <> '0' group by uid) t2
			  on t1.uid=t2.uid
			"""
		sql
	}

	def main(args: Array[String]): Unit = {

		if (args.length < 1) {
			throw new IllegalArgumentException("configuration path is missing")
		}

		val config = ConfigUtils.loadFromFileAsMap(args(0))
		val bootstrapServers = config("bootstrap.servers")
		val topic = config("topic")
		val appName = config("app.name")
		val successMarkPath = config("success.mark.path")

		val sparkSession = SparkSession.builder
			.enableHiveSupport()
			.appName(appName)
			.config("hive.exec.dynamic.partition.mode", "nonstrict")
			.config("hive.exec.stagingdir","/tmp/staging/.hive-staging")
			.config("hive.txn.manager","org.apache.hadoop.hive.ql.lockmgr.DbTxnManager")
			.config("hive.support.concurrency","true")
			.config("hive.enforce.bucketing","true")
			.getOrCreate()
		try{
			val userInfo = sparkSession.sql(getVerifyContributionSql())
			val data = userInfo.rdd.map(row => {
				logger.info(s"row=${row}")
				val json = new JSONObject()
				json.put("uid", row(0))
				json.put("contribution", row(1))
				if(row(2) != null) {
					json.put("verify", row(2))
				}
				if(row(3) != null) {
					json.put("register_time", row(3))
				}
				if(row(4) != null) {
					json.put("inviterId", row(4))
					json.put("inviter_time", row(5))
				}
				if(row(6) != null) {
					json.put("act_time", row(6))
				}
				json.toJSONString
			})

			val producerBroadCast: Broadcast[KafkaSink[String, String]] =
				sparkSession.sparkContext.broadcast(
					KafkaSink.create[String, String](getKafkaParams(bootstrapServers))
				)
			val kafkaSink = producerBroadCast.value
			data.foreach(row => {
				if(row != null) {
					val record = new ProducerRecord[String, String](topic, row)
					kafkaSink.send(record, new Callback {
						override def onCompletion(metadata: RecordMetadata, exception: Exception): Unit = {
							Option(exception).foreach { ex =>
								logger.error(s"Failed to send verifyContribution :${row} to ${topic}.", ex)
							}
						}
					})
				}
			})

			touchSuccessMark(sparkSession.sparkContext, successMarkPath, new DateTime())
		} catch {
			case e: Exception => e.printStackTrace()
		}
	}

	def touchSuccessMark(sparkContext: SparkContext, successMarkPath: String, time: DateTime) = {
		val timeFormat = "yyyy-MM-dd"
		val path = s"${successMarkPath}/${time.toString(timeFormat)}_${sparkContext.appName}"
		touchFile(sparkContext, path)
	}
	private def getKafkaParams(bootstrapServers: String): Map[String, Object] = {
		Map(
			"bootstrap.servers" -> bootstrapServers,
			"key.serializer" -> classOf[StringSerializer],
			"value.serializer" -> classOf[StringSerializer],
			"enable.auto.commit" -> (false: java.lang.Boolean)
		)
	}
}
