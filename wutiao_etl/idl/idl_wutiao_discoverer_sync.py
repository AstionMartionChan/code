# coding: utf-8

#**********************程序说明*********************************#
#*模块：idl
#*功能：区块链-发现者中间表
#*作者：Leo
#*时间：2018-07-20
#*备注：区块链-发现者中间表
#***************************************************************#

import kingnetdc

SQL_DAY = '''
INSERT overwrite TABLE %(targettab)s partition (ds='%(ds)s')
SELECT nvl(t1.uid,t2.uid) AS uid,
       nvl(t1.last_apply_date,t2.last_apply_date) AS last_apply_date,
       nvl(t1.last_quit_date,t2.last_quit_date) AS last_quit_date,
       nvl(t1.next_apply_date,t2.next_apply_date) AS next_apply_date,
       nvl(t1.status,t2.status) AS status,
       nvl(t1.create_dt,t2.create_dt) AS create_dt,
       nvl(t1.update_dt,t2.update_dt) AS update_dt
FROM
  (SELECT uid
         ,last_apply_date
         ,last_quit_date
         ,next_apply_date
         ,status
         ,create_dt
         ,update_dt
   FROM %(sourcetab)s
   WHERE ds='%(ds)s') AS t1
FULL OUTER JOIN
  (SELECT *
   FROM %(targettab)s
   WHERE ds=date_sub('%(ds)s',1)) AS t2 ON t1.uid=t2.uid
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.odl_wutiao_discoverer_sync',
        'targettab': 'wutiao.idl_wutiao_discoverer_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
