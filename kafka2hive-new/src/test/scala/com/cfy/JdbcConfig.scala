package com.cfy

class JdbcConfig {
  private val url: String = "jdbc:mysql://172.16.32.8:3306/leo?useSSL=false";
  private val user: String = "datac";
  private val pwd: String = "E9QSVyj26gw/Q";

  def getUrl() = {url}

  def getUser() = {user}

  def getPwd() = {pwd}
}
