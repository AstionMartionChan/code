# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-用户互动分析
#*作者：Leo
#*时间：2018-05-04
#*备注：区块链-用户互动分析
#***************************************************************#

import kingnet
import os

sql_day = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds ,
       nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       'time_cnt' as checktype,
       sum(comment_cnt) AS comment_cnt,
       sum(vote_cnt) AS vote_cnt,
       sum(search_cnt) AS search_cnt,
       sum(favor_cnt) AS favor_cnt,
       sum(like_cnt) AS like_cnt,
       sum(share_cnt) AS share_cnt,
       sum(attention_cnt) AS attention_cnt,
       sum(reply_cnt) AS reply_cnt,
       sum(report_cnt) AS report_cnt,
       sum(nointerest_cnt) AS nointerest_cnt
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             nvl(comment_cnt,0) as comment_cnt,
             nvl(vote_cnt,0) as vote_cnt,
             nvl(search_cnt,0) as search_cnt,
             nvl(favor_cnt,0) as favor_cnt,
             nvl(like_cnt,0) as like_cnt,
             nvl(share_cnt,0) as share_cnt,
             nvl(attention_cnt,0) as attention_cnt,
             nvl(reply_cnt,0) as reply_cnt,
             nvl(report_cnt,0) as report_cnt,
             nvl(nointerest_cnt,0) as nointerest_cnt
        FROM {sourcetab1}
       WHERE ds='{ds}') as t
    GROUP BY os,appver,channel with cube
UNION ALL    
SELECT '{ds}' AS fds ,
       nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       'user_cnt' as checktype,
       sum(comment_user_cnt) AS comment_cnt,
       sum(vote_user_cnt) AS vote_cnt,
       sum(search_user_cnt) AS search_cnt,
       sum(favor_user_cnt) AS favor_cnt,
       sum(like_user_cnt) AS like_cnt,
       sum(share_user_cnt) AS share_cnt,
       sum(attention_user_cnt) AS attention_cnt,
       sum(reply_user_cnt) AS reply_cnt,
       sum(report_user_cnt) AS report_cnt,
       sum(nointerest_user_cnt) AS nointerest_cnt
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(appver,'-1') AS appver,
             nvl(channel,'-1') AS channel,
             nvl(comment_user_cnt,0) as comment_user_cnt,
             nvl(vote_user_cnt,0) as vote_user_cnt,
             nvl(search_user_cnt,0) as search_user_cnt,
             nvl(favor_user_cnt,0) as favor_user_cnt,
             nvl(like_user_cnt,0) as like_user_cnt,
             nvl(share_user_cnt,0) as share_user_cnt,
             nvl(attention_user_cnt,0) as attention_user_cnt,
             nvl(reply_user_cnt,0) as reply_user_cnt,
             nvl(report_user_cnt,0) as report_user_cnt,
             nvl(nointerest_user_cnt,0) as nointerest_user_cnt
        FROM {sourcetab1}
       WHERE ds='{ds}') as t
    GROUP BY os,appver,channel with cube;
'''

delete='''echo "use wutiao; delete from report_wutiao_interact where fds ='{ds}'; " |mysql -h 172.17.2.91 -u root -p123456 '''
sync='''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/report_wutiao_interact/ds='{ds}' --connect jdbc:mysql://172.17.2.91:3306/wutiao --username "root" --password '123456' --table "report_wutiao_interact" --input-fields-terminated-by "|" --input-null-string "\\\\\N" --input-null-non-string "\\\\\N" '''


def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.report_wutiao_interact', sourcetab1='wutiao.adl_wutiao_activeuser')
    kdc.debug = True

    # print sql
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('wutiao.report_wutiao_interact insert execute failure!!')

    res1 = os.system(delete.format(ds=ds))
    res2 = os.system(sync.format(ds=ds))

    if (res1 or res2) !=0 :
        print res1,res2
        raise Exception('wutiao.report_wutiao_interact mysql delete&insert execute failure!!')
    else :
        print res1,res2
        print 'complete'



if __name__ == '__main__':
    main()
