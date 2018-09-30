package com.cfy.config

import java.util.Properties

object ConfigurationManager {

  val prop = new Properties()
  prop.load(ConfigurationManager.getClass.getClassLoader.getResourceAsStream("my.properties"))


  def getString(key: String) = {
    prop.getProperty(key)
  }

  def getInt(key: String) = {

    prop.get(key).toString.toInt

  }


}
