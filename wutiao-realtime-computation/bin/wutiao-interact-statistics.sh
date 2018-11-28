#!/usr/bin/env bash

num_executors="10"
executor_cores="2"
driver_memory="3g"
executor_memory="3g"

if [ -f /opt/app/wutiao-realtime-computation/bin/realtime-env.sh ]; then
    . /opt/app/wutiao-realtime-computation/bin/realtime-env.sh
else
   echo "realtime-env.sh doesn't exist"
   exit
fi

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "realtime-wutiao-interact-statistics" --verbose --queue ${QUEUE} \
    --jars ${JAR_PATH}/RoaringBitmap-0.7.9.jar,${JAR_PATH}/spark-influx-sink-0.4.0-3-g0584cfd.jar,${JAR_PATH}/metrics-influxdb-1.2.3-SNAPSHOT.jar,${CONF_PATH}/interact.properties,${CONF_PATH}/metrics.properties \
    --conf spark.metrics.conf=metrics.properties \
    --conf spark.driver.extraClassPath=metrics.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar:RoaringBitmap-0.7.9.jar \
    --conf spark.executor.extraClassPath=metrics.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar:RoaringBitmap-0.7.9.jar \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --properties-file ${CONF_PATH}/interact-statistics-spark.conf \
    --class "com.kingnetdc.job.InteractStatistics" ${ROOT_PATH}/interact/blockchain-realtime-computation-1.0-SNAPSHOT.jar \
    interact.properties