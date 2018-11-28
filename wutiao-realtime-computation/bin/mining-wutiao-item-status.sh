#!/usr/bin/env bash
# configuration for Spark execution
num_executors="3"
executor_cores="2"
driver_memory="1g"
executor_memory="1g"

if [ -f /opt/app/wutiao-realtime-computation/bin/realtime-env.sh ]; then
    . /opt/app/wutiao-realtime-computation/bin/realtime-env.sh
else
   echo "realtime-env.sh doesn't exist"
   exit
fi

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "mining-wutiao-item-status" --queue root.realtime \
    --jars ${JAR_PATH}/RoaringBitmap-0.7.9.jar,${JAR_PATH}/spark-influx-sink-0.4.0-3-g0584cfd.jar,${JAR_PATH}/metrics-influxdb-1.2.3-SNAPSHOT.jar,${CONF_PATH}/item.properties,${CONF_PATH}/metrics-mining.properties \
    --conf spark.metrics.conf=metrics-mining.properties \
    --conf spark.driver.extraClassPath=metrics-mining.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar:RoaringBitmap-0.7.9.jar \
    --conf spark.executor.extraClassPath=metrics-mining.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar:RoaringBitmap-0.7.9.jar \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --properties-file ${CONF_PATH}/mining-wutiao-item-status.conf \
    --class "com.kingnetdc.job.ArticleStatusCheck" ${APP_JAR} \
    item.properties