#!/usr/bin/env bash

num_executors="7"
executor_cores="2"
driver_memory="3g"
executor_memory="4g"

if [ -f /opt/app/wutiao-realtime-computation/bin/realtime-env.sh ]; then
    . /opt/app/wutiao-realtime-computation/bin/realtime-env.sh
else
   echo "realtime-env.sh doesn't exist"
   exit
fi

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "realtime-wutiao-user-statistics" --verbose --queue ${QUEUE} \
    --jars ${JAR_PATH}/RoaringBitmap-0.7.9.jar,${JAR_PATH}/spark-influx-sink-0.4.0-3-g0584cfd.jar,${JAR_PATH}/metrics-influxdb-1.2.3-SNAPSHOT.jar,${CONF_PATH}/user.properties,${CONF_PATH}/metrics.properties,${CONF_PATH}/cac
he-user-dimension-prod.yml \
    --conf spark.metrics.conf=metrics.properties \
    --conf spark.driver.extraClassPath=metrics.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar:RoaringBitmap-0.7.9.jar:user.properties \
    --conf spark.executor.extraClassPath=metrics.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar:RoaringBitmap-0.7.9.jar \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --properties-file ${CONF_PATH}/user-statistics-spark.conf \
    --class "com.kingnetdc.job.UserStatistics" ${ROOT_PATH}/user/blockchain-realtime-computation-1.0-SNAPSHOT.jar user.properties