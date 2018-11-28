package com.kingnetdc.utils

import com.kingnetdc.model.EnvironmentEnum
import com.kingnetdc.watermelon.utils.{StringUtils, Logging}
import org.rogach.scallop.Scallop

// see also http://blog.rogach.org/2012/04/better-cli-option-parsing-in-scala.html
abstract class AbstractOptions(className: String, args: Seq[String]) extends Logging {

  def build(className: String, args: Seq[String]) = {
    val opts = Scallop(args)
      .banner(s"""Usage: ${className} [OPTION]
                 |Options:
                 |""".stripMargin)
      .opt[String](
      name="env", required = true,
      validate = (env: String) => EnvironmentEnum.values.find(_.toString == env).nonEmpty,
      descr = "env: dev/test/prod"
    ).opt[String](
      name="topic", required = true,
      validate = (topic: String) => StringUtils.nonEmpty(topic),
      descr = "kafka topic name"
    ).opt[String](
      name="output", required = true,
      validate = (output: String) => StringUtils.nonEmpty(output),
      descr = "output identifier, table name or influxdb measurement"
    ).opt[String](
      name="group", required = true,
      validate = (consumerGroup: String) => StringUtils.nonEmpty(consumerGroup),
      descr = "kafka topic consumer group"
    )
    opts
  }

  protected val options = build(className, args)

  lazy val env = options[String]("env")
  lazy val topic = options[String]("topic")
  lazy val output = options[String]("output")
  lazy val group = options[String]("group")

  def verify {
    try {
      options.verify
    } catch {
      case e: Exception =>
        logger.error(e.getMessage)
        options.printHelp
        System.exit(-1)
    }
  }

}
