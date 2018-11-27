#!/usr/bin/env python
# -*- coding: UTF-8 -*-
import subprocess
import re
from time import sleep
from string import printable
from .constant import WGET_CONN_TRY, WGET_TIMEOUT, WGET_WAITRETRY, WGET_LIMIT_RATE, KINGNET_MAIL
from .config_utils import custom_config as __config


def exec_cmd(cmd, is_print=True):
    """
    call linux command by python
    :param cmd: str(); linux command
    :param is_print: display command
    :return: str(); output or Exception
    """
    if is_print:
        print('Starting to exec: %s' % cmd)
    (status, output) = subprocess.getstatusoutput(cmd)
    if status:
        raise Exception(output)
    return output


def sql_to_hive(db_params, sql, hive_table, partiton_key=None, partiton_val=None, split_by=None, m=None):
    cmd = '''{sqoop_cmd} import --connect jdbc:mysql://{host}:{port}/ --username {username} --password {password}
            --query '{sql}' {split_by}
            --hive-import --hive-overwrite --target-dir /tmp/sqoop/tmp/{hive_table} --hive-table {hive_table}
            {partiton_key} {partiton_val}
            '''.format(host=db_params['host'], port=db_params['port'], username=db_params['user'],
                       password=db_params['password'], sql=sql,
                       split_by='--split-by {split_by} {m}'.format(split_by=split_by, m='--m {0}'.format(
                           m) if m else '') if split_by else '--m 1', hive_table=hive_table,
                       partiton_key='--hive-partition-key {0}'.format(partiton_key) if partiton_key else '',
                       partiton_val='--hive-partition-value \'{0}\''.format(partiton_val) if partiton_val else '',
                       sqoop_cmd=__config['sqoop']['command_sqoop'])
    exec_cmd(cmd.replace('\n', ''))


def table_to_hive(db_params, db, src_table, hive_table, partiton_key=None, partiton_val=None, split_by=None, m=None,
                  where=None):
    cmd = '''{sqoop_cmd} import --connect jdbc:mysql://{host}:{port}/{db} --username {username} --password {password}
            --table {src_table} {split_by} {where}
            --hive-import --hive-overwrite --target-dir /tmp/sqoop/tmp/{hive_table} --hive-table {hive_table}
            {partiton_key} {partiton_val}
            '''.format(host=db_params['host'], port=db_params['port'], username=db_params['user'],
                       password=db_params['password'], db=db, src_table=src_table,
                       split_by='--split-by {split_by} {m}'.format(split_by=split_by, m='--m {0}'.format(
                           m) if m else '') if split_by else '--m 1',
                       where='--where \'{0}\''.format(where) if where else '',
                       hive_table=hive_table,
                       partiton_key='--hive-partition-key {0}'.format(partiton_key) if partiton_key else '',
                       partiton_val='--hive-partition-value \'{0}\''.format(partiton_val) if partiton_val else '',
                       sqoop_cmd=__config['sqoop']['command_sqoop'])
    exec_cmd(cmd.replace('\n', ''))


def output_data2file(file_path, data_list):
    with open(file_path, 'w') as f:
        f.writelines(data_list)


def mk_dir(dir_path):
    cmd = "mkdir -p %s" % dir_path
    exec_cmd(cmd)


def scp_src2tgt(source, target):
    cmd = 'scp -r %s %s' % (source, target)
    exec_cmd(cmd)


def wget_file(local_dir, source_file, conn_try=WGET_CONN_TRY, timeout=WGET_TIMEOUT,
              waitretry=WGET_WAITRETRY, limit_rate=WGET_LIMIT_RATE, outfile=None):
    cmd = "cd %s && wget -c -t%s --timeout=%s --waitretry=%s --limit-rate=%sk %s" % (
        local_dir, conn_try, timeout, waitretry, limit_rate, source_file)
    if outfile:
        cmd += ' -O %s' % outfile
    for x in range(5):
        try:
            exec_cmd(cmd)
            break
        except Exception as e:
            print('wget_file err:', e)
            if x == 4:
                raise e
            sleep(5)


def cp_file(source_path, target_path):
    cmd = "cp -f %s %s" % (source_path, target_path)
    exec_cmd(cmd)


def merge_file(source_path, target_path):
    cmd = 'cat %s > %s' % (source_path, target_path)
    exec_cmd(cmd)


def has_chinese_charactar(content):
    """
    find chinese charactar in string
    :param content: str()
    :return: True or False
    """
    zh_pattern = re.compile(u'[\u4e00-\u9fa5]+')
    match = zh_pattern.search(content)
    return True if match else False


def filter_non_printable(raw_str):
    return filter(lambda x: x in printable, raw_str.replace('\n', ''))


def ip2int(ip_str):
    return sum([256**j*int(i) for j, i in enumerate(ip_str.split('.')[::-1])])


def int2ip(ip_int):
    return '.'.join([str(int(ip_int/(256**i) % 256)) for i in range(3, -1, -1)])


def just_ip_str(ip_str):
    ip_match = re.compile(r'(?<![\\.\d])(?:\d{1,3}\.){3}\d{1,3}(?![\\.\d])')
    if ip_str not in ('', ' '):
        client_ip = ip_match.findall(ip_str)
        just = False if client_ip == [] else True
    else:
        just = True
    return just


def send_mail(mes, title, users, attach_file=''):
    add_file_str = ''
    if attach_file:
        add_file_str = ' -a %s ' % attach_file
    mail_cmd = "echo '%s' | mail -s '%s' %s -r %s %s " % (mes, title, add_file_str, KINGNET_MAIL, users)
    exec_cmd(mail_cmd)


def get_hadoop_job_info_by_appid(appid):
    """ see -
    Hadoop YARN - Introduction to the web services REST API’s
    http://hadoop.apache.org/docs/stable/hadoop-yarn/hadoop-yarn-site/WebServicesIntro.html
    :param appid: the application id, string. e.g. 'application_1528101679469_0179'
    :return: dict -  similar to information about the job submitted along with the application id
        e.g. {'code': 200, 'data': {...}, 'msg': ''}
        'code' == 200 : The request is successful. 'data' has dict.  'msg' is None.
        'code' == 999 : The python code error. 'data' is {}. 'msg' has error message.
        'code' != 200 and != 999: see 'msg',  is http error. 'data' is {}.
    """
    from pycurl import Curl, URL, HTTPHEADER, WRITEFUNCTION, FOLLOWLOCATION, MAXREDIRS, CONNECTTIMEOUT
    from io import BytesIO
    from json import loads

    try:
        all_urls = __config.options('hadoop')
        url_li = [__config.get('hadoop', x) + appid for x in all_urls if 'rest_url' in x]
        if not url_li:
            raise Exception('config file not has [hadoop] section or [rest_url] options')
    except Exception as e:
        return {
            'data': {},
            'msg': str(e),
            'code': 9999
        }

    def single_request(url):
        result_json = {
            'data': {},
            'msg': ''
        }
        try:
            curl_instance = Curl()
            byte_steam = BytesIO()
            curl_instance.setopt(URL, url)
            curl_instance.setopt(HTTPHEADER, ["Accept: application/json"])
            curl_instance.setopt(WRITEFUNCTION, byte_steam.write)  # call-back
            curl_instance.setopt(FOLLOWLOCATION, 1)
            curl_instance.setopt(MAXREDIRS, 5)  # redirect
            curl_instance.setopt(CONNECTTIMEOUT, 60)  # Connection timeout
            curl_instance.perform()  # running
            status = curl_instance.getinfo(curl_instance.HTTP_CODE)
            result_json['code'] = status
            if status == 200:
                html_s = byte_steam.getvalue()
                result_json['data'] = loads(html_s)['app']
            else:
                result_json['msg'] = 'http error: %s' % result_json['code']
        except Exception as err_msg:
            result_json['msg'] = str(err_msg).replace('\n', '')
            result_json['code'] = 9999
        return result_json

    # Try all configured urls
    has_url_num = len(url_li)
    for index, i in enumerate(url_li):
        info_dict = single_request(i)
        if info_dict['code'] != 200 and index != has_url_num:
            continue
        return info_dict
