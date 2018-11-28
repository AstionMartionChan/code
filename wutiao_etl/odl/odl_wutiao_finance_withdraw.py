# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: odl_wutiao_finance_withdraw -> ODL
# *功能: 区块链-五条项目-财务需求-提现记录快照备份
# *作者: sunyu
# *时间: 2018-07-17
# *备注:
# ***************************************************************#

import kingnetdc
import sys


def main(begin_date, end_date):
    kdc = kingnetdc.kdc
    args = {
        'run_date': kdc.workDate,
        'source_tb': 'wutiao.bdl_wutiao_event',
        'target_tb': 'wutiao.odl_wutiao_finance_withdraw',
        'month_begin': begin_date,
        'month_end': end_date,
    }

    kdc.debug = True

    if not (args['month_begin'] and args['month_end']):
        args['month_begin'] = args['run_date'][:-2] + '01'
        args['month_end'] = args['run_date']

    sql = '''
    set hive.merge.smallfiles.avgsize=256000000;
    set hive.merge.size.per.task=256000000;
    set hive.merge.mapredfiles=true;
    set hive.merge.tezfiles=true;
    set mapreduce.map.memory.mb=4000;
    set mapreduce.reduce.memory.mb=4000;

    insert overwrite table %(target_tb)s partition(ds)
    select ouid, ts, ip, status, coin, rate, rmb, order_id,ds as org_ds, '%(month_begin)s' as ds
    from %(source_tb)s
    where event = 'withdraw'
    and eventtype = 'low'
    and ds between '%(month_begin)s' and '%(month_end)s'
    ''' % args
    kdc.doHive(sql)


if __name__ == '__main__':
    if len(sys.argv) == 3:
        month_begin = sys.argv[1]
        month_end = sys.argv[2]
    else:
        month_begin = None
        month_end = None
    main(month_begin, month_end)
