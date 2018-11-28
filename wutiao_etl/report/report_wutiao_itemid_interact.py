# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-内容互动分析
#*作者：Leo
#*时间：2018-07-05
#*备注：区块链-内容互动分析
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds,
       'allsortid' AS sortid,
       nvl(itemtype,'allitemtype') AS itemtype,
       sum(day_addcomment_cnt) AS comment_cnt,
       sum(day_itemid_discover_vote_cnt) AS vote_cnt,
       sum(day_itemid_like_cnt) AS like_cnt,
       sum(day_report_cnt) AS report_cnt,
       sum(day_nointerest_cnt) AS nointerest_cnt
FROM 
(
    SELECT nvl(itemtype,'-1') as itemtype,
           day_addcomment_cnt,
           day_itemid_discover_vote_cnt,
           day_itemid_like_cnt,
           day_report_cnt,
           day_nointerest_cnt
      FROM {sourcetab}
     WHERE ds='{ds}'
) as m
GROUP BY itemtype WITH CUBE
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.idl_wutiao_itemid_wb',
        'targettab': 'wutiao.report_wutiao_itemid_interact',
        'mysqltab': 'wutiao.report_wutiao_itemid_interact'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT fds,sortid,itemtype,comment_cnt,vote_cnt,like_cnt,report_cnt,nointerest_cnt 
                     FROM %(targettab)s WHERE ds='%(ds)s' ''' % args
    ret = kingnetdc.hive_execsqlr(presto_sql)

    if ret:
        args['column_str'] = ','.join(['%s'] * len(ret[0]))
        del_sql = ''' delete from %(mysqltab)s where fds = '%(ds)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        insert_sql = ''' insert into %(mysqltab)s values(%(column_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret)
    else:
        raise Exception('none result')


if __name__ == '__main__':
    main()
