# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-详情页转化漏斗
#*作者：Leo
#*时间：2018-06-29
#*备注：区块链-详情页转化漏斗
#***************************************************************#
import kylib
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
set hive.new.job.grouping.set.cardinality=2048;
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}'AS fds,
       os,
       channel,
       itemtype,
       sum(content_pv) AS content_pv,
       sum(content_uv) AS content_uv,
       sum(content_second_pv) AS content_second_pv,
       sum(content_second_uv) AS content_second_uv,
       sum(content_readlist_pv) AS content_readlist_pv,
       sum(content_readlist_uv) AS content_readlist_uv,
       sum(content_exit_pv) AS content_exit_pv,
       sum(content_exit_uv) AS content_exit_uv
FROM
  (SELECT nvl(os,'allos') AS os,
          nvl(channel,'allchannel') AS channel,
          nvl(itemtype,'allitemtype') as itemtype,
          ouid,
          sum(if(curr_event='click' AND target='P',1,0)) AS content_pv,
          max(if(curr_event='click' AND target='P',1,0)) AS content_uv,
          sum(if(curr_event='click' AND pos='P' AND target IS NOT NULL,1,0)) AS content_second_pv,
          max(if(curr_event='click' AND pos='P' AND target IS NOT NULL,1,0)) AS content_second_uv,
          sum(if(curr_event='click' AND pos='P' AND target='P_1',1,0)) AS content_readlist_pv,
          max(if(curr_event='click' AND pos='P' AND target='P_1',1,0)) AS content_readlist_uv,
          sum(if((curr_event='click' AND target='P') AND (next_event IS NULL OR next_event<>'click'),1,0)) AS content_exit_pv,
          max(if((curr_event='click' AND target='P'),1,0))
          - max(if(curr_event='click' AND target='P' AND next_event='click',1,0)) AS content_exit_uv
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(channel,'-1') AS channel,
             nvl(itemtype,'-1') as itemtype,
             ouid,
             curr_event,
             next_event,
             pos,
             target
      FROM {sourcetab}
      WHERE ds='{ds}') AS a
   GROUP BY os,
            channel,
            itemtype,
            ouid
   GROUPING sets((os,channel,itemtype,ouid)
                ,(os,channel,ouid)
                ,(os,itemtype,ouid)
                ,(channel,itemtype,ouid)
                ,(os,ouid)
                ,(channel,ouid)
                ,(itemtype,ouid)
                ,(ouid))) AS t
GROUP BY os,
         channel,
         itemtype;
'''


def main():
    kdc = kylib.KDC
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.idl_wutiao_user_event',
        'targettab': 'wutiao.report_wutiao_content_funnel',
        'mysqltab': 'wutiao.report_wutiao_content_funnel'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])
    kdc.debug = True

    # print sql
    kylib.execute_by_task('doHive', sql)

    presto_sql = ''' SELECT fds,os,channel,itemtype,content_pv,content_uv,content_second_pv,content_second_uv
                           ,content_readlist_pv,content_readlist_uv,content_exit_pv,content_exit_uv
                     FROM %(targettab)s 
                     WHERE ds='%(ds)s' ''' % args
    ret = kylib.presto_exec(presto_sql)

    if ret:
        ret_len = len(ret[0])
        args['column_str'] = ','.join(['%s'] * ret_len)
        del_sql = ''' delete from %(mysqltab)s where fds = '%(ds)s' ''' % args
        kylib.new_execsqlr(del_sql, kylib.DB_PARAMS_91)
        insert_sql = ''' insert into %(mysqltab)s values(%(column_str)s) ''' % args
        kylib.executemany_batches(kylib.DB_PARAMS_91, insert_sql, ret, 30000)
    else:
        raise Exception('none result')


if __name__ == '__main__':
    main()
