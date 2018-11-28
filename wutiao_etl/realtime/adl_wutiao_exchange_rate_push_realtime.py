# !/usr/bin/env python
# -*-coding: UTF-8 -*-

#**********************程序说明*********************************#
#*模块：ADL
#*功能：离线汇率计算
#*作者：zhouml
#*时间：2018-06-12
#*备注：根据累计注册用户, 领币, 提现等计算汇率
#***************************************************************#

import kingnetdc
import time
import math
import hashlib
import json
from urllib import request, parse
from datetime import datetime
from datetime import timedelta
import os
from functools import reduce

WB_IN_MINUTE = 4756

WB_IN_DAY = WB_IN_MINUTE * 60 * 24

DAY_IN_MONTH = 30

ONLINE_DATE = datetime(2018, 8, 29)

STARTING_PRICE = 1

# etl database
DB_PARAMS = {
    'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306
}

# realtime database
OUTPUT_DB_PARAMS = {
    'host': '172.27.2.72', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306
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
    return (1 - math.pow(0.8, (gap_day + 19) / 20)) / (1 - 0.8)


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


def calculate_exchange_rate(yesterday_register, yesterday_price, remaining_rmb, remaining_coin):
    exchange_rate = (yesterday_register * yesterday_price + remaining_rmb) / (remaining_coin + float(WB_IN_DAY))
    return exchange_rate


def send_result(host, formatted_exchange_rate):
    current_seconds = int(current_millis() / 1000)
    hl = hashlib.md5()
    combined_key = '|'.join((SALT, str(current_seconds), formatted_exchange_rate))
    hl.update(combined_key.encode(encoding='utf-8'))
    sign = hl.hexdigest()
    url = '''http://{}/api/rate/report?date={}&rate={}&sign={}'''.format(host, current_seconds, formatted_exchange_rate, sign)
    print('url: ' + url)
    response = request.urlopen(url=url).read()
    jobj = json.loads(response.decode("utf8"))
    if int(jobj['code']) == 200:
        print('Push exchange rate {} at {}'.format(formatted_exchange_rate, current_date_millis()))
    else:
        kingnetdc.send_phone('五条汇率推送失败', ['17717811376'])
        raise Exception('Failed to push exchange rate {} at {}'.format(formatted_exchange_rate, current_date_millis()))


def build_query(upper_bound):
    query = '''
        select
            fds, total_register, total_withdraw_rmb, total_withdraw_coin, total_money_gain
        from
            wutiao.adl_wutiao_exchange_rate_realtime
        where fds <= '{}' and fds >= '{}' order by fds desc
    '''.format(upper_bound, ONLINE_DATE.strftime("%Y-%m-%d"))
    return query


def save_exchange_rate_sql(ds, exchange_rate, remaining_rmb, remaining_coin):
    """
    :param ds:
    :param exchange_rate:
    :param remaining_rmb:
    :param remaining_coin:
    :return: 结果存储到上海的实时库中
    """
    query = '''
        insert into wutiao.idl_wutiao_exchange_rate (ds, exchange_rate, remaining_rmb, remaining_coin) values ('{}', {}, {}, {})
        on duplicate key update exchange_rate = {}, remaining_rmb = {}, remaining_coin = {}
    '''.format(ds, exchange_rate, remaining_rmb, remaining_coin, exchange_rate, remaining_rmb, remaining_coin)
    return query


def get_idl_exchange_rate(dt):
    calculation_ds = dt.strftime("%Y-%m-%d")
    total_results = kingnetdc.original_execsqlr(db_params=DB_PARAMS, sql=build_query(calculation_ds))

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


def main():
    calculation_datetime = (datetime.today() - timedelta(days=1))

    print('calculation datetime ' + str(calculation_datetime))

    host = 'www.wutiao.com'

    (n_minus_1_register, daily_remainings) = get_idl_exchange_rate(calculation_datetime)

    total_remaining_money, total_remaining_coin = reduce(lambda x, y: (x[0] + y[0], x[1] + y[1]), daily_remainings)

    exchange_rate = calculate_exchange_rate(n_minus_1_register, price_by_day(calculation_datetime), total_remaining_money, total_remaining_coin)

    formatted_exchange_rate = "{:0.18f}".format(exchange_rate)

    kingnetdc.original_execsqlr(
        db_params=OUTPUT_DB_PARAMS,
        sql=save_exchange_rate_sql(calculation_datetime.strftime("%Y-%m-%d"), formatted_exchange_rate, total_remaining_money, total_remaining_coin)
    )

    send_result(host, formatted_exchange_rate)

    success_mark = '/usr/bin/hadoop fs -touchz /user/hive/warehouse/check_point/{}_{}'.format(kingnetdc.kdc.workDate, os.path.basename(__file__))
    kingnetdc.kdc.doCommand(success_mark)

if __name__ == '__main__':
    main()
