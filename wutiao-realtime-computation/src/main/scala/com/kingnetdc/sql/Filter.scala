package com.kingnetdc.sql

import org.apache.spark.sql.Column
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.DataType

sealed trait Filter {
  def asExpression: Column
}

case class Equal(fieldName: String, fieldValue: Any, fieldType: DataType) extends Filter {
  override def asExpression: Column = col(fieldName) === lit(fieldValue)
}

case class NotEqual(fieldName: String, fieldValue: Any, fieldType: DataType) extends Filter {
  override def asExpression: Column = col(fieldName) =!= lit(fieldValue)
}

case class LessThan(fieldName: String, fieldValue: Any, fieldType: DataType) extends Filter {
  override def asExpression: Column = col(fieldName) < lit(fieldValue)
}

case class LessThanOrEqual(fieldName: String, fieldValue: Any, fieldType: DataType) extends Filter {
  override def asExpression: Column = col(fieldName) <= lit(fieldValue)
}

case class GreaterThan(fieldName: String, fieldValue: Any, fieldType: DataType) extends Filter {
  override def asExpression: Column = col(fieldName) > lit(fieldValue)
}

case class GreaterOrEqual(fieldName: String, fieldValue: Any, fieldType: DataType) extends Filter {
  override def asExpression: Column = col(fieldName) >= lit(fieldValue)
}

case class And(filters: List[Filter]) extends Filter {
  require(filters.nonEmpty, "Filters should not be empty")
  override def asExpression: Column = filters.map(_.asExpression).reduce(_ and _)
}

case class Or(filters: List[Filter]) extends Filter {
  require(filters.nonEmpty, "Filters should not be empty")
  override def asExpression: Column = filters.map(_.asExpression).reduce(_ or _)
}

