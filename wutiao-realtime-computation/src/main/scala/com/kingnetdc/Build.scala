package com.kingnetdc

import scala.io.Source
import scala.xml.{TopScope, Elem, XML, Node}

/**
  * + set some dependency as provided when package through dynamic xml modification
  * + mvn clean package -f xxx.xml
  */
object Build  {

  // user.dir -> The current working directory means the root folder of your current Java project
  private val pom = s"${System.getProperty("user.dir")}/pom.xml"
  private val outputPom = s"${System.getProperty("user.dir")}/build.xml"

  private val artifactIds = List(
    "spark-core_${scala.lib.version}",
    "spark-sql_${scala.lib.version}",
    "spark-streaming_${scala.lib.version}",
    "hbase-client",
    "hbase-common",
    "hbase-server"
  )
  private val appendedNode = <scope>provided</scope>

  // remove namespace on node
  private def clearScope(node: Node): Node = {
    node match {
      case elem: Elem => elem.copy(scope = TopScope, child = elem.child.map(clearScope))
      case other => other
    }
  }

  private def prepareXML(path: String) = {
    val xml = XML.load(path)
    val newPom =
      xml match {
        case e @ Elem(_, _, _, scope, nodes @ _*) => {
          val changedSubNodes =
            nodes.map { node =>
              if (node.label == "dependencies") {
                node match {
                  case <dependencies>{ dependencies @ _* }</dependencies> => {
                    val changedChild =
                      dependencies.flatMap { dependencyNode =>
                        val artifactId = (dependencyNode \ "artifactId").text
                        dependencyNode match {
                          case elem: Elem =>
                            val toBeAdded =
                              if (artifactIds.contains(artifactId)) {
                                elem.child ++ appendedNode
                              } else {
                                elem.child
                              }
                            Some(elem.copy(child = toBeAdded))
                          case other => Some(other)
                        }
                      }
                    <dependencies>{ changedChild }</dependencies>
                  }
                }
              } else {
                node
              }
            }
          e.copy(child = changedSubNodes.map(clearScope))
        }
        case _ => xml
      }
    newPom
  }

  // scalastyle:off
  def runMvnPackage(pomPath: String) = {
    val cmds = List("mvn", "clean", "-DskipTests", "package", "-f", pomPath)
    val processBuilder = new ProcessBuilder(cmds.toArray: _*).redirectErrorStream(true)

    var process: Process = null
    try {
      process = processBuilder.start()
      val bufferedSource = Source.fromInputStream(process.getInputStream())
      bufferedSource.getLines().foreach { line =>
        println(line)
      }
    } catch {
      case ex: Exception =>
        throw ex
    } finally {
      if (process != null) {
        process.destroyForcibly()
      }
    }
  }
  // scalastyle:on println

  def main(args: Array[String]) = {
    XML.save(outputPom, prepareXML(pom))
    runMvnPackage(outputPom)
  }

}