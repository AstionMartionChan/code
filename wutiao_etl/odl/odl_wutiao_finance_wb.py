# !/usr/bin/env python
# -*-coding: UTF-8 -*-

# **********************程序说明*********************************#
# *模块: odl_wutiao_finance_wb -> ODL
# *功能: 区块链-五条项目-财务需求-分币记录快照备份
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
        'source_tb': 'wutiao.odl_wutiao_wb',
        'target_tb': 'wutiao.odl_wutiao_finance_wb',
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
    select uid, sourceid,sourcetype,time,actiontype,value, '%(month_begin)s' as ds
    from %(source_tb)s
    where ds between '%(month_begin)s' and '%(month_end)s'
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
