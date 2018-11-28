# coding: utf-8
# adl_wutiao_play_read_statistics

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 区块链播放阅读事件统计
#*作者: Leo
#*时间: 2018-05-31
#*备注: 区块链播放阅读事件统计
#***************************************************************#

import kingnetdc

SQL_DAY = '''
insert overwrite table {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds,
       os,
       channel,
       sortid,
       usertype,
       itemtype,
       sum(if(event='play' AND itemtype=2 AND type=1,1,0)) AS his_play_cnt,
       sum(if(event='play' AND itemtype=2 AND type=1,1,0)) AS play_cnt,
       sum(if(event='validread' AND itemtype=2,1,0)) AS valid_play_cnt,
       sum(if(event='play' AND itemtype=2 AND type=3,1,0)) AS finish_play_cnt,
       sum(if(event='play' AND itemtype=2 AND type=3,staytime,0)) AS playtime,
       sum(if(event='click' AND itemtype=2 AND target='P_1',1,0)) AS recommend_play_cnt,
       sum(if(event='read' AND itemtype=1,1,0)) AS his_read_cnt,
       sum(if(event='read' AND itemtype=1,1,0)) AS read_cnt,
       sum(if(event='validread' AND itemtype=1,1,0)) AS valid_read_cnt,
       0 AS finish_read_cnt,
       sum(if(event='leaveread' AND itemtype=1,1,0)) AS leaveread_cnt,
       sum(if(event='leaveread' AND itemtype=1,staytime,0)) AS readtime,
       sum(if(event='click' AND itemtype=1 AND target='P_1',1,0)) AS recommend_read_cnt,
       count(DISTINCT if(event='click', target,NULL)) AS pagenum,
       sum(if(event='comment' AND replyid IS NULL,1,0)) AS comment_cnt,
       0 AS reforward_cnt,
       0 AS reward_cnt,
       sum(if(event='attention' AND status=1,1,0)) AS attention_cnt,
       sum(if(event='attention' AND status=-1,1,0)) AS cancel_attention_cnt,
       sum(if(event='favor' AND status=1,1,0)) AS favor_cnt,
       sum(if(event='favor' AND status=-1,1,0)) AS cancel_favor_cnt,
       sum(if(event='like' AND status=1,1,0)) AS like_cnt,
       sum(if(event='like' AND status=-1,1,0)) AS cancel_like_cnt,
       sum(if(event='comment',1,0)) AS reply_cnt
FROM
  (SELECT nvl(nvl(t3.os, t2.os ),'-1') AS os,
          nvl(nvl(t3.channel, t2.channel ),'-1') AS channel,
          nvl(t1.sortid,'-1') AS sortid,
          nvl(t1.itemtype,'-1') AS itemtype,
          t1.event,
          nvl(nvl(t1.ouid,t1.did_ouid),'-1') as ouid,
          t1.target,
          t1.status,
          t1.replyid,
          t1.staytime,
          t1.type,
          CASE
              WHEN (t2.last_did_ouid_time IS NULL or t2.last_did_ouid_time<>t1.ds) THEN 'visitor'
              WHEN datediff(t3.first_login_time,t1.ds)<0 THEN 'olduser'
              WHEN datediff(t3.first_login_time,t1.ds)=0 THEN 'newuser'
              ELSE '-1'
          END AS usertype
   FROM
     (SELECT if(ouid='-1',null,ouid) as ouid,
             if(did='-1',null,did) as did,
             max(ouid)over(partition by did) as did_ouid,
             event,
             sortid,
             itemtype,
             target,
             status,
             replyid,
             staytime,
             type,
             ds,
             ts
      FROM {sourcetab1}
      WHERE ds='{ds}') AS t1
   LEFT JOIN
     (SELECT ouid,
             last_os as os,
             last_channel as channel,
             from_unixtime(last_did_ouid_time,'yyyy-MM-dd') as last_did_ouid_time
      FROM {sourcetab2}
      WHERE ds='{ds}'
        AND ut='did'
        AND from_unixtime(last_login_time,'yyyy-MM-dd')=ds ) AS t2 ON t1.did=t2.ouid
   LEFT JOIN
     (SELECT ouid,
             last_os as os,
             last_channel as channel,
             from_unixtime(first_login_time,'yyyy-MM-dd') AS first_login_time
      FROM {sourcetab2}
      WHERE ds='{ds}'
        AND ut='uid'
        AND from_unixtime(last_login_time,'yyyy-MM-dd')=ds ) AS t3 ON nvl(t1.ouid,t1.did_ouid)=t3.ouid)AS m
GROUP BY os,
         channel,
         sortid,
         usertype,
         itemtype;

insert overwrite table {targettab} partition (ds='{ds}')
select '{ds}' AS fds,
       nvl(t1.os,t2.os) AS os,
       nvl(t1.channel,t2.channel) as channel,
       nvl(t1.sortid,t2.sortid) as sortid,
       nvl(t1.usertype,t2.usertype) as usertype,
       nvl(t1.itemtype,t2.itemtype) as itemtype,
       nvl(t1.play_cnt,0)+nvl(t2.his_play_cnt,0) AS his_play_cnt,
       nvl(t1.play_cnt,0) as play_cnt,
       nvl(t1.valid_play_cnt,0) as valid_play_cnt,
       nvl(t1.finish_play_cnt,0) as finish_play_cnt,
       nvl(t1.playtime,0) as playtime,
       nvl(t1.recommend_play_cnt,0) as recommend_play_cnt,
       nvl(t1.read_cnt,0)+nvl(t2.his_read_cnt,0) as his_read_cnt,
       nvl(t1.read_cnt,0) as read_cnt,
       nvl(t1.valid_read_cnt,0) as valid_read_cnt,
       nvl(t1.finish_read_cnt,0) as finish_read_cnt,
       nvl(t1.leaveread_cnt,0) as leaveread_cnt,
       nvl(t1.readtime,0) as readtime,
       nvl(t1.recommend_read_cnt,0) as recommend_read_cnt,
       nvl(t1.pagenum,0) as pagenum,
       nvl(t1.comment_cnt,0) as comment_cnt,
       nvl(t1.reforward_cnt,0) as reforward_cnt,
       nvl(t1.reward_cnt,0) as reward_cnt,
       nvl(t1.attention_cnt,0) as attention_cnt,
       nvl(t1.cancel_attention_cnt,0) as cancel_attention_cnt,
       nvl(t1.favour_cnt,0) as favour_cnt,
       nvl(t1.cancel_favour_cnt,0) as cancel_favour_cnt,
       nvl(t1.like_cnt,0) as like_cnt,
       nvl(t1.cancel_like_cnt,0) as cancel_like_cnt,
       nvl(t1.reply_cnt,0) as reply_cnt
from
(select * from {targettab} where ds = '{ds}') as t1
full outer join
(select * from {targettab} where ds = date_sub('{ds}',1)) as t2
on t1.os = t2.os
and t1.channel = t2.channel
and t1.sortid = t2.sortid
and t1.usertype = t2.usertype
and t1.itemtype = t2.itemtype ;
'''


def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = SQL_DAY.format(ds=ds, targettab='wutiao.adl_wutiao_play_read_statistics',
                         sourcetab1='wutiao.bdl_wutiao_event', sourcetab2='wutiao.idl_wutiao_user')
    kdc.debug = True
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
