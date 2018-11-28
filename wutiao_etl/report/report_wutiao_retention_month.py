# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-留存月数据
#*作者：Leo
#*时间：2018-08-16
#*备注：区块链-留存月数据
#***************************************************************#
import kingnetdc
import sys
import os
import datetime
import time
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
set hive.new.job.grouping.set.cardinality=2048;

INSERT overwrite TABLE %(db)s.%(targettab)s partition (ds='%(ds)s')
SELECT x.ds AS fds,
       x.os,
       x.appver,
       x.channel,
       x.month_active_did_cnt,
       x.month_active_ouid_cnt,
       x.month_new_did_cnt,
       y.month_new_ouid_cnt,
       z.month_withdraw_ouid_cnt,
       z.month_withdraw_rmb,
       y.month1_retention,
       y.month2_retention,
       y.month3_retention,
       y.month4_retention,
       y.month5_retention,
       y.month6_retention
FROM
  (SELECT m.ds,
          nvl(m.os,'allos') AS os,
          nvl(m.appver,'allappver') AS appver,
          nvl(m.channel,'allchannel') AS channel,
          count(DISTINCT month_active_did) AS month_active_did_cnt,
          count(DISTINCT month_active_ouid) AS month_active_ouid_cnt,
          count(DISTINCT month_new_did) AS month_new_did_cnt
   FROM
     (SELECT date_format(ds,'yyyy-MM-01') AS ds,
             nvl(last_os,'-1') AS os,
             nvl(last_appver,'-1') AS appver,
             nvl(last_channel,'-1') AS channel,
             if(ut='uid' AND date_format(from_unixtime(last_login_time,'yyyy-MM-dd'),'yyyy-MM-01')=date_format(ds,'yyyy-MM-01'),ouid,NULL) AS month_active_ouid,
             if(ut='did' AND date_format(from_unixtime(last_login_time,'yyyy-MM-dd'),'yyyy-MM-01')=date_format(ds,'yyyy-MM-01'),ouid,NULL) AS month_active_did,
             if(ut='did' AND date_format(from_unixtime(first_login_time,'yyyy-MM-dd'),'yyyy-MM-01')=date_format(ds,'yyyy-MM-01'),ouid,NULL) AS month_new_did
      FROM %(db)s.%(sourcetab)s
      WHERE ds IN (%(month_last_date)s)) AS m
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
          count(DISTINCT if(           m.ds           =           n.ds         ,m.ouid,NULL)) AS month_new_ouid_cnt,
          count(DISTINCT if(cast(month(m.ds) as int)+1=cast(month(n.ds) as int),m.ouid,NULL)) AS month1_retention,
          count(DISTINCT if(cast(month(m.ds) as int)+2=cast(month(n.ds) as int),m.ouid,NULL)) AS month2_retention,
          count(DISTINCT if(cast(month(m.ds) as int)+3=cast(month(n.ds) as int),m.ouid,NULL)) AS month3_retention,
          count(DISTINCT if(cast(month(m.ds) as int)+4=cast(month(n.ds) as int),m.ouid,NULL)) AS month4_retention,
          count(DISTINCT if(cast(month(m.ds) as int)+5=cast(month(n.ds) as int),m.ouid,NULL)) AS month5_retention,
          count(DISTINCT if(cast(month(m.ds) as int)+6=cast(month(n.ds) as int),m.ouid,NULL)) AS month6_retention
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
                date_format(ds,'yyyy-MM-01') AS ds
         FROM %(db)s.%(sourcetab)s
         WHERE ds IN (%(month_last_date)s)
           AND ut='uid') AS t
      WHERE date_format(first_login_time,'yyyy-MM-01')=ds) AS m
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
                date_format(ds,'yyyy-MM-01') AS ds
         FROM %(db)s.%(sourcetab)s
         WHERE ds IN (%(month_last_date)s)
           AND ut='uid') AS t
      WHERE date_format(last_login_time,'yyyy-MM-01')=ds) AS n ON m.ouid=n.ouid
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
          count(DISTINCT if(date_format(last_withdraw_time,'yyyy-MM-01')=ds,ouid,NULL)) AS month_withdraw_ouid_cnt,
          sum(month_withdraw_rmb) AS month_withdraw_rmb
   FROM
     (SELECT date_format(ds,'yyyy-MM-01') AS ds,
             ouid,
             maxbytimenotnull(nvl(last_os,'-1'),unix_timestamp(ds,'yyyy-MM-dd')) AS os,
             maxbytimenotnull(nvl(last_appver,'-1'),unix_timestamp(ds,'yyyy-MM-dd')) AS appver,
             maxbytimenotnull(nvl(last_channel,'-1'),unix_timestamp(ds,'yyyy-MM-dd')) AS channel,
             from_unixtime(max(last_withdraw_time),'yyyy-MM-dd') AS last_withdraw_time,
             sum(if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_rmb,0)) AS month_withdraw_rmb
      FROM %(db)s.%(sourcetab)s
      WHERE ds BETWEEN '%(begin_date)s' AND '%(end_date)s'
        AND ut='uid'
      GROUP BY date_format(ds,'yyyy-MM-01'),
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

delete = r'''echo "use %(db)s; delete from %(mysqltab)s where fds in(%(month_first_date)s); " |mysql -h %(host)s -u %(user)s -p%(pasw)s '''
sync = r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/%(db)s.db/%(targettab)s/ds=%(ds)s --connect jdbc:mysql://%(host)s:%(port)s/%(db)s --username "%(user)s" --password '%(pasw)s' --table "%(mysqltab)s" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''


def add_months(ds, months):
    year = time.strptime(ds, '%Y-%m-%d')[0]
    month = time.strptime(ds, '%Y-%m-%d')[1]
    new_month = (month+months-1)%12+1
    new_year = year+(month+months-1)//12
    # print(new_year, new_month)
    return datetime.datetime(new_year, new_month, 1).strftime('%Y-%m-%d')


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'db': 'wutiao',
        'sourcetab': 'idl_wutiao_user',
        'targettab': 'report_wutiao_retention_month',
        'mysqltab': 'report_wutiao_retention_month'
    }
    args['ds'] = kdc.dateAdd(1, args['ds'])
    monthday = time.strptime(args['ds'], '%Y-%m-%d')[2]
    if monthday != 1:
        print("run date is not first day of month: " + str(monthday))
        return
    args['begin_date'] = add_months(args['ds'], -7)
    args['end_date'] = kdc.dateSub(1, args['ds'])
    args['month_last_date'] = ''
    for i in range(0, 7):
        args['month_last_date'] = args['month_last_date'] + ',\'' + kdc.dateSub(1, add_months(args['ds'], -i)) + '\''
    args['month_last_date'] = args['month_last_date'].lstrip(',')
    # print(args['month_last_date'])
    sql = SQL_DAY % args
    kdc.debug = True
    kdc.doHive(sql)

    args['host'] = DB_PARAMS['host']
    args['port'] = DB_PARAMS['port']
    args['user'] = DB_PARAMS['user']
    args['pasw'] = DB_PARAMS['password']
    args['month_first_date'] = ''
    for i in range(1, 8):
        args['month_first_date'] = args['month_first_date'] + ',\'' + add_months(args['ds'], -i) + '\''
    args['month_first_date'] = args['month_first_date'].lstrip(',')
    # print(args['month_first_date'])
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
