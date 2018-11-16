package com.cfy

import com.jayway.jsonpath.JsonPath
import scala.collection.JavaConverters._

object JsonPathTest {


  def main(args: Array[String]): Unit = {
    val json = "{\"relation\":[{\"jsonPath\":\"name\",\"hiveField\":\"name\",\"hiveType\":\"string\"},{\"jsonPath\":\"age\",\"hiveField\":\"age\",\"hiveType\":\"int\"},{\"jsonPath\":\"sex\",\"hiveField\":\"sex\",\"hiveType\":\"int\"},{\"jsonPath\":\"properties.price\",\"hiveField\":\"price\",\"hiveType\":\"double\"}]}"
//    val list = JsonPath.parse(json).read[java.util.List[String]]("$.relation[*].hiveField")
//    val list2 = list.asScala
//    list2.foreach(println(_))
    val value = "{\"name\": \"cfy\", \"age\": 25, \"sex\": \"男\", \"properties\":{\"price\": 8888888888.88}}"

    val jsonPathList = JsonPath.parse(json).read[java.util.List[String]]("$.relation[*].jsonPath").asScala.toList
    val result = jsonPathList.map(jsonPath => {
      JsonPath.parse(value).read[Any]("$." + jsonPath)
    })

    result.foreach(println(_))
  }

}
