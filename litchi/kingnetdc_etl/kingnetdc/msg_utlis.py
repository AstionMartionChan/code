#!/usr/bin/python3.6
# -*- coding: UTF-8 -*-
import hashlib
import copy
import time
from urllib import request, parse
from .config_utils import custom_config as __config


__business_id = __config.get('wechat', 'business_id')
__wechat_pub_key = __config.get('wechat', 'wechat_pub_key')
__wechat_url = __config.get('wechat', 'wechat_url')
__phone_pub_key = __config.get('phone', 'phone_pub_key')
__phone_department = __config.get('phone', 'phone_department')
__phone_url = __config.get('phone', 'phone_url')


def send_wechat(msg, users, is_print=True):
    """
    微信报警api
    :param msg: str类型的数据
    :param users: 'ss@xxx.com,www@xxx.com,...
    :return: None
    """
    if '|' in users:
        users = users.replace('|', ',')

    msg = msg.lstrip(' ')  # 清理左边空格造成签名异常情况
    data = {
        'business_id': __business_id,
        'content': msg,
        'options': 'weixin',
        'receiver': users,
        'ts': int(time.time())
    }
    # 获取字典副本
    new_dict = copy.deepcopy(data)
    # 修改指定key的编码方式
    new_dict['content'] = parse.quote_plus(msg)
    new_dict['receiver'] = parse.quote_plus(data['receiver'])
    # 计算签名算法时各key的顺序
    sign_computer = 'business_id=%(business_id)s&content=%(content)s&options=%(options)s&receiver=%(receiver)s&ts=%(ts)s' % new_dict
    # 组合字符串
    megre_str = sign_computer + __wechat_pub_key
    # 获取签名
    data['sign'] = hashlib.md5(megre_str.encode('utf8')).hexdigest()
    # 发送请求
    f = request.urlopen(url=__wechat_url, data=parse.urlencode(data).encode('utf8'))
    # 获取返回结果
    ret = f.read()
    # 关闭读取
    f.close()
    if is_print:
        print(time.ctime(), ret)


def send_phone(msg, phone_li, is_print=True):
    """
    电话报警
    :param msg: 需要语音播报内容 str格式
    :param phone_li: ['132xxx','123xx','332xxx',...]
    :return: None
    """
    phones = ','.join(phone_li)
    k_str = (__phone_pub_key+phones).encode('utf8')
    sign = hashlib.md5(k_str).hexdigest()
    data = {
        'phones': phones,
        'content': msg,
        'depart': __phone_department,
        'sign': sign
    }
    f = request.urlopen(url=__phone_url, data=parse.urlencode(data).encode('utf8'))
    ret = f.read()
    f.close()
    if is_print:
        print(time.ctime(), ret)
