#!/usr/bin/env bash

if [ -f /opt/app/wutiao-realtime-computation/bin/realtime-env.sh ]; then
    . /opt/app/wutiao-realtime-computation/bin/realtime-env.sh
else
   echo "realtime-env.sh doesn't exist"
   exit
fi

num_executors="8"
executor_cores="2"
driver_memory="3g"
executor_memory="3g"

export USER=mining

export HADOOP_USER_NAME=mining

/opt/app/spark/spark-2.2.1/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "realtime-wutiao-homepage-rcmd" --verbose --queue root.realtime \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --jars ${JAR_PATH}/spark-influx-sink-0.4.0-3-g0584cfd.jar,${JAR_PATH}/metrics-influxdb-1.2.3-SNAPSHOT.jar,${JAR_PATH}/hbase-client-2.1.0.jar,/${JAR_PATH}/hbase-common-2.1.0.jar,${JAR_PATH}/hbase-server-2.1.0.jar,${JAR_PATH}/hbase-protocol-2.1.0.jar,${JAR_PATH}/htrace-core-3.2.0-incubating.jar,${JAR_PATH}/guava-23.0.jar,${JAR_PATH}/curator-framework-4.0.1.jar,${JAR_PATH}/curator-client-4.0.1.jar,${JAR_PATH}/hbase-shaded-miscellaneous-2.1.0.jar,${JAR_PATH}/hbase-shaded-protobuf-2.1.0.jar,${JAR_PATH}/hbase-shaded-netty-2.1.0.jar,${JAR_PATH}/hbase-protocol-shaded-2.1.0.jar,${CONF_PATH}/homepage-rcmd.properties,${CONF_PATH}/cache-prod.yml,${CONF_PATH}/metrics.properties \
    --conf spark.driver.extraClassPath=guava-23.0.jar:curator-framework-4.0.1.jar:curator-client-4.0.1.jar \
    --conf spark.executor.extraClassPath=guava-23.0.jar:curator-framework-4.0.1.jar:curator-client-4.0.1.jar \
    --conf spark.streaming.kafka.maxRetries=5 \
    --conf spark.serializer=org.apache.spark.serializer.KryoSerializer \
    --conf spark.streaming.duration=120000 \
    --conf spark.streaming.kafka.maxRatePerPartition=1000 \
    --conf spark.streaming.stopGracefullyOnShutdown=true \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --conf spark.streaming.kafka.consumer.poll.ms=180000 \
    --conf spark.yarn.executor.memoryOverhead=512m \
    --conf spark.output.partition=100 \
    --conf spark.streaming.concurrentJobs=2 \
    --conf spark.metrics.conf=metrics.properties \
    --conf spark.kafka.offset.zookeeper.connect=172.27.6.54:2181,172.27.6.103:2181,172.27.6.141:2181,172.27.6.183:2181,172.27.6.122:2181 \
    --class "com.kingnetdc.job.HomePageMixRcmd" /opt/app/wutiao-realtime-computation/homepage-jar/blockchain-realtime-computation-1.0-SNAPSHOT.jar homepage-rcmd.properties