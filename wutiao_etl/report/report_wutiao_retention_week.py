# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-留存周数据
#*作者：Leo
#*时间：2018-08-16
#*备注：区块链-留存周数据
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

INSERT overwrite TABLE %(db)s.%(targettab)s partition (ds='%(ds)s')
SELECT date_sub(x.ds,6) AS fds,
       year(x.ds) AS yeardate,
       weekofyear(x.ds) AS weeknum,
       x.os,
       x.appver,
       x.channel,
       x.week_active_did_cnt,
       x.week_active_ouid_cnt,
       x.week_new_did_cnt,
       y.week_new_ouid_cnt,
       z.week_withdraw_ouid_cnt,
       z.week_withdraw_rmb + z.week_withdraw_duobao_rmb,
       y.week1_retention,
       y.week2_retention,
       y.week3_retention,
       y.week4_retention,
       y.week5_retention,
       y.week6_retention,
       y.week7_retention,
       y.week8_retention
FROM
  (SELECT m.ds,
          nvl(m.os,'allos') AS os,
          nvl(m.appver,'allappver') AS appver,
          nvl(m.channel,'allchannel') AS channel,
          count(DISTINCT week_active_did) AS week_active_did_cnt,
          count(DISTINCT week_active_ouid) AS week_active_ouid_cnt,
          count(DISTINCT week_new_did) AS week_new_did_cnt
   FROM
     (SELECT ds,
             nvl(last_os,'-1') AS os,
             nvl(last_appver,'-1') AS appver,
             nvl(last_channel,'-1') AS channel,
             if(ut='uid' AND datediff(ds,from_unixtime(last_login_time,'yyyy-MM-dd'))<=6,ouid,NULL) AS week_active_ouid,
             if(ut='did' AND datediff(ds,from_unixtime(last_login_time,'yyyy-MM-dd'))<=6,ouid,NULL) AS week_active_did,
             if(ut='did' AND datediff(ds,from_unixtime(first_login_time,'yyyy-MM-dd'))<=6,ouid,NULL) AS week_new_did
      FROM %(db)s.%(sourcetab)s
      WHERE ds IN (%(week_last_date)s)) AS m
   GROUP BY m.os,
            m.appver,
            m.channel,
            m.ds
   GROUPING sets((m.os,m.appver,m.channel,m.ds) 
                ,(m.os,m.appver,m.ds) 
                ,(m.os,m.channel,m.ds) 
                ,(m.appver,m.channel,m.ds) 
                ,(m.os,m.ds) 
                ,(m.appver,m.ds) 
                ,(m.channel,m.ds) 
                ,(m.ds))
   ) AS x
LEFT JOIN
  (SELECT m.ds,
          nvl(m.os,'allos') AS os,
          nvl(m.appver,'allappver') AS appver,
          nvl(m.channel,'allchannel') AS channel,
          count(DISTINCT if(         m.ds     =n.ds,m.ouid,NULL)) AS week_new_ouid_cnt,
          count(DISTINCT if(date_add(m.ds,1*7)=n.ds,m.ouid,NULL)) AS week1_retention,
          count(DISTINCT if(date_add(m.ds,2*7)=n.ds,m.ouid,NULL)) AS week2_retention,
          count(DISTINCT if(date_add(m.ds,3*7)=n.ds,m.ouid,NULL)) AS week3_retention,
          count(DISTINCT if(date_add(m.ds,4*7)=n.ds,m.ouid,NULL)) AS week4_retention,
          count(DISTINCT if(date_add(m.ds,5*7)=n.ds,m.ouid,NULL)) AS week5_retention,
          count(DISTINCT if(date_add(m.ds,6*7)=n.ds,m.ouid,NULL)) AS week6_retention,
          count(DISTINCT if(date_add(m.ds,7*7)=n.ds,m.ouid,NULL)) AS week7_retention,
          count(DISTINCT if(date_add(m.ds,8*7)=n.ds,m.ouid,NULL)) AS week8_retention
   FROM
     (SELECT ds,
             os,
             appver,
             channel,
             ouid
      FROM
        (SELECT nvl(last_os,'-1') AS os,
                nvl(last_appver,'-1') AS appver,
                nvl(last_channel,'-1') AS channel,
                from_unixtime(first_login_time,'yyyy-MM-dd') AS first_login_time,
                from_unixtime(last_login_time,'yyyy-MM-dd') AS last_login_time,
                ouid,
                ds
         FROM %(db)s.%(sourcetab)s
         WHERE ds IN (%(week_last_date)s)
           AND ut='uid') AS t
      WHERE datediff(ds,t.first_login_time)<=6) AS m
   LEFT JOIN
     (SELECT ds,
             os,
             appver,
             channel,
             ouid
      FROM
        (SELECT nvl(last_os,'-1') AS os,
                nvl(last_appver,'-1') AS appver,
                nvl(last_channel,'-1') AS channel,
                from_unixtime(first_login_time,'yyyy-MM-dd') AS first_login_time,
                from_unixtime(last_login_time,'yyyy-MM-dd') AS last_login_time,
                ouid,
                ds
         FROM %(db)s.%(sourcetab)s
         WHERE ds IN (%(week_last_date)s)
           AND ut='uid') AS t
      WHERE datediff(ds,t.last_login_time)<=6) AS n ON m.ouid=n.ouid
   AND m.ds<=n.ds
   GROUP BY m.os,
            m.appver,
            m.channel,
            m.ds
   GROUPING sets((m.os,m.appver,m.channel,m.ds) 
                ,(m.os,m.appver,m.ds) 
                ,(m.os,m.channel,m.ds) 
                ,(m.appver,m.channel,m.ds) 
                ,(m.os,m.ds) 
                ,(m.appver,m.ds) 
                ,(m.channel,m.ds) 
                ,(m.ds))
   ) AS y ON x.os=y.os
AND x.appver=y.appver
AND x.channel=y.channel
AND x.ds=y.ds
LEFT JOIN
  (SELECT m.ds ,
          nvl(m.os,'allos') AS os,
          nvl(m.appver,'allappver') AS appver,
          nvl(m.channel,'allchannel') AS channel,
          count(DISTINCT if(datediff(ds,last_withdraw_time)<=6 or datediff(ds,last_withdraw_duobao_time)<=6, ouid, NULL)) AS week_withdraw_ouid_cnt,
          sum(week_withdraw_rmb) AS week_withdraw_rmb,
          sum(week_withdraw_duobao_rmb) AS week_withdraw_duobao_rmb
   FROM
     (SELECT date_add(ds,7-cast(date_format(ds,'u') as int)) AS ds,
             ouid,
             maxbytimenotnull(nvl(last_os,'-1'),unix_timestamp(ds,'yyyy-MM-dd')) AS os,
             maxbytimenotnull(nvl(last_appver,'-1'),unix_timestamp(ds,'yyyy-MM-dd')) AS appver,
             maxbytimenotnull(nvl(last_channel,'-1'),unix_timestamp(ds,'yyyy-MM-dd')) AS channel,
             from_unixtime(max(last_withdraw_time),'yyyy-MM-dd') AS last_withdraw_time,
             from_unixtime(max(last_withdraw_duobao_time),'yyyy-MM-dd') AS last_withdraw_duobao_time,
             sum(if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_rmb,0)) AS week_withdraw_rmb,
             sum(if(from_unixtime(last_withdraw_duobao_time,'yyyy-MM-dd')=ds,last_day_withdraw_duobao_rmb,0)) AS week_withdraw_duobao_rmb
      FROM %(db)s.%(sourcetab)s
      WHERE ds BETWEEN '%(begin_date)s' AND '%(end_date)s'
        AND ut='uid'
      GROUP BY date_add(ds,7-cast(date_format(ds,'u') as int)),
               ouid) AS m
   GROUP BY m.os,
            m.appver,
            m.channel,
            m.ds
   GROUPING sets((m.os,m.appver,m.channel,m.ds) 
                ,(m.os,m.appver,m.ds) 
                ,(m.os,m.channel,m.ds) 
                ,(m.appver,m.channel,m.ds) 
                ,(m.os,m.ds) 
                ,(m.appver,m.ds) 
                ,(m.channel,m.ds) 
                ,(m.ds))
   ) AS z ON x.os=z.os
AND x.appver=z.appver
AND x.channel=z.channel
AND x.ds=z.ds
;
'''

delete = r'''echo "use %(db)s; delete from %(mysqltab)s where fds in(%(week_first_date)s); " |mysql -h %(host)s -u %(user)s -p%(pasw)s '''
sync = r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/%(db)s.db/%(targettab)s/ds=%(ds)s --connect jdbc:mysql://%(host)s:%(port)s/%(db)s --username "%(user)s" --password '%(pasw)s' --table "%(mysqltab)s" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'db': 'wutiao',
        'sourcetab': 'idl_wutiao_user',
        'targettab': 'report_wutiao_retention_week',
        'mysqltab': 'report_wutiao_retention_week'
    }
    args['ds'] = kdc.dateAdd(1, args['ds'])
    weekday = kingnetdc.get_weekday(args['ds'])
    if weekday != 0:
        print("run date is not monday: " + str(weekday))
        return
    args['begin_date'] = kdc.dateSub(9*7, args['ds'])
    args['end_date'] = kdc.dateSub(1, args['ds'])
    args['week_last_date'] = ''
    for i in range(0, 9):
        args['week_last_date'] = args['week_last_date'] + ',\'' + kdc.dateSub(i*7, args['end_date']) + '\''
    args['week_last_date'] = args['week_last_date'].lstrip(',')
    # print(args['week_last_date'])
    sql = SQL_DAY % args
    kdc.debug = True
    kdc.doHive(sql)

    args['host'] = DB_PARAMS['host']
    args['port'] = DB_PARAMS['port']
    args['user'] = DB_PARAMS['user']
    args['pasw'] = DB_PARAMS['password']
    args['week_first_date'] = ''
    for i in range(1, 10):
        args['week_first_date'] = args['week_first_date'] + ',\'' + kdc.dateSub(i*7, args['ds']) + '\''
    args['week_first_date'] = args['week_first_date'].lstrip(',')
    # print(args['week_first_date'])
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
