# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-nps月数据
#*作者：Leo
#*时间：2018-08-01
#*备注：区块链-nps月数据
#***************************************************************#
import kingnetdc
import sys
import os
import datetime
import time
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_YX

DB_PARAMS = DB_PARAMS_YX

SQL_DAY = '''

INSERT overwrite TABLE %(targettab)s partition (dt='%(dt)s',ds='%(ds)s')
SELECT '%(ds)s' AS fds,
       count(t.activeid) AS active_user_cnt,
       count(DISTINCT if(invite_user_cnt>=2,t.fouid,NULL)) AS create_user_cnt,
       nvl(sum(if(invite_user_cnt>=2,t.invite_user_cnt,0)),0) AS invited_user_cnt,
       count(DISTINCT if(invite_user_cnt>=5,t.fouid,NULL)) AS recommend_user_cnt,
       count(DISTINCT if(invite_user_cnt IN (3,4),t.fouid,NULL)) AS unactive_user_cnt,
       count(DISTINCT if(invite_user_cnt=2,t.fouid,NULL)) AS derogate_user_cnt
FROM
  (SELECT m.ouid AS activeid,
          n.ouid AS fouid,
          n.invite_user_cnt
   FROM
     (SELECT ouid
      FROM %(sourcetab1)s
      WHERE ds='%(ds)s'
        AND ut='uid'
        AND from_unixtime(last_login_time,'yyyy-MM-dd') BETWEEN '%(dsm)s' AND '%(ds)s') AS m
   LEFT JOIN
     (SELECT ouid,
             familyid,
             createid,
             count(ouid)over(partition BY familyid) AS invite_user_cnt
      FROM %(sourcetab2)s
      WHERE ds='%(ds)s'
        AND ut='uid') AS n ON m.ouid=n.ouid
   AND createid IS NOT NULL) AS t
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'dt': 'month',
        'sourcetab1': 'wutiao.idl_wutiao_user',
        'sourcetab2': 'wutiao.idl_wutiao_user_family',
        'targettab': 'wutiao.report_wutiao_nps_output',
        'mysqltab': 'user_market_center.report_wutiao_nps_output'
    }
    args['ds'] = kdc.dateAdd(1, args['ds'])
    monthday = time.strptime(args['ds'], '%Y-%m-%d')[2]
    if monthday != 1:
        print("run date is not first day of month: " + str(monthday))
        return
    args['ds'] = kdc.dateSub(1, args['ds'])
    days = time.strptime(args['ds'], '%Y-%m-%d')[2]
    args['dsm'] = kdc.dateSub(days-1, args['ds'])
    sql = SQL_DAY % args
    kdc.debug = True
    kdc.doHive(sql)

    presto_sql = ''' SELECT  dt,fds,active_user_cnt,create_user_cnt,invited_user_cnt,recommend_user_cnt
                            ,unactive_user_cnt,derogate_user_cnt
                     FROM %(targettab)s WHERE dt='%(dt)s' and ds='%(ds)s' ''' % args
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
