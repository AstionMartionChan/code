# coding: utf-8

#**********************程序说明*********************************#
#*模块: 监控
#*功能: 五条实时指标监控
#*作者: zhouml
#*时间: 2018-08-20
#*备注: 五条实时报表入库监控 -- 上次入库距离当前时间超过多少则报警
#***************************************************************#

import kingnetdc

from datetime import datetime
from datetime import timedelta

# 超过15分钟没有数据写入则报警
THRESHOLD_MINUTE = 15

# realtime database
DB_PARAMS = {
    'host': '172.27.2.72', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306
}


def build_query(table_name, lower_bound):
    query = '''
        select window from {} where fds >= '{}' and window >= '{}' limit 1
    '''.format(table_name, lower_bound.strftime("%Y-%m-%d"), lower_bound.strftime("%Y-%m-%d %H:%M:%S"))
    return query


def build_interact_query(table_name, lower_bound, metrics_name):
    query ='''
           select
                window
            from
                {}
            where fds >= '{}' and window >= '{}' and metrics_name = '{}' limit 1
        '''.format(table_name, lower_bound.strftime("%Y-%m-%d"), lower_bound.strftime("%Y-%m-%d %H:%M:%S"), metrics_name)
    return query


def get_table_and_sql(lower_bound):
    result = [
        ('设备', build_query('wutiao.realtime_wutiao_device_kpi', lower_bound)),
        ('用户', build_query('wutiao.realtime_wutiao_user_kpi', lower_bound)),
        ('提现', build_query('wutiao.realtime_wutiao_withdraw_kpi', lower_bound)),
        ('互动次数', build_interact_query('wutiao.realtime_wutiao_interact_kpi', lower_bound, 'time_cnt')),
        ('互动人数', build_interact_query('wutiao.realtime_wutiao_interact_kpi', lower_bound, 'user_cnt'))
    ]
    return result


def channelByTime(alert_msg):
    formattedHour = datetime.now().strftime("%H")
    if formattedHour >= '01' and formattedHour <= '09':
        kingnetdc.send_phone(alert_msg, ['17717811376', '18930784151'])
    else:
        kingnetdc.send_wechat(alert_msg, 'zhouml@kingnet.com,zhoujiongyu@kingnet.com')


def latest_window_check():
    expected_last_window = datetime.now() - timedelta(minutes=THRESHOLD_MINUTE)

    for (realtime_table, query) in get_table_and_sql(expected_last_window):
        total_results = kingnetdc.original_execsqlr(db_params=DB_PARAMS, sql=query)
        if not total_results:
            alert_msg = '{} 五条实时指标: {}在{}分钟内没有接收到新数据'.format(datetime.now().strftime("%Y-%m-%d %H:%M:%S"), realtime_table, str(THRESHOLD_MINUTE))
            channelByTime(alert_msg)
            print(alert_msg)
        else:
            print('{} 五条实时指标: {}正常'.format(datetime.now().strftime("%Y-%m-%d %H:%M:%S"), realtime_table))


def main():
    latest_window_check()


if __name__ == '__main__':
    main()


