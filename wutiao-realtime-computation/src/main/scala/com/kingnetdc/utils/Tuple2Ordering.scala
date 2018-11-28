package com.kingnetdc.utils

class Tuple2Ordering[K: Ordering, V : Ordering] extends Ordering[(K, V)] {

  override def compare(x: (K, V), y: (K, V)): Int = {
    implicitly[Ordering[V]].compare(x._2, y._2)
  }

}
