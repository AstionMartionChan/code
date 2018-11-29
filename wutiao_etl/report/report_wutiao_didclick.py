# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-设备点击分析
#*作者：Leo
#*时间：2018-07-26
#*备注：区块链-设备点击分析
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
       t1.os,
       t1.appver,
       t1.channel,
       t1.didflag,
       homepage_pv,
       homepage_attention_pv,
       homepage_latest_pv,
       homepage_income_pv,
       search_pv,
       video_pv,
       find_pv,
       wallet_pv,
       personal_pv,
       banner_pv,
       recommend_pv,
       search_no_result_pv,
       homepage_uv,
       homepage_attention_uv,
       homepage_latest_uv,
       homepage_income_uv,
       search_uv,
       video_uv,
       find_uv,
       wallet_uv,
       personal_uv,
       banner_uv,
       recommend_uv,
       search_no_result_uv
FROM
  (SELECT nvl(os,'allos') AS os,
          nvl(appver,'allappver') AS appver,
          nvl(channel,'allchannel') AS channel,
          nvl(didflag,'alldidflag') AS didflag,
          sum(homepage_cnt) AS homepage_pv,
          sum(homepage_attention_cnt) AS homepage_attention_pv,
          sum(homepage_latest_cnt) AS homepage_latest_pv,
          sum(homepage_income_cnt) AS homepage_income_pv,
          sum(search_cnt) AS search_pv,
          sum(video_cnt) AS video_pv,
          sum(find_cnt) AS find_pv,
          sum(wallet_cnt) AS wallet_pv,
          sum(personal_cnt) AS personal_pv,
          sum(banner_cnt) AS banner_pv,
          sum(recommend_cnt) AS recommend_pv,
          sum(homepage_usercnt) AS homepage_uv,
          sum(homepage_attention_usercnt) AS homepage_attention_uv,
          sum(homepage_latest_usercnt) AS homepage_latest_uv,
          sum(homepage_income_usercnt) AS homepage_income_uv,
          sum(search_usercnt) AS search_uv,
          sum(video_usercnt) AS video_uv,
          sum(find_usercnt) AS find_uv,
          sum(wallet_usercnt) AS wallet_uv,
          sum(personal_usercnt) AS personal_uv,
          sum(banner_usercnt) AS banner_uv,
          sum(recommend_usercnt) AS recommend_uv
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             nvl(usertype,'-1') AS didflag,
             nvl(homepage_click_cnt,0) AS homepage_cnt,
             nvl(homepage_attention_click_cnt,0) AS homepage_attention_cnt,
             nvl(homepage_latest_click_cnt,0) AS homepage_latest_cnt,
             nvl(homepage_income_click_cnt,0) AS homepage_income_cnt,
             nvl(search_click_cnt,0) AS search_cnt,
             nvl(video_click_cnt,0) AS video_cnt,
             nvl(find_click_cnt,0) AS find_cnt,
             nvl(wallet_click_cnt,0) AS wallet_cnt,
             nvl(personal_click_cnt,0) AS personal_cnt,
             nvl(banner_click_cnt,0) AS banner_cnt,
             nvl(recommend_click_cnt,0) AS recommend_cnt,
             nvl(homepage_click_usercnt,0) AS homepage_usercnt,
             nvl(homepage_attention_click_usercnt,0) AS homepage_attention_usercnt,
             nvl(homepage_latest_click_usercnt,0) AS homepage_latest_usercnt,
             nvl(homepage_income_click_usercnt,0) AS homepage_income_usercnt,
             nvl(search_click_usercnt,0) AS search_usercnt,
             nvl(video_click_usercnt,0) AS video_usercnt,
             nvl(find_click_usercnt,0) AS find_usercnt,
             nvl(wallet_click_usercnt,0) AS wallet_usercnt,
             nvl(personal_click_usercnt,0) AS personal_usercnt,
             nvl(banner_click_usercnt,0) AS banner_usercnt,
             nvl(recommend_click_usercnt,0) AS recommend_usercnt
      FROM {sourcetab1}
      WHERE ds='{ds}'
        AND ut='did') AS t
   GROUP BY os,
            appver,
            channel,
            didflag WITH CUBE) AS t1
LEFT JOIN
  (SELECT nvl(os,'allos') AS os,
          nvl(appver,'allappver') AS appver,
          nvl(channel,'allchannel') AS channel,
          nvl(didflag,'alldidflag') AS didflag,
          sum(if(status=0,1,0)) AS search_no_result_pv,
          count(DISTINCT if(status=0,did,NULL)) AS search_no_result_uv
    FROM(
         SELECT nvl(n.os,m.os) AS os,
                nvl(n.appver,m.appver) AS appver,
                nvl(n.channel,m.channel) AS channel,
                nvl(n.ouid,m.did) AS did,
                m.status,
                if(first_login_time='{ds}',1,0) as didflag    
           FROM
             (SELECT nvl(os,'-1') AS os,
                     nvl(appver,'-1') AS appver,
                     nvl(channel,'-1') AS channel,
                     did,
                     status,
                     ouid
              FROM {sourcetab2}
              WHERE ds='{ds}'
                AND event='search') AS m
            LEFT JOIN
             (SELECT nvl(last_os,'-1') AS os,
                     nvl(last_appver,'-1') AS appver,
                     nvl(last_channel,'-1') AS channel,
                     ouid,
                     from_unixtime(first_login_time,'yyyy-MM-dd') as first_login_time
              FROM {sourcetab3}
              WHERE ds='{ds}'
                AND ut='did'
                AND from_unixtime(last_login_time,'yyyy-MM-dd')=ds
             ) AS n
             ON m.did=n.ouid
        ) AS t
   GROUP BY os,
            appver,
            channel,
            didflag WITH CUBE) AS t2 ON t1.os=t2.os
AND t1.appver=t2.appver
AND t1.channel=t2.channel
AND t1.didflag=t2.didflag;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.adl_wutiao_click_statistics',
        'sourcetab2': 'wutiao.bdl_wutiao_event',
        'sourcetab3': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_didclick',
        'mysqltab': 'wutiao.report_wutiao_didclick'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'], sourcetab2=args['sourcetab2'], sourcetab3=args['sourcetab3'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT  fds,os,appver,channel,didflag,homepage_pv,homepage_attention_pv,homepage_latest_pv
                            ,homepage_income_pv,search_pv,video_pv,find_pv,wallet_pv,personal_pv,banner_pv,recommend_pv
                            ,search_no_result_pv,homepage_uv,homepage_attention_uv,homepage_latest_uv,homepage_income_uv
                            ,search_uv,video_uv,find_uv,wallet_uv,personal_uv,banner_uv,recommend_uv,search_no_result_uv
                     FROM %(targettab)s WHERE ds='%(ds)s' ''' % args
    ret = kingnetdc.presto_execsqlr(presto_sql)

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