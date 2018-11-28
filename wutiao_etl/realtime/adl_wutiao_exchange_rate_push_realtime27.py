# !/usr/bin/env python
# -*-coding: UTF-8 -*-

#**********************程序说明*********************************#
#*模块：ADL
#*功能：离线汇率计算
#*作者：zhouml
#*时间：2018-06-12
#*备注：根据累计注册用户, 领币, 提现等计算汇率
#***************************************************************#

import kylib
import kingnet
import time
import urllib2
import math
import hashlib
import json
from datetime import datetime
from datetime import timedelta
import os
import sys

WB_IN_MINUTE = 4756

WB_IN_DAY = WB_IN_MINUTE * 60 * 24

ONLINE_DATE = datetime(2018, 7, 27)

DB_PARAMS = {
    'host': '172.17.2.91', 'user': 'root1',
    'password': 'yunying.kingnet', 'port': 3306
}

OUTPUT_DB_PARAMS = {
    'host': '172.17.2.91', 'user': 'root1',
    'password': 'yunying.kingnet', 'port': 3306
}

SALT = 'AcVb%(dX98!!#04m~'

current_date = lambda: datetime.now().strftime("%Y-%m-%d")

current_date_hour = lambda: datetime.now().strftime("%Y-%m-%d-%H")

current_date_millis = lambda: datetime.now().strftime("%Y-%m-%d %H:%M:%S")

current_millis = lambda: int(round(time.time() * 1000))


def price_by_day(ds):
    """
    :param ds:
    :return:
    python2 整数默认地板除法

    1 - 0.8 ^ ((T + 19) / 20)
    /
    1 - 0.8

    上线第一天 T为1
    """
    gap_day = (ds - ONLINE_DATE).days + 1
    return (1 - math.pow(0.8, (gap_day + float(19)) / 20)) / (1 - 0.8)


def daily_remaining_money(ds, register, withdraw_rmb):
    """
    :param ds:
    :param register: 每日新增注册用户
    :param withdraw_rmb: 每日总提现金额
    :return: 当天剩余钱
    """
    return register * price_by_day(ds) - withdraw_rmb


def daily_remaining_coin(money_gain, withdraw_coin):
    """
    :param money_gain: 每日总领币
    :param withdraw_coin: 每日总提现币
    :return: 当天剩余币
    """
    return money_gain - withdraw_coin


def calculate_exchange_rate(register, price, remaining_rmb, remaining_coin):
    exchange_rate = (register * price + remaining_rmb) / (remaining_coin + float(WB_IN_DAY))
    return exchange_rate


def save_exchange_rate_sql(ds, exchange_rate, remaining_rmb, remaining_coin):
    query = '''
        insert into wutiao.idl_wutiao_exchange_rate (ds, exchange_rate, remaining_rmb, remaining_coin) values ('{}', {}, {}, {})
        on duplicate key update exchange_rate = {}, remaining_rmb = {}, remaining_coin = {}
    '''.format(ds, exchange_rate, remaining_rmb, remaining_coin, exchange_rate, remaining_rmb, remaining_coin)
    return query


def send_result(host, formatted_rate):
    current_seconds = current_millis() / 1000
    hl = hashlib.md5()
    combined_key = '|'.join((SALT, str(current_seconds), formatted_rate))
    hl.update(combined_key.encode(encoding='utf-8'))
    sign = hl.hexdigest()

    url = '''http://{}/api/rate/report?date={}&rate={}&sign={}'''.format(host, current_seconds, formatted_rate, sign)

    print('url: ' + url)

    response = urllib2.urlopen(url).read()

    jobj = json.loads(response.decode("utf8"))

    if int(jobj['code']) == 200:
        print 'Push exchange rate {} at {}'.format(formatted_rate, current_date_millis())
    else:
        raise Exception('Failed to push exchange rate {} at {}'.format(formatted_rate, current_date_millis()))


def build_query(ds):
    query = '''
        select
            fds, total_register, total_withdraw_rmb, total_withdraw_coin, total_money_gain
        from
            wutiao.adl_wutiao_exchange_rate_realtime
        where fds <= '{}' order by fds desc
    '''.format(ds)
    return query


def get_idl_exchange_rate(dt):
    LK = kylib.Iblink(DB_PARAMS)

    calculation_ds = dt.strftime("%Y-%m-%d")
    total_results = LK.original_execsqlr(build_query(calculation_ds))

    if total_results:
        # n - 1天的注册用户
        n_minus_1_register = total_results[0][1]

        daily_remainings = []

        for total_result in total_results:
            ds, total_register, total_withdraw_rmb, total_withdraw_coin, total_money_gain = total_result
            remaining_money = daily_remaining_money(datetime(ds.year, ds.month, ds.day), total_register, total_withdraw_rmb)
            remaining_coin = daily_remaining_coin(total_money_gain, total_withdraw_coin)

            daily_remainings.append((remaining_money, remaining_coin))

        return n_minus_1_register, daily_remainings
    else:
        raise Exception('ERROR : wutiao.adl_wutiao_exchange_rate_realtime is empty')


def date_parse(datestr, format="%Y-%m-%d"):
    return datetime.strptime(datestr, format)


def main(argv):
    # 默认计算昨天的汇率, 反之则计算传递的日期对应的汇率
    if (len(argv) > 1):
        calculation_datetime = date_parse(argv[1])
    else:
        calculation_datetime = (datetime.today() - timedelta(days=1))

    print('calculation datetime ' + str(calculation_datetime))

    kdc = kingnet.kdc()
    host = 'test.www.wutiao.com'

    (n_minus_1_register, daily_remainings) = get_idl_exchange_rate(calculation_datetime)

    total_remaining_money, total_remaining_coin = reduce(lambda x, y: (x[0] + y[0], x[1] + y[1]), daily_remainings)

    exchange_rate = calculate_exchange_rate(n_minus_1_register, price_by_day(calculation_datetime), total_remaining_money, total_remaining_coin)

    formatted_exchange_rate = "{:0.18f}".format(exchange_rate)

    LK = kylib.Iblink(OUTPUT_DB_PARAMS)
    LK.original_execsqlr(
        save_exchange_rate_sql(calculation_datetime.strftime("%Y-%m-%d"), formatted_exchange_rate, total_remaining_money, total_remaining_coin)
    )

    send_result(host, formatted_exchange_rate)

    success_mark = '/usr/bin/hadoop fs -touchz /user/hive/warehouse/check_point/{}_{}'.format(current_date_hour(), os.path.basename(__file__))
    kdc.doCommand(success_mark)

if __name__ == '__main__':
    main(sys.argv)
