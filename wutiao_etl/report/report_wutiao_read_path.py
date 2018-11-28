# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-访问路径分析
#*作者：Leo
#*时间：2018-05-28
#*备注：区块链-访问路径分析
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
set hive.new.job.grouping.set.cardinality=2048;
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}'AS fds,
       os,
       channel,
       itemtype,
       sum(homepage_pv) AS homepage_pv,
       sum(homepage_uv) AS homepage_uv,
       sum(second_pv) AS second_pv,
       sum(second_uv) AS second_uv,
       sum(content_pv) AS content_pv,
       sum(content_uv) AS content_uv,
       sum(readlist_pv) AS readlist_pv,
       sum(readlist_uv) AS readlist_uv,
       sum(exit_pv) AS exit_pv,
       sum(exit_uv) AS exit_uv
FROM
  (SELECT nvl(os,'allos') AS os,
          nvl(channel,'allchannel') AS channel,
          nvl(itemtype,'allitemtype') as itemtype,
          ouid,
          sum(if((curr_event='click' AND target='A') OR (curr_event='openclient'),1,0))
          - sum(if(curr_event='click' AND pos='S' AND target='S_1',1,0)) AS homepage_pv,
          max(if((curr_event='click' AND target='A') OR (curr_event='openclient'),1,0)) AS homepage_uv,
          sum(if(curr_event='click' AND pos='A' AND target IS NOT NULL,1,0)) AS second_pv,
          max(if(curr_event='click' AND pos='A' AND target IS NOT NULL,1,0)) AS second_uv,
          sum(if(curr_event='click' AND target='P',1,0)) AS content_pv,
          max(if(curr_event='click' AND target='P',1,0)) AS content_uv,
          sum(if(curr_event='readlist',1,0)) AS readlist_pv,
          max(if(curr_event='readlist',1,0)) AS readlist_uv,
          sum(if(curr_event='openclient' AND (next_event IS NULL OR next_event<>'click'),1,0)) AS exit_pv,
          max(if(curr_event='openclient',1,0))
          - max(if(curr_event='openclient' AND next_event='click',1,0)) AS exit_uv
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
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.idl_wutiao_user_event',
        'targettab': 'wutiao.report_wutiao_read_path',
        'mysqltab': 'wutiao.report_wutiao_read_path'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT fds,os,channel,itemtype,homepage_pv,homepage_uv,second_pv,second_uv,content_pv
                            ,content_uv,readlist_pv,readlist_uv,exit_pv,exit_uv
                     FROM %(targettab)s 
                     WHERE ds='%(ds)s' ''' % args
    ret = kingnetdc.hive_execsqlr(presto_sql)

    if ret:
        ret_len = len(ret[0])
        args['column_str'] = ','.join(['%s'] * ret_len)
        del_sql = ''' delete from %(mysqltab)s where fds = '%(ds)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        insert_sql = ''' insert into %(mysqltab)s values(%(column_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret)
    else:
        raise Exception('none result')


if __name__ == '__main__':
    main()
