# coding: utf-8

#**********************程序说明*********************************#
#*模块: IDL
#*功能: 区块链全部用户统计表
#*作者:gant
#*时间:2018-05-03
#*备注:区块链全部用户统计表
#***************************************************************#

import kingnetdc

sql_day = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds='{ds}')
select fds
,channel
,appver
,model
,res
,os
,nettype
,carrier
,sum(openclient_user_cnt) as openclient_user_cnt
,sum(openclient_cnt) as openclient_cnt
,sum(comment_user_cnt) as comment_user_cnt
,sum(comment_cnt) as comment_cnt
,sum(vote_user_cnt) as vote_user_cnt
,sum(vote_cnt) as vote_cnt
,sum(search_user_cnt) as search_user_cnt
,sum(search_cnt) as search_cnt
,sum(favor_user_cnt) as favor_user_cnt
,sum(favor_cnt) as favor_cnt
,sum(like_user_cnt) as like_user_cnt
,sum(like_cnt) as like_cnt
,sum(share_user_cnt) as share_user_cnt
,sum(share_cnt) as share_cnt
,sum(attention_user_cnt) as attention_user_cnt
,sum(attention_cnt) as attention_cnt
,sum(reply_user_cnt) as reply_user_cnt
,sum(reply_cnt) as reply_cnt
,sum(enterbackground_cnt) as enterbackgroud_cnt
,sum(usetime) as use_time
,sum(pagenum) as pagenum
,sum(crash_cnt) as crash_cnt
,sum(crash_user_cnt) as crash_user_cnt
from
(select
ds as fds 
,ouid        
,last_channel as channel
,last_appver as appver
,model
,res                 
,last_os as os
,nettype
,carrier
,if(from_unixtime(last_openclient_time,'yyyy-MM-dd')=ds,1,0) as openclient_user_cnt
,if(from_unixtime(last_openclient_time,'yyyy-MM-dd')=ds,last_openclient_cnt,0) as openclient_cnt
,if(from_unixtime(last_comment_time,'yyyy-MM-dd')=ds,1,0) as comment_user_cnt
,if(from_unixtime(last_comment_time,'yyyy-MM-dd')=ds,last_comment_cnt,0) as comment_cnt
,if(from_unixtime(last_vote_time,'yyyy-MM-dd')=ds,1,0) as vote_user_cnt
,if(from_unixtime(last_vote_time,'yyyy-MM-dd')=ds,last_vote_cnt,0) as vote_cnt
,if(from_unixtime(last_search_time,'yyyy-MM-dd')=ds,1,0) as search_user_cnt
,if(from_unixtime(last_search_time,'yyyy-MM-dd')=ds,last_search_cnt,0) as search_cnt
,if(from_unixtime(last_favor_time,'yyyy-MM-dd')=ds,1,0) as favor_user_cnt
,if(from_unixtime(last_favor_time,'yyyy-MM-dd')=ds,last_favor_cnt,0) as favor_cnt
,if(from_unixtime(last_like_time,'yyyy-MM-dd')=ds,1,0) as like_user_cnt
,if(from_unixtime(last_like_time,'yyyy-MM-dd')=ds,last_like_cnt,0) as like_cnt
,if(from_unixtime(last_share_time,'yyyy-MM-dd')=ds,1,0) as share_user_cnt
,if(from_unixtime(last_share_time,'yyyy-MM-dd')=ds,last_share_cnt,0) as share_cnt
,if(from_unixtime(last_attention_time,'yyyy-MM-dd')=ds,1,0) as attention_user_cnt
,if(from_unixtime(last_attention_time,'yyyy-MM-dd')=ds,last_attention_cnt,0) as attention_cnt
,if(from_unixtime(last_reply_time,'yyyy-MM-dd')=ds,1,0) as reply_user_cnt
,if(from_unixtime(last_reply_time,'yyyy-MM-dd')=ds,last_reply_cnt,0) as reply_cnt
,enterbackground_cnt
,usetime
,pagenum
,crash_cnt
,if(crash_cnt>0,1,0) as crash_user_cnt
from {sourcetab} 
where ds = '{ds}' and ut='did')t1
group by fds
,channel
,appver
,model
,res
,os
,nettype
,carrier;
'''

def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.adl_wutiao_alluser', sourcetab='wutiao.idl_wutiao_user')
    kdc.debug = True
    print(sql)
    kdc.doHive(sql)

if __name__ == '__main__':
    main()
