# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-发现者统计分析
#*作者：Leo
#*时间：2018-07-20
#*备注：区块链-发现者统计分析
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
INSERT overwrite TABLE %(targettab)s partition (ds='%(ds)s')
SELECT '%(ds)s' AS fds,
       x.itemtype,
       x.sortid,
       day_new_discoverer_cnt,
       day_active_discoverer_cnt,
       day_all_discoverer_cnt,
       nvl(day_voted_discoverer_cnt,0) as day_voted_discoverer_cnt,
       day_voted_content_sum,
       day_voted_content_cnt,
       day_content_sum
FROM
  (SELECT nvl(item_type,'allitemtype') AS itemtype,
          nvl(content_sortid,'allsortid') AS sortid,
          count(item_id) AS day_content_sum,
          count(if(day_itemid_discover_vote_cnt>0 ,item_id,NULL)) AS day_voted_content_sum,
          sum(nvl(day_itemid_discover_vote_cnt,0)) AS day_voted_content_cnt,
          1 AS id
   FROM
     (SELECT item_id,
             nvl(item_type,'-1') AS item_type,
             nvl(content_sortid,'-1') AS content_sortid,
             audit_status,
             last_edit_time
      FROM %(sourcetab1)s
      WHERE ds='%(ds)s'
        AND audit_status in (2,5,6,9,12)
     ) AS m
   LEFT JOIN
     (SELECT itemid,
             sum(nvl(day_itemid_discover_vote_cnt,0)) as day_itemid_discover_vote_cnt
      FROM %(sourcetab2)s
      WHERE ds='%(ds)s'
      GROUP BY itemid) AS n ON m.item_id=n.itemid
   GROUP BY item_type,
            content_sortid
   WITH CUBE) AS x
INNER JOIN
  (SELECT count(if(datediff(create_dt,'%(ds)s')=0,uid,NULL)) AS day_new_discoverer_cnt,
          count(if(datediff(last_login_time,'%(ds)s')=0,uid,NULL)) AS day_active_discoverer_cnt,
          count(uid) AS day_all_discoverer_cnt,
          1 AS id
   FROM
     (SELECT uid,
             create_dt,
             update_dt
      FROM %(sourcetab3)s
      WHERE ds='%(ds)s'
        AND status=1) AS m
   LEFT JOIN
     (SELECT cast(ouid as bigint) as ouid,
             from_unixtime(last_login_time,'yyyy-MM-dd') AS last_login_time,
             from_unixtime(last_vote_time,'yyyy-MM-dd') AS last_vote_time,
             nvl(last_vote_cnt,0) AS last_vote_cnt
      FROM %(sourcetab4)s
      WHERE ds='%(ds)s'
        AND ut='uid') AS n ON m.uid=n.ouid) AS y ON x.id=y.id
LEFT JOIN
  (SELECT nvl(item_type,'allitemtype') AS itemtype,
          nvl(content_sortid,'allsortid') AS sortid,
          count(DISTINCT ouid) AS day_voted_discoverer_cnt
    FROM
     (SELECT ouid,itemid
        FROM %(sourcetab5)s
       WHERE ds='%(ds)s'
         AND event='like'
         AND eventtype='super'
         AND type=3) AS m
     INNER JOIN
     (SELECT item_id,
             nvl(item_type,'-1') AS item_type,
             nvl(content_sortid,'-1') AS content_sortid,
             audit_status,
             last_edit_time
      FROM %(sourcetab1)s
      WHERE ds='%(ds)s'
        AND audit_status in (2,5,6,9,12)
     ) AS n
     ON m.itemid=n.item_id
     GROUP BY item_type,
              content_sortid
     WITH CUBE
  ) AS z 
  ON x.itemtype=z.itemtype
  AND x.sortid=z.sortid
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.idl_wutiao_useritemid_sync',
        'sourcetab2': 'wutiao.idl_wutiao_itemid_wb',
        'sourcetab3': 'wutiao.idl_wutiao_discoverer_sync',
        'sourcetab4': 'wutiao.idl_wutiao_user',
        'sourcetab5': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.report_wutiao_discoverer_sync',
        'mysqltab': 'wutiao.report_wutiao_discoverer_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT fds,itemtype,sortid,day_new_discoverer_cnt,day_active_discoverer_cnt,day_all_discoverer_cnt
                           ,day_voted_discoverer_cnt,day_voted_content_sum,day_voted_content_cnt,day_content_sum
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
