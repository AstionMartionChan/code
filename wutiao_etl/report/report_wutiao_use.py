# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-用户使用分析
#*作者：Leo
#*时间：2018-05-04
#*备注：区块链-用户使用分析
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
INSERT overwrite TABLE %(db)s.%(targettab)s partition (ds='%(ds)s')
SELECT '%(ds)s' AS fds,
       a.os,
       a.appver,
       a.channel,
       a.didflag,
       a.appUV,
       a.appPV,
       a.usetime,
       a.enterbackground_cnt,
       nvl(b.day_read_cnt,0) AS day_read_cnt,
       nvl(b.day_play_cnt,0) AS day_play_cnt,
       nvl(b.day_like_cnt,0) AS day_like_cnt,
       nvl(b.day_comment_cnt,0) AS day_comment_cnt,
       nvl(b.day_share_cnt,0) AS day_share_cnt,
       nvl(b.day_favor_cnt,0) AS day_favor_cnt
FROM
(SELECT nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       nvl(new_flag,'alldidflag') AS didflag,
       sum(appUV) AS appUV,
       sum(appPV) AS appPV,
       sum(usetime) AS usetime,
       sum(enterbackground_cnt) AS enterbackground_cnt
FROM
  (SELECT nvl(last_os,'-1') AS os,
          nvl(last_appver,'-1') AS appver,
          nvl(last_channel,'-1') AS channel,
          nvl(ouid,'-1') AS did,
          if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) AS new_flag,
          if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,last_login_cnt,0) AS appPV,
          if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,1,0) AS appUV,
          nvl(enterbackground_cnt,0)-nvl(enterfront_under30_cnt,0) as enterbackground_cnt,
          nvl(usetime,0) as usetime
   FROM %(db)s.%(sourcetab3)s
   WHERE ds='%(ds)s'
     AND ut='did') AS m
GROUP BY os,
         appver,
         channel,
         new_flag WITH CUBE) as a
left join
(SELECT nvl(os,'allos') AS os,
        nvl(appver,'allappver') AS appver,
        nvl(channel,'allchannel') AS channel,
        nvl(new_flag,'alldidflag') AS didflag,
        sum(read_flag) AS day_read_cnt,
        sum(play_flag) AS day_play_cnt,
        sum(like_flag) AS day_like_cnt,
        sum(comment_flag) AS day_comment_cnt,
        sum(share_flag) AS day_share_cnt,
        sum(favour_flag) AS day_favor_cnt
FROM
  (SELECT nvl(os,'-1') AS os,
          nvl(appver,'-1') AS appver,
          nvl(channel,'-1') AS channel,
          nvl(new_flag,'-1') AS new_flag,
          if(event='read' AND itemtype=1,1,0) AS read_flag,
          if(event='play' AND itemtype=2 AND type=1,1,0) AS play_flag,
          if(event='like' and type=1,1,0) as like_flag,
          if(event='comment' and status=1,1,0) as comment_flag,
          if(event='share' and status=3,1,0) as share_flag,
          if(event='favour' and status=1,1,0) as favour_flag
   FROM
     (SELECT did,
             event,
             os,
             appver,
             channel,
             itemid,
             itemtype,
             type,
             status,
             new_flag
      FROM
        (SELECT nvl(did,'-1') as did,
                event,
                os,
                appver,
                channel,
                nvl(itemid,'-1') as itemid,
                nvl(itemtype,'-1') as itemtype,
                type,
                status
         FROM %(db)s.%(sourcetab1)s
         WHERE ds='%(ds)s'
           AND event in('read','play','like','comment','share','favour')
        ) AS a
      LEFT JOIN
        (SELECT ouid,
                if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) AS new_flag
         FROM %(db)s.%(sourcetab3)s
         WHERE ds='%(ds)s'
           AND ut='did'
        ) AS o ON a.did=o.ouid 
     )AS c
 ) AS d
GROUP BY os,
         appver,
         channel,
         new_flag
WITH CUBE) as b
on a.os=b.os
and a.appver=b.appver
and a.channel=b.channel
and a.didflag=b.didflag
;
'''

delete = r'''echo "use %(db)s; delete from %(mysqltab)s where fds='%(ds)s'; " |mysql -h %(host)s -u %(user)s -p%(pasw)s '''
sync = r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/%(db)s.db/%(targettab)s/ds=%(ds)s --connect jdbc:mysql://%(host)s:%(port)s/%(db)s --username "%(user)s" --password '%(pasw)s' --table "%(mysqltab)s" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'db': 'wutiao',
        'sourcetab1': 'bdl_wutiao_event',
        'sourcetab3': 'idl_wutiao_user',
        'targettab': 'report_wutiao_use',
        'mysqltab': 'report_wutiao_use'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    args['host'] = DB_PARAMS['host']
    args['port'] = DB_PARAMS['port']
    args['user'] = DB_PARAMS['user']
    args['pasw'] = DB_PARAMS['password']
    res1 = os.system(delete % args)
    res2 = os.system(sync % args)
    if (res1 or res2) != 0:
        print(res1, res2)
        raise Exception('%(mysqltab)s mysql table delete or insert execute failure!!' % args)
    else:
        print(res1, res2)
        print('complete')


if __name__ == '__main__':
    main()
