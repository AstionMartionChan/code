package com.kingnetdc.watermelon.utils

import org.apache.hadoop.fs.{Path, FileStatus}
import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import scala.reflect.ClassTag
import scala.util.Try

/**
  * Created by zhouml on 19/05/2018.
  */
object SparkUtils extends Logging {

  /**
   * @param rdd  rdd to checkpoint
   * @param startTime 起始时间
   * @param currentTime 当前时间
   * @tparam T 类型
   *   每隔多长时间检出一次, 比如说开始时间 18:00:00, 每5分钟检出一次, 则18:05:00将会触发一次检出,
   *   于此同时需要确认检出时的分区数
   *
   *   恢复的时候, 需要指定从CP_PATH下面的哪个时间点开始恢复
   */
  def periodicRDDCheckpoint[T](rdd: RDD[T], startTime: Long, currentTime: Long): Unit = {
    val sparkConf = rdd.context.getConf

    require(
      sparkConf.contains(SPARK_RDD_CP_PATH),
      s"${SPARK_RDD_CP_PATH} is missing, Please set it in SparkConf"
    )

    val checkpointPath = sparkConf.get(SPARK_RDD_CP_PATH)
    val checkpointInterval = sparkConf.getLong(SPARK_RDD_CP_INTERVAL, DEFAULT_SPARK_RDD_CP_INTERVAL)
    val partitionNumber = sparkConf.getInt(SPARK_RDD_CP_PARTITION, DEFAULT_RDD_CP_PARTITION)

    val formattedTime = DateUtils.getYMDHM.format(currentTime)

    if (
        currentTime != startTime &&
        (currentTime - startTime) % checkpointInterval == 0 &&
        !rdd.isEmpty()
    ) {
      val start = System.currentTimeMillis()

      val currentSnapShotDir = s"${checkpointPath}/${formattedTime}"
      rdd.repartition(partitionNumber).saveAsObjectFile(currentSnapShotDir)
      val millis = (System.currentTimeMillis() - start)

      logger.info(
        s"Finish rdd checkpoint in batch {}, completed in {}",
        List(DateUtils.getYMDHMS.format(currentTime), millis.toString): _*
      )
    }
  }

  /**
   * @param sparkContext
   * @param path 指定目录 hdfs://localhost/xxx
   *
   * @return  List[FileStatus]
   *
   *  获取指定目录的子目录
   */
  def getSubDirs(sparkContext: SparkContext, path: String): List[FileStatus] = {
    val fsPath = new Path(path)
    val fs = fsPath.getFileSystem(sparkContext.hadoopConfiguration)
    if (fs.exists(fsPath)) {
      val fileStatus = fs.listStatus(fsPath).toList
      fileStatus.filter(_.isDirectory())
    } else {
      Nil
    }
  }

  def existsPath(sparkContext: SparkContext, path: String) = {
    val fsPath = new Path(path)
    val fs = fsPath.getFileSystem(sparkContext.hadoopConfiguration)
    fs.exists(fsPath)
  }

  def touchFile(sparkContext: SparkContext, path: String) = {
    val fsPath = new Path(path)
    val fs = fsPath.getFileSystem(sparkContext.hadoopConfiguration)
    fs.createNewFile(fsPath)
  }

  def mkdir(sparkContext: SparkContext, path: String) = {
    val fsPath = new Path(path)
    val fs = fsPath.getFileSystem(sparkContext.hadoopConfiguration)
    fs.mkdirs(fsPath)
  }


  /**
   * @param sparkContext
   * @param path hdfs://localhost/xxx
   * @tparam T
   *
   * @return
   *    从指定目录读取文件并生成RDD
   */
  def getFromHDFS[T : ClassTag](sparkContext: SparkContext, path: String): RDD[T] = {
    val fsPath = new Path(path)
    val fs = fsPath.getFileSystem(sparkContext.hadoopConfiguration)
    if (fs.exists(fsPath)) {
      sparkContext.objectFile[T](path)
    } else {
      sparkContext.emptyRDD
    }
  }

  /**
   * @param sparkContext
   * @param path RDD的根路径,
   * @param restoreFromOpt 某个时间点, 若不传, 则从最近的时间点恢复
   *
   *  + path: hdfs://localhost/xxx  restoreFrom: Some(201807311430)
   *    则直接从  hdfs://localhost/xxx/201807311430
   *
   *   + path: hdfs://localhost/xxx  restoreFrom: None
   *   找到hdfs://localhost/xxx, 下面最近的时间点恢复
   *   hdfs://localhost/xxx/latest_point
   *
   */
  def getInitialRDD[T: ClassTag](
    sparkContext: SparkContext, path: String, restoreFromOpt: Option[String]
  ): RDD[T] = {
    val actualPath =
      restoreFromOpt match {
        case Some(restoreFrom) => s"${path}/${restoreFrom}"
        case _ =>
          val subDirs = getSubDirs(sparkContext, path)
          if (subDirs.isEmpty) path
          else {
            val sortedFileStatusByTs =
              subDirs.filter { fileStatus =>
                 Try { DateUtils.getYMDHM.parse(fileStatus.getPath().getName) }.toOption.nonEmpty
              }.sortWith {
                case (prev, next) => prev.getPath().getName > next.getPath().getName
              }
            sortedFileStatusByTs.headOption.map(_.getPath.toString).getOrElse(path)
          }
      }

    logger.info(s"Try to get initial rdd from path: ${actualPath}")
    getFromHDFS[T](sparkContext, actualPath)
  }

}

