# coding: utf-8

#**********************程序说明*********************************#
#*模块：idl
#*功能：区块链-自媒体用户中间表
#*作者：Leo
#*时间：2018-07-13
#*备注：区块链-自媒体用户中间表
#***************************************************************#

import kingnetdc

SQL_DAY = '''
INSERT overwrite TABLE %(targettab)s partition (ds='%(ds)s')
SELECT nvl(t1.uid,t2.uid) AS uid,
       nvl(t1.mediauser_type,t2.mediauser_type) AS mediauser_type,
       nvl(t1.welfaretype,t2.welfaretype) AS welfaretype,
       nvl(t1.organtype,t2.organtype) AS organtype,
       nvl(t1.mediauser_sortid,t2.mediauser_sortid) AS mediauser_sortid,
       nvl(t1.apply_status,t2.apply_status) AS apply_status,
       nvl(t1.success_status,t2.success_status) AS success_status,
       nvl(t1.current_status,t2.current_status) AS current_status,
       nvl(t2.current_status,-1) AS last_status,
       nvl(t1.role,t2.role) AS role,
       nvl(t1.create_dt,t2.create_dt) AS create_dt,
       nvl(t1.update_dt,t2.update_dt) AS update_dt
FROM
  (SELECT nvl(m.uid,n.uid) as uid,
          m.mediauser_type,
          n.welfaretype,
          n.organtype,
          o.field_id as mediauser_sortid,
          n.apply_status,
          n.success_status,
          n.current_status,
          m.role,
          m.create_dt,
          if(n.update_dt>m.update_dt,n.update_dt,m.update_dt) AS update_dt
   FROM
     (SELECT uid,
             source AS mediauser_type,
             field,
             role,
             create_dt,
             update_dt
      FROM %(sourcetab)s
      WHERE ds='%(ds)s') AS m
   LEFT JOIN %(sourcetab3)s AS o ON m.field=o.field_name AND o.level=0
   FULL OUTER JOIN
     (SELECT uid,
             type AS organtype,
             public_welfare AS welfaretype,
             1 AS apply_status,
             if(status=2,status,null) AS success_status,
             status AS current_status,
             create_dt,
             update_dt
      FROM %(sourcetab2)s
      WHERE ds='%(ds)s') AS n ON m.uid=n.uid) AS t1
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
        'sourcetab': 'wutiao.odl_wutiao_users_sync',
        'sourcetab2': 'wutiao.odl_wutiao_user_authentication_sync',
        'sourcetab3': 'wutiao.odl_wutiao_field_sync',
        'targettab': 'wutiao.idl_wutiao_mediauser_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
