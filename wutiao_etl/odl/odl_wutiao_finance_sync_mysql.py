# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: odl_wutiao_finance_sync_mysql -> ODL
# *功能: 区块链-五条项目-财务需求-提现及用户编号mysql表快照同步
# *作者: sunyu
# *时间: 2018-07-17
# *备注:
# ***************************************************************#

import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TX


def main(begin_date, end_date):
    kdc = kingnetdc.kdc
    args = {
        'run_date': kdc.workDate,
        'month_begin': begin_date,
        'month_end': end_date,
    }

    if not (args['month_begin'] and args['month_end']):
        args['month_begin'] = args['run_date'][:-2] + '01'
        args['month_end'] = args['run_date']

    account_sql = '''select id, address, create_time, uid, unix_timestamp(create_time) as ts
        from kyc_db_sendcoin.account
        where create_time between '%(month_begin)s 00:00:00' and '%(month_end)s 23:59:59'
    '''
    tx_record_sql = ''' select id, type, tx_hash, from_address, to_address, update_time, state, create_time, amount, nonce, push_state, push_times, push_time, unix_timestamp(create_time) as ts
        from kyc_db_sendcoin.tx_record
        where create_time between '%(month_begin)s 00:00:00' and '%(month_end)s 23:59:59'
    '''

    tasks = [
        ['account', 'wutiao.odl_wutiao_finance_account', account_sql],
        ['tx_record', 'wutiao.odl_wutiao_finance_tx_record', tx_record_sql],
    ]

    for tag, hive_tb, task_sql in tasks:
        out_file = '/'.join(os.path.abspath(__file__).split('/')[:-1]) + '/' + tag + '_' + args['run_date'] + '.txt'
        sql = task_sql % args
        print(sql)
        kingnetdc.select_into_file_by_sql(out_file, DB_PARAMS_TX, sql)

        args['file'] = out_file
        args['hive_tb'] = hive_tb
        hql = '''
        set hive.merge.smallfiles.avgsize=256000000;
        set hive.merge.size.per.task=256000000;
        set hive.merge.mapredfiles=true;
        set hive.merge.tezfiles=true;
        set mapreduce.map.memory.mb=4000;
        set mapreduce.reduce.memory.mb=4000;
        load data local inpath '%(file)s' OVERWRITE into table %(hive_tb)s partition(ds='%(month_begin)s')
        ''' % args
        print(hql)
        kdc.doHive(hql)

        rm_cmd = '''rm -rf %s ''' % out_file
        print(rm_cmd)
        kingnetdc.exec_cmd(rm_cmd)


if __name__ == '__main__':
    if len(sys.argv) == 3:
        month_begin = sys.argv[1]
        month_end = sys.argv[2]
    else:
        month_begin = None
        month_end = None
    main(month_begin, month_end)
