#!/usr/bin/env bash

num_executors="4"
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
    --master yarn --deploy-mode cluster --name "realtime-wutiao-exchange-rate" --verbose --queue ${QUEUE} \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --jars ${JAR_PATH}/RoaringBitmap-0.7.9.jar,${JAR_PATH}/spark-influx-sink-0.4.0-3-g0584cfd.jar,${JAR_PATH}/metrics-influxdb-1.2.3-SNAPSHOT.jar,${CONF_PATH}/exchange-rate.properties,${CONF_PATH}/metrics.properties \
    --conf spark.metrics.conf=metrics.properties \
    --conf spark.kafka.offset.storage=mysql \
    --conf spark.kafka.offset.mysql.url=jdbc:mysql://172.27.2.251:4310/wutiao?useSSL=false \
    --conf spark.kafka.offset.mysql.user=data_test \
    --conf spark.kafka.offset.mysql.password=data_test_pass \
    --conf spark.kafka.offset.mysql.table=exchange_rate_statistics_topic_partition \
    --conf spark.streaming.duration=120000 \
    --conf spark.streaming.kafka.maxRatePerPartition=10000 \
    --conf spark.streaming.kafka.maxRetries=10 \
    --conf spark.checkpoint.directory=hdfs://nameservice1/user/etl/realtime/wutiao/exchange-rate/checkpoint \
    --conf spark.streaming.stopGracefullyOnShutdown=true \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --class "com.kingnetdc.job.ExchangeRateStatistics" ${APP_JAR} exchange-rate.properties