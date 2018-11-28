# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-内容阅读播放
#*作者：Leo
#*时间：2018-07-18
#*备注：区块链-内容阅读播放
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
       sortid,
       total_article_sum,
       total_video_sum,
       day_read_content_sum,
       day_read_cnt,
       day_play_content_sum,
       day_play_cnt,
       day_valid_read_content_sum,
       day_valid_read_cnt,
       day_valid_play_content_sum,
       day_valid_play_cnt,
       day_active_did_cnt,
       day_read_did_cnt,
       day_play_did_cnt,
       day_validread_did_cnt,
       day_validplay_did_cnt,
       day_showinpage1_content_sum,
       day_showinpage1_cnt,
       day_showinpage2_content_sum,
       day_showinpage2_cnt
FROM
  (SELECT nvl(content_sortid,'allsortid') AS sortid,
          count(DISTINCT if(itemtype=1 AND audit_status in (2,5,6,9,12),itemid,null)) AS total_article_sum,
          count(DISTINCT if(itemtype=2 AND audit_status in (2,5,6,9,12),itemid,null)) AS total_video_sum,
          count(DISTINCT if(day_read_cnt>0,itemid,null)) AS day_read_content_sum,
          sum(day_read_cnt) AS day_read_cnt,
          count(DISTINCT if(day_play_cnt>0,itemid,null)) AS day_play_content_sum,
          sum(day_play_cnt) AS day_play_cnt,
          count(DISTINCT if(day_valid_read_cnt>0,itemid,null)) AS day_valid_read_content_sum,
          sum(day_valid_read_cnt) AS day_valid_read_cnt,
          count(DISTINCT if(day_valid_play_cnt>0,itemid,null)) AS day_valid_play_content_sum,
          sum(day_valid_play_cnt) AS day_valid_play_cnt,
          count(DISTINCT if(day_showinpage1_cnt>0,itemid,null)) AS day_showinpage1_content_sum,
          sum(day_showinpage1_cnt) AS day_showinpage1_cnt,
          count(DISTINCT if(day_showinpage2_cnt>0,itemid,null)) AS day_showinpage2_content_sum,
          sum(day_showinpage2_cnt) AS day_showinpage2_cnt,
          1 AS id
   FROM
     (SELECT m.item_id AS itemid,
             nvl(m.item_type,'-1') AS itemtype,
             nvl(m.content_sortid,'-1') AS content_sortid,
             audit_status,
             nvl(day_read_cnt,0) AS day_read_cnt,
             nvl(day_play_cnt,0) AS day_play_cnt,
             nvl(day_valid_read_cnt,0) AS day_valid_read_cnt,
             nvl(day_valid_play_cnt,0) AS day_valid_play_cnt,
             nvl(day_showinpage1_cnt,0) AS day_showinpage1_cnt,
             nvl(day_showinpage2_cnt,0) AS day_showinpage2_cnt
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
                if(itemtype=1,day_read_cnt,0) AS day_read_cnt,
                if(itemtype=2,day_play_cnt,0) AS day_play_cnt,
                if(itemtype=1,day_validread_cnt,0) AS day_valid_read_cnt,
                if(itemtype=2,day_validread_cnt,0) AS day_valid_play_cnt,
                if(itemtype=1,day_showinpage_cnt,0) AS day_showinpage1_cnt,
                if(itemtype=2,day_showinpage_cnt,0) AS day_showinpage2_cnt
         FROM %(sourcetab2)s
         WHERE ds='%(ds)s') AS n ON m.item_id=n.itemid) AS t
   GROUP BY content_sortid WITH CUBE) AS x
INNER JOIN
  (SELECT count(DISTINCT if(event in('login','active','openclient','enterfront','register'),did,null)) AS day_active_did_cnt,
          count(distinct if(event = 'read',did, null)) AS day_read_did_cnt,
          count(distinct if(event = 'play' AND type=1,did, null)) AS day_play_did_cnt,
          count(distinct if(event = 'validread' and itemtype=1,did, null)) AS day_validread_did_cnt,
          count(distinct if(event = 'validread' and itemtype=2,did, null)) AS day_validplay_did_cnt,
          1 AS id
   FROM %(sourcetab3)s
   WHERE ds='%(ds)s'
     AND nvl(did,'-1')<>'-1'
     AND event in('login','active','openclient','enterfront','read','play','validread','register')) AS y ON x.id=y.id
;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.idl_wutiao_useritemid_sync',
        'sourcetab2': 'wutiao.idl_wutiao_itemid_wb',
        'sourcetab3': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.report_wutiao_content_readplay_sync',
        'mysqltab': 'wutiao.report_wutiao_content_readplay_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT  fds,sortid,total_article_sum,total_video_sum,day_read_content_sum,day_read_cnt
                            ,day_play_content_sum,day_play_cnt,day_valid_read_content_sum,day_valid_read_cnt
                            ,day_valid_play_content_sum,day_valid_play_cnt,day_active_did_cnt,day_read_did_cnt
                            ,day_play_did_cnt,day_validread_did_cnt,day_validplay_did_cnt,day_showinpage1_content_sum
                            ,day_showinpage1_cnt,day_showinpage2_content_sum,day_showinpage2_cnt
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
