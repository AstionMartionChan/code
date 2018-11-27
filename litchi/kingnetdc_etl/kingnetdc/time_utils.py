#!/usr/bin/python3.6
# -*- coding: UTF-8 -*-

import datetime
import time
from .constant import DATE_FORMAT_STR as __DATE_FORMAT_STR, DATE_TIME_FORMAT_STR as __DATE_TIME_FORMAT_STR
import signal
from dateutil.relativedelta import relativedelta
from .msg_utlis import send_phone, send_wechat


def get_now_format(date_format=__DATE_FORMAT_STR):
    return get_time_format(time.time(), date_format)


# 当前的时间差
def date_sub_by_date(day_num, date=None):
    if not date:
        date = get_time_format()
    date = str(date).strip("'")
    end_time = to_timestamp(date, __DATE_FORMAT_STR)
    ret_time = end_time - int(day_num) * 24 * 3600
    return get_time_format(ret_time)


def date_add_by_date(day_num, date=None):
    if not date:
        date = get_time_format()
    date = str(date).strip("'")
    end_time = to_timestamp(date, __DATE_FORMAT_STR)
    ret_time = end_time + int(day_num) * 24 * 3600
    return get_time_format(ret_time)


# 格式化时间
def get_time_format(time_day=0, date_format=__DATE_FORMAT_STR):
    if not time_day:
        time_day = time.time() - 24 * 3600
    return time.strftime(date_format, time.localtime(float(time_day)))


def to_timestamp(date_str, format_str=__DATE_TIME_FORMAT_STR):
    """
    Turn second time stamp date string.
    :param date_str: 
    :param format_str: 
    :return: int(). length for 10 .
    """
    return int(time.mktime(time.strptime(date_str, format_str)))


def sleep(seconds=5):
    time.sleep(seconds)


def get_current_time():
    """ Return the current time in seconds since the Epoch
    :return: float()
    """
    return time.time()


def get_date_str_by_type(date_type, date_str, interval, date_format=__DATE_TIME_FORMAT_STR):
    """ Date calculation.
    :param date_type: days|seconds|microseconds|milliseconds|minutes|hours|weeks
    :param date_str: raw date_type str to parse
    :param interval: date_type span to generate new date_type
    :param date_format: fmt to parse date_type
    :return: str()
    """
    date_time = datetime.datetime.strptime(date_str, date_format)
    new_date_time = date_time + eval('datetime.timedelta(%s=interval)' % date_type)
    return new_date_time.strftime(date_format)


def get_date_str_by_days(day_s, days_i, date_format=__DATE_FORMAT_STR):
    """
    According to the date of the specified string and interval number to obtain corresponding date string.
    :param day_s: date string
    :param days_i: int(), Number of days. Positive growth and negative to reduce
    :param date_format: date format. default: Y-m-d
    :return: date string.
    """
    return get_date_str_by_type('days', day_s, days_i, date_format)


def get_diff_day_num(day_1, day_2, format_str=__DATE_FORMAT_STR):
    """
    Calculate the interval between two date specified number of days.
    :param day_1: date string. 
    :param day_2: date string.
    :param format_str: date format. default: Y-m-d
    :return: int(). Number of days.
    """
    d1 = datetime.datetime.strptime(day_1, format_str)
    d2 = datetime.datetime.strptime(day_2, format_str)
    return abs((d1 - d2).days)


def get_someday(day_num, format_str=__DATE_FORMAT_STR):
    day = datetime.datetime.now() - datetime.timedelta(days=day_num)
    return day.strftime(format_str)


# 包括传入的date_str
def get_last_n_days(date_str, n, format_str=__DATE_FORMAT_STR):
    """
    return list by date string. 
    :param date_str: date string.
    :param n: int(), Number of days
    :param format_str: date format. default: Y-m-d
    :return: [T, T-1,..., T-n]
    """
    return [get_date_str_by_days(date_str, -i, format_str) for i in range(n)]


def get_weekday(date_str, fmt=__DATE_FORMAT_STR):
    """
    weekday 0(Monday) to 6(Sunday)
    """
    dt = datetime.datetime.strptime(date_str, fmt)
    return dt.weekday()


def find_latest_weekday(input_date, fmt, weekday_needed):
    """
    weekday_needed: from 0(Monday) to 6(Sunday)
    """
    dt = datetime.datetime.strptime(input_date, fmt)
    dt_delta = datetime.timedelta(days=1)
    while dt.weekday() != weekday_needed:
        dt = dt - dt_delta
    return datetime.datetime.strftime(dt, fmt)


def get_week_tuple(date_str, format_str=__DATE_FORMAT_STR):
    """
    获取第几周及星期天数
    :param date_str: 日期字符串
    :param format_str: 日期字符串格式
    :return: tuple (年, 一年的第几周, 星期几[0-6])
    """
    dt = datetime.datetime.strptime(date_str, format_str)
    return dt.isocalendar()


def get_day_list(start_sd, end_sd):
    """
    返回2个日期内的所有日期字符串的列表格式,包含开始日期和结束日期
    :param start_sd: 开始日期 Y-m-d
    :param end_sd: 结束日期 Y-m-d
    :return: list() [start_sd, start_sd+1, ..., end_sd-1, end_sd] 
            例如： ['2018-01-01', '2018-01-02', '2018-01-03',...]
    """
    sy, sm, sd = start_sd.split('-')
    ey, em, ed = end_sd.split('-')
    d1 = datetime.date(int(sy), int(sm), int(sd))
    d2 = datetime.date(int(ey), int(em), int(ed))
    date_diff = d2 - d1
    n = abs(date_diff.days) + 1
    return ['%s' % (d1 + datetime.timedelta(i)) for i in range(n)]


def get_last_month_same_date(date_str, num, format_str=__DATE_FORMAT_STR):
    """
    返回间隔n月的相同日期. e.g: 2018-04-30 的2个月前的相同天是 2018-02-28 .
    :param date_str: 开始日期. 字符串格式, 默认 Y-m-d 格式
    :param num: 间隔长度, int()
    :param format_str: 日期格式, 默认 Y-m-d 格式
    :return: 日期字符串, xxxx-xx-xx 格式
    """
    d = datetime.datetime.strptime(date_str, format_str)
    return (d - relativedelta(months=num)).strftime(format_str)


def alarm_clock_call(interval, msg, call_type, users, is_raise=False):
    """
    By setting the time to monitor the function time, overtime,
        send tailored content to the designated contacts
    :param interval: int(), Time interval, the unit of seconds
    :param msg: str(), Inform the content
    :param call_type: wechat (Through WeChat notice) or phone (Through the phone call)
    :param users: list(), When for micro letter alarm. e.g: ['xxx@kingnet.com', 'xx@xx.xx',...] The mail list;
           When to call the warning. e.g: ['11323994','132321445',...] cell phone number list
    :param is_raise: True or False. After a timeout trigger the exception
    :return: func()
    """
    def wraps(func):
        def handler(*args):
            if call_type == 'wechat':
                send_wechat(msg, ','.join(users), is_print=False)
            elif call_type == 'phone':
                send_phone(msg, users, is_print=False)
            if is_raise:
                raise TimeoutError(msg)

        def deco(*args, **kwargs):
            signal.signal(signal.SIGALRM, handler)
            signal.alarm(interval)
            res = func(*args, **kwargs)
            signal.alarm(0)
            return res
        return deco
    return wraps
