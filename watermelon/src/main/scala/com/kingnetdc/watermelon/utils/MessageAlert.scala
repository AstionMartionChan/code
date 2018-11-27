package com.kingnetdc.watermelon.utils

import java.net.URLEncoder
import com.kingnetdc.watermelon.utils.AppConstants.UTF8
import org.apache.commons.io.IOUtils
import org.apache.http.HttpStatus
import org.apache.http.client.entity.UrlEncodedFormEntity
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.{CloseableHttpClient, HttpClientBuilder}
import org.apache.http.message.BasicNameValuePair
import scala.collection.JavaConversions._
import scala.io

/**
 * Created by zhouml on 17/08/2018.
 */
object MessageAlert extends Logging {

    private def buildWeixinMessage(jsonStr: String, receivers: String): List[(String, Any)] = {
        List(
            "business_id" -> "1",
            "content" -> jsonStr,
            "options" -> "weixin",
            "receiver" -> receivers,
            "ts" -> (System.currentTimeMillis() / 1000)
        )
    }

    private val wechatPubKey = "KiLK#2T5@e"
    private val wechatURL = "http://sms.op.kingnet.com/wechat/send_msg.php"

    private val phonePubKey = "5ed3b071fff5e1ed0978e3734a8624ce"
    private val phoneDepartment = "data_center"
    private val phoneUrl = "http://sms.op.kingnet.com/voice_msg/api.php"

    private def postResponse(url: String, params: List[(String, String)]) = {
        val httpPost = new HttpPost(url)
        var client: CloseableHttpClient = null
        try {
            val nvps = params.map { case (key, value) => new BasicNameValuePair(key, value) }
            httpPost.setEntity(new UrlEncodedFormEntity(nvps, UTF8))
            client = HttpClientBuilder.create().build()
            val response = client.execute(httpPost)

            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                val is = response.getEntity().getContent()
                logger.info("Message alert response: " + io.Source.fromInputStream(is, UTF8).getLines().mkString)
            }
        } catch {
            case ex: Exception =>
                logger.error(String.format("Failed to get response from %s", url), ex);
        } finally {
            IOUtils.closeQuietly(client)
        }
    }

    /**
     *
     * @param jsonMessage 待发送的消息
     * @param emails 接受用户的邮箱  A@163.com,B@163.com
     */
    def sendToWeixin(jsonMessage: String, emails: String): Unit = {
        val messagePairs = buildWeixinMessage(jsonMessage, emails)

        val withValueEncoded =
            messagePairs.map {
                case (key, value) => (key, URLEncoder.encode(value.toString(), UTF8))
            }

        val sign = StringUtils.md5(
            withValueEncoded.map {
                case (key, value) => s"${key}=${value}"
            }.mkString("&") + wechatPubKey
        )

        val parameters = ("sign", sign) :: withValueEncoded

        val finalUrl =
            wechatURL + "?" + parameters.map {
                case (key, value) => s"${key}=${value}"
            }.mkString("&")

        postResponse(finalUrl, Nil)
    }

    /**
     * @param voiceMessage 需要语音播报的内容
     * @param phoneNumbers 接受用户的手机 186xxx,178xxx
     */
    def sendToPhone(voiceMessage: String, phoneNumbers: String): Unit = {
        val sign = StringUtils.md5(phonePubKey + phoneNumbers)

        val parameters =
            List(
                "phones" -> phoneNumbers,
                "content" -> voiceMessage,
                "depart" -> phoneDepartment,
                "sign" -> sign
            ).map {
                case (key, value) => s"${key}=${URLEncoder.encode(value.toString(), UTF8)}"
            }.mkString("&")

        val finalUrl = s"${phoneUrl}?${parameters}"
        postResponse(finalUrl, Nil)
    }

}
