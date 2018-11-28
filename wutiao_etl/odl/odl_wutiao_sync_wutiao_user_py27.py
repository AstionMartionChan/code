# coding: utf-8

# **********************程序说明*********************************#
# *模块：odl
# *功能：mysql wutiao同步
# *作者：Leo
# *时间：2018-08-23
# *备注：mysql wutiao同步
# ***************************************************************#

import kylib
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_WUTIAO, DB_PARAMS_WUTIAO_HOSTS, WUTIAO_TAB_SYNC_INFO


def sync_data(mysqlhosts, db_params, outfile, mysql_query, load_data):
    for mysqlhost in mysqlhosts:
        db_params['host'] = mysqlhost
        try:
            kylib.select_into_file_bysql(outfile, db_params, mysql_query)
        except Exception as err:
            print err
            continue
        else:
            break
    try:
        kylib.exec_cmd('''source /etc/profile && hive -S -e "%s;" ''' % load_data)
        os.remove(outfile)
    except Exception as err:
        print err
    else:
        pass


def main():
    kdc = kylib.KDC
    args = dict()
    args['ds'] = kdc.workDate
    args['ds+1'] = kdc.dateAdd(1)
    args['today'] = kdc.getNowFormat()
    args['yesterday'] = kdc.dateSub(1,args['today'])
    # print args['today'], args['yesterday']

    kdc.debug = True
    args['filepath'] = os.getcwd()
    args['db_params'] = DB_PARAMS_WUTIAO
    args['mysqlhosts'] = DB_PARAMS_WUTIAO_HOSTS
    args['mysqltabs'] = WUTIAO_TAB_SYNC_INFO
    # print args

    for mysqltab in args['mysqltabs']:
        args['mysqldb'] = mysqltab['db']
        args['mysqltab'] = mysqltab['table']
        args['byfield'] = mysqltab['byfield']
        args['fields'] = mysqltab['fields']
        args['hivetab'] = 'wutiao.odl_wutiao_'+args['mysqltab']+'_sync'
        args['outfile'] = '%(filepath)s/%(mysqltab)s' % args
        if args['byfield'] == '':
            args['mysql_query'] = r''' SET NAMES utf8; select * from %(mysqldb)s.%(mysqltab)s ''' % args
            args['load_data'] = r''' load data local inpath '%(outfile)s' overwrite into table %(hivetab)s ''' % args
        else:
            # if ds equal to 2000-01-01, extract all data before today to hive
            if args['ds'] == '2000-01-01':
                if args['fields'] == '':
                    args['mysql_query'] = r''' SET NAMES utf8; select * from %(mysqldb)s.%(mysqltab)s where %(byfield)s<'%(today)s' ''' % args
                else:
                    args['mysql_query'] = r''' SET NAMES utf8; select %(fields)s from %(mysqldb)s.%(mysqltab)s where %(byfield)s<'%(today)s' ''' % args
                args['load_data'] = r''' load data local inpath '%(outfile)s' overwrite into table %(hivetab)s partition (ds='%(yesterday)s') ''' % args
                args['hive_sql'] = ''' alter table %(hivetab)s drop partition( ds >= '%(ds)s') ''' % args
                kdc.doHive(args['hive_sql'])
            else:
                if args['fields'] == '':
                    args['mysql_query'] = r''' SET NAMES utf8; select * from %(mysqldb)s.%(mysqltab)s where %(byfield)s>='%(ds)s' and %(byfield)s<'%(ds+1)s' ''' % args
                else:
                    args['mysql_query'] = r''' SET NAMES utf8; select %(fields)s from %(mysqldb)s.%(mysqltab)s where %(byfield)s>='%(ds)s' and %(byfield)s<'%(ds+1)s' ''' % args
                args['load_data'] = r''' load data local inpath '%(outfile)s' overwrite into table %(hivetab)s partition (ds='%(ds)s') ''' % args

        sync_data(args['mysqlhosts'], args['db_params'], args['outfile'], args['mysql_query'], args['load_data'])


if __name__ == '__main__':
    main()
