package org.apache.spark.streaming.dstream

import org.apache.spark.Partitioner
import org.apache.spark.rdd.RDD
import org.apache.spark.streaming.Time
import scala.reflect.ClassTag

/**
 * 因为Spark streaming 该方法的包限制, 所以采用了
 *
 * Created by zhouml on 18/08/2018.
 */
class KingnetPairDStreamFunctions[K, V](self: DStream[(K, V)])(
    implicit kt: ClassTag[K], vt: ClassTag[V], ord: Ordering[K]
) extends PairDStreamFunctions[K, V](self)(kt, vt, ord) {

    def updateStateByKey[S: ClassTag](
        updateFunc: (Time, Iterator[(K, Seq[V], Option[S])]) => Iterator[(K, S)],
        partitioner: Partitioner,
        rememberPartitioner: Boolean,
        initialRDD: RDD[(K, S)]
    ): DStream[(K, S)] = ssc.withScope {
        val newUpdateFunc = ssc.sc.clean(updateFunc)
        new StateDStream(self, newUpdateFunc, partitioner, rememberPartitioner, Some(initialRDD))
    }

}
