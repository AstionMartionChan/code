package com.cfy.config

import java.util.Properties

object ConfigurationManager {

  val prop = new Properties()
  prop.load(ConfigurationManager.getClass.getClassLoader.getResourceAsStream("my.properties"))


  def getString(key: String) = { Option(prop.getProperty(key)) }

  def getInt(key: String) = { Option(prop.get(key).toString.toInt) }


}
