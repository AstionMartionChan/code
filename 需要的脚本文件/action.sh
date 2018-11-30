num_executors="10"
executor_cores="2"
driver_memory="2g"
executor_memory="2g"

if [ -f /data/project/new_wutiao_hive/bin/realtime-env.sh ]; then
    . /data/project/new_wutiao_hive/bin/realtime-env.sh
else
   echo "realtime-env.sh doesn't exist"
   exit
fi

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "realtime-wutiao-json-to-hive-new" --verbose --queue ${QUEUE} \
    --jars ${CONF_PATH}/action.properties,${CONF_PATH}/hive-site.xml,${JAR_PATH}/curator-framework-4.0.1.jar,${JAR_PATH}/curator-client-4.0.1.jar \
    --conf spark.driver.extraClassPath=curator-framework-4.0.1.jar:curator-client-4.0.1.jar \
    --conf spark.executor.extraClassPath=curator-framework-4.0.1.jar:curator-client-4.0.1.jar \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --properties-file ${CONF_PATH}/action.conf \
    --class "com.cfy.job.SingleTopic2Hive" ${APP_JAR} \
    action.properties
