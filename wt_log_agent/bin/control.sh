#!/usr/bin/env bash

source /etc/profile

function start() {
    init
    rc=0
    PID="${PID_PATH}/app.pid"
    NOW_SECOND=`date "+%Y-%m-%d-%H-%M-%S"`
    if [ -f ${PID} ]; then
        status
    else
        nohup ${JAVA_CMD} ${JAVA_OPTS} -jar log_agent-1.0.jar >> ${LOG_PATH}/server_${NOW_SECOND}.out 2>&1 &
        echo $! > ${PID}
        sleep 5
        status
    fi
}

function stop() {
    init
    PID="${PID_PATH}/app.pid"
    rc=0
    if [ -f ${PID} ]; then
        process=`cat ${PID}`
        if [ "${#process}" -gt 0 ]; then
            if kill -0 `cat ${PID}` > /dev/null 2>&1; then
                echo -n stopping ${APP_NAME}
                echo "`date` killing ${APP_NAME}"
                kill ${process}
                while kill -0 ${process} > /dev/null 2>&1; do
                    echo -n "."
                    sleep 1;
                done
                rm ${PID}
                echo ""
            else
                rc=$?
                echo "${APP_NAME} stop error because kill -0 of pid `cat ${PID}` failed with status ${rc}"
            fi
        else
            rm ${PID}
            echo "${PID} file is invalid, ${APP_NAME} is not running!"
        fi
    else
        echo "${APP_NAME} stop error because no pid file ${PID}"
    fi
}


function status() {
    init
    PID="${PID_PATH}/app.pid"
    rc=0
    if [ -f ${PID} ]; then
        process=`cat ${PID}`
        if [ "${#process}" -gt 0 ]; then
            count=`ps -ef | grep ${process} | grep -v "grep" | wc -l`
            if [ ${count} == 0 ]; then
                rm ${PID}
                echo "PID file ${PID} is not valid, process is ${process}, now rm it."
            else
                echo "${APP_NAME} is running, pid is ${process}"
            fi
        else
            rm ${PID}
            echo "${PID} file is invalid, ${APP_NAME} is not running!"
        fi
    else
        echo "${APP_NAME} is not running!"
    fi
}

function init() {
    APP_BIN_PATH=$(cd "$(dirname "$0")"; pwd)
    APP_PATH=$(dirname ${APP_BIN_PATH})
    JAVA_CMD=`which java`
    PID_PATH="${APP_PATH}/pid"
    LOG_PATH="/data/logs/frontdata/"
    APP_NAME="danalogagent"
    if [ -f ${APP_PATH}/env.dat ]; then
        ENV_INFO=`cat ${APP_PATH}/env.dat`
    else
        ENV_INFO="dev"
    fi
    JAVA_OPTS="-server -Xms2048m -Xmx2048m  -XX:+UseParallelGC  -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.port=8999 -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false"
}

# See how we were called.
case "$1" in
start)
    start
    ;;
stop)
    stop
    ;;
status)
    status
    ;;
restart|reload|force-reload)
    stop
    start
    rc=$?
    ;;
*)
    echo $"Usage: $0 {start|stop|status|restart|reload|force-reload}"
    exit 2
esac

exit ${rc}
