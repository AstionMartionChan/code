# coding: utf-8

#**********************程序说明*********************************#
#*模块: IDL
#*功能: 区块链文章价值中间表
#*作者:gant
#*时间:2018-06-26
#*备注:文章价值中间表
#***************************************************************#
import kingnet

sql_day = '''
insert overwrite table {targettab} partition (ds='{ds}')
select nvl(t1.itemid,t2.itemid) as itemid,
nvl(t1.itemtype,t2.itemtype) as itemtype,
day_creative_income,
day_read_income,   
day_vote_income,   
day_share_income,
day_comment_income,
his_creative_income,
his_read_income,    
his_vote_income,    
his_share_income,  
his_comment_income,
day_contribute,     
day_verifypoint,    
his_contribute,     
his_verifypoint,
nvl(t1.day_addcomment_cnt,0) as day_addcomment_cnt,
nvl(t1.day_addcomment_cnt,0)+nvl(t2.his_addcomment_cnt,0) as his_addcomment_cnt,
nvl(t1.day_delcomment_cnt,0) as day_delcomment_cnt,
nvl(t1.day_delcomment_cnt,0)+nvl(t2.his_delcomment_cnt,0) as his_delcomment_cnt,
nvl(t1.day_itemid_like_cnt,0) as day_itemid_like_cnt,
nvl(t1.day_itemid_like_cnt,0)+nvl(t2.his_itemid_like_cnt,0) as his_itemid_like_cnt,
nvl(t1.day_itemid_commentlike_cnt,0) as day_itemid_commentlike_cnt,
nvl(t1.day_itemid_commentlike_cnt,0)+nvl(t2.day_itemid_commentlike_cnt,0) as his_itemid_commentlike_cnt,
nvl(t1.day_itemid_discover_vote_cnt,0) as day_itemid_discover_vote_cnt,
nvl(t1.day_itemid_discover_vote_cnt,0)+nvl(t2.his_itemid_discover_vote_cnt,0) as his_itemid_discover_vote_cnt,
nvl(t1.day_clickshare_cnt,0) as day_clickshare_cnt,
nvl(t1.day_clickshare_cnt,0)+nvl(t2.his_clickshare_cnt,0) as his_clickshare_cnt,
nvl(t1.day_cancelshare_cnt,0) as day_cancelshare_cnt,
nvl(t1.day_cancelshare_cnt,0)+nvl(t2.his_cancelshare_cnt,0) as his_cancelshare_cnt,
nvl(t1.day_successshare_cnt,0) as day_successshare_cnt,
nvl(t1.day_successshare_cnt,0)+nvl(t2.his_successshare_cnt,0) as his_successshare_cnt,
nvl(t1.day_read_cnt,0) as day_read_cnt,
nvl(t1.day_read_cnt,0)+nvl(t2.his_read_cnt,0) as his_read_cnt,
nvl(t1.day_validread_cnt,0) as day_validread_cnt,
nvl(t1.day_validread_cnt,0)+nvl(t2.his_validread_cnt,0) as his_validread_cnt,
nvl(t1.day_report_cnt,0) as day_report_cnt,
nvl(t1.day_report_cnt,0)+nvl(t2.his_report_cnt,0) as his_report_cnt,
nvl(t1.day_nointerest_cnt,0) as day_nointerest_cnt,
nvl(t1.day_nointerest_cnt,0)+nvl(t2.his_nointerest_cnt,0) as his_nointerest_cnt,
nvl(t1.day_addfavour_cnt,0) as day_addfavour_cnt,
nvl(t1.day_addfavour_cnt,0)+nvl(t2.his_addfavour_cnt,0) as his_addfavour_cnt,
nvl(t1.day_delfavour_cnt,0) as day_delfavour_cnt,
nvl(t1.day_delfavour_cnt,0)+nvl(t2.his_delfavour_cnt,0) as his_delfavour_cnt,
nvl(t1.day_play_cnt,0) as day_play_cnt,
nvl(t1.day_play_cnt,0)+nvl(t2.his_play_cnt,0) as his_play_cnt,
nvl(t1.day_showinpage_cnt,0) as day_showinpage_cnt,
nvl(t1.day_showinpage_cnt,0)+nvl(t2.his_showinpage_cnt,0) as his_showinpage_cnt,
day_income,
his_income,
null as uid
from(
    select itemid,
    maxbytimenotnull(itemtype,ts) as itemtype,
    sum(if(event='comment' and status=1,1,0)) as day_addcomment_cnt,
    sum(if(event='comment' and status=2,1,0)) as day_delcomment_cnt,
    sum(if(event='like' and type=1,1,0)) as day_itemid_like_cnt,
    sum(if(event='like' and type=2,1,0)) as day_itemid_commentlike_cnt,
    sum(if(event='like' and type=3,1,0)) as day_itemid_discover_vote_cnt,
    sum(if(event='share' and status=1,1,0)) as day_clickshare_cnt,
    sum(if(event='share' and status=2,1,0)) as day_cancelshare_cnt,
    sum(if(event='share' and status=3,1,0)) as day_successshare_cnt,
    sum(if(event='read',1,0)) as day_read_cnt,
    sum(if(event='validread',1,0)) as day_validread_cnt,
    sum(if(event='report' and status=1,1,0)) as day_report_cnt,
    sum(if(event='nointerest',1,0)) as day_nointerest_cnt,
    sum(if(event='favour' and status=1,1,0)) as day_addfavour_cnt,
    sum(if(event='favour' and status='-1',1,0)) as day_delfavour_cnt,
    sum(if(event='play' and type=1,1,0)) as day_play_cnt,
    sum(if(event='showinpage',1,0)) as day_showinpage_cnt
    from (
    select ts,event,status,type,itemid,itemtype
    from {sourcetab1}
    where ds='{ds}' and event in('comment','like','share','read','validread','report','nointerest','play')
    union all
    select ts,event,status,type,split(itemid,'@')[0] as itemid,if(split(itemid,'@')[2]='article',1,2) as itemtype 
    from (select ts,event,status,type,itemlist from {sourcetab1} where ds='{ds}' and event='favour')t1 
    lateral view explode (split(itemlist,','))item as itemid
    union all
    select ts,event,status,type,split(itemid,'@')[0] as itemid,if(split(itemid,'@')[1]='article',1,2) as itemtype 
    from (select ts,event,status,type,itemlist from {sourcetab1} where ds='{ds}' and event='showinpage')t1 
    lateral view explode (split(itemlist,','))item as itemid
    )tt1
    where nvl(itemid,'')<>'' and nvl(itemid,'(null)')<>'(null)'
    group by itemid
)t1
full join(select * from {targettab} where ds=date_sub('{ds}',1))t2
on cast(t1.itemid as bigint)=cast(t2.itemid as bigint);

insert overwrite table {targettab} partition (ds='{ds}')
select nvl(tt1.itemid,tt2.itemid) as itemid,
tt2.itemtype as itemtype,
nvl(tt1.day_creative_income,0) as day_creative_income,
nvl(tt1.day_read_income,0) as day_read_income,   
nvl(tt1.day_vote_income,0) as day_vote_income,   
nvl(tt1.day_share_income,0) as day_share_income,
nvl(tt1.day_comment_income,0) as day_comment_income,
nvl(tt1.day_creative_income,0)+nvl(tt2.his_creative_income,0) as his_creative_income,
nvl(tt1.day_read_income,0)+nvl(tt2.his_read_income,0) as his_read_income,    
nvl(tt1.day_vote_income,0)+nvl(tt2.his_vote_income,0) as his_vote_income,    
nvl(tt1.day_share_income,0)+nvl(tt2.his_share_income,0) as his_share_income,  
nvl(tt1.day_comment_income,0)+nvl(tt2.his_comment_income,0) as his_comment_income,
nvl(tt1.day_contributionpoint,0) as day_contribute,     
nvl(tt1.day_verifypoint,0) as day_verifypoint,    
nvl(tt1.day_contributionpoint,0)+nvl(tt2.his_contribute,0) as his_contribute,     
nvl(tt1.day_verifypoint,0)+nvl(his_verifypoint,0) as  his_verifypoint,
day_addcomment_cnt,
his_addcomment_cnt,
day_delcomment_cnt,
his_delcomment_cnt,
day_itemid_like_cnt,
his_itemid_like_cnt,
his_itemid_commentlike_cnt,
his_itemid_commentlike_cnt,
day_itemid_discover_vote_cnt,
his_itemid_discover_vote_cnt,
day_clickshare_cnt,
his_clickshare_cnt,
day_cancelshare_cnt,
his_cancelshare_cnt,
day_successshare_cnt,
his_successshare_cnt,
day_read_cnt,
his_read_cnt,
day_validread_cnt,
his_validread_cnt,
day_report_cnt,
his_report_cnt,
day_nointerest_cnt,
his_nointerest_cnt,
day_addfavour_cnt,
his_addfavour_cnt,
day_delfavour_cnt,
his_delfavour_cnt,
day_play_cnt,
his_play_cnt,
day_showinpage_cnt,
his_showinpage_cnt,
nvl(tt1.day_income,0) as day_income,
nvl(tt1.day_income,0)+nvl(his_income,0) as his_income,
null as uid
from(
    select nvl(t1.itemid,t2.itemid) as itemid,
    nvl(t1.creative_income,0) as day_creative_income,
    nvl(t1.read_income,0) as day_read_income,
    nvl(t1.vote_income,0) as day_vote_income,
    nvl(t1.share_income,0) as day_share_income,
    nvl(t1.comment_income,0) as day_comment_income,
    nvl(t2.contributionpoint,0) as day_contributionpoint,
    nvl(t2.verifypoint,0) as day_verifypoint,
    nvl(t1.day_income,0) as day_income
    from(
        select itemid,
        sum(if(actiontype=10,value,0)) as creative_income,
        sum(if(actiontype=11,value,0)) as read_income,
        sum(if(actiontype=12,value,0)) as vote_income,
        sum(if(actiontype=13,value,0)) as share_income,
        sum(if(actiontype=14,value,0)) as comment_income,
        sum(value) as day_income
        from {sourcetab2}
        where ds = '{ds}' and actiontype in(10,11,12,13,14) and itemid is not null
        group by itemid
    )t1
    full outer join(
        select itemid,
        sum(if(event='contributioninc',contributionpoint,0)) as contributionpoint,
        sum(if(event='verifypointinc',verifypoint,0)) as verifypoint
        from {sourcetab1} 
        where ds = '{ds}' and event in ('contributioninc','verifypointinc') and itemid is not null
        group by itemid
    )t2
    on cast(t1.itemid as bigint)=cast(t2.itemid as bigint)
)tt1
full join(select * from {targettab} where ds='{ds}')tt2
on cast(tt1.itemid as bigint)=cast(tt2.itemid as bigint);

insert overwrite table {targettab} partition (ds='{ds}')
select t1.itemid,
itemtype,
day_creative_income,
day_read_income,   
day_vote_income,   
day_share_income,
day_comment_income,
his_creative_income,
his_read_income,    
his_vote_income,    
his_share_income,  
his_comment_income,
day_contribute,     
day_verifypoint,    
his_contribute,     
his_verifypoint,
day_addcomment_cnt,
his_addcomment_cnt,
day_delcomment_cnt,
his_delcomment_cnt,
day_itemid_like_cnt,
his_itemid_like_cnt,
his_itemid_commentlike_cnt,
his_itemid_commentlike_cnt,
day_itemid_discover_vote_cnt,
his_itemid_discover_vote_cnt,
day_clickshare_cnt,
his_clickshare_cnt,
day_cancelshare_cnt,
his_cancelshare_cnt,
day_successshare_cnt,
his_successshare_cnt,
day_read_cnt,
his_read_cnt,
day_validread_cnt,
his_validread_cnt,
day_report_cnt,
his_report_cnt,
day_nointerest_cnt,
his_nointerest_cnt,
day_addfavour_cnt,
his_addfavour_cnt,
day_delfavour_cnt,
his_delfavour_cnt,
day_play_cnt,
his_play_cnt,
day_showinpage_cnt,
his_showinpage_cnt,
day_income,
his_income,
t2.uid
from (select * from {targettab} where ds='{ds}')t1
left join (select item_id,uid from {sourcetab3} where ds='2018-08-22')t2
on cast(t1.itemid as bigint)=cast(t2.item_id as bigint)
where t1.itemid is not null;
'''

def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.idl_wutiao_itemid_wb', sourcetab1='wutiao.bdl_wutiao_event',sourcetab2='wutiao.odl_wutiao_wb',sourcetab3='wutiao.idl_wutiao_useritemid_sync')
    kdc.debug = True
    res = kdc.doHive(sql,True,True)
    if res != 0:
        raise Exception('wutiao.idl_wutiao_itemid_wb insert error!')

if __name__ == '__main__':
    main()
