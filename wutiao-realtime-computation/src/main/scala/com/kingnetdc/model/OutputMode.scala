package com.kingnetdc.model

object OutputMode extends Enumeration {

  // every batch is independent
  val BATCH = Value(1, "batch")

  // continous accumulation
  val ACCUMULATION = Value(2, "accumulation")

}
