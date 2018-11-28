#!/usr/bin/env bash

num_executors="5"
executor_cores="2"
driver_memory="3g"
executor_memory="3g"

if [ -f /opt/app/offline-computation/bin/offline-env.sh ]; then
    . /opt/app/offline-computation/bin/offline-env.sh
else
   echo "offline-env.sh doesn't exist"
   exit
fi

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "offline-wutiao-discoverer-rank" --verbose --queue ${QUEUE} \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --jars ${CONF_PATH}/discover-rank.properties,${CONF_PATH}/hive-site.xml \
    --conf spark.driver.extraClassPath=discover-rank.properties,hive-site.xml \
    --conf spark.sql.shuffle.partitions=50 \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --class "com.kingnetdc.offline.job.DiscovererRankStatistics" ${APP_JAR} discover-rank.properties
