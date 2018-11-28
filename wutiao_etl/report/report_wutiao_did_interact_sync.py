# coding: utf-8

#**********************程序说明*********************************#
#*模块：report
#*功能：区块链-设备阅读播放
#*作者：Leo
#*时间：2018-08-24
#*备注：区块链-设备阅读播放
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

INSERT overwrite TABLE %(db)s.%(targettab)s partition (ds='%(ds)s')
SELECT '%(ds)s' AS fds,
       os,
       appver,
       channel,
       didflag,
       itemtype,
       sortid,
       day_share_cnt,
       day_favor_cnt,
       day_comment_cnt,
       day_like_cnt,
       day_attention_cnt,
       day_report_cnt,
       day_nointerest_cnt,
       max(day_activedid_cnt) over (partition by os,appver,channel,didflag),
       day_share_did_cnt,
       day_favor_did_cnt,
       day_comment_did_cnt,
       day_like_did_cnt,
       day_attention_did_cnt
FROM(
SELECT nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       nvl(new_flag,'alldidflag') AS didflag,
       nvl(itemtype,'allitemtype') AS itemtype,
       nvl(content_sortid,'allsortid') AS sortid,
       count(sharedid) AS day_share_cnt,
       count(favordid) AS day_favor_cnt,
       count(commentdid) AS day_comment_cnt,
       count(likedid) AS day_like_cnt,
       count(attentiondid) AS day_attention_cnt,
       count(reportdid) AS day_report_cnt,
       count(nointerestdid) AS day_nointerest_cnt,
       count(DISTINCT activedid) AS day_activedid_cnt,
       count(DISTINCT sharedid) AS day_share_did_cnt,
       count(DISTINCT favordid) AS day_favor_did_cnt,
       count(DISTINCT commentdid) AS day_comment_did_cnt,
       count(DISTINCT likedid) AS day_like_did_cnt,
       count(DISTINCT attentiondid) AS day_attention_did_cnt
FROM
  (SELECT nvl(os,'-1') AS os,
          nvl(appver,'-1') AS appver,
          nvl(channel,'-1') AS channel,
          nvl(itemtype,'-1') AS itemtype,
          nvl(content_sortid,'-1') AS content_sortid,
          nvl(new_flag,'-1') AS new_flag,
          if(event in('login','active','openclient','enterfront','register'),did,NULL) AS activedid,
          if(event = 'share' AND status='3',did,NULL) AS sharedid,
          if(event = 'favour' AND status='1',did,NULL) AS favordid,
          if(event = 'comment' AND status = '1',did,NULL) AS commentdid,
          if(event = 'like' AND status='1' AND type=1,did,NULL) AS likedid,
          if(event = 'attention' AND status='1',did,NULL)AS attentiondid,
          if(event = 'report' AND status='1',did,NULL) AS reportdid,
          if(event = 'nointerest',did,NULL) AS nointerestdid
   FROM
     (SELECT did,
             ts,
             event,
             os,
             appver,
             channel,
             itemid,
             itemtype,
             status,
             type,
             content_sortid,
             new_flag
      FROM
        (SELECT nvl(did,'-1') as did,
                ts,
                event,
                os,
                appver,
                channel,
                nvl(itemid,'-1') as itemid,
                nvl(itemtype,'-1') as itemtype,
                status,
                type
         FROM %(db)s.%(sourcetab1)s
         WHERE ds='%(ds)s'
           AND ((event in('login','active','openclient','enterfront','attention','report','nointerest') AND eventtype='high') 
            OR (event in('like','share','comment','register') AND eventtype='super'))
        UNION ALL
        SELECT nvl(did,'-1') as did,
               ts,
               event,
               os,
               appver,
               channel,
               nvl(split(itemid,'@')[0],'-1') as itemid,
               if(split(itemid,'@')[2]='article','1','2') as itemtype,
               status,
               type
          FROM (SELECT did,ts,event,os,appver,channel,status,type,itemlist
                  FROM %(db)s.%(sourcetab1)s
                 WHERE ds='%(ds)s'
                   AND event='favour'
                   AND eventtype='high'
               ) AS t
               LATERAL VIEW EXPLODE (split(itemlist,',')) item as itemid
        )AS a
      LEFT JOIN
        (SELECT cast(item_id as string) as item_id,
                item_type,
                content_sortid
         FROM %(db)s.%(sourcetab2)s
         WHERE ds='%(ds)s'
        ) AS b ON a.itemid=b.item_id
      AND a.itemtype=b.item_type
      LEFT JOIN
        (SELECT ouid,
                if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) AS new_flag
         FROM %(db)s.%(sourcetab3)s
         WHERE ds='%(ds)s'
           AND ut='did'
        ) AS o ON a.did=o.ouid 
     )AS c
 ) AS d
GROUP BY os,
         appver,
         channel,
         itemtype,
         content_sortid,
         new_flag
WITH CUBE) AS t
;
'''

delete = r'''echo "use %(db)s; delete from %(mysqltab)s where fds='%(ds)s'; " |mysql -h %(host)s -u %(user)s -p%(pasw)s '''
sync = r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/%(db)s.db/%(targettab)s/ds=%(ds)s --connect jdbc:mysql://%(host)s:%(port)s/%(db)s --username "%(user)s" --password '%(pasw)s' --table "%(mysqltab)s" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'db': 'wutiao',
        'sourcetab1': 'bdl_wutiao_event',
        'sourcetab2': 'idl_wutiao_useritemid_sync',
        'sourcetab3': 'idl_wutiao_user',
        'targettab': 'report_wutiao_did_interact_sync',
        'mysqltab': 'report_wutiao_did_interact_sync'
    }
    sql = SQL_DAY % args
    kdc.debug = True
    # print sql
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
