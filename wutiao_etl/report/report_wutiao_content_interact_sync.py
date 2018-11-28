# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-内容互动分析
#*作者：Leo
#*时间：2018-07-16
#*备注：区块链-内容互动分析
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST

SQL_DAY = '''

INSERT overwrite TABLE %(targettab)s partition (ds='%(ds)s')
SELECT '%(ds)s' AS fds,
       itemtype,
       sortid,
       total_itemid_sum,
       day_readplay_content_sum,
       day_share_content_sum,
       day_share_cnt,
       day_favour_content_sum,
       day_favour_cnt,
       day_comment_content_sum,
       day_comment_cnt,
       day_like_content_sum,
       day_like_cnt,
       day_attention_cnt,
       day_nointerest_cnt,
       day_report_cnt,
       day_active_did_cnt,
       day_readplay_did_cnt,
       day_share_did_cnt,
       day_favor_did_cnt,
       day_comment_did_cnt,
       day_like_did_cnt,
       day_attention_did_cnt
FROM
  (SELECT nvl(itemtype,'allitemtype') AS itemtype,
          nvl(content_sortid,'allsortid') AS sortid,
          count(DISTINCT if(audit_status in (2,5,6,9,12),itemid,NULL)) AS total_itemid_sum,
          sum(day_readplay_cnt) AS day_readplay_content_sum,
          count(DISTINCT if(day_share_cnt>0,itemid,NULL)) AS day_share_content_sum,
          sum(day_share_cnt) AS day_share_cnt,
          count(DISTINCT if(day_favour_cnt>0,itemid,NULL)) AS day_favour_content_sum,
          sum(day_favour_cnt) AS day_favour_cnt,
          count(DISTINCT if(day_comment_cnt>0,itemid,NULL)) AS day_comment_content_sum,
          sum(day_comment_cnt) AS day_comment_cnt,
          count(DISTINCT if(day_like_cnt>0,itemid,NULL)) AS day_like_content_sum,
          sum(day_like_cnt) AS day_like_cnt,
          sum(day_nointerest_cnt) AS day_nointerest_cnt,
          sum(day_report_cnt) AS day_report_cnt,
          1 as id
   FROM
     (SELECT m.item_id AS itemid,
             nvl(m.item_type,'-1') AS itemtype,
             nvl(m.content_sortid,'-1') AS content_sortid,
             audit_status,
             nvl(day_successshare_cnt,0) AS day_share_cnt,
             nvl(day_addfavour_cnt,0) AS day_favour_cnt,
             nvl(day_addcomment_cnt,0) AS day_comment_cnt,
             nvl(day_itemid_like_cnt,0) AS day_like_cnt,
             nvl(day_nointerest_cnt,0) AS day_nointerest_cnt,
             nvl(day_report_cnt,0) AS day_report_cnt,
             if((day_read_cnt>0 and itemtype=1) or (day_play_cnt>0 and itemtype=2),1,0) AS day_readplay_cnt
      FROM
        (SELECT item_id,
                item_type,
                uid,
                content_sortid,
                audit_status
         FROM %(sourcetab1)s
         WHERE ds='%(ds)s'
        ) AS m
      LEFT JOIN
        (SELECT itemid,
                itemtype,
                day_successshare_cnt,
                day_addfavour_cnt,
                day_delfavour_cnt,
                day_addcomment_cnt,
                day_delcomment_cnt,
                day_itemid_like_cnt,
                day_nointerest_cnt,
                day_report_cnt,
                day_read_cnt,
                day_play_cnt
         FROM %(sourcetab2)s
         WHERE ds='%(ds)s') AS n ON m.item_id=n.itemid) AS t
   GROUP BY itemtype,
            content_sortid WITH CUBE) AS x
INNER JOIN
  (SELECT count(DISTINCT ouid) AS day_active_did_cnt,
          count(DISTINCT if(from_unixtime(last_share_time,'yyyy-MM-dd')=ds,ouid,null)) AS day_share_did_cnt,
          count(DISTINCT if(from_unixtime(last_favor_time,'yyyy-MM-dd')=ds,ouid,null)) AS day_favor_did_cnt,
          count(DISTINCT if(from_unixtime(last_comment_time,'yyyy-MM-dd')=ds or from_unixtime(last_reply_time,'yyyy-MM-dd')=ds,ouid,null)) AS day_comment_did_cnt,
          count(DISTINCT if(from_unixtime(last_like_time,'yyyy-MM-dd')=ds,ouid,null)) AS day_like_did_cnt,
          count(DISTINCT if(from_unixtime(last_read_time,'yyyy-MM-dd')=ds or from_unixtime(last_play_time,'yyyy-MM-dd')=ds,ouid,null)) AS day_readplay_did_cnt,
          count(DISTINCT if(from_unixtime(last_attention_time,'yyyy-MM-dd')=ds,ouid,null)) AS day_attention_did_cnt,
          sum(if(from_unixtime(last_attention_time,'yyyy-MM-dd')=ds,last_attention_cnt,0)) AS day_attention_cnt,
          1 as id
   FROM %(sourcetab3)s
   WHERE ds='%(ds)s'
     AND ut='did'
     AND from_unixtime(last_login_time,'yyyy-MM-dd')=ds) AS y ON x.id=y.id
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.idl_wutiao_useritemid_sync',
        'sourcetab2': 'wutiao.idl_wutiao_itemid_wb',
        'sourcetab3': 'wutiao.idl_wutiao_user',
        'targettab': 'wutiao.report_wutiao_content_interact_sync',
        'mysqltab': 'wutiao.report_wutiao_content_interact_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT  fds,itemtype,sortid,total_itemid_sum,day_readplay_content_sum,day_share_content_sum
                            ,day_share_cnt,day_favour_content_sum,day_favour_cnt,day_comment_content_sum,day_comment_cnt
                            ,day_like_content_sum,day_like_cnt,day_attention_cnt,day_nointerest_cnt,day_report_cnt
                            ,day_active_did_cnt,day_readplay_did_cnt,day_share_did_cnt,day_favor_did_cnt
                            ,day_comment_did_cnt,day_like_did_cnt,day_attention_did_cnt
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
