# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-用户搜索分析
#*作者：Leo
#*时间：2018-06-27
#*备注：区块链-用户搜索分析
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
       t1.os,
       t1.appver,
       t1.channel,
       search_click_pv,
       search_click_uv,
       search_no_result_pv,
       search_no_result_uv,
       search_result_click_pv,
       search_result_click_uv
FROM
  (SELECT nvl(os,'allos') AS os,
          nvl(appver,'allappver') AS appver,
          nvl(channel,'allchannel') AS channel,
          sum(search_click_cnt) AS search_click_pv,
          sum(search_click_usercnt) AS search_click_uv,
          sum(search_result_click_cnt) AS search_result_click_pv,
          sum(search_result_click_usercnt) AS search_result_click_uv
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             search_click_cnt,
             search_click_usercnt,
             search_result_click_cnt,
             search_result_click_usercnt
      FROM {sourcetab1}
      WHERE ds='{ds}'
        AND ut='uid') AS t
   GROUP BY os,
            appver,
            channel WITH CUBE) AS t1
LEFT JOIN
  (SELECT nvl(os,'allos') AS os,
          nvl(appver,'allappver') AS appver,
          nvl(channel,'allchannel') AS channel,
          sum(if(status=0,1,0)) AS search_no_result_pv,
          count(DISTINCT if(status=0,ouid,NULL)) AS search_no_result_uv
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             status,
             ouid
      FROM {sourcetab2}
      WHERE ds='{ds}'
        AND event='search') AS t
   GROUP BY os,
            appver,
            channel WITH CUBE) AS t2 ON t1.os=t2.os
AND t1.appver=t2.appver
AND t1.channel=t2.channel
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.adl_wutiao_click_statistics',
        'sourcetab2': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.report_wutiao_search_analysis',
        'mysqltab': 'wutiao.report_wutiao_search_analysis'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'], sourcetab2=args['sourcetab2'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT  fds,os,appver,channel,search_click_pv,search_click_uv,search_no_result_pv,
                             search_no_result_uv,search_result_click_pv,search_result_click_uv  
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
