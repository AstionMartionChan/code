package com.kingnetdc.sql

import com.kingnetdc.sql.StatisticsFunction.{summaryCategory, countingCategory, deduplicationCategory, rankCategory}
import com.kingnetdc.watermelon.utils.StringUtils
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types._
import scala.collection.mutable

/**
 * Created by zhouml on 20/05/2018.
 */

/**
 * @param aggregationField      聚合字段名 sum(fieldName), count(fieldName)
 * @param aggregationFieldType  聚合字段名 类型 StringType DoubleType
 * @param aggregationFieldAlias 聚合之后的别名
 * @param aggregationFunction   聚合采用的函数  sum | count | first | last
 * @param filterOpt             聚合过程的筛选条件 sum(if (x = 1), 1, 0) as xx
 */
case class ComputationRule(
    aggregationField: String,
    aggregationFieldType: DataType,
    aggregationFieldAlias: String, aggregationFunction: String,
    filterOpt: Option[Filter] = None
) {

    val inSumCategory = summaryCategory.exists(_.toString == aggregationFunction)

    val inCountingCategory = countingCategory.exists(_.toString == aggregationFunction)

    val inDeduplicationCategory = deduplicationCategory.exists(_.toString == aggregationFunction)

    val inRankCategory = rankCategory.exists(_.toString == aggregationFunction)

    def requireTransformToLongType: Boolean = aggregationFieldType.isInstanceOf[StringType] && inDeduplicationCategory

    def getAggregationFieldType = withDataTypeTransformed

    /*
        去重的字段若为String类型, 则转换成Long, 以减少内存占用;
     */
    private def withDataTypeTransformed: DataType = {
        if (requireTransformToLongType) LongType
        else aggregationFieldType
    }

    def withFieldValueTransformed(fieldValue: Any): Any = {
        aggregationFieldType match {
            case StringType if inDeduplicationCategory => StringUtils.hashString(fieldValue.toString).asLong()
            case _ => fieldValue
        }
    }

    def getFieldsFromFilter(): List[(String, DataType)] = {
        filterOpt match {
            case Some(filter) => getFieldsFromFilter(filter)
            case None => Nil
        }
    }

    private def getFieldsFromFilter(filter: Filter): List[(String, DataType)] = {
        filter match {
            case Equal(name, _, dataType) => (name -> dataType) :: Nil
            case LessThan(name, _, dataType) => (name -> dataType) :: Nil
            case LessThanOrEqual(name, _, dataType) => (name -> dataType) :: Nil
            case GreaterThan(name, _, dataType) => (name -> dataType) :: Nil
            case GreaterOrEqual(name, _, dataType) => (name -> dataType) :: Nil
            case And(filters) => filters.flatMap(getFieldsFromFilter)
            case Or(filters) => filters.flatMap(getFieldsFromFilter)
        }
    }

}


object ComputationRule {

    private[sql] def toFieldRule(computationRules: List[ComputationRule]): List[(String, ComputationRule)] = {
        val fieldAndDataType: mutable.LinkedHashMap[String, ComputationRule] = new mutable.LinkedHashMap()

        computationRules.foreach { computationRule =>
            val field = computationRule.aggregationField
            fieldAndDataType.get(field) match {
                case Some(rule) =>
                    if (rule.requireTransformToLongType) rule
                    else if (computationRule.requireTransformToLongType) {
                        fieldAndDataType.update(field, computationRule)
                    }
                case None =>
                    fieldAndDataType.update(computationRule.aggregationField, computationRule)
            }
        }

        fieldAndDataType.toList
    }

    private[sql] def toStructFields(computationRules: List[ComputationRule]): List[StructField] = {
        toFieldRule(computationRules).map {
            case (field, rule) => StructField(field, rule.getAggregationFieldType, false)
        }
    }

    private[sql] def filterFieldsToStructFields(computationRules: List[ComputationRule]): List[StructField] = {
        computationRules.flatMap(_.getFieldsFromFilter).distinct.map {
            case (name, dataType) => StructField(name, dataType, false)
        }
    }

    def convertToStructFields(computationRules: List[ComputationRule]): List[StructField] = {
        toStructFields(computationRules) ::: filterFieldsToStructFields(computationRules)
    }

    private[sql] def toStructFieldValues(
        aggregationFieldValueMap: Map[String, Any], computationRules: List[ComputationRule]
    ): List[Any] = {
        toFieldRule(computationRules).map {
            case (field, rule) => {
                // MUST BE THERE
                val originalFieldValue = aggregationFieldValueMap(field)
                rule.withFieldValueTransformed(originalFieldValue)
            }
        }
    }

    private[sql] def toFilterFieldValues(
        filterFieldValueMap: Map[String, Any], computationRules: List[ComputationRule]
    ): List[Any] = {
        computationRules.flatMap(_.getFieldsFromFilter).distinct.map {
            case (filterField, _) => filterFieldValueMap(filterField)
        }
    }

    def convertToRow(
        aggregationFieldValueMap: Map[String, Any], filterFieldValueMap: Map[String, Any],
        computationRules: List[ComputationRule]
    ): List[Any] = {
        toStructFieldValues(aggregationFieldValueMap, computationRules) :::
        toFilterFieldValues(filterFieldValueMap, computationRules)
    }


    def getAggregateExpression(computationRules: List[ComputationRule]) = {
        computationRules.map {
            case computationRule @ ComputationRule(aggregationField, _, aggregationFieldAlias, _, filterOpt) =>
                if (computationRule.inDeduplicationCategory) {
                    filterOpt match {
                        case Some(filter) =>
                            collect_set(when(filter.asExpression, col(aggregationField))).as(aggregationFieldAlias)
                        case None =>
                            collect_set(aggregationField).as(aggregationFieldAlias)
                    }
                } else if (computationRule.inCountingCategory) {
                    val rawColumn =
                        filterOpt match {
                            case Some(filter) =>
                                count(when(filter.asExpression, col(aggregationField)))
                            case None =>
                                count(aggregationField)
                        }

                    (computationRule.aggregationFieldType match {
                        case IntegerType => when(rawColumn.isNull, 0).otherwise(rawColumn)
                        case LongType => when(rawColumn.isNull, 0L).otherwise(rawColumn)
                        case _ => rawColumn
                    }).as(aggregationFieldAlias)
                } else if (computationRule.inSumCategory) {
                    val rawColumn =
                        filterOpt match {
                            case Some(filter) =>
                                sum(when(filter.asExpression, col(aggregationField)))
                            case None =>
                                sum(aggregationField)
                        }

                    (computationRule.aggregationFieldType match {
                        case LongType =>
                            when(rawColumn.isNull, 0L).otherwise(rawColumn)
                        case DoubleType =>
                            when(rawColumn.isNull, 0D).otherwise(rawColumn)
                        case _ => rawColumn
                    }).as(aggregationFieldAlias)
                } else if (computationRule.inRankCategory) {
                    // TODO
                    throw new IllegalArgumentException("Not support yet")
                } else {
                    throw new IllegalArgumentException(s"Unknown rule:  ${computationRule}")
                }
        }
    }

}
