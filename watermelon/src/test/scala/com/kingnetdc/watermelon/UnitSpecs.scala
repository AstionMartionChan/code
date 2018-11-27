package com.kingnetdc.watermelon

import org.scalatest._

abstract class UnitSpecs extends FlatSpec
  with Matchers with OptionValues
  with Inside with Inspectors
  with BeforeAndAfter with BeforeAndAfterAll {}