# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-内容分析
#*作者：Leo
#*时间：2018-05-28
#*备注：区块链-内容分析
#***************************************************************#
import kingnet
import os

sql_day = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds,
       nvl(os,'allos') AS os,
       nvl(channel,'allchannel') AS channel,
       nvl(sortid,'allsortid') AS sortid,
       nvl(usertype,'allusertype') AS usertype,
       nvl(itemtype,'allitemtype') AS itemtype,
       sum(his_play_cnt) AS his_play_cnt,
       sum(play_cnt) AS play_cnt,
       sum(valid_play_cnt) AS valid_play_cnt,
       sum(finish_play_cnt) AS finish_play_cnt,
       sum(avg_playtime) AS avg_playtime,
       sum(recommend_play_cnt) AS recommend_play_cnt,
       sum(pagenum) AS pagenum,
       sum(his_read_cnt) AS his_read_cnt,
       sum(read_cnt) AS read_cnt,
       sum(valid_read_cnt) AS valid_read_cnt,
       sum(finish_read_cnt) AS finish_read_cnt,
       sum(jumpout_cnt) AS jumpout_cnt,
       sum(recommend_read_cnt) AS recommend_read_cnt,
       sum(comment_cnt) AS comment_cnt,
       sum(reforward_cnt) AS reforward_cnt,
       sum(reward_cnt) AS reward_cnt,
       sum(attention_cnt) AS attention_cnt
FROM
  (SELECT nvl(os,'-1') as os,
          nvl(channel,'-1') as channel,
          nvl(sortid,'-1') as sortid,
          nvl(usertype,'-1') as usertype,
          nvl(itemtype,'-1') as itemtype,
          his_play_cnt,
          play_cnt,
          valid_play_cnt,
          finish_play_cnt,
          0 as avg_playtime,
          recommend_play_cnt,
          pagenum,
          his_read_cnt,
          read_cnt,
          valid_read_cnt,
          finish_read_cnt,
          0 as jumpout_cnt,
          recommend_read_cnt,
          comment_cnt,
          reforward_cnt,
          reward_cnt,
          attention_cnt
   FROM {sourcetab1}
   WHERE ds='{ds}') AS m
GROUP BY m.os,
         m.channel,
         m.sortid,
         m.usertype,
         m.itemtype WITH CUBE
UNION ALL
SELECT '{ds}' AS fds,
       nvl(os,'allos') as os,
       nvl(channel,'allchannel') channel,
       nvl(sortid,'allsortid') sortid,
       'activeuser' AS usertype,
       nvl(itemtype,'allitemtype')itemtype,
       sum(his_play_cnt) his_play_cnt,
       sum(play_cnt)play_cnt,
       sum(valid_play_cnt)valid_play_cnt,
       sum(finish_play_cnt)finish_play_cnt,
       sum(avg_playtime)avg_playtime,
       sum(recommend_play_cnt)recommend_play_cnt,
       sum(pagenum)pagenum,
       sum(his_read_cnt)his_read_cnt,
       sum(read_cnt)read_cnt,
       sum(valid_read_cnt)valid_read_cnt,
       sum(finish_read_cnt)finish_read_cnt,
       sum(jumpout_cnt)jumpout_cnt,
       sum(recommend_read_cnt)recommend_read_cnt,
       sum(comment_cnt)comment_cnt,
       sum(reforward_cnt)reforward_cnt,
       sum(reward_cnt)reward_cnt,
       sum(attention_cnt)attention_cnt
FROM
  (SELECT nvl(os,'-1') as os,
          nvl(channel,'-1') as channel,
          nvl(sortid,'-1') as sortid,
          nvl(itemtype,'-1') as itemtype,
          his_play_cnt,
          play_cnt,
          valid_play_cnt,
          finish_play_cnt,
          0 as avg_playtime,
          recommend_play_cnt,
          pagenum,
          his_read_cnt,
          read_cnt,
          valid_read_cnt,
          finish_read_cnt,
          0 as jumpout_cnt,
          recommend_read_cnt,
          comment_cnt,
          reforward_cnt,
          reward_cnt,
          attention_cnt
   FROM {sourcetab1}
   WHERE ds='{ds}'
     AND usertype in('newuser','olduser')) AS m
GROUP BY m.os,m.channel,m.sortid,m.itemtype WITH CUBE;
'''

delete='''echo "use wutiao; delete from report_wutiao_content_analysis where fds ='{ds}'; " |mysql -h 172.17.2.91 -u root -p123456 '''
sync='''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/report_wutiao_content_analysis/ds='{ds}' --connect jdbc:mysql://172.17.2.91:3306/wutiao --username "root" --password '123456' --table "report_wutiao_content_analysis" --input-fields-terminated-by "|" --input-null-string "\\\\\N" --input-null-non-string "\\\\\N" '''


def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.report_wutiao_content_analysis', sourcetab1='wutiao.adl_wutiao_play_read_statistics')
    kdc.debug = True

    print sql
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('wutiao.report_wutiao_content_analysis insert execute failure!!')

    res1 = os.system(delete.format(ds=ds))
    res2 = os.system(sync.format(ds=ds))

    if (res1 or res2) !=0 :
        print res1,res2
        raise Exception('wutiao.report_wutiao_content_analysis mysql delete&insert execute failure!!')
    else :
        print res1,res2
        print 'complete'


if __name__ == '__main__':
    main()
