# configuration for Spark execution
num_executors="10"
executor_cores="2"
driver_memory="2g"
executor_memory="2g"

if [ -f /data/project/streaming/wutiao_hive/bin/realtime-env.sh ]; then
    . /data/project/streaming/wutiao_hive/bin/realtime-env.sh
else
   echo "realtime-env.sh doesn't exist"
   exit
fi

${SPARK_HOME}/bin/spark-submit \
    --master yarn --deploy-mode cluster --name "realtime-wutiao-json-to-hive" --verbose --queue ${QUEUE} \
    --jars ${CONF_PATH}/action.properties,${CONF_PATH}/metrics.properties,${JAR_PATH}/spark-influx-sink-0.4.0-3-g0584cfd.jar,${JAR_PATH}/metrics-influxdb-1.2.3-SNAPSHOT.jar \
    --conf spark.metrics.conf=metrics.properties \
    --conf spark.driver.extraClassPath=metrics.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar \
    --conf spark.executor.extraClassPath=metrics.properties:spark-influx-sink-0.4.0-3-g0584cfd.jar:metrics-influxdb-1.2.3-SNAPSHOT.jar \
    --num-executors ${num_executors} --executor-cores ${executor_cores} \
    --driver-memory ${driver_memory} --executor-memory ${executor_memory} \
    --properties-file ${CONF_PATH}/action-to-hive.conf \
    --class "com.kingnetdc.job.MultiTopics2Hive" ${APP_JAR} \
    action.properties
~
