package com.kingnetdc.sql

import com.kingnetdc.UnitSpecs
import org.apache.spark.sql.types._

/**
  * Created by zhouml on 20/05/2018.
  */
class ComputationRuleSuite extends UnitSpecs {

  val fixture = {
    new {
      val equalFilter1 =  Equal("is_new_device", 1, ByteType)
      val equalFilter2 =  Equal("is_new_device", 0, ByteType)

      val equalFilter3 =  Equal("is_new_user", 1, ByteType)
      val equalFilter4 =  Equal("is_new_user", 0, ByteType)

      val multipleFilter1 =
        And(Equal("is_new_device", 1, ByteType) :: Equal("date", "2018-05-11", StringType) :: Nil)
      val multipleFilter2 =
        And(Equal("is_new_device", 0, ByteType) :: Equal("date", "2018-05-11", StringType) :: Nil)

      val rules1 = List(
        ComputationRule("did", StringType, "dad", "count", None),
        ComputationRule("did", StringType, "dad_new", "count_distinct", Some(equalFilter1)),
        ComputationRule("did", StringType, "dad_old", "count_distinct", Some(equalFilter2)),
        ComputationRule("did", StringType, "dad_all_new", "count", Some(equalFilter1))
      )

      val rules2 = List(
        ComputationRule("did", StringType, "dad", "count", None),
        ComputationRule("did", StringType, "dad_new", "count_distinct", Some(multipleFilter1)),
        ComputationRule("did", StringType, "dad_old", "count_distinct", Some(multipleFilter2)),
        ComputationRule("did", StringType, "dad_all_new", "count", Some(multipleFilter1))
      )

      val rules3 = List(
        ComputationRule("did", StringType, "dad", "count", None),
        ComputationRule("did", StringType, "dad_new", "count_distinct", Some(equalFilter1)),
        ComputationRule("did", StringType, "dad_old", "count_distinct", Some(equalFilter2)),
        ComputationRule("did", StringType, "dad_all_new", "count", Some(equalFilter1)),
        ComputationRule("did", StringType, "dad_top_n", "top_n", Some(equalFilter1)),

        ComputationRule("ouid", StringType, "dau", "count", None),
        ComputationRule("ouid", StringType, "dau_new", "count_distinct", Some(equalFilter3)),
        ComputationRule("ouid", StringType, "dau_old", "count_distinct", Some(equalFilter4)),
        ComputationRule("ouid", StringType, "dau_all_new", "count", Some(equalFilter3))
      )

      // aggregation field is LongType
      val rules4 = List(
        ComputationRule("did", LongType, "dad", "count", None),
        ComputationRule("did", LongType, "dad_new", "count_distinct", Some(equalFilter1)),
        ComputationRule("did", LongType, "dad_old", "count_distinct", Some(equalFilter2)),
        ComputationRule("did", LongType, "dad_all_new", "count", Some(equalFilter1)),
        ComputationRule("did", LongType, "dad_top_n", "top_n", Some(equalFilter1)),

        ComputationRule("ouid", LongType, "dau", "count", None),
        ComputationRule("ouid", LongType, "dau_new", "count_distinct", Some(equalFilter3)),
        ComputationRule("ouid", LongType, "dau_old", "count_distinct", Some(equalFilter4)),
        ComputationRule("ouid", LongType, "dau_all_new", "count", Some(multipleFilter1))
      )


      val equalFilter5 =  Equal("is_new_device_1", 1, ByteType)
      val equalFilter6 =  Equal("is_new_device_2", 1, ByteType)
      val equalFilter7 =  Equal("is_new_device_3", 1, ByteType)
      val equalFilter8 =  Equal("old_device", 1, ByteType)

      val rules5 =
        List(
          ComputationRule("did", StringType, "dad", "count", None),
          ComputationRule("did", StringType, "dad_new_1", "count_distinct", Some(equalFilter5)),
          ComputationRule("did", StringType, "dad_new_2", "count_distinct", Some(equalFilter6)),
          ComputationRule("did", StringType, "dad_new_3", "count_distinct", Some(equalFilter7)),
          ComputationRule("did", StringType, "dad_old", "count_distinct", Some(equalFilter8))
        )
    }
  }

  "ComputationRule getAggregationField" should "pass check" in {
    import fixture._
    rules1.map(_.aggregationField).distinct == List("did") shouldBe true
  }

  "ComputationRule.toStructFields" should "pass check" in {
    import fixture._

    ComputationRule.toStructFields(rules1).head shouldBe StructField("did", LongType, false)

    ComputationRule.toStructFields(rules4) shouldBe
      List(StructField("did", LongType, false), StructField("ouid",LongType,false))

    ComputationRule.convertToStructFields(rules4) shouldBe {
      List(
        StructField("did", LongType, false), StructField("ouid", LongType, false),
        StructField("is_new_device", ByteType, false), StructField("is_new_user", ByteType, false),
        StructField("date", StringType, false)
      )
    }
  }

  "ComputationRule.filterFieldsToStructFields" should "pass check" in {
    import fixture._

    ComputationRule.filterFieldsToStructFields(rules2) shouldBe
      List(StructField("is_new_device", ByteType, false), StructField("date", StringType, false))
    ComputationRule.filterFieldsToStructFields(rules4) shouldBe
      List(
        StructField("is_new_device", ByteType, false),
        StructField("is_new_user", ByteType, false),
        StructField("date", StringType, false)
      )
  }

  "ComputationRule convertToStructFields" should "pass check" in {
    import fixture._

    ComputationRule.convertToStructFields(rules3).map(_.name) shouldBe
      List("did", "ouid", "is_new_device", "is_new_user")

    ComputationRule.convertToStructFields(rules4).map(_.name) shouldBe
      List("did", "ouid", "is_new_device", "is_new_user", "date")
  }

}

