# !/usr/bin/env python
# -*-coding: UTF-8 -*-
 
#**********************程序说明*********************************#
#*模块：IDL
#*功能：同步新用户到mysql
#*作者：zhoujiongyu
#*时间：2018-05-29
#*备注：同步当天的新用户增量同步到mysql
#***************************************************************#
import kingnetdc
import os
import sys
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

sql_day = '''
use wutiao;
INSERT overwrite TABLE {targettab} partition (ds='{ds}', ut='uid')
select 
ouid as ouid,
concat(cast(first_login_time * 1000 as string), ',', nvl(channel,'')) as info
from {sourcetab1}
where ds='{ds}' and ut='uid' and ouid is not null and first_login_time is not null
and from_unixtime(first_login_time, 'yyyy-MM-dd')='{ds}';

INSERT overwrite TABLE {targettab} partition (ds='{ds}', ut='did')
select
ouid as ouid,
first_openclient_time * 1000 as info
from {sourcetab1}
where ds='{ds}' and ut='did' and ouid is not null and first_openclient_time is not null
and from_unixtime(first_openclient_time, 'yyyy-MM-dd')='{ds}';
'''

delete='''echo "use wutiao; delete from {mysql_table} where from_unixtime(time, '%Y-%m-%d') ='{ds}'; " |mysql -h {db_ip} -u {db_user} -p{db_pwd} -P{db_port} '''
sync=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/{hive_table}/ds='{ds}'/ut='{ut}' --connect jdbc:mysql://{db_ip}:{db_port}/wutiao --username "{db_user}" --password '{db_pwd}' --table "{mysql_table}" --input-fields-terminated-by "\001" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    hive_table = 'idl_wutiao_newuser_daily_realtime'
    source_hive_table = 'idl_wutiao_user'
    user_table = 'realtime_new_user'
    device_table = 'realtime_new_device'
    # 执行hql
    sql = sql_day.format(ds=ds, targettab=hive_table, sourcetab1=source_hive_table)
    kdc.debug = True
    res = kdc.doHive(sql)

    if res != 0:
        raise Exception('%s insert execute failure!!' % hive_table)

    # 导出到mysql用户表
    user_res1 = os.system(delete.format(ds=ds, mysql_table=user_table
                                        , db_ip=DB_PARAMS['host']
                                        , db_port=DB_PARAMS['port']
                                        , db_user=DB_PARAMS['user']
                                        , db_pwd=DB_PARAMS['password']))
    user_res2 = os.system(sync.format(ds=ds, mysql_table=user_table, ut='uid', hive_table=hive_table
                                      , db_ip=DB_PARAMS['host']
                                      , db_port=DB_PARAMS['port']
                                      , db_user=DB_PARAMS['user']
                                      , db_pwd=DB_PARAMS['password']
                                      ))

    if (user_res1 or user_res2) != 0:
        print(user_res1, user_res2)
        raise Exception('%s delete&insert execute failure!!' % user_table)
    else:
        print(user_res1, user_res2)
        print('user complete')

    # 导出到mysql设备表
    device_res1 = os.system(delete.format(ds=ds, mysql_table=device_table
                                          , db_ip=DB_PARAMS['host']
                                          , db_port=DB_PARAMS['port']
                                          , db_user=DB_PARAMS['user']
                                          , db_pwd=DB_PARAMS['password']))
    device_res2 = os.system(sync.format(ds=ds, mysql_table=device_table, ut='did', hive_table=hive_table
                                        , db_ip=DB_PARAMS['host']
                                        , db_port=DB_PARAMS['port']
                                        , db_user=DB_PARAMS['user']
                                        , db_pwd=DB_PARAMS['password']))

    if (device_res1 or device_res2) != 0:
        print(device_res1, device_res2)
        raise Exception('%s delete&insert execute failure!!' % device_table)
    else:
        print(device_res1, device_res2)
        print('device complete')


if __name__ == '__main__':
    main()