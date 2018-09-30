package com.cfy.utils

import com.alibaba.fastjson.{JSON, JSONObject}
import com.cfy.FieldMapper
import org.apache.kafka.clients.consumer.ConsumerRecord
import com.cfy.constants.ConfigurationKey._

object JSONUtil {

  var fieldMapper = List()[FieldMapper]


  def parseJSON(record: ConsumerRecord[String, String], nestedField: String) = {
    val topic = record.topic()
    val jsonObject = JSON.parseObject(record.value().toString)
    nestedSearch(jsonObject, new JSONObject(), nestedField, topic)
  }


  def nestedSearch(source: JSONObject, target: JSONObject, nestedField: String, parentField: String): JSONObject = {
    source.entrySet().forEach(e => {
      if (nestedField == e.getKey){
        nestedSearch(source.getJSONObject(e.getKey), target, nestedField, parentField + UNDERLINE + e.getKey)
      } else {
        val tempName = parentField + UNDERLINE + e.getKey
        target.put(tempName, e.getValue)
        fieldMapper.:+(new FieldMapper(tempName.substring(0, tempName.indexOf(UNDERLINE)), tempName, e.getKey))
      }
    })
    target
  }

}
