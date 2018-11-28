package com.kingnetdc.model

import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import java.io.{InputStream, File, FileInputStream}
import java.util
import org.yaml.snakeyaml.Yaml
import scala.beans.BeanProperty

// https://www.programcreek.com/scala/org.yaml.snakeyaml.Yaml
class ApplicationConfig extends Serializable {

  @BeanProperty var metricsMySql: java.util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]()
  @BeanProperty var metricsInfluxDB: java.util.Map[String, AnyRef] = new util.HashMap[String, AnyRef]()
  @BeanProperty var kafka: java.util.Map[String, String] = new util.HashMap[String, String]()
  @BeanProperty var hbase: java.util.Map[String, String] = new util.HashMap[String, String]()

  override def toString: String = {
    s"metricsInfluxDB: ${metricsMySql}, metricsInfluxDB: ${metricsInfluxDB}, kafka: ${kafka}"
  }

  def getHBaseConfig: Map[String, String] = {
     Map(
       ZkConnect -> Option(hbase.get(ZkConnect)).map(_.toString).getOrElse("")
     )
  }

  def getMySqlConfig: Map[String, String] = {
    Map(
      MYSQL_URL -> Option(metricsMySql.get(MYSQL_URL)).map(_.toString).getOrElse(""),
      MYSQL_USER -> Option(metricsMySql.get(MYSQL_USER)).map(_.toString).getOrElse(""),
      MYSQL_PASSWORD -> Option(metricsMySql.get(MYSQL_PASSWORD)).map(_.toString).getOrElse("")
    )
  }

  def getInfluxDBConfig: Map[String, String] = {
    Map(
      INFLUXDB_HOST -> Option(metricsInfluxDB.get(INFLUXDB_HOST)).map(_.toString).getOrElse(""),
      INFLUXDB_USERNAME -> Option(metricsInfluxDB.get(INFLUXDB_USERNAME)).map(_.toString).getOrElse(""),
      INFLUXDB_PASSWORD -> Option(metricsInfluxDB.get(INFLUXDB_PASSWORD)).map(_.toString).getOrElse(""),
      INFLUXDB_DB -> Option(metricsInfluxDB.get(INFLUXDB_DB)).map(_.toString).getOrElse("")
    )
  }

  def getKafkaConfig: Map[String, String] = {
    Map(
      BootstrapServers -> Option(kafka.get(BootstrapServers)).map(_.toString).getOrElse(""),
      ZkConnect -> Option(kafka.get(ZkConnect)).map(_.toString).getOrElse("")
    )
  }

}

object ApplicationConfigParser {

  def loadFromResources(path: String): ApplicationConfig = {
    val yaml: Yaml = new Yaml()
    val applicationConfig: ApplicationConfig =
      yaml.loadAs(getClass.getClassLoader.getResourceAsStream(path), classOf[ApplicationConfig])
    applicationConfig
  }

  def load(inputStream: InputStream): ApplicationConfig = {
    val yaml: Yaml = new Yaml()
    val applicationConfig: ApplicationConfig =
      yaml.loadAs(inputStream, classOf[ApplicationConfig])
    applicationConfig
  }

  def load(path: String): ApplicationConfig = {
    val yaml: Yaml = new Yaml()
    val applicationConfig: ApplicationConfig =
      yaml.loadAs(new FileInputStream(new File(path)), classOf[ApplicationConfig])
    applicationConfig
  }

}
