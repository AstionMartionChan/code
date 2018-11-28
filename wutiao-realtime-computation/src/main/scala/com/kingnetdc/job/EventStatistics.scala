package com.kingnetdc.job

import com.kingnetdc.metrics.{Aggregator, MetricsBuffer}
import com.kingnetdc.model.EventTypeEnum._
import com.kingnetdc.model.{AggregatorMergeStrategy, DurationConfiguration, Event, KPIRecord}
import com.kingnetdc.sql.ComputationRule
import com.kingnetdc.sink.MySqlSink
import com.kingnetdc.watermelon.utils.AppConstants._
import com.kingnetdc.watermelon.utils.ConfigurationKeys._
import com.kingnetdc.utils.{Tuple2Ordering, Tuple2Partitioner}
import com.kingnetdc.watermelon.utils.{DateUtils, Logging, SparkUtils}
import org.apache.spark.{HashPartitioner, Partitioner}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.types.{LongType, StringType, StructField, StructType}
import org.apache.spark.sql.SparkSession
import org.apache.spark.streaming.dstream.{KingnetPairDStreamFunctions, DStream, MapWithStateDStream}
import it.unimi.dsi.fastutil.longs.{Long2ObjectOpenHashMap => ScalaMutableLongMap}
import it.unimi.dsi.fastutil.objects.{ReferenceArrayList => ScalaMutableList}
import org.apache.spark.streaming.{State, StateSpec, Time}
import org.apache.spark.sql._
import org.apache.spark.sql.functions._
import scala.collection.JavaConversions._
import com.kingnetdc.utils.StatisticsUtils._


// scalastyle:off
trait EventStatistics extends Serializable with Logging {

    protected val all = "all"

    protected val windowColumnName = "window"

    protected val computationRules: List[ComputationRule]

    protected val durationConfigurations: List[DurationConfiguration]

    lazy val durations: List[Long] = durationConfigurations.map(_.calculationDuration)

    protected val dimensions: List[String]

    protected val newEventSet: Set[String] = Set(
        OPEN_CLIENT, LOGIN, ACTIVE, ENTERFRONT, REGISTER
    ).map(_.toString)

    protected val activeEventSet: Set[String] = Set(
        COMMENT, FAVOUR, LIKE, SHARE, READ, CLICK, NOINTEREST, REPORT, ATTENTION,
        LEAVEREAD, PLAY, SEARCH
    ).map(_.toString) ++ newEventSet

    private[job] def getSchema(
        dimensions: List[String], computationRules: List[ComputationRule]
    ): StructType = {
        StructType(
            dimensions.map { dimensionName =>
                StructField(dimensionName, StringType, false)
            } :::
            List(
                StructField(windowColumnName, LongType, false)
            ) :::
            ComputationRule.convertToStructFields(computationRules)
        )
    }

    private[job] def getRow(
        event: Event, minimalDuration: Long, computationRules: List[ComputationRule]
    ): List[Row] = {
        event.dimensionValues.map { dimensionValue =>
            val singleRow: List[Any] =
                dimensionValue :::
                windowStartByDuration(event.time, minimalDuration) ::
                ComputationRule.convertToRow(
                    event.fieldValueMap, event.getFilterFieldValueMap,
                    computationRules
                )
            Row(singleRow: _*)
        }
    }

    def windowStartByDuration(time: Long, duration: Long) = DateUtils.floor(time, duration)

    def convertToDataFrame(
      sparkSession: SparkSession, eventRDD: RDD[Event],
      mininalDuration: Long, computationRules: List[ComputationRule]
    ) = {
        val eventSchema: StructType = getSchema(dimensions, computationRules)

        if (logger.isDebugEnabled) {
            logger.info("Event dataframe schema: " + eventSchema.simpleString)
        }

        sparkSession.createDataFrame(
            eventRDD.flatMap { event =>
                getRow(event, mininalDuration, computationRules)
            }, eventSchema
        )
    }

    protected[job] def  preAggregate(
        eventStream: DStream[Event], mininalDuration: Long
    ): DStream[(String, Aggregator)] = {
        eventStream.transform { eventRDD =>
            if (eventRDD.isEmpty) {
                eventRDD.sparkContext.emptyRDD
            } else {
                preAggregate(eventRDD, mininalDuration)
            }
        }
    }

    protected[job] def preAggregate(
        eventRDD: RDD[Event], mininalDuration: Long
    ): RDD[(String, Aggregator)] = {
        implicit val tupleOrdering = new Tuple2Ordering[String, Int]

        val sparkConf = eventRDD.sparkContext.getConf
        val sparkSession = SparkSession.builder().config(sparkConf).getOrCreate()

        import sparkSession.implicits._

        val eventDataFrame: DataFrame =
            if (logger.isDebugEnabled) {
                convertToDataFrame(sparkSession, eventRDD, mininalDuration, computationRules).persist()
            } else {
                convertToDataFrame(sparkSession, eventRDD, mininalDuration, computationRules)
            }

        if (logger.isDebugEnabled) {
            logger.debug("EventDataFrame DataFrame")
            eventDataFrame.printSchema()
            eventDataFrame.show()
        }

        val head :: tail = ComputationRule.getAggregateExpression(computationRules)

        val groupedDataFrame =
            eventDataFrame.groupBy(
                (dimensions ::: List(windowColumnName)).map(col): _*
            ).agg(head, tail: _*)

        if (logger.isDebugEnabled) {
            logger.debug("Grouped DataFrame")
            groupedDataFrame.printSchema()
            groupedDataFrame.show()
        }

        val partitioner: Partitioner = new Tuple2Partitioner(sparkConf.getInt("spark.tuple.partition", 100))

        groupedDataFrame.rdd.map { row =>
            val dimensionCompositeKey = dimensions.map { dimension =>
                row.getAs[String](dimension)
            }.mkString(COMMA)

            val window = row.getAs[Long](windowColumnName)
            val aggregator = new Aggregator(window).setBufferByMetrics(computationRules, row)
            ((dimensionCompositeKey, aggregator.window), aggregator)
        }.repartitionAndSortWithinPartitions(partitioner).mapPartitions { iter =>
            // repartitionAndSortWithinPartitions 确保同一dimension组合, 在分区内按先后顺序处理
            iter.map {
                case ((dim, _), aggregator) => (dim, aggregator)
            }
        }
    }

    def convertKeyValueRDDToAggregator(
        keyValueRDD: RDD[((String, String), (Int, scala.List[Byte]))]
    ): RDD[(String, Aggregator)] = {
        val sparkContext = keyValueRDD.sparkContext
        val partitionNum = sparkContext.getConf.getInt("spark.sql.shuffle.partitions", 50)

        if (!keyValueRDD.isEmpty()) {
            val aggregatedRDD =
                keyValueRDD.combineByKey(
                    createCombiner _,
                    mergeValue _,
                    mergeCombiner _,
                    partitionNum
                )

            aggregatedRDD.map {
                case ((key, strategy), bitmaps) =>
                    val dimensionAndWindow = key.split(COMMA).toList
                    val dimensionCompositeKey = dimensionAndWindow.init.mkString(COMMA)
                    val window = dimensionAndWindow.last.toLong

                    val aggregator =
                        new Aggregator(window)
                                .setBufferByMetrics(computationRules, bitmaps)
                                .setMergeStrategy(strategy)

                    (dimensionCompositeKey, aggregator)
            }
        } else {
            sparkContext.emptyRDD
        }
    }

    def initMetricsBufferByDuration(
        durationConfiguration: DurationConfiguration,
        dimensionValue: List[(String, String)],
        initializedAt: Long
    ) = {
        if (logger.isDebugEnabled()) {
            logger.info("Initializing time point: " + initializedAt)
        }

        val metricsBuffer =
            new MetricsBuffer(
                durationConfiguration.calculationDuration,
                durationConfiguration.flushDuration,
                durationConfiguration.windowDisplayMode
            ).setDimensionValue(dimensionValue)
            .setEventTime(initializedAt)
            .setFlushTime(initializedAt)

        metricsBuffer
    }

    protected def initState(dimensionValue: List[(String, String)], aggregator: Aggregator) = {
        val state = new ScalaMutableLongMap[MetricsBuffer]()

        durationConfigurations.foreach { durationConfiguration =>
            val metricsBuffer =
                initMetricsBufferByDuration(durationConfiguration, dimensionValue, aggregator.window)
            state.put(durationConfiguration.calculationDuration, metricsBuffer)
        }
        state
    }

    /**
     *  如果是增加值的话, 需要先确认是否超時, 然后进行合并, 确保不影响上一个批次的值
     *  如果是减少值的话, 则需要先对于原来的值进行更新, 然后再判断是否超时
     *
     * @return
     */
    def increaseOrDecrease(
        metricsBuffer: MetricsBuffer, thatAggregator: Aggregator,
        calculationDuration: Long, rules: List[ComputationRule]
    ): ScalaMutableList[KPIRecord] = {
        if (thatAggregator.getMergeStrategy == AggregatorMergeStrategy.Increase.toString) {
            val resultList = metricsBuffer.checkTimeoutAndFlush(thatAggregator.window, rules)
            metricsBuffer.merge(thatAggregator, rules)
            resultList
        } else {
            // 如果不是最小的统计区间, 才进行清理, 一般为5min
            val minimalDuration = durations.sorted.head

            if (calculationDuration != minimalDuration) {
                metricsBuffer.merge(thatAggregator, rules)
                metricsBuffer.checkTimeoutAndFlush(thatAggregator.window, rules)
            } else {
                new ScalaMutableList[KPIRecord]()
            }
        }
    }

    protected def updateAndDumpRecordsFunc(
        config: Map[String, String], time: Time,
        keyValuesStateIter: Iterator[(String, Seq[Aggregator], Option[ScalaMutableLongMap[MetricsBuffer]])]
    ): Iterator[(String, ScalaMutableLongMap[MetricsBuffer])] = {
        val updatedKeyStateAndResultBufferIter =
            keyValuesStateIter.map {
                case (dimensionCompositeKey, aggregators, stateOpt) =>
                    val resultBuffer = new ScalaMutableList[KPIRecord]()
                    val dimensionValue = dimensions.zip(dimensionCompositeKey.split(COMMA))

                    // 确保同一个维度, 按照时间先后顺序处理
                    val dimensionAggregators = aggregators.sortWith {
                        case (prev, next) => prev.window < next.window
                    }

                    if (dimensionAggregators.nonEmpty) {
                        val aggregator = dimensionAggregators.head

                        val stateMap =
                            if (stateOpt.nonEmpty) {
                                stateOpt.get
                            } else {
                                val state = new ScalaMutableLongMap[MetricsBuffer]()

                                durationConfigurations.foreach { durationConfiguration =>
                                    val metricsBuffer =
                                        initMetricsBufferByDuration(durationConfiguration, dimensionValue, aggregator.window)
                                    state.put(durationConfiguration.calculationDuration, metricsBuffer)
                                }
                                state
                            }

                        dimensionAggregators.foreach { aggregator =>
                            durationConfigurations.foreach {
                                case durationConfiguration @ DurationConfiguration(
                                    calculationDuration, flushDuration, windowDisplayMode
                                ) =>
                                    val metricsBuffer =
                                        Option(stateMap.get(calculationDuration)).getOrElse {
                                            if (logger.isDebugEnabled) {
                                                logger.debug(
                                                    s"DimensionValue ${dimensionValue} is empty in duration ${calculationDuration}, so initialize a new one"
                                                )
                                            }

                                            val buffer = initMetricsBufferByDuration(
                                                durationConfiguration, dimensionValue, aggregator.window
                                            )
                                            stateMap.put(calculationDuration, buffer)
                                            buffer
                                        }

                                    if (logger.isDebugEnabled()) {
                                        val formattedDate = DateUtils.getYMDHMS.format(aggregator.window)
                                        logger.debug(
                                            s"Aggregator in duration ${calculationDuration}: ${formattedDate}:${metricsBuffer.getDimensionValue}"
                                        )
                                    }

                                    resultBuffer.addAll(
                                        increaseOrDecrease(
                                            metricsBuffer, aggregator,
                                            calculationDuration, computationRules
                                        )
                                    )
                            }
                        }

                        (Some(dimensionCompositeKey -> stateMap), resultBuffer)
                    } else {
                        // 当前批次没有值, 则对于已有的历史状态进行更改
                        val dimensionStateOpt =
                            if (stateOpt.nonEmpty) {
                                val durationMetricsBuffer = stateOpt.get

                                val modifiedMetricsBuffer = {
                                    val state = new ScalaMutableLongMap[MetricsBuffer]()

                                    durationMetricsBuffer.foreach {
                                        case (duration, metricsBuffer) =>
                                            resultBuffer.addAll(
                                                metricsBuffer.checkTimeoutAndFlush(time.milliseconds, computationRules)
                                            )

                                            if (metricsBuffer.nonEmpty) {
                                                state.put(duration, metricsBuffer)
                                            } else {
                                                if (logger.isDebugEnabled) {
                                                    logger.debug(s"DimensionValue ${dimensionValue} is empty in duration ${duration}, so kick out")
                                                }
                                            }
                                    }
                                    state
                                }

                                if (modifiedMetricsBuffer.isEmpty) {
                                    if (logger.isDebugEnabled) {
                                        logger.debug(s"DimensionValue ${dimensionValue} is empty in all duration, so remove from the state")
                                    }
                                    None
                                } else {
                                    Some(dimensionCompositeKey -> modifiedMetricsBuffer)
                                }
                            } else {
                                None
                            }
                        (dimensionStateOpt, resultBuffer)
                    }
            }

        updatedKeyStateAndResultBufferIter.flatMap {
            case (state, resultBuffer) =>
                MySqlSink.saveKPIRecord(resultBuffer.toList, config)
                state
        }
    }

    /**
     * @param batchTime
     * @param dimensionCompositeKey 当前key
     * @param dimensionAggregatorOpt 当前key对应的值
     * @param dimensionMetricsState 当前key在内存中的状态
     *
     * @return
     *
     *   如果事件时间已到, 则将该duration从key的状态中剔除掉
     *
     */
    @Deprecated
    private[job] def mappingFunction(
        batchTime: Time, dimensionCompositeKey: String,
        dimensionAggregatorOpt: Option[Aggregator],
        dimensionMetricsState: State[ScalaMutableLongMap[MetricsBuffer]]
    ): Option[ScalaMutableList[KPIRecord]] = {
        val buffer = new ScalaMutableList[KPIRecord]()
        val stateMap =
            if (dimensionMetricsState.exists()) {
                dimensionMetricsState.get()
            } else {
                val state = new ScalaMutableLongMap[MetricsBuffer]()
                durationConfigurations.foreach {
                    case DurationConfiguration(calculationDuration, flushDuration, windowDisplayMode) =>
                        val initializedAt =
                            dimensionAggregatorOpt.map(_.window).getOrElse(batchTime.milliseconds)

                        if (logger.isDebugEnabled()) {
                            logger.info("Initializing time point: " + initializedAt)
                        }

                        val dimensionValue = dimensions.zip(dimensionCompositeKey.split(COMMA))
                        val metricsBuffer =
                            new MetricsBuffer(calculationDuration, flushDuration, windowDisplayMode)
                                    .setDimensionValue(dimensionValue)
                                    .setEventTime(initializedAt)
                                    .setFlushTime(initializedAt)
                        state.put(calculationDuration, metricsBuffer)
                }
                state
            }

        dimensionAggregatorOpt match {
            case Some(aggregatorInDuration) =>
                durations.foreach { calculationDuration =>
                    Option(stateMap.get(calculationDuration)).foreach { metricsBuffer =>
                        if (logger.isDebugEnabled()) {
                            val formattedDate = DateUtils.getYMDHMS.format(aggregatorInDuration.window)
                            logger.debug(
                                s"Aggreator in duration ${calculationDuration}: ${formattedDate}:${metricsBuffer.getDimensionValue}"
                            )
                        }

                        metricsBuffer.increaseOrDecrease(aggregatorInDuration, computationRules)
                        /*
                        buffer.addAll(
                            metricsBuffer.checkTimeoutAndFlush(
                                aggregatorInDuration.window, computationRules
                            )
                        )

                        metricsBuffer.merge(aggregatorInDuration, computationRules)
                        */
                    }
                }
                dimensionMetricsState.update(stateMap)
            case _ =>
        }

        Some(buffer)
    }

    @Deprecated
    type EventMapWithStateStream =
        MapWithStateDStream[String, Aggregator, ScalaMutableLongMap[MetricsBuffer], ScalaMutableList[KPIRecord]]

    protected def updateWithAggregator(
        aggregatorStream: DStream[(String, Aggregator)], config: Map[String, String]
    ): DStream[(String, ScalaMutableLongMap[MetricsBuffer])] = {
        val sparkContext = aggregatorStream.context.sparkContext
        val sparkConf = sparkContext.getConf

        require(
            sparkConf.contains(SPARK_RDD_CP_PATH),
            s"${SPARK_RDD_CP_PATH} is missing, Please set it in SparkConf"
        )

        require(computationRules.nonEmpty, "Computation rules should not be empty")
        require(durationConfigurations.nonEmpty, "Duration configuration should not be empty")

        val initialRDD =
            SparkUtils.getInitialRDD[(String, ScalaMutableLongMap[MetricsBuffer])](
                sparkContext, sparkConf.get(SPARK_RDD_CP_PATH),
                sparkConf.getOption(SPARK_RDD_CP_RESTORE_FROM)
            )

        val partitionNum = sparkConf.getInt("spark.sql.shuffle.partitions", 50)

        new KingnetPairDStreamFunctions[String, Aggregator](aggregatorStream).updateStateByKey(
            updateAndDumpRecordsFunc(config, _, _),
            new HashPartitioner(partitionNum),
            true, initialRDD
        )
    }

    @Deprecated
    protected def mapWithAggregator(
        aggregatorStream: DStream[(String, Aggregator)]
    ): EventMapWithStateStream = {
        val sparkContext = aggregatorStream.context.sparkContext
        val sparkConf = sparkContext.getConf

        require(
            sparkConf.contains(SPARK_RDD_CP_PATH),
            s"${SPARK_RDD_CP_PATH} is missing, Please set it in SparkConf"
        )

        require(computationRules.nonEmpty, "Computation rules should not be empty")
        require(durationConfigurations.nonEmpty, "Duration configuration should not be empty")

        val rddCPPath = sparkConf.get(SPARK_RDD_CP_PATH)

        val initialRDD =
            SparkUtils.getInitialRDD[(String, ScalaMutableLongMap[MetricsBuffer])](
                sparkContext,
                rddCPPath,
                sparkConf.getOption(SPARK_RDD_CP_RESTORE_FROM)
            )

        aggregatorStream.mapWithState(
            StateSpec.function(mappingFunction _).initialState(initialRDD)
        )
    }

    @Deprecated
    protected def getMapWithStateStream(eventStream: DStream[Event]): EventMapWithStateStream = {
        val minimalDuration = durations.sorted.head
        mapWithAggregator(preAggregate(eventStream, minimalDuration))
    }

    protected def getUpdateWithStateStream(
        eventStream: DStream[Event], mysqlConfig: Map[String, String]
    ): DStream[(String, ScalaMutableLongMap[MetricsBuffer])] = {
        val minimalDuration = durations.sorted.head
        updateWithAggregator(
            preAggregate(eventStream, minimalDuration), mysqlConfig
        )
    }

}
// scalastyle:on