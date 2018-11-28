# coding: utf-8

#**********************程序说明*********************************#
#*模块: IDL
#*功能: 区块链活跃用户中间表
#*作者:gant
#*时间:2018-05-03
#*备注:发现者状态每日更新，只要一个用户曾经成为发现者，first_discover_time就不为null
#      last_did_ouid_time:最后一次有这个ouid的时间，用在did分区中，用于统计日周月的游客
#***************************************************************#

import kingnetdc

sql_day = '''
set parquet.compression=SNAPPY;
set mapreduce.map.memory.mb=4000;
set mapreduce.reduce.memory.mb=4000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds='{ds}', ut='uid')
select
nvl(t2.ouid,t1.ouid) as ouid,
nvl(t2.nettype,t1.nettype) as nettype,
nvl(t2.model,t1.model) as model,
nvl(t2.first_osver,t1.first_osver) as first_osver,
nvl(t1.last_osver,t2.last_osver) as last_osver,
nvl(t2.first_os,t1.first_os) as first_os,
nvl(t1.last_os,t2.last_os) as last_os,
nvl(t2.res,t1.res) as res,
nvl(t2.carrier,t1.carrier) as carrier,
nvl(t2.first_channel,t1.first_channel) as first_channel,
null as channel_type,
nvl(t2.first_ip,t1.first_ip) as first_ip,
nvl(t1.last_ip,t2.last_ip) as last_ip,
nvl(t2.first_did_ouid,t1.first_did_ouid) as first_did_ouid,
nvl(t1.last_did_ouid,t2.last_did_ouid) as last_did_ouid,
nvl(t2.first_appver,t1.first_appver) as first_appver,
nvl(t1.last_appver,t2.last_appver) as last_appver,
nvl(t2.registe_time,t1.registe_time) as registe_time,
nvl(t2.first_login_time,t1.first_login_time) as first_login_time,
nvl(t1.last_login_time,t2.last_login_time) as last_login_time,
nvl(t2.first_openclient_time,t1.first_openclient_time) as first_openclient_time,
nvl(t1.last_openclient_time,t2.last_openclient_time) as last_openclient_time,
nvl(if(t1.last_openclient_cnt=0,null,t1.last_openclient_cnt),t2.last_openclient_cnt) as last_openclient_cnt,
nvl(t2.first_comment_time,t1.first_comment_time) as first_comment_time,
nvl(t1.last_comment_time,t2.last_comment_time) as last_comment_time,
nvl(if(t1.last_comment_cnt=0,null,t1.last_comment_cnt),t2.last_comment_cnt) as last_comment_cnt,
nvl(t2.first_vote_time,t1.first_vote_time) as first_vote_time,
nvl(t1.last_vote_time,t2.last_vote_time) as last_vote_time,
nvl(if(t1.last_vote_cnt=0,null,t1.last_vote_cnt),t2.last_vote_cnt) as last_vote_cnt,
nvl(t2.first_search_time,t1.first_search_time) as first_search_time,
nvl(t1.last_search_time,t2.last_search_time) as last_search_time,
nvl(if(t1.last_search_cnt=0,null,t1.last_search_cnt),t2.last_search_cnt) as last_search_cnt,
nvl(t2.first_favor_time,t1.first_favor_time) as first_favor_time,
nvl(t1.last_favor_time,t2.last_favor_time) as last_favor_time,
nvl(if(t1.last_favor_cnt=0,null,t1.last_favor_cnt),t2.last_favor_cnt) as last_favor_cnt,
nvl(t2.first_like_time,t1.first_like_time) as first_like_time,
nvl(t1.last_like_time,t2.last_like_time) as last_like_time,
nvl(if(t1.last_like_cnt=0,null,t1.last_like_cnt),t2.last_like_cnt) as last_like_cnt,
nvl(t2.first_share_time,t1.first_share_time) as first_share_time,
nvl(t1.last_share_time,t2.last_share_time) as last_share_time,
nvl(if(t1.last_share_cnt=0,null,t1.last_share_cnt),t2.last_share_cnt) as last_share_cnt,
nvl(t2.first_attention_time,t1.first_attention_time) as first_attention_time,
nvl(t1.last_attention_time,t2.last_attention_time) as last_attention_time,
nvl(if(t1.last_attention_cnt=0,null,t1.last_attention_cnt),t2.last_attention_cnt) as last_attention_cnt,
nvl(t2.first_reply_time,t1.first_reply_time) as first_reply_time,
nvl(t1.last_reply_time,t2.last_reply_time) as last_reply_time,
nvl(if(t1.last_reply_cnt=0,null,t1.last_reply_cnt),t2.last_reply_cnt) as last_reply_cnt,
nvl(t1.enterbackground_cnt,0) as enterbackground_cnt,
nvl(t1.usetime,0) as usetime,
nvl(t1.pagenum,0) as pagenum,
nvl(t1.crash_cnt,0) as crash_cnt,
nvl(t1.last_validread_time,t2.last_validread_time) as last_validread_time
,nvl(if(t1.last_validread_cnt=0,null,t1.last_validread_cnt),t2.last_validread_cnt) as last_validread_cnt
,nvl(t2.first_contribution_time,t1.first_contribution_time) as first_contribution_time
,nvl(t1.last_contribution_time,t2.last_contribution_time) as last_contribution_time
,nvl(if(t1.last_day_contributionpoint=0,null,t1.last_day_contributionpoint),t2.last_day_contributionpoint) as last_day_contributionpoint
,nvl(t2.first_verify_time,t1.first_verify_time) as first_verify_time
,nvl(t1.last_verify_time,t2.last_verify_time) as last_verify_time
,nvl(if(t1.last_day_verifypoint=0,null,t1.last_day_verifypoint),t2.last_day_verifypoint) as last_day_verifypoint
,nvl(t2.first_read_time,t1.first_read_time) as first_read_time 
,nvl(t1.last_read_time,t2.last_read_time) as last_read_time
,nvl(if(t1.last_read_cnt=0,null,t1.last_read_cnt),t2.last_read_cnt) as last_read_cnt 
,nvl(t2.first_play_time,t1.first_play_time) as first_play_time
,nvl(t1.last_play_time,t2.last_play_time) as last_play_time  
,nvl(if(t1.last_play_cnt=0,null,t1.last_play_cnt),t2.last_play_cnt) as last_play_cnt
,t2.familyid
,nvl(t2.first_discover_time,t1.first_discover_time) as first_discover_time
,nvl(if(t1.is_discover=2,0,t1.is_discover),t2.is_discover) as is_discover
,nvl(t2.inviterid,t1.inviterid) as inviterid
,nvl(t2.inviter_time,t1.inviter_time) as inviter_time
,nvl(if(t1.media_status=2,0,t1.media_status),t2.media_status) as media_status
,nvl(t2.first_withdraw_time,t1.first_withdraw_time) as first_withdraw_time
,nvl(t1.last_withdraw_time,t2.last_withdraw_time) as last_withdraw_time
,nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_rmb,0),t2.last_day_withdraw_rmb) as last_day_withdraw_rmb
,nvl(t2.his_withdraw_rmb,0)+nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_rmb,0),0) as his_withdraw_rmb
,nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_coin,0),t2.last_day_withdraw_coin) as last_day_withdraw_coin
,nvl(t2.his_withdraw_coin,0)+nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_coin,0),0) as his_withdraw_coin
,t2.first_money_gain_time
,t2.last_money_gain_time
,t2.last_day_money_gain 
,t2.his_money_gain
,nvl(t2.first_report_time,t1.first_report_time) as  first_report_time
,nvl(t1.last_report_time,t2.last_report_time) as last_report_time
,nvl(t1.last_report_cnt,t2.last_report_cnt) as last_report_cnt
,nvl(t2.first_nointerest_time,t1.first_nointerest_time) as first_nointerest_time
,nvl(t1.last_nointerest_time,t2.last_nointerest_time) as last_nointerest_time
,nvl(t1.last_nointerest_cnt,t2.last_nointerest_cnt) as last_nointerest_cnt
,nvl(if(t1.media_status=2,null,t1.media_type),if(t1.media_status=2,null,t2.media_type)) as media_type
,nvl(if(t1.last_login_cnt=0,null,t1.last_login_cnt),t2.last_login_cnt) as last_login_cnt
,nvl(t1.last_did_ouid_time,t2.last_did_ouid_time) as last_did_ouid_time
,nvl(t1.enterfront_cnt,0) as enterfront_cnt
,nvl(t1.gaptime,0) as gaptime
,nvl(t1.last_getwb_time,t2.last_getwb_time) as last_getwb_time
,nvl(if(t1.last_getwb_time is not null,t1.last_day_getwb_wb,0),t2.last_day_getwb_wb) as last_day_getwb_wb
,nvl(t2.his_getwb_wb,0)+nvl(if(t1.last_getwb_time is not null,t1.last_day_getwb_wb,0),0) as his_getwb_wb
,nvl(t1.last_channel,t2.last_channel) as last_channel
,nvl(t1.enterfront_under30_cnt,0) as enterfront_under30_cnt
,nvl(t1.phone,t2.phone) as phone
,nvl(t2.registe_inviter,t1.registe_inviter) as registe_inviter
,nvl(t2.first_withdraw_hongbao_time,t1.first_withdraw_hongbao_time) as first_withdraw_hongbao_time
,nvl(t1.last_withdraw_hongbao_time,t2.last_withdraw_hongbao_time) as last_withdraw_hongbao_time
,nvl(if(t1.last_withdraw_hongbao_time is not null,t1.last_day_withdraw_hongbao_rmb,0),t2.last_day_withdraw_hongbao_rmb) as last_day_withdraw_hongbao_rmb
,nvl(t2.his_withdraw_hongbao_rmb,0)+nvl(if(t1.last_withdraw_hongbao_time is not null,t1.last_day_withdraw_hongbao_rmb,0),0) as his_withdraw_hongbao_rmb
,nvl(t2.first_market_channel_type,t1.first_market_channel_type) as first_market_channel_type
,nvl(t1.last_market_channel_type,t2.last_market_channel_type) as last_market_channel_type

,nvl(t2.first_withdraw_duobao_time,t1.first_withdraw_duobao_time) as first_withdraw_duobao_time
,nvl(t1.last_withdraw_duobao_time,t2.last_withdraw_duobao_time) as last_withdraw_duobao_time
,nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_rmb,0),t2.last_day_withdraw_duobao_rmb) as last_day_withdraw_duobao_rmb
,nvl(t2.his_withdraw_duobao_rmb,0)+nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_rmb,0),0) as his_withdraw_duobao_rmb
,nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_coin,0),t2.last_day_withdraw_duobao_coin) as last_day_withdraw_duobao_coin
,nvl(t2.his_withdraw_duobao_coin,0)+nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_coin,0),0) as his_withdraw_duobao_coin
from
(select 
ouid       
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), nettype, null), -1*ts) as nettype               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), model, null), -1*ts) as model                 
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), osver, null), -1*ts) as first_osver           
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), osver, null), ts) as last_osver            
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), os, null), -1*ts) as first_os              
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), os, null), ts) as last_os               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), res, null), -1*ts) as res                   
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), carrier, null), -1*ts) as carrier
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), channel, null), -1*ts) as first_channel               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), channel, null), ts) as last_channel               
,null as channel_type          
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), ip, null), -1*ts) as first_ip              
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), ip, null), ts) as last_ip               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), did, null), -1*ts) as first_did_ouid       
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), if(did='-1',null,did), null), ts) as last_did_ouid        
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), appver, null), -1*ts) as first_appver          
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), appver, null), ts) as last_appver           
,min(if(event='register' and step=0,ts,null)) as registe_time
,maxbytimenotnull(if(event='register' and step=0 and type=1,phone,null),ts) as phone
,maxbytimenotnull(if(event='register' and step=0 and type=1,inviterid,null), -1*ts) as registe_inviter            
,min(if(event in('login','active','openclient','enterfront','register'),ts, null)) as first_login_time      
,max(if(event in('login','active','openclient','enterfront','register'),ts, null)) as last_login_time
,sum(if(event in('login','active','openclient','enterfront','register'),1, 0)) as last_login_cnt          
,min(if(event in('enterfront','openclient'),ts, null)) as first_openclient_time 
,max(if(event in('enterfront','openclient'),ts, null)) as last_openclient_time  
,sum(if(event in('enterfront','openclient'),1, 0)) as last_openclient_cnt   
,min(if(event = 'comment' and replyid = '0' and status = '1',ts, null)) as first_comment_time    
,max(if(event = 'comment' and replyid = '0' and status = '1',ts, null)) as last_comment_time     
,sum(if(event = 'comment' and replyid = '0' and status = '1',1, 0)) as last_comment_cnt      
,min(if(event = 'like' and type=3,ts, null)) as first_vote_time       
,max(if(event = 'like' and type=3,ts, null)) as last_vote_time        
,sum(if(event = 'like' and type=3,1, 0)) as last_vote_cnt         
,min(if(event = 'search',ts, null)) as first_search_time     
,max(if(event = 'search',ts, null)) as last_search_time      
,sum(if(event = 'search',1, 0)) as last_search_cnt       
,min(if(event = 'favour' and status='1',ts, null)) as first_favor_time      
,max(if(event = 'favour' and status='1',ts, null)) as last_favor_time       
,sum(if(event = 'favour' and status='1',1, 0)) as last_favor_cnt        
,min(if(event = 'like' and type=1,ts, null)) as first_like_time       
,max(if(event = 'like' and type=1,ts, null)) as last_like_time        
,sum(if(event = 'like' and type=1,1, 0)) as last_like_cnt         
,min(if(event = 'share' and status='3',ts, null)) as first_share_time      
,max(if(event = 'share' and status='3',ts, null)) as last_share_time       
,sum(if(event = 'share' and status='3',1, 0)) as last_share_cnt        
,min(if(event = 'attention' and status='1',ts, null)) as first_attention_time  
,max(if(event = 'attention' and status='1',ts, null)) as last_attention_time   
,sum(if(event = 'attention' and status='1',1, 0)) as last_attention_cnt    
,min(if(event = 'comment' and replyid <> '0' and status = '1',ts, null)) as first_reply_time     
,max(if(event = 'comment' and replyid <> '0' and status = '1',ts, null)) as last_reply_time           
,sum(if(event = 'comment' and replyid <> '0' and status = '1',1, 0)) as last_reply_cnt       
,sum(if(event = 'enterbackground' and staytime>=1000 and staytime<=24*60*60*1000,1, 0)) as enterbackground_cnt   
,sum(if(event = 'enterbackground' and staytime>=1000 and staytime<=24*60*60*1000,staytime, 0)) as usetime               
,sum(if(event = 'read',1, 0)) as pagenum               
,sum(if(event = 'apperr' and type=1,1, 0)) as crash_cnt
,max(if(event = 'validread',ts, null)) as last_validread_time
,sum(if(event = 'validread',1,0)) as last_validread_cnt
,min(if(event = 'contributioninc' and contributionpoint>0,ts,null)) as first_contribution_time
,max(if(event = 'contributioninc' and contributionpoint>0,ts,null)) as last_contribution_time
,sum(if(event = 'contributioninc' and contributionpoint>0,contributionpoint,0)) as last_day_contributionpoint
,min(if(event = 'verifypointinc' and verifypoint>0,ts,null)) as first_verify_time
,max(if(event = 'verifypointinc' and verifypoint>0,ts,null)) as last_verify_time
,sum(if(event = 'verifypointinc' and verifypoint>0,verifypoint,0)) as last_day_verifypoint
,min(if(event = 'read',ts, null)) as first_read_time    
,max(if(event = 'read',ts, null)) as last_read_time     
,sum(if(event = 'read',1, 0)) as last_read_cnt
,min(if(event = 'play',ts, null)) as first_play_time    
,max(if(event = 'play',ts, null)) as last_play_time     
,sum(if(event = 'play',1, 0)) as last_play_cnt
,min(if(event = 'discoverer' and status=1,ts,null)) as first_discover_time
,maxbytimenotnull(if(event = 'discoverer' and status=1,1,status),ts) as is_discover  
,maxbytimenotnull(if(event='invite' and inviterid<>0 and type='5' and step='0',inviterid,null),-1*ts) as inviterid
,min(if(event='invite' and inviterid<>0 and type='5' and step='0',ts,null)) as inviter_time 
,maxbytimenotnull(if(event = 'media' and status=1,1,status),ts) as media_status
,maxbytimenotnull(if(event = 'media' and status=1,type,null),ts) as media_type
,min(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,ts,null)) as first_withdraw_time
,max(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,ts,null)) as last_withdraw_time
,sum(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,rmb,0)) as last_day_withdraw_rmb
,sum(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,coin,0)) as last_day_withdraw_coin
,min(if(event='report' and status=1,ts,null)) as  first_report_time
,max(if(event='report' and status=1,ts,null)) as last_report_time
,sum(if(event='report' and status=1,1,0)) as last_report_cnt
,min(if(event='nointerest',ts,null)) as first_nointerest_time
,max(if(event='nointerest',ts,null)) as last_nointerest_time
,sum(if(event='nointerest',1,0)) as last_nointerest_cnt
,max(if(event in('login','active','enterfront','openclient','register') and nvl(did,'-1')<>'-1',ts, null)) as last_did_ouid_time
,sum(if(event = 'enterfront',1, 0)) as enterfront_cnt   
,sum(if(event = 'enterfront',staytime, 0)) as gaptime
,sum(if(event = 'enterfront' and staytime<30000,1,0)) as enterfront_under30_cnt
,max(if(event='getwb' and ts>=1535472000 and status=2,ts,null)) as last_getwb_time
,sum(if(event='getwb' and ts>=1535472000 and status=2,coin,0)) as last_day_getwb_wb
,min(if(event='withdraw' and ts>=1535472000 and status=2 and type=2,ts,null)) as first_withdraw_hongbao_time
,max(if(event='withdraw' and ts>=1535472000 and status=2 and type=2,ts,null)) as last_withdraw_hongbao_time
,sum(if(event='withdraw' and ts>=1535472000 and status=2 and type=2,rmb,0)) as last_day_withdraw_hongbao_rmb
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), market_channel_type, null), -1*ts) as first_market_channel_type
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), market_channel_type, null), ts) as last_market_channel_type
,min(if(event='withdraw' and ts>=1535472000 and type=3,ts,null)) as first_withdraw_duobao_time
,max(if(event='withdraw' and ts>=1535472000 and type=3,ts,null)) as last_withdraw_duobao_time
,sum(if(event='withdraw' and ts>=1535472000 and type=3,rmb,0)) as last_day_withdraw_duobao_rmb
,sum(if(event='withdraw' and ts>=1535472000 and type=3,coin,0)) as last_day_withdraw_duobao_coin
from {sourcetab} 
where ds = '{ds}' and nvl(ouid,'-1')<>'-1' and eventtype in ('super','high','medium')
and event in('login','active','register','openclient','comment','search','favour','like','share','attention','enterfront',
'enterbackground','read','apperr','validread','contributioninc','verifypointinc','discoverer','invite','withdraw','report','nointerest','getwb') group by ouid)t1
full outer join
(select * from {targettab} where ds = date_sub('{ds}',1) and ut = 'uid'
)t2
on t1.ouid = t2.ouid;

insert overwrite table {targettab} partition (ds='{ds}', ut='did')
select
nvl(t2.ouid,t1.ouid) as ouid,
nvl(t2.nettype,t1.nettype) as nettype,
nvl(t2.model,t1.model) as model,
nvl(t2.first_osver,t1.first_osver) as first_osver,
nvl(t1.last_osver,t2.last_osver) as last_osver,
nvl(t2.first_os,t1.first_os) as first_os,
nvl(t1.last_os,t2.last_os) as last_os,
nvl(t2.res,t1.res) as res,
nvl(t2.carrier,t1.carrier) as carrier,
nvl(t2.first_channel,t1.first_channel) as first_channel,
null as channel_type,
nvl(t2.first_ip,t1.first_ip) as first_ip,
nvl(t1.last_ip,t2.last_ip) as last_ip,
nvl(t2.first_did_ouid,t1.first_did_ouid) as first_did_ouid,
nvl(t1.last_did_ouid,t2.last_did_ouid) as last_did_ouid,
nvl(t2.first_appver,t1.first_appver) as first_appver,
nvl(t1.last_appver,t2.last_appver) as last_appver,
nvl(t2.registe_time,t1.registe_time) as registe_time,
nvl(t2.first_login_time,t1.first_login_time) as first_login_time,
nvl(t1.last_login_time,t2.last_login_time) as last_login_time,
nvl(t2.first_openclient_time,t1.first_openclient_time) as first_openclient_time,
nvl(t1.last_openclient_time,t2.last_openclient_time) as last_openclient_time,
nvl(if(t1.last_openclient_cnt=0,null,t1.last_openclient_cnt),t2.last_openclient_cnt) as last_openclient_cnt,
nvl(t2.first_comment_time,t1.first_comment_time) as first_comment_time,
nvl(t1.last_comment_time,t2.last_comment_time) as last_comment_time,
nvl(if(t1.last_comment_cnt=0,null,t1.last_comment_cnt),t2.last_comment_cnt) as last_comment_cnt,
nvl(t2.first_vote_time,t1.first_vote_time) as first_vote_time,
nvl(t1.last_vote_time,t2.last_vote_time) as last_vote_time,
nvl(if(t1.last_vote_cnt=0,null,t1.last_vote_cnt),t2.last_vote_cnt) as last_vote_cnt,
nvl(t2.first_search_time,t1.first_search_time) as first_search_time,
nvl(t1.last_search_time,t2.last_search_time) as last_search_time,
nvl(if(t1.last_search_cnt=0,null,t1.last_search_cnt),t2.last_search_cnt) as last_search_cnt,
nvl(t2.first_favor_time,t1.first_favor_time) as first_favor_time,
nvl(t1.last_favor_time,t2.last_favor_time) as last_favor_time,
nvl(if(t1.last_favor_cnt=0,null,t1.last_favor_cnt),t2.last_favor_cnt) as last_favor_cnt,
nvl(t2.first_like_time,t1.first_like_time) as first_like_time,
nvl(t1.last_like_time,t2.last_like_time) as last_like_time,
nvl(if(t1.last_like_cnt=0,null,t1.last_like_cnt),t2.last_like_cnt) as last_like_cnt,
nvl(t2.first_share_time,t1.first_share_time) as first_share_time,
nvl(t1.last_share_time,t2.last_share_time) as last_share_time,
nvl(if(t1.last_share_cnt=0,null,t1.last_share_cnt),t2.last_share_cnt) as last_share_cnt,
nvl(t2.first_attention_time,t1.first_attention_time) as first_attention_time,
nvl(t1.last_attention_time,t2.last_attention_time) as last_attention_time,
nvl(if(t1.last_attention_cnt=0,null,t1.last_attention_cnt),t2.last_attention_cnt) as last_attention_cnt,
nvl(t2.first_reply_time,t1.first_reply_time) as first_reply_time,
nvl(t1.last_reply_time,t2.last_reply_time) as last_reply_time,
nvl(if(t1.last_reply_cnt=0,null,t1.last_reply_cnt),t2.last_reply_cnt) as last_reply_cnt,
nvl(t1.enterbackground_cnt,0) as enterbackground_cnt,
nvl(t1.usetime,0) as usetime,
nvl(t1.pagenum,0) as pagenum,
nvl(t1.crash_cnt,0) as crash_cnt
,nvl(t1.last_validread_time,t2.last_validread_time) as last_validread_time
,nvl(if(t1.last_validread_cnt=0,null,t1.last_validread_cnt),t2.last_validread_cnt) as last_validread_cnt
,nvl(t2.first_contribution_time,t1.first_contribution_time) as first_contribution_time
,nvl(t1.last_contribution_time,t2.last_contribution_time) as last_contribution_time
,nvl(if(t1.last_day_contributionpoint=0,null,t1.last_day_contributionpoint),t2.last_day_contributionpoint) as last_day_contributionpoint
,nvl(t2.first_verify_time,t1.first_verify_time) as first_verify_time
,nvl(t1.last_verify_time,t2.last_verify_time) as last_verify_time
,nvl(if(t1.last_day_verifypoint=0,null,t1.last_verify_time),t2.last_verify_time) as last_day_verifypoint
,nvl(t2.first_read_time,t1.first_read_time) as first_read_time 
,nvl(t1.last_read_time,t2.last_read_time) as last_read_time
,nvl(if(t1.last_read_cnt=0,null,t1.last_read_cnt),t2.last_read_cnt) as last_read_cnt 
,nvl(t2.first_play_time,t1.first_play_time) as first_play_time
,nvl(t1.last_play_time,t2.last_play_time) as last_play_time  
,nvl(if(t1.last_play_cnt=0,null,t1.last_play_cnt),t2.last_play_cnt) as last_play_cnt
,null as familyid
,nvl(t2.first_discover_time,t1.first_discover_time) as first_discover_time
,nvl(if(t1.is_discover=2,0,t1.is_discover),t2.is_discover) as is_discover
,null as inviterid
,null as inviter_time
,null as media_status
,nvl(t2.first_withdraw_time,t1.first_withdraw_time) as first_withdraw_time
,nvl(t1.last_withdraw_time,t2.last_withdraw_time) as last_withdraw_time
,nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_rmb,0),t2.last_day_withdraw_rmb) as last_day_withdraw_rmb
,nvl(t2.his_withdraw_rmb,0)+nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_rmb,0),0) as his_withdraw_rmb
,nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_coin,0),t2.last_day_withdraw_coin) as last_day_withdraw_coin
,nvl(t2.his_withdraw_coin,0)+nvl(if(t1.last_withdraw_time is not null,t1.last_day_withdraw_coin,0),0) as his_withdraw_coin
,t2.first_money_gain_time
,t2.last_money_gain_time
,t2.last_day_money_gain 
,t2.his_money_gain
,nvl(t2.first_report_time,t1.first_report_time) as  first_report_time
,nvl(t1.last_report_time,t2.last_report_time) as last_report_time
,nvl(t1.last_report_cnt,t2.last_report_cnt) as last_report_cnt
,nvl(t2.first_nointerest_time,t1.first_nointerest_time) as first_nointerest_time
,nvl(t1.last_nointerest_time,t2.last_nointerest_time) as last_nointerest_time
,nvl(t1.last_nointerest_cnt,t2.last_nointerest_cnt) as last_nointerest_cnt
,null as media_type
,nvl(if(t1.last_login_cnt=0,null,t1.last_login_cnt),t2.last_login_cnt) as last_login_cnt
,nvl(t1.last_did_ouid_time,t2.last_did_ouid_time) as last_did_ouid_time
,nvl(t1.enterfront_cnt,0) as enterfront_cnt
,nvl(t1.gaptime,0) as gaptime
,null as last_getwb_time
,null as last_day_getwb_wb
,null as his_getwb_wb
,nvl(t1.last_channel,t2.last_channel) as last_channel
,nvl(t1.enterfront_under30_cnt,0) as enterfront_under30_cnt
,null as phone
,null as registe_inviter
,nvl(t2.first_withdraw_hongbao_time,t1.first_withdraw_hongbao_time) as first_withdraw_hongbao_time
,nvl(t1.last_withdraw_hongbao_time,t2.last_withdraw_hongbao_time) as last_withdraw_hongbao_time
,nvl(if(t1.last_withdraw_hongbao_time is not null,t1.last_day_withdraw_hongbao_rmb,0),t2.last_day_withdraw_hongbao_rmb) as last_day_withdraw_hongbao_rmb
,nvl(t2.his_withdraw_hongbao_rmb,0)+nvl(if(t1.last_withdraw_hongbao_time is not null,t1.last_day_withdraw_hongbao_rmb,0),0) as his_withdraw_hongbao_rmb
,nvl(t2.first_market_channel_type,t1.first_market_channel_type) as first_market_channel_type
,nvl(t1.last_market_channel_type,t2.last_market_channel_type) as last_market_channel_type

,nvl(t2.first_withdraw_duobao_time,t1.first_withdraw_duobao_time) as first_withdraw_duobao_time
,nvl(t1.last_withdraw_duobao_time,t2.last_withdraw_duobao_time) as last_withdraw_duobao_time
,nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_rmb,0),t2.last_day_withdraw_duobao_rmb) as last_day_withdraw_duobao_rmb
,nvl(t2.his_withdraw_duobao_rmb,0)+nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_rmb,0),0) as his_withdraw_duobao_rmb
,nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_coin,0),t2.last_day_withdraw_duobao_coin) as last_day_withdraw_duobao_coin
,nvl(t2.his_withdraw_duobao_coin,0)+nvl(if(t1.last_withdraw_duobao_time is not null,t1.last_day_withdraw_duobao_coin,0),0) as his_withdraw_duobao_coin
from
(select 
did as ouid   
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), nettype, null), -1*ts) as nettype               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), model, null), -1*ts) as model                 
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), osver, null), -1*ts) as first_osver           
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), osver, null), ts) as last_osver            
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), os, null), -1*ts) as first_os              
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), os, null), ts) as last_os               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), res, null), -1*ts) as res                   
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), carrier, null), -1*ts) as carrier               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), channel, null), -1*ts) as first_channel               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), channel, null), ts) as last_channel              
,null as channel_type          
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), ip, null), -1*ts) as first_ip              
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), ip, null), ts) as last_ip               
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), ouid, null), -1*ts) as first_did_ouid       
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), if(ouid='-1',null,ouid), null), ts) as last_did_ouid        
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), appver, null), -1*ts) as first_appver          
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), appver, null), ts) as last_appver           
,min(if(event='register' and step=0,ts,null)) as registe_time          
,min(if(event in('login','active','openclient','enterfront','register'),ts, null)) as first_login_time      
,max(if(event in('login','active','openclient','enterfront','register'),ts, null)) as last_login_time
,sum(if(event in('login','active','openclient','enterfront','register'),1, 0)) as last_login_cnt    
,min(if(event in('enterfront','openclient'),ts, null)) as first_openclient_time 
,max(if(event in('enterfront','openclient'),ts, null)) as last_openclient_time  
,sum(if(event in('enterfront','openclient'),1, 0)) as last_openclient_cnt   
,min(if(event = 'comment' and replyid = '0' and status = '1',ts, null)) as first_comment_time    
,max(if(event = 'comment' and replyid = '0' and status = '1',ts, null)) as last_comment_time     
,sum(if(event = 'comment' and replyid = '0' and status = '1',1, 0)) as last_comment_cnt     
,min(if(event = 'like' and type=3,ts, null)) as first_vote_time       
,max(if(event = 'like' and type=3,ts, null)) as last_vote_time        
,sum(if(event = 'like' and type=3,1, 0)) as last_vote_cnt         
,min(if(event = 'search',ts, null)) as first_search_time     
,max(if(event = 'search',ts, null)) as last_search_time      
,sum(if(event = 'search',1, 0)) as last_search_cnt       
,min(if(event = 'favour' and status='1',ts, null)) as first_favor_time      
,max(if(event = 'favour' and status='1',ts, null)) as last_favor_time       
,sum(if(event = 'favour' and status='1',1, 0)) as last_favor_cnt        
,min(if(event = 'like' and type=1,ts, null)) as first_like_time       
,max(if(event = 'like' and type=1,ts, null)) as last_like_time        
,sum(if(event = 'like' and type=1,1, 0)) as last_like_cnt         
,min(if(event = 'share' and status='3',ts, null)) as first_share_time      
,max(if(event = 'share' and status='3',ts, null)) as last_share_time       
,sum(if(event = 'share' and status='3',1, 0)) as last_share_cnt        
,min(if(event = 'attention' and status='1',ts, null)) as first_attention_time  
,max(if(event = 'attention' and status='1',ts, null)) as last_attention_time   
,sum(if(event = 'attention' and status='1',1, 0)) as last_attention_cnt    
,min(if(event = 'comment' and replyid <> '0' and status = '1',ts, null)) as first_reply_time     
,max(if(event = 'comment' and replyid <> '0' and status = '1',ts, null)) as last_reply_time           
,sum(if(event = 'comment' and replyid <> '0' and status = '1',1, 0)) as last_reply_cnt
,sum(if(event = 'enterbackground' and staytime>=1000 and staytime<=24*60*60*1000,1, 0)) as enterbackground_cnt   
,sum(if(event = 'enterbackground' and staytime>=1000 and staytime<=24*60*60*1000,staytime, 0)) as usetime                
,sum(if(event = 'read',1, 0)) as pagenum               
,sum(if(event = 'apperr' and type=1,1, 0)) as crash_cnt    
,max(if(event = 'validread',ts, null)) as last_validread_time
,sum(if(event = 'validread',1,0)) as last_validread_cnt
,min(if(event = 'contributioninc' and contributionpoint>0,ts,null)) as first_contribution_time
,max(if(event = 'contributioninc' and contributionpoint>0,ts,null)) as last_contribution_time
,sum(if(event = 'contributioninc' and contributionpoint>0,contributionpoint,0)) as last_day_contributionpoint
,min(if(event = 'verifypointinc' and verifypoint>0,ts,null)) as first_verify_time
,max(if(event = 'verifypointinc' and verifypoint>0,ts,null)) as last_verify_time
,sum(if(event = 'verifypointinc' and verifypoint>0,verifypoint,0)) as last_day_verifypoint
,min(if(event = 'read',ts, null)) as first_read_time    
,max(if(event = 'read',ts, null)) as last_read_time     
,sum(if(event = 'read',1, 0)) as last_read_cnt
,min(if(event = 'play',ts, null)) as first_play_time    
,max(if(event = 'play',ts, null)) as last_play_time     
,sum(if(event = 'play',1, 0)) as last_play_cnt
,min(if(event = 'discoverer' and status=1,ts,null)) as first_discover_time
,maxbytimenotnull(if(event = 'discoverer' and status=1,1,status),ts) as is_discover
,min(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,ts,null)) as first_withdraw_time
,max(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,ts,null)) as last_withdraw_time
,sum(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,rmb,0)) as last_day_withdraw_rmb
,sum(if(event='withdraw' and ts>=1535472000 and status=2 and type=1,coin,0)) as last_day_withdraw_coin
,min(if(event='report' and status=1,ts,null)) as  first_report_time
,max(if(event='report' and status=1,ts,null)) as last_report_time
,sum(if(event='report' and status=1,1,0)) as last_report_cnt
,min(if(event='nointerest',ts,null)) as first_nointerest_time
,max(if(event='nointerest',ts,null)) as last_nointerest_time
,sum(if(event='nointerest',1,0)) as last_nointerest_cnt    
,max(if(event in('login','active','enterfront','openclient','register') and nvl(ouid,'-1')<>'-1',ts, null)) as last_did_ouid_time
,sum(if(event = 'enterfront',1, 0)) as enterfront_cnt   
,sum(if(event = 'enterfront',staytime, 0)) as gaptime
,sum(if(event = 'enterfront' and staytime<30000,1,0)) as enterfront_under30_cnt
,min(if(event='withdraw' and ts>=1535472000 and status=2 and type=2,ts,null)) as first_withdraw_hongbao_time
,max(if(event='withdraw' and ts>=1535472000 and status=2 and type=2,ts,null)) as last_withdraw_hongbao_time
,sum(if(event='withdraw' and ts>=1535472000 and status=2 and type=2,rmb,0)) as last_day_withdraw_hongbao_rmb
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), market_channel_type, null), -1*ts) as first_market_channel_type
,maxbytimenotnull(if(event in('login','active','enterfront','openclient','register'), market_channel_type, null), ts) as last_market_channel_type
,min(if(event='withdraw' and ts>=1535472000 and type=3,ts,null)) as first_withdraw_duobao_time
,max(if(event='withdraw' and ts>=1535472000 and type=3,ts,null)) as last_withdraw_duobao_time
,sum(if(event='withdraw' and ts>=1535472000 and type=3,rmb,0)) as last_day_withdraw_duobao_rmb
,sum(if(event='withdraw' and ts>=1535472000 and type=3,coin,0)) as last_day_withdraw_duobao_coin
from {sourcetab} 
where ds = '{ds}' and nvl(did,'-1')<>'-1'
group by did)t1
full outer join
(select * from {targettab} where ds = date_sub('{ds}',1) and ut = 'did'
)t2
on t1.ouid = t2.ouid;

insert overwrite table {targettab} partition (ds='{ds}', ut='uid')
select t1.ouid,
nettype,
model,
first_osver,
last_osver,
first_os,
last_os,
res,
carrier,
first_channel,
channel_type,
first_ip,
last_ip,
first_did_ouid,
last_did_ouid,
first_appver,
last_appver,
registe_time,
first_login_time,
last_login_time,
first_openclient_time,
last_openclient_time,
last_openclient_cnt,
first_comment_time,
last_comment_time,
last_comment_cnt,
first_vote_time,
last_vote_time,
last_vote_cnt,
first_search_time,
last_search_time,
last_search_cnt,
first_favor_time,
last_favor_time,
last_favor_cnt,
first_like_time,
last_like_time,
last_like_cnt,
first_share_time,
last_share_time,
last_share_cnt,
first_attention_time,
last_attention_time,
last_attention_cnt,
first_reply_time,
last_reply_time,
last_reply_cnt,
enterbackground_cnt,
usetime,
pagenum,
crash_cnt,
last_validread_time,
last_validread_cnt,
first_contribution_time,
last_contribution_time,
last_day_contributionpoint,
first_verify_time,
last_verify_time,
last_day_verifypoint,
first_read_time,
last_read_time,
last_read_cnt, 
first_play_time,
last_play_time,  
last_play_cnt,
case when t2.event='joinfamily' then t2.familyid
     when t2.event='leavefamily' then null
     else t1.familyid
end as familyid,
first_discover_time,
is_discover,
inviterid,
inviter_time,
media_status
,first_withdraw_time
,last_withdraw_time
,last_day_withdraw_rmb
,his_withdraw_rmb
,last_day_withdraw_coin
,his_withdraw_coin
,first_money_gain_time
,last_money_gain_time
,last_day_money_gain 
,his_money_gain
,first_report_time
,last_report_time
,last_report_cnt
,first_nointerest_time
,last_nointerest_time
,last_nointerest_cnt
,media_type
,last_login_cnt
,last_did_ouid_time
,enterfront_cnt
,gaptime
,last_getwb_time
,last_day_getwb_wb
,his_getwb_wb
,last_channel
,enterfront_under30_cnt
,phone
,registe_inviter
,first_withdraw_hongbao_time
,last_withdraw_hongbao_time
,last_day_withdraw_hongbao_rmb
,his_withdraw_hongbao_rmb
,first_market_channel_type
,last_market_channel_type
,first_withdraw_duobao_time
,last_withdraw_duobao_time
,last_day_withdraw_duobao_rmb
,his_withdraw_duobao_rmb
,last_day_withdraw_duobao_coin
,his_withdraw_duobao_coin
from(select * from {targettab} where ds='{ds}' and ut='uid')t1
left join(
    select ouid,
    maxbytime(familyid,ts) as familyid,
    maxbytime(event,ts) as event
    from {sourcetab}
    where ds = '{ds}' and eventtype='medium' and event in ('joinfamily','leavefamily')
    group by ouid
)t2
on t1.ouid=t2.ouid;

insert overwrite table {targettab} partition (ds='{ds}', ut='uid')
select nvl(t1.ouid,t2.uid) as ouid,
nettype,
model,
first_osver,
last_osver,
first_os,
last_os,
res,
carrier,
first_channel,
channel_type,
first_ip,
last_ip,
first_did_ouid,
last_did_ouid,
first_appver,
last_appver,
registe_time,
first_login_time,
last_login_time,
first_openclient_time,
last_openclient_time,
last_openclient_cnt,
first_comment_time,
last_comment_time,
last_comment_cnt,
first_vote_time,
last_vote_time,
last_vote_cnt,
first_search_time,
last_search_time,
last_search_cnt,
first_favor_time,
last_favor_time,
last_favor_cnt,
first_like_time,
last_like_time,
last_like_cnt,
first_share_time,
last_share_time,
last_share_cnt,
first_attention_time,
last_attention_time,
last_attention_cnt,
first_reply_time,
last_reply_time,
last_reply_cnt,
enterbackground_cnt,
usetime,
pagenum,
crash_cnt,
last_validread_time,
last_validread_cnt,
first_contribution_time,
last_contribution_time,
last_day_contributionpoint,
first_verify_time,
last_verify_time,
last_day_verifypoint,
first_read_time,
last_read_time,
last_read_cnt, 
first_play_time,
last_play_time,  
last_play_cnt,
familyid,
first_discover_time,
is_discover,
inviterid,
inviter_time,
media_status
,first_withdraw_time
,last_withdraw_time
,last_day_withdraw_rmb
,his_withdraw_rmb
,last_day_withdraw_coin
,his_withdraw_coin
,nvl(t1.first_money_gain_time,t2.first_money_gain_time) as first_money_gain_time
,nvl(t2.last_money_gain_time,t1.last_money_gain_time) as last_money_gain_time
,nvl(if(t2.last_money_gain_time is not null,t2.last_day_money_gain,null),t1.last_day_money_gain) as last_day_money_gain
,nvl(t1.his_money_gain,0)+nvl(if(t2.last_money_gain_time is not null,t2.last_day_money_gain,null),0) as his_money_gain
,first_report_time
,last_report_time
,last_report_cnt
,first_nointerest_time
,last_nointerest_time
,last_nointerest_cnt
,media_type
,last_login_cnt
,last_did_ouid_time
,enterfront_cnt
,gaptime
,last_getwb_time
,last_day_getwb_wb
,his_getwb_wb
,last_channel
,enterfront_under30_cnt
,phone
,registe_inviter
,first_withdraw_hongbao_time
,last_withdraw_hongbao_time
,last_day_withdraw_hongbao_rmb
,his_withdraw_hongbao_rmb
,first_market_channel_type
,last_market_channel_type
,first_withdraw_duobao_time
,last_withdraw_duobao_time
,last_day_withdraw_duobao_rmb
,his_withdraw_duobao_rmb
,last_day_withdraw_duobao_coin
,his_withdraw_duobao_coin
from(select * from {targettab} where ds='{ds}' and ut='uid')t1
full join(
    select uid,
    min(time) as first_money_gain_time,
    max(time) as last_money_gain_time,
    sum(value) as last_day_money_gain
    from {sourcetab2}
    where ds = '{ds}'
    group by uid
)t2
on t1.ouid=t2.uid;
'''


def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.idl_wutiao_user', sourcetab='wutiao.bdl_wutiao_event', sourcetab2='wutiao.odl_wutiao_money_gain')
    kdc.debug = True
    res = kdc.doHive(sql, True, True)
    if res != 0:
        raise Exception("wutiao.idl_wutiao_user insert error")


if __name__ == '__main__':
    main()
