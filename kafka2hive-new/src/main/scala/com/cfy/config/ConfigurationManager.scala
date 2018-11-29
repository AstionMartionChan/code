package com.cfy.config

import java.io.FileInputStream
import java.util.Properties

object ConfigurationManager {

  def load(path: String) = {
    val prop = new Properties()
    prop.load(new FileInputStream(path))
    prop
  }

//  def getString(key: String) = { Option(prop.getProperty(key)) }
//
//  def getInt(key: String) = { Option(prop.get(key).toString.toInt) }


}
