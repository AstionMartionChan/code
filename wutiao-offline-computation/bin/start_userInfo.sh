#!/usr/bin/env bash

num_executors="2"
executor_cores="2"
driver_memory="2g"
executor_memory="2g"

# mkdir -p ${LOG_PATH}

if [ -f /opt/app/offline-computation/bin/offline-env.sh ]; then
    . /opt/app/offline-computation/bin/offline-env.sh
else
   echo "offline-env.sh doesn't exist"
   exit
fi

#  --properties-file /home/blockchain/srv/apps/kingnetdc/blockchain/conf/user-statistics-spark.conf 

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "user_info_verify_contribution" --verbose --queue ${QUEUE} \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --jars ${CONF_PATH}/user_info.properties,${CONF_PATH}/hive-site.xml \
    --conf spark.driver.extraClassPath=user_info.properties:hive-site.xml \
    --conf spark.yarn.submit.waitAppCompletion=false \
    --class "com.kingnetdc.offline.job.VerifyContributionToKafka" ${APP_JAR} user_info.properties
