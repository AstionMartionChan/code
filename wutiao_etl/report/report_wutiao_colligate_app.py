# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-手机端-综合数据
#*作者：Leo
#*时间：2018-08-17
#*备注：区块链-手机端-综合数据
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
SELECT nvl(a.ds,d.ds) AS fds,
       nvl(a.os,d.os) AS os,
       nvl(a.appver,d.appver) AS appver,
       nvl(a.channel,d.channel) AS channel,
       nvl(d.total_newdid_cnt,0) AS total_newdid_cnt,
       nvl(d.activedid_cnt,0) AS activedid_cnt,
       nvl(d.newdid_cnt,0) AS newdid_cnt,
       nvl(a.total_newuser_cnt,0) AS total_newuser_cnt,
       nvl(a.activeuser_cnt,0) AS activeuser_cnt,
       nvl(a.newuser_cnt,0) AS newuser_cnt,
       nvl(c.day_withdraw_success_usercnt,0) AS withdraw_success_usercnt,
       nvl(c.day_withdraw_success_rmb,0) AS withdraw_success_rmb,
       nvl(b.day1_retention,0) AS day1_retention,
       nvl(b.day3_retention,0) AS day3_retention,
       nvl(b.day7_retention,0) AS day7_retention
FROM
  (SELECT os,
          appver,
          channel,
          total_newuser_cnt,
          activeuser_cnt,
          newuser_cnt,
          ds
   FROM %(db)s.%(sourcetab1)s
   WHERE ds between '%(ds2)s' and '%(ds)s') AS a
LEFT JOIN
  (SELECT os,
          appver,
          channel,
          day1_retention,
          day3_retention,
          day7_retention,
          ds
   FROM %(db)s.%(sourcetab2)s
   WHERE ds between '%(ds2)s' and '%(ds)s') AS b ON a.os=b.os
AND a.appver=b.appver
AND a.channel=b.channel
AND a.ds=b.ds
LEFT JOIN
  (SELECT os,
          appver,
          channel,
          day_withdraw_success_rmb,
          day_withdraw_success_usercnt,
          ds
   FROM %(db)s.%(sourcetab3)s
   WHERE ds between '%(ds2)s' and '%(ds)s'
     AND user_flag='alluserflag'
     AND (moneytype='wb' or moneytype is null)
   )AS c ON a.os=c.os
AND a.appver=c.appver
AND a.channel=c.channel
AND a.ds=c.ds
FULL OUTER JOIN
  (SELECT os,
          appver,
          channel,
          total_newdid_cnt,
          activedid_cnt,
          newdid_cnt,
          ds
   FROM %(db)s.%(sourcetab4)s
   WHERE ds between '%(ds2)s' and '%(ds)s') AS d ON a.os=d.os
AND a.appver=d.appver
AND a.channel=d.channel
AND a.ds=d.ds
;
'''

delete = r'''echo "use %(db)s; delete from %(mysqltab)s where fds between '%(ds2)s' and '%(ds)s'; " |mysql -h %(host)s -u %(user)s -p%(pasw)s '''
sync = r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/%(db)s.db/%(targettab)s/ds=%(ds)s --connect jdbc:mysql://%(host)s:%(port)s/%(db)s --username "%(user)s" --password '%(pasw)s' --table "%(mysqltab)s" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'db': 'wutiao',
        'sourcetab1': 'report_wutiao_activeuser',
        'sourcetab2': 'report_wutiao_retention',
        'sourcetab3': 'report_wutiao_total_assets',
        'sourcetab4': 'report_wutiao_activedid',
        'targettab': 'report_wutiao_colligate_app',
        'mysqltab': 'report_wutiao_colligate_app'
    }
    args['ds2'] = kdc.dateSub(7, args['ds'])
    sql = SQL_DAY % args
    kdc.debug = True
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
