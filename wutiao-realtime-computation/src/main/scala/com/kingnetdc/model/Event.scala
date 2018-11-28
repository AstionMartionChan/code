package com.kingnetdc.model

/**
  * DimensionValues是一个多行的结果, 是因为某个维度会出现的全部的统计项

  * A     B
  * all   B
  * A     all
  * all   all

  * List(
  * List(A, B),
  * List(all, B),
  * List(A, all),
  * List(all, all)
  * )
  */
class Event(
    val name: String,
    val time: Long,
    val dimensionValues: List[List[String]],
    val fieldValueMap: Map[String, Any]
) extends Serializable {

    require(dimensionValues.nonEmpty, "Dimension values should not be empty")

    private var filterFieldValueMap: Map[String, Any] = Map.empty

    def setFilterFieldValueMap(map: Map[String, Any]) = {
        this.filterFieldValueMap = map
        this
    }

    def getFilterFieldValueMap = filterFieldValueMap

    override def toString = s"Event($time, $dimensionValues, $fieldValueMap, $filterFieldValueMap)"

}
