# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-用户互动分析
#*作者：Leo
#*时间：2018-05-04
#*备注：区块链-用户互动分析
#***************************************************************#

import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

SQL_DAY = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds ,
       nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       'time_cnt' as checktype,
       sum(comment_cnt) AS comment_cnt,
       sum(vote_cnt) AS vote_cnt,
       sum(search_cnt) AS search_cnt,
       sum(favor_cnt) AS favor_cnt,
       sum(like_cnt) AS like_cnt,
       sum(share_cnt) AS share_cnt,
       sum(attention_cnt) AS attention_cnt,
       sum(reply_cnt) AS reply_cnt,
       sum(report_cnt) AS report_cnt,
       sum(nointerest_cnt) AS nointerest_cnt
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             nvl(comment_cnt,0) as comment_cnt,
             nvl(vote_cnt,0) as vote_cnt,
             nvl(search_cnt,0) as search_cnt,
             nvl(favor_cnt,0) as favor_cnt,
             nvl(like_cnt,0) as like_cnt,
             nvl(share_cnt,0) as share_cnt,
             nvl(attention_cnt,0) as attention_cnt,
             nvl(reply_cnt,0) as reply_cnt,
             nvl(report_cnt,0) as report_cnt,
             nvl(nointerest_cnt,0) as nointerest_cnt
        FROM {sourcetab1}
       WHERE ds='{ds}') as t
    GROUP BY os,appver,channel with cube
UNION ALL    
SELECT '{ds}' AS fds ,
       nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       'user_cnt' as checktype,
       sum(comment_user_cnt) AS comment_cnt,
       sum(vote_user_cnt) AS vote_cnt,
       sum(search_user_cnt) AS search_cnt,
       sum(favor_user_cnt) AS favor_cnt,
       sum(like_user_cnt) AS like_cnt,
       sum(share_user_cnt) AS share_cnt,
       sum(attention_user_cnt) AS attention_cnt,
       sum(reply_user_cnt) AS reply_cnt,
       sum(report_user_cnt) AS report_cnt,
       sum(nointerest_user_cnt) AS nointerest_cnt
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             nvl(comment_user_cnt,0) as comment_user_cnt,
             nvl(vote_user_cnt,0) as vote_user_cnt,
             nvl(search_user_cnt,0) as search_user_cnt,
             nvl(favor_user_cnt,0) as favor_user_cnt,
             nvl(like_user_cnt,0) as like_user_cnt,
             nvl(share_user_cnt,0) as share_user_cnt,
             nvl(attention_user_cnt,0) as attention_user_cnt,
             nvl(reply_user_cnt,0) as reply_user_cnt,
             nvl(report_user_cnt,0) as report_user_cnt,
             nvl(nointerest_user_cnt,0) as nointerest_user_cnt
        FROM {sourcetab1}
       WHERE ds='{ds}') as t
    GROUP BY os,appver,channel with cube;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.adl_wutiao_activeuser',
        'targettab': 'wutiao.report_wutiao_interact',
        'mysqltab': 'wutiao.report_wutiao_interact'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT  fds,os,appver,channel,checktype,comment_cnt,vote_cnt,search_cnt,favour_cnt,like_cnt
                            ,share_cnt,attention_cnt,reply_cnt,report_cnt,nointerest_cnt 
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
