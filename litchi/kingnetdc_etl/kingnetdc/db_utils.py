#!/usr/bin/python3.6
# -*- coding: UTF-8 -*-
import subprocess
import pymysql
import sys
import time
from .utils import exec_cmd as __exec_cmd
from .config_utils import custom_config as __config

try:
    from pyhive import presto
    from pyhive import hive
except ImportError:
    presto = None
    hive = None


# This method needs to been optimized.
def standardize_sql(sql):
    no_sql = [('##', '\n')]
    sql2 = ''
    for stem in no_sql:
        start = stem[0]
        end = stem[1]
        sql2 = ''

        x = 0
        for i in range(len(sql)):
            if i + 2 <= len(sql):
                if sql[i:i + 2] == start:
                    x = 1
                if sql[i] == end:
                    x = 0
            if x == 0:
                sql2 = sql2 + sql[i]
        sql = sql2

    sql3 = sql2.replace('\n', ' ')
    sql4 = sql3
    for i in range(len(sql3)):
        sql4 = sql4.replace('  ', ' ')
    sql5 = sql4.strip(';') + ';'
    return sql5


def gen_params_dict(host, user, pwd, port=3306, charset='utf8'):
    """
    生成数据库连接信息字典
    :return: dict()
    """
    params = {'host': host,
              'user': user,
              'password': pwd,
              'port': port,
              'charset': charset}
    return params


# This method executes one sql and fetches result.
def new_execsqlr(db_params, sql, is_print=True, is_format_sql=True):
    """
    use "mysql -e" executes sql and read return result . return list.
    :return: list
    """
    if is_format_sql:
        sql = standardize_sql(sql)
    cmd = '''mysql -h%s -u%s -p%s -P%s -e"%s" ''' % (db_params['host'],
                                                     db_params['user'],
                                                     db_params['password'],
                                                     db_params['port'],
                                                     sql)
    if is_print:
        print(sql)
    (status, result) = subprocess.getstatusoutput(cmd)
    if status:
        raise Exception(result)
    if result == '':
        return []
    else:
        return result.split('\n')


# This method execute sql in batches.
def execsql_manysql(db_params, sqls, is_print=False):
    """
    batch execution many sql, use python pymysql do. no return
    :param db_params :  dict: gen_params_dict return 
    :param is_print: print every sql
    :param sqls: [sql1,sql2,sql3,...]
    """
    try:
        conn = pymysql.connect(**db_params)
        cur = conn.cursor()
        for sql in sqls:
            if is_print:
                print(sql)
            cur.execute(sql)
    except Exception as e:
        conn.rollback()
        print('ERROR execsql manysql:', e, sql)
        sys.exit(1)
    finally:
        cur.close()
        conn.commit()
        conn.close()


# This method is old method from MySQLdb.
def original_execsqlr(db_params, sql, is_print=True, is_format_sql=True):
    """
    use python pymysql do. return tuple.
    :param db_params : dict: gen_params_dict return  
    :param is_print: print sql
    :param is_format_sql: format sql string to compression
    :return: tuple
    """
    try:
        conn = pymysql.connect(**db_params)
        cur = conn.cursor()
        if is_format_sql:
            sql = standardize_sql(sql)
        if is_print:
            print(sql)
        cur.execute(sql)
        result = cur.fetchall()
    except Exception as e:
        conn.rollback()
        print('ERROR original_execsqlr:', e, sql)
        sys.exit(1)
    finally:
        cur.close()
        conn.commit()
        conn.close()
    return result


def original_executemany(db_params, sql, args, is_print=True, is_format_sql=True):
    """
    use python MySQLdb do and execution many insert. no return
    :param db_params: dict: gen_params_dict return 
    :param args: [(),(),..] or ((),(),...) or ([],[],...)
    :param db_params : dict 
    :param is_print: print sql
    :param is_format_sql: format sql string to compression
    """
    try:
        conn = pymysql.connect(**db_params)
        cur = conn.cursor()
        if is_format_sql:
            sql = standardize_sql(sql)
        if is_print:
            print('%s' % db_params['host'], sql, 'args length:', len(args))
        cur.executemany(sql, args)
    except Exception as e:
        conn.rollback()
        print('ERROR original_executemany:', e, sql)
        sys.exit(1)
    finally:
        cur.close()
        conn.commit()
        conn.close()


def presto_execsqlr(sql, print_sql=False, host='', port=0):
    """
    execution pyhive.presto from presto select result . return tuple, if have result.
    :param sql: 
    :param print_sql: print sql
    :param host: presto server ip
    :param port: presto server port
    :return: tuple
    """
    if not presto:
        print('no had pyhive.presto ')
        raise Exception('no had pyhive.presto ')

    if not host:
        host = __config.get('presto', 'presto_ip')
        port = __config.getint('presto', 'presto_port')

    try:
        conn = presto.connect(host=host, port=port)
        cur = conn.cursor()
        if print_sql:
            print(sql)
        cur.execute(sql)
        result = cur.fetchall()
    except Exception as e:
        conn.rollback()
        print('ERROR presto_execsqlr:', str(e), sql)
        raise Exception(e)
    finally:
        cur.close()
        conn.commit()
        conn.close()
    return result


def hive_execsqlr(sql, print_sql=False, host='', port=0):
    """
    execution pyhive.hive from hive select result . return tuple, if have result.
    :param sql: hive syntax sql
    :param print_sql: print sql
    :param host: hive server ip
    :param port: hive server port
    :return: tuple
    """
    if not hive:
        print('no had pyhive.hive ')
        raise Exception('no had pyhive.hive ')

    if not host:
        host = __config.get('hive', 'hive_ip')
        port = __config.getint('hive', 'hive_port')

    try:
        conn = hive.connect(host=host, port=port)
        cur = conn.cursor()
        if print_sql:
            print(sql)
        cur.execute(sql)
        result = cur.fetchall()
    except Exception as e:
        conn.rollback()
        print('ERROR hive_execsqlr:', str(e), sql)
        raise Exception(e)
    finally:
        cur.close()
        conn.commit()
        conn.close()
    return result


def desc_table(table_name, db_params):
    result = new_execsqlr(db_params, 'desc %s' % table_name)[1:]
    colstr = ''
    for stem in result:
        colstr = colstr + ',' + stem.split('\t')[0]
    return colstr.strip(',')


def show_create_table(table_name, db_params, enc='utf8', only_column=False):
    result = new_execsqlr(db_params, 'set names %s;show create table %s;' % (enc, table_name))[1]
    table_info = result.split('(',1)[1].split(') ENGINE=')[0]
    if only_column:
        table_info = ''.join([l for l in table_info.split('\\n') if l.strip() != '' and l.strip()[0] == '`' ])
        table_info = table_info[:-1]
    return table_info.replace('`', '')


def get_table_struct(table_name, db_params):
    result = new_execsqlr(db_params, 'desc %s;' % table_name)[1:]
    colstr = ''
    for stem in result:
        colname = stem.split('\t')[0]
        coltype = stem.split('\t')[1]
        colstr = colstr + colname + ' ' + coltype + ','
    return colstr.strip(',')


def get_table_column_list(table_name, db_params):
    return [x.strip('\n').split('\t')[0] for x in new_execsqlr(db_params, 'desc %s;' % table_name)[1:]]


def create_table(db_params, target_table, table_struct, engine='InnoDB', is_drop=False):
    if is_drop:
        drop_sql = '''DROP TABLE IF EXISTS %s;''' % target_table
        new_execsqlr(db_params, drop_sql)
    sql = '''SET NAMES UTF8; CREATE TABLE IF NOT EXISTS %s(%s)
        ENGINE=%s DEFAULT CHARSET=utf8;
        ''' % (target_table, table_struct, engine)
    new_execsqlr(db_params, sql)


def load_info_from_file(db_params, file_path, target_table, sep='\t'):
    sql = '''LOAD DATA LOCAL INFILE '%s' INTO TABLE %s
            CHARACTER SET utf8
            FIELDS TERMINATED BY '%s'
            LINES TERMINATED BY '\\\\n';''' % (file_path, target_table, sep)
    new_execsqlr(db_params, sql)


def executemany_batches(db_params, sql, data_list, commit_num=80000, is_print=True, is_format_sql=False):
    data_len = len(data_list)
    for i in range(0, data_len, commit_num):
        start_time = time.time()
        original_executemany(db_params, sql, data_list[i: i+commit_num], is_print, is_format_sql)
        print('executed datas %s/%s, time:%s' % (i+commit_num, data_len, time.time() - start_time))


def select_into_file_by_sql(outfile, db_params, sql):
    cmd = """mysql -h%s -u%s -p%s -P%i -N -e"%s" > %s""" % (
        db_params['host'], db_params['user'], db_params['password'], db_params['port'], sql, outfile)
    __exec_cmd(cmd)


def select_into_file(source_table, outfile, db_params, columns='*'):
    sql = "SET NAMES utf8; SELECT %s FROM %s" % (columns, source_table)
    select_into_file_by_sql(outfile, db_params, sql)


def create_db(db_params, db_name, is_drop=False):
    if is_drop:
        drop_sql = '''DROP DATABASE IF EXISTS %s;''' % db_name
        new_execsqlr(db_params, drop_sql)
    sql = '''
            CREATE DATABASE IF NOT EXISTS %s
            CHARACTER SET 'utf8'
            COLLATE 'utf8_general_ci';''' % db_name
    new_execsqlr(db_params, sql)


def do_hql_exec(hql, ip_str=''):
    """
    return read result by call 'hive -e' command 
    :param hql: hive sql string
    :param ip_str: ip str
    :return: str
    """
    sql = hql.replace('\n', ' ')
    if ip_str:
        execs_hive_sql = 'ssh %s \" source /etc/profile && hive -S -e\\"%s;\\" \" ' % (ip_str, sql)
    else:
        execs_hive_sql = '''source /etc/profile && hive -S -e "%s;" ''' % sql
    data_str = __exec_cmd(execs_hive_sql)
    return data_str
