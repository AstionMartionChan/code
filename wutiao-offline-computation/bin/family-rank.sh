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
    --master yarn --deploy-mode cluster --name "offline-wutiao-family-rank" --verbose --queue ${QUEUE} \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --jars ${JAR_PATH}/hiveudf-1.1.0.jar,${CONF_PATH}/family-rank.properties,${CONF_PATH}/hive-site.xml \
    --conf spark.driver.extraClassPath=family-rank.properties,hiveudf-1.1.0.jar,hive-site.xml \
    --conf spark.executor.extraClassPath=hiveudf-1.1.0.jar \
    --conf spark.sql.shuffle.partitions=50 \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --class "com.kingnetdc.offline.job.FamilyContributionRankStatistics" ${APP_JAR} family-rank.properties
