# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-自媒体入驻分析
#*作者：Leo
#*时间：2018-07-13
#*备注：区块链-自媒体入驻分析
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
       nvl(welfaretype,'allwelfaretype') AS welfaretype,
       nvl(organtype,'allorgantype') AS organtype,
       nvl(mediauser_sortid,'allsortid') AS mediauser_sortid,
       count(distinct if(apply_status=1,uid,NULL)) AS his_mediauser_apply_sum,
       count(distinct if(apply_status=1 and (last_status=-1 or (last_status!=current_status and current_status in (0,1))),uid,NULL)) AS day_mediauser_apply_sum,
       count(distinct if(success_status=2,uid,NULL)) AS his_mediauser_success_sum,
       count(distinct if(current_status=2 AND last_status<>2,uid,NULL)) AS day_mediauser_success_sum,
       count(distinct if(datediff(create_dt,'%(ds)s')=0,uid,NULL)) AS day_uploadcontent_mediauser_sum,
       count(distinct if(datediff(create_dt,'%(ds)s')=0,item_id,NULL)) AS day_uploadcontent_sum,
       count(distinct if(last_audit_status not in (2,5,6,9,12) AND audit_status in (2,5,6,9,12),item_id,NULL)) AS new_audit_content_sum,
       count(distinct if(datediff(create_dt,'%(ds)s')=0 AND datediff(insert_dt,'%(ds)s')=0,item_id,NULL)) AS new_insert_day_content_sum,
       count(distinct if(datediff(create_dt,'%(ds)s')<0 AND datediff(insert_dt,'%(ds)s')=0,item_id,NULL)) AS new_insert_his_content_sum,
       count(distinct if(datediff(create_dt,'%(ds)s')=0 or datediff(insert_dt,'%(ds)s')=0,item_id,NULL)) AS new_day_content_sum
FROM
  (SELECT nvl(m.mediauser_type,'-1') AS mediauser_type,
          nvl(m.welfaretype,'-1') AS welfaretype,
          nvl(m.organtype,'-1') AS organtype,
          nvl(m.mediauser_sortid,'-1') AS mediauser_sortid,
          nvl(n.content_sortid,'-1') AS content_sortid,
          m.uid,
          m.apply_status,
          m.success_status,
          m.current_status,
          m.last_status,
          n.item_id,
          n.create_dt,
          n.insert_dt,
          n.audit_status,
          n.last_audit_status,
          n.last_edit_time
   FROM
     (SELECT *
      FROM %(sourcetab1)s
      WHERE ds='%(ds)s') AS m
   LEFT JOIN
     (SELECT *
      FROM %(sourcetab2)s
      WHERE ds='%(ds)s') AS n ON m.uid=n.uid) AS t
GROUP BY welfaretype,
         organtype,
         mediauser_sortid WITH CUBE
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.idl_wutiao_mediauser_sync',
        'sourcetab2': 'wutiao.idl_wutiao_useritemid_sync',
        'targettab': 'wutiao.report_wutiao_mediauser_sync',
        'mysqltab': 'wutiao.report_wutiao_mediauser_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT fds,welfaretype,organtype,mediauser_sortid,his_mediauser_apply_sum,day_mediauser_apply_sum
                           ,his_mediauser_success_sum,day_mediauser_success_sum,day_uploadcontent_mediauser_sum
                           ,day_uploadcontent_sum,new_audit_content_sum,new_insert_day_content_sum
                           ,new_insert_his_content_sum,new_day_content_sum
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
