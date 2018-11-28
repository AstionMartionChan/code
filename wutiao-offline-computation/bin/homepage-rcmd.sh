#!/usr/bin/env bash

num_executors="3"
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
    --master yarn --deploy-mode cluster --name "offline-wutiao-homepage-rcmd" --verbose --queue ${QUEUE} \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --jars ${JAR_PATH}/hbase-client-1.2.0-cdh5.14.2.jar,${JAR_PATH}/hbase-common-1.2.0-cdh5.14.2.jar,${JAR_PATH}/hbase-server-1.2.0-cdh5.14.2.jar,${JAR_PATH}/htrace-core-3.2.0-incubating.jar,${JAR_PATH}/hbase-protocol-1.2.0-cdh5.14.2.jar,${CONF_PATH}/homepage-rcmd.properties,${CONF_PATH}/hive-site.xml \
    --conf spark.driver.extraClassPath=homepage-rcmd.properties,hive-site.xml \
    --conf spark.sql.shuffle.partitions=50 \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --class "com.kingnetdc.offline.job.HomePagePopularItemRecommend" ${APP_JAR} homepage-rcmd.properties