# coding: utf-8

#**********************程序说明*********************************#
#*模块：idl
#*功能：区块链-用户内容中间表
#*作者：Leo
#*时间：2018-08-23
#*备注：区块链-用户内容中间表
#***************************************************************#

import kingnet

SQL_DAY = '''
INSERT overwrite TABLE %(targettab)s partition (ds='%(ds)s')
SELECT nvl(t1.uid,t2.uid) AS uid,
       nvl(t1.item_id,t2.item_id) AS item_id,
       nvl(t1.item_type,t2.item_type) AS item_type,
       nvl(if(o.level=0,o.field_id,o.parent_id),t2.content_sortid) AS content_sortid,
       nvl(t1.audit_status,t2.audit_status) AS audit_status,
       nvl(t2.audit_status,0) AS last_audit_status,
       nvl(t1.create_dt,t2.create_dt) AS create_dt,
       nvl(t1.update_dt,t2.update_dt) AS update_dt,
       nvl(t1.last_edit_time,t2.last_edit_time) AS last_edit_time
FROM
  (SELECT nvl(m.uid,n.uid) as uid,
          nvl(m.item_id,n.item_id) as item_id,
          nvl(m.item_type,n.item_type) as item_type,
          nvl(m.content_sortid,n.content_sortid) as content_sortid,
          n.audit_status,
          m.create_dt,
          m.update_dt,
          n.last_edit_time
   FROM
     (SELECT uid,
             item_id,
             if(item_type='article',1,2) as item_type,
             split(split(category,'\;')[0],'\,')[0] AS content_sortid,
             create_time AS create_dt,
             update_time AS update_dt
      FROM %(sourcetab)s
      WHERE ds='%(ds)s'
        AND status=0) AS m
   FULL OUTER JOIN
     (SELECT cast(media_id as bigint) as uid,
             cast(content_id as bigint) as item_id,
             audit_content_type as item_type,
             split(split(content_type,'\;')[0],'\,')[0] as content_sortid,
             audit_status,
             last_edit_time
      FROM %(sourcetab2)s
      WHERE ds='%(ds)s') AS n ON m.item_id=n.item_id) AS t1
LEFT JOIN %(sourcetab3)s AS o ON t1.content_sortid=o.field_id
FULL OUTER JOIN
  (SELECT *
   FROM %(targettab)s
   WHERE ds=date_sub('%(ds)s',1)) AS t2 ON t1.item_id=t2.item_id
;
'''


def main():
    kdc = kingnet.kdc()
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.odl_wutiao_mp_article_sync',
        'sourcetab2': 'wutiao.odl_wutiao_audit_contents_sync',
        'sourcetab3': 'wutiao.odl_wutiao_field_sync',
        'targettab': 'wutiao.idl_wutiao_useritemid_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
