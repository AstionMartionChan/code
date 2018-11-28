# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-留存用户
#*作者：Leo
#*时间：2018-05-05
#*备注：区块链-留存用户
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

INSERT overwrite TABLE %(hive_tb)s partition (ds)
SELECT m.ds AS fds,
       m.os,
       m.appver,
       m.channel,
       count(DISTINCT m.ouid),
       count(DISTINCT if(date_add(m.ds,1)=n.ds,m.ouid,NULL)) AS day1_retention,
       count(DISTINCT if(date_add(m.ds,2)=n.ds,m.ouid,NULL)) AS day2_retention,
       count(DISTINCT if(date_add(m.ds,3)=n.ds,m.ouid,NULL)) AS day3_retention,
       count(DISTINCT if(date_add(m.ds,4)=n.ds,m.ouid,NULL)) AS day4_retention,
       count(DISTINCT if(date_add(m.ds,5)=n.ds,m.ouid,NULL)) AS day5_retention,
       count(DISTINCT if(date_add(m.ds,6)=n.ds,m.ouid,NULL)) AS day6_retention,
       count(DISTINCT if(date_add(m.ds,7)=n.ds,m.ouid,NULL)) AS day7_retention,
       count(DISTINCT if(date_add(m.ds,15)=n.ds,m.ouid,NULL)) AS day15_retention,
       count(DISTINCT if(date_add(m.ds,30)=n.ds,m.ouid,NULL)) AS day30_retention,
       m.ds
FROM
  (SELECT ds,
          os,
          appver,
          channel,
          ouid
   FROM
     (SELECT nvl(os,'allos') AS os,
             nvl(appver,'allappver') AS appver,
             nvl(channel,'allchannel') AS channel,
             min(first_login_time) AS first_login_time,
             max(last_login_time) AS last_login_time,
             ouid,
             ds
      FROM
        (SELECT nvl(last_os,'-1') AS os,
                nvl(last_appver,'-1') AS appver,
                nvl(last_channel,'-1') AS channel,
                from_unixtime(first_login_time,'yyyy-MM-dd') AS first_login_time,
                from_unixtime(last_login_time,'yyyy-MM-dd') AS last_login_time,
                ouid,
                ds
         FROM %(src_tb)s
         WHERE ds BETWEEN '%(ds_30)s' AND '%(ds)s'
           AND ut='uid') AS a
      GROUP BY os,
               appver,
               channel,
               ouid,
               ds
      GROUPING sets ((os, appver, channel, ouid ,ds) 
                     ,(os, appver, ouid ,ds) 
                     ,(os, channel, ouid ,ds) 
                     ,(appver, channel, ouid ,ds) 
                     ,(os, ouid ,ds) 
                     ,(appver, ouid ,ds) 
                     ,(channel, ouid ,ds) 
                     ,(ouid ,ds))) AS t
   WHERE t.first_login_time=ds) AS m
LEFT JOIN
  (SELECT ds,
          os,
          appver,
          channel,
          ouid
   FROM
     (SELECT nvl(os,'allos') AS os,
             nvl(appver,'allappver') AS appver,
             nvl(channel,'allchannel') AS channel,
             min(first_login_time) AS first_login_time,
             max(last_login_time) AS last_login_time,
             ouid,
             ds
      FROM
        (SELECT nvl(last_os,'-1') AS os,
                nvl(last_appver,'-1') AS appver,
                nvl(last_channel,'-1') AS channel,
                from_unixtime(first_login_time,'yyyy-MM-dd') AS first_login_time,
                from_unixtime(last_login_time,'yyyy-MM-dd') AS last_login_time,
                ouid,
                ds
         FROM %(src_tb)s
         WHERE ds BETWEEN date_add('%(ds_30)s',1) AND '%(ds)s'
           AND ut='uid') AS a
      GROUP BY os,
               appver,
               channel,
               ouid,
               ds
      GROUPING sets ((os, appver, channel, ouid ,ds) 
                     ,(os, appver, ouid ,ds) 
                     ,(os, channel, ouid ,ds) 
                     ,(appver, channel, ouid ,ds) 
                     ,(os, ouid ,ds) 
                     ,(appver, ouid ,ds) 
                     ,(channel, ouid ,ds) 
                     ,(ouid ,ds))) AS t
   WHERE t.last_login_time=ds
     AND t.first_login_time<ds) AS n ON m.os=n.os
AND m.appver=n.appver
AND m.channel=n.channel
AND m.ouid=n.ouid
AND m.ds<n.ds
GROUP BY m.os,
         m.appver,
         m.channel,
         m.ds;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'ds_30': kdc.dateSub(30),
        'src_tb': 'wutiao.idl_wutiao_user',
        'hive_tb': 'wutiao.report_wutiao_retention',
        'mysql_tb': 'wutiao.report_wutiao_retention'
    }

    hql = SQL_DAY % args
    kdc.debug = True
    # print(hql)
    kdc.doHive(hql)

    presto_sql = '''
        SELECT fds,os,appver,channel,newuser_cnt,day1_retention,day2_retention,day3_retention,day4_retention
               ,day5_retention,day6_retention,day7_retention,day15_retention,day30_retention
        FROM %(hive_tb)s
        WHERE ds between '%(ds_30)s' and '%(ds)s'
    ''' % args
    ret = kingnetdc.presto_execsqlr(presto_sql)

    if ret:
        args['column_str'] = ','.join(['%s']*len(ret[0]))
        del_sql = ''' delete from %(mysql_tb)s where fds between '%(ds_30)s' and '%(ds)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        insert_sql = ''' insert into %(mysql_tb)s values(%(column_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret)
    else:
        raise Exception('none result')


if __name__ == '__main__':
    main()