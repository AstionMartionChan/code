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
        'target_tb': 'wutiao.idl_wutiao_finance_uid_coin',
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
    select uid, tp, num, '%(month_begin)s' as ds
    from (
        select uid, cast(actiontype as string) as tp, sum(coin_value) as num
        from wutiao.odl_wutiao_finance_wb
        where ds = '%(month_begin)s'
        group by uid, cast(actiontype as string)
        union all
        select uid, 'tx_coin' as tp, sum(coin) as num
        from (
            select uid, order_id, coin
            from wutiao.odl_wutiao_finance_withdraw
            where ds = '%(month_begin)s'
            and status = 2
            group by uid, order_id, coin
        ) t
        group by uid
        union all
        select uid, 'tx_money' as tp, sum(rmb) as num
        from (
            select uid, order_id, rmb
            from wutiao.odl_wutiao_finance_withdraw
            where ds = '%(month_begin)s'
            and status = 2
            group by uid, order_id, rmb
        ) t
        group by uid
        union all
        select nvl(uid,'-1') as uid, 'jl' as tp, sum(add_v) as num
        from (
               select to_address, sum(amount) as add_v
               from wutiao.odl_wutiao_finance_tx_record
               where type = 1
               and state = 4
               and ds = '%(month_begin)s'
               group by to_address
        ) a
        left join
        (
               select uid, address
               from wutiao.odl_wutiao_finance_account
               where ds = '%(month_begin)s'
        ) b
        on a.to_address = b.address
        group by nvl(uid,'-1')
    ) t
    ''' % args
    kdc.doHive(sql)


if __name__ == '__main__':
    if len(sys.argv) == 3:
        month_begin = sys.argv[0]
        month_end = sys.argv[1]
    else:
        month_begin = None
        month_end = None
    main(month_begin, month_end)
