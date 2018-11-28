# coding: utf-8

#**********************程序说明*********************************#
#*模块: IDL
#*功能: 区块链活跃用户统计表
#*作者:gant
#*时间:2018-05-03
#*备注:区块链活跃用户统计表
#***************************************************************#

import kingnet

sql_day = '''
insert overwrite table {targettab} partition (ds='{ds}',ut='uid')
select fds
,user_flag
,appver
,channel
,model
,res
,os
,nettype
,carrier
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
,sum(openclient_user_cnt) as openclient_user_cnt
,sum(openclient_cnt) as openclient_cnt
,sum(report_user_cnt) as report_user_cnt     
,sum(report_cnt) as report_cnt          
,sum(nointerest_user_cnt) as nointerest_user_cnt 
,sum(nointerest_cnt) as nointerest_cnt      
,sum(appPV) as appPV               
,sum(appUV) as appUV
,sum(enterfront_cnt) as enterfront_cnt
,sum(gaptime) as gaptime
from
(select
ds as fds 
,ouid         
,if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as user_flag              
,channel
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
,if(from_unixtime(last_report_time,'yyyy-MM-dd')=ds,1,0) as report_user_cnt     
,if(from_unixtime(last_report_time,'yyyy-MM-dd')=ds,last_report_cnt,0) as report_cnt          
,if(from_unixtime(last_nointerest_time,'yyyy-MM-dd')=ds,1,0) as nointerest_user_cnt 
,if(from_unixtime(last_nointerest_time,'yyyy-MM-dd')=ds,last_nointerest_cnt,0) as nointerest_cnt      
,if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,last_login_cnt,0)+if(from_unixtime(last_openclient_time,'yyyy-MM-dd')=ds,last_openclient_cnt,0) as appPV               
,if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds or from_unixtime(last_openclient_time,'yyyy-MM-dd')=ds,1,0) as appUV               
,enterbackground_cnt
,usetime
,pagenum
,crash_cnt
,if(crash_cnt>0,1,0) as crash_user_cnt
,enterfront_cnt
,gaptime
from {sourcetab} 
where ds = '{ds}' and ut='uid')t1
group by fds
,user_flag
,channel
,appver
,model
,res
,os
,nettype
,carrier;
'''

def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.adl_wutiao_activeuser', sourcetab='wutiao.idl_wutiao_user')
    kdc.debug = True
    print(sql)
    kdc.doHive(sql)

if __name__ == '__main__':
    main()
