# !/usr/bin/env python
# -*-coding: UTF-8 -*-

#**********************程序说明*********************************#
#*模块: 监控
#*功能: 五条实时指标消息接收监控
#*作者: zhoujiongyu
#*时间: 2018-08-31
#*备注: 五条实时指标消息接收监控 -- 消息为0则报警
#***************************************************************#

import kingnetdc

from datetime import datetime
from datetime import timedelta
from influxdb import InfluxDBClient

influxdb_host = "172.27.6.44"
influxdb_port = 8086
influxdb_user = "sparkstreaming"
influxdb_password = "sparkstreaming-influxdb"
influxdb_name = "realtime"


def make_query(measurement_name):
    query = """select time, lastReceivedBatch_records from "%s" order by time desc limit 1""" % measurement_name
    return query


def get_influxdb_client(db):
    client = InfluxDBClient(host=influxdb_host, port=influxdb_port, username=influxdb_user, password=influxdb_password, database=db)
    return client


def get_app_and_sql():
    result = [
        ("曝光", "realtime-wutiao-user-showinpage", make_query("realtime-wutiao-user-showinpage"), "realtime"),
#        ("首页", "realtime-wutiao-homepage-rcmd", make_query("realtime-wutiao-homepage-rcmd"), "realtime"),
        ("行为日志入库", "realtime-wutiao-json-to-hive", make_query("realtime-wutiao-json-to-hive"), "realtime"),
        ("分币入库", "realtime-wutiao-moneydistribute-to-hive", make_query("realtime-wutiao-moneydistribute-to-hive"), "realtime"),
        ("领币入库", "realtime-wutiao-moneygain-to-hive", make_query("realtime-wutiao-moneygain-to-hive"), "realtime")
    ]
    return result


def channelByTime(alert_msg):
    formattedHour = datetime.now().strftime("%H")
    kingnetdc.send_phone(alert_msg, ['17717811376', '18930784151'])
    kingnetdc.send_wechat(alert_msg, 'zhouml@kingnet.com,zhoujiongyu@kingnet.com')


def excute_check():
    realtime_client = get_influxdb_client("realtime")
    mining_client = get_influxdb_client("mining")
    for name, app, sql, db in get_app_and_sql():
        result = -1
        if db == "realtime":
            result = realtime_client.query(sql)
        else:
            result = mining_client.query(sql)
        records = result.raw['series'][0]['values'][0][-1]
        if records <= 0:
            alert_msg = '%s 五条实时程序 [%s], 任务名 [%s] 最近一个批次接收到的记录为 [%d] 异常' % (datetime.now().strftime("%Y-%m-%d %H:%M:%S"), name, app, records)
            channelByTime(alert_msg)
        else:
            print('%s 五条实时程序 [%s], 任务名 [%s] 接收的记录为 [%d] 正常' % (datetime.now().strftime("%Y-%m-%d %H:%M:%S"), name, app, records))
    realtime_client.close()
    mining_client.close()


def main():
    print("*************** start check receive records ***************")
    excute_check()


if __name__ == "__main__":
    main()
