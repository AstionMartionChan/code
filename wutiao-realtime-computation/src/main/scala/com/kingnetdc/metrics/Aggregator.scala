package com.kingnetdc.metrics

import com.kingnetdc.model.AggregatorMergeStrategy
import com.kingnetdc.sql.ComputationRule
import it.unimi.dsi.fastutil.objects.{ObjectArrayList => ScalaMutableStringList}
import it.unimi.dsi.fastutil.objects.{Object2ReferenceOpenHashMap => ScalaMutableHashMap}
import org.roaringbitmap.{RoaringBitmap => BitMap}
import org.apache.spark.sql.Row

// scalastyle:off
class Aggregator(val window: Long) extends Serializable {

    def bitmapOf(int: Int*) = {
        val bitmap = new BitMap()
        bitmap.add(int: _*)
        bitmap
    }

    private var mergeStrategy: String = AggregatorMergeStrategy.Increase.toString

    def setMergeStrategy(mergeStrategy: String) = {
        this.mergeStrategy = mergeStrategy
        this
    }

    def getMergeStrategy = mergeStrategy

    private val bufferByMetrics: ScalaMutableHashMap[String, Any] = new ScalaMutableHashMap()

    private val fieldAliasDurationRegex = """(\w+)_(\d+)""".r

    private def getFieldValue(fieldAlias: String, computationRule: ComputationRule) = {
        if (computationRule.inDeduplicationCategory) {
            Option(bufferByMetrics.get(fieldAlias)) match {
                case Some(bitMap: BitMap) => bitMap.getLongCardinality()
                case _ => 0L
            }
        } else if (computationRule.inCountingCategory) {
            Option(bufferByMetrics.get(fieldAlias)) match {
                case Some(count: Long) => count
                case _ => 0L
            }
        } else if (computationRule.inSumCategory) {
            Option(bufferByMetrics.get(fieldAlias)) match {
                case Some(number: Any) => number
                case _ => 0L
            }
        } else {
            throw new IllegalArgumentException(s"Unknown rule: ${computationRule}")
        }
    }

    def evaluate(
        calculationDuration: Long, computationRules: List[ComputationRule]
    ): ScalaMutableStringList[(String, Any)] = {
        val fieldValuePairs = new ScalaMutableStringList[(String, Any)]()

        computationRules.foreach { computationRule =>
            val fieldAlias = computationRule.aggregationFieldAlias

            fieldAlias match {
                case fieldAliasDurationRegex(alias, duration) =>
                    if (calculationDuration.toString == duration) {
                        fieldValuePairs.add(alias -> getFieldValue(fieldAlias, computationRule))
                    }
                case other =>
                    fieldValuePairs.add(fieldAlias -> getFieldValue(other, computationRule))
            }
        }

        new ScalaMutableStringList(fieldValuePairs)
    }

    def setBufferByMetrics(computationRules: List[ComputationRule], bitMaps: List[BitMap]): this.type = {
        val ruleSize = computationRules.size
        val bitMapSize = bitMaps.size
        require(ruleSize == bitMapSize, "Rule size should be equal to bit maps")

        (computationRules.map(_.aggregationFieldAlias) zip bitMaps).map {
            case (alias, bitmap) => bufferByMetrics.put(alias, bitmap)
        }
        this
    }

    def setBufferByMetrics(computationRules: List[ComputationRule], row: Row): this.type = {
        computationRules.foreach { computationRule =>
            val fieldAlias = computationRule.aggregationFieldAlias

            if (computationRule.inDeduplicationCategory) {
                val wrappedArray = row.getAs[Seq[Long]](fieldAlias)
                bufferByMetrics.put(fieldAlias, bitmapOf(wrappedArray.map(_.toInt): _*))
            } else if (computationRule.inCountingCategory) {
                val count = row.getAs[Long](fieldAlias)
                bufferByMetrics.put(fieldAlias, count)
            } else if (computationRule.inSumCategory) {
                val sum = row.getAs[Any](fieldAlias)
                bufferByMetrics.put(fieldAlias, sum)
            } else {
                throw new IllegalArgumentException(s"Unknown rule: ${computationRule}")
            }
        }
        this
    }

    def getBufferByMetrics = bufferByMetrics

    def mergeByMetrics(thatAggregator: Aggregator, computationRules: List[ComputationRule]): Unit = {
        computationRules.foreach { computationRule =>
            val fieldAlias = computationRule.aggregationFieldAlias

            if (
                computationRule.inDeduplicationCategory &&
                thatAggregator.mergeStrategy == AggregatorMergeStrategy.Increase.toString
            ) {
                (Option(bufferByMetrics.get(fieldAlias)), Option(thatAggregator.bufferByMetrics.get(fieldAlias))) match {
                    case (Some(thisBitMap: BitMap), Some(thatBitMap: BitMap)) =>
                        thisBitMap.or(thatBitMap)
                        bufferByMetrics.put(fieldAlias, thisBitMap)
                    case (None, Some(thatBitMap: BitMap)) =>
                        val thisBitMap = bitmapOf()
                        thisBitMap.or(thatBitMap)
                        bufferByMetrics.put(fieldAlias, thisBitMap)
                    case _ =>
                }
            } else if (
               computationRule.inDeduplicationCategory &&
               thatAggregator.mergeStrategy == AggregatorMergeStrategy.Decrease.toString
            ) {
                (Option(bufferByMetrics.get(fieldAlias)), Option(thatAggregator.bufferByMetrics.get(fieldAlias))) match {
                    // 从之前的bitmap中移除的对应的值, 也就是在 one diff two
                    case (Some(thisBitMap: BitMap), Some(thatBitMap: BitMap)) =>
                        if (!thisBitMap.isEmpty && !thatBitMap.isEmpty) {
                            thisBitMap.andNot(thatBitMap)
                            bufferByMetrics.put(fieldAlias, thisBitMap)
                        }
                    case _ =>
                }
           } else if (computationRule.inCountingCategory) {
                (Option(bufferByMetrics.get(fieldAlias)), Option(thatAggregator.bufferByMetrics.get(fieldAlias))) match {
                    case (Some(thisCount: Long), Some(thatCount: Long)) =>
                        bufferByMetrics.put(fieldAlias, thisCount + thatCount)
                    case (None, Some(thatCount: Long)) =>
                        bufferByMetrics.put(fieldAlias, thatCount)
                    case _ =>
                }
            } else if (computationRule.inSumCategory) {
                (Option(bufferByMetrics.get(fieldAlias)), Option(thatAggregator.bufferByMetrics.get(fieldAlias))) match {
                    case (Some(thisSum: Long), Some(thatSum: Long)) =>
                        bufferByMetrics.put(fieldAlias, thisSum + thatSum)
                    case (Some(thisSum: Double), Some(thatSum: Double)) =>
                        bufferByMetrics.put(fieldAlias, thisSum + thatSum)
                    case (None, Some(thatSum: Long)) =>
                        bufferByMetrics.put(fieldAlias, thatSum)
                    case (None, Some(thatSum: Double)) =>
                        bufferByMetrics.put(fieldAlias, thatSum)
                    case _ =>
                }
            }  else {
                throw new IllegalArgumentException(s"Unknown rule: ${computationRule}")
            }
        }
    }

    def clear(): Unit = bufferByMetrics.clear()

    def isEmpty(): Boolean = bufferByMetrics.isEmpty()

}
// scalastyle:on
