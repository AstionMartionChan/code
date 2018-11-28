# coding: utf-8

#**********************程序说明*********************************#
#*模块: 监控
#*功能: 五条实时指标监控
#*作者: zhouml
#*时间: 2018-08-31
#*备注: 五条实时报表入库监控 -- 设备和用户活跃,新增,去新涨幅异常(5min)
#***************************************************************#

import kingnetdc

from datetime import datetime
from datetime import timedelta
from functools import reduce

FLUCTUATION_PERC = 3

# realtime database
DB_PARAMS = {
    'host': '172.27.2.72', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306
}


def build_query(table_name):
    query = '''
        select active, new, old from {} where duration = '300000' and fds = '{}' and channel = 'allchannel' and appver = 'allappver' and os = 'allos' order by window desc limit 2
    '''.format(table_name, datetime.now().strftime("%Y-%m-%d"))
    return query


def get_table_and_sql():
    result = [
        ('设备', build_query('wutiao.realtime_wutiao_device_kpi')),
        ('用户', build_query('wutiao.realtime_wutiao_user_kpi'))
    ]
    return result


def channel_by_time(alert_msg):
    formattedHour = datetime.now().strftime("%H")
    if formattedHour >= '00' and formattedHour <= '09':
        kingnetdc.send_phone(alert_msg, ['17717811376', '18930784151'])
    else:
        kingnetdc.send_wechat(alert_msg, 'zhouml@kingnet.com,zhoujiongyu@kingnet.com')


def active_new_old_check():
    # 活跃 | 新增 | 去新 任意一个涨跌幅度超过阈值
    for (realtime_table, query) in get_table_and_sql():
        total_results = kingnetdc.original_execsqlr(db_params=DB_PARAMS, sql=query)

        active_new_old = []

        if len(total_results) == 2:
            for total_result in total_results:
                active_new_old.append(total_result)

            (active_diff, new_diff, old_diff) = reduce(lambda x, y: (abs((x[0] - y[0])) / y[0], abs((x[1] - y[1])) / y[1], abs((x[2] - y[2])) / y[2]), active_new_old)

            if active_diff >= FLUCTUATION_PERC or new_diff >= FLUCTUATION_PERC or old_diff >= FLUCTUATION_PERC:
                alert_msg = "{} 五条实时指标: 活跃或新增或去新涨跌幅异常".format(datetime.now().strftime("%Y-%m-%d %H:%M:%S"), realtime_table)
                print(active_diff, new_diff, old_diff)
                channel_by_time(alert_msg)
            else:
                alert_msg = "{} 五条实时指标: 活跃或新增或去新涨跌幅正常".format(datetime.now().strftime("%Y-%m-%d %H:%M:%S"), realtime_table)
                print(alert_msg)
        else:
            print('Nothing to do !!!')


def main():
    active_new_old_check()


if __name__ == '__main__':
    main()
