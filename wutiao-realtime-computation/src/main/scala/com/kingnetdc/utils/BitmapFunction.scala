package com.kingnetdc.utils

import org.apache.spark.sql.Row
import org.apache.spark.sql.expressions.{MutableAggregationBuffer, UserDefinedAggregateFunction}
import org.apache.spark.sql.types.{DataType, BinaryType, LongType, StructType}
import org.roaringbitmap.{RoaringBitmap => BitMap}

/**
 * Created by zhouml on 04/08/2018.
 */
/**
 *  The main overhead is frequently serilization & deserialization, so the performance is worse than
 *  collect_set && rdd_agg
 */
class BitmapFunction extends UserDefinedAggregateFunction {

    override def inputSchema: StructType = new StructType().add("input", LongType)

    override def update(buffer: MutableAggregationBuffer, input: Row): Unit = {
        val internal = buffer.getAs[Array[Byte]](0)
        val bitmap = BitmapUtils.asBitMap(internal)
        bitmap.add(input.getAs[Long](0).toInt)
        buffer.update(0, BitmapUtils.toByteArray(bitmap))
    }

    override def bufferSchema: StructType = (new StructType()).add("byteArray", BinaryType)

    override def merge(buffer1: MutableAggregationBuffer, buffer2: Row): Unit = {
        val bitmap1 = BitmapUtils.asBitMap(buffer1.getAs[Array[Byte]](0))
        val bitmap2 = BitmapUtils.asBitMap(buffer2.getAs[Array[Byte]](0))
        bitmap1.or(bitmap2)
        buffer1.update(0, BitmapUtils.toByteArray(bitmap1))
    }

    override def initialize(buffer: MutableAggregationBuffer): Unit = {
        val bitmap = new BitMap()
        buffer.update(0, BitmapUtils.toByteArray(bitmap))
    }

    override def deterministic: Boolean = true

    override def evaluate(buffer: Row): Any = {
        buffer.getAs[Array[Byte]](0)
    }

    override def dataType: DataType = BinaryType

}