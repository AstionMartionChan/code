package com.kingnetdc.offline.utils

import java.io.IOException

import com.fasterxml.jackson.annotation.JsonInclude.Include
import com.fasterxml.jackson.core.JsonParser.Feature
import com.fasterxml.jackson.core.{JsonParseException, JsonProcessingException}
import com.fasterxml.jackson.databind.{JsonMappingException, ObjectMapper}
import com.fasterxml.jackson.module.scala.DefaultScalaModule

object JsonUtils {

  private val defaultMapper = initDefaultObjectMapper

  def initDefaultObjectMapper: ObjectMapper = {
    val mapper = new ObjectMapper()
    mapper.registerModule(DefaultScalaModule)
    // exclude null fields when rendering json
    mapper.setSerializationInclusion(Include.NON_NULL)
    mapper.configure(Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true)
    mapper
  }

  @throws(classOf[IOException])
  @throws(classOf[JsonParseException])
  @throws(classOf[JsonMappingException])
  def parse[T](jsonStr: String, valueType: Class[T]): T = {
    defaultMapper.readValue(jsonStr, valueType)
  }

  @throws(classOf[IOException])
  @throws(classOf[JsonParseException])
  @throws(classOf[JsonMappingException])
  def parse[T](objectMapper: ObjectMapper, jsonStr: String, valueType: Class[T]): T = {
    objectMapper.readValue(jsonStr, valueType)
  }

  @throws(classOf[JsonProcessingException])
  def render(any: Any): String = {
    render(any, defaultMapper)
  }

  @throws(classOf[JsonProcessingException])
  def render(any: Any, objectMapper: ObjectMapper): String = {
    defaultMapper.writeValueAsString(any)
  }

  def getJsonNode(jsonStr: String) = {
    defaultMapper.readTree(jsonStr)
  }

}
