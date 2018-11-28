# coding: utf-8

#**********************程序说明*********************************#
#*模块: adl_wutiao_click_statistics -> ADL
#*功能: 区块链用户点击统计表
#*作者: sunyu
#*时间: 2018-06-19
#*备注:
#***************************************************************#

import kingnet

SQL = ''' 
set hive.exec.dynamic.partition=true;
set hive.exec.dynamic.partition.mode=nonstrict;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;
insert overwrite table %(targettab)s partition(ds)
select '%(run_date)s' as fds, usertype, nvl(channel,'-1'), nvl(appver,'-1'), nvl(model,'-1'), nvl(res,'-1')
    , nvl(os,'-1'), nvl(nettype,'-1'), nvl(carrier,'-1')
    ,sum(if(home_page_tag=1,1,0)) as homepage_click_cnt
    ,count(distinct if(home_page_tag=1,ouid,null)) as homepage_click_usercnt

    ,sum(if(home_page_attention_tag=1,1,0)) as homepage_attention_click_cnt
    ,count(distinct if(home_page_attention_tag=1,ouid,null)) as homepage_attention_click_usercnt

    ,sum(if(home_page_latest_tag=1,1,0)) as homepage_latest_click_cnt
    ,count(distinct if(home_page_latest_tag=1,ouid,null)) as homepage_latest_click_usercnt

    ,sum(if(home_page_income_tag=1,1,0)) as homepage_income_click_cnt
    ,count(distinct if(home_page_income_tag=1,ouid,null)) as homepage_income_click_usercnt

    ,sum(if(search_tag=1,1,0)) as search_click_cnt
    ,count(distinct if(search_tag=1,ouid,null)) as search_click_usercnt

    ,sum(if(video_tag=1,1,0)) as video_click_cnt
    ,count(distinct if(video_tag=1,ouid,null)) as video_click_usercnt

    ,sum(if(find_tag=1,1,0)) as find_click_cnt
    ,count(distinct if(find_tag=1,ouid,null)) as find_click_usercnt

    ,sum(if(wallet_tag=1,1,0)) as wallet_click_cnt
    ,count(distinct if(wallet_tag=1,ouid,null)) as wallet_click_usercnt

    ,sum(if(me_tag=1,1,0)) as personal_click_cnt
    ,count(distinct if(me_tag=1,ouid,null)) as personal_click_usercnt
    
    ,sum(if(search_ret_tag=1,1,0)) as search_result_click_cnt
    ,count(distinct if(search_ret_tag=1,ouid,null)) as search_result_click_usercnt
    
    ,'%(run_date)s' as ds
from (
    select a.ouid, nettype, model, last_os as os, res, carrier, channel, last_appver as appver
        , if(target='A',1,0) as home_page_tag
        , if(target='A_2',1,0) as home_page_attention_tag
        , if(target='A_5',1,0) as home_page_latest_tag
        , if(target='A_1_1',1,0) as home_page_income_tag
        , if(target='B',1,0) as search_tag
        , if(target='C',1,0) as video_tag
        , if(target='F',1,0) as find_tag
        , if(target='E',1,0) as me_tag
        , if(target='D',1,0) as wallet_tag
        , if(target='B_2',1,0) as search_ret_tag
        , if(b.new_tag=1,1,0) as usertype
   from (
        select nvl(ouid,'-1') as ouid, target
        from %(bdl_table)s
        where ds='%(run_date)s'
        and event = 'click'
   ) as a
   left join
   (
        select nvl(ouid,'-1') as ouid, nvl(nettype,'-1') as nettype, nvl(model,'-1') as model
              , nvl(last_os,'-1') as last_os, nvl(res,'-1') as res, nvl(carrier,'-1') as carrier
              , nvl(last_channel,'-1') as channel, nvl(last_appver,'-1') as last_appver
              , if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) as new_tag
        from %(idl_table)s
        where ds='%(run_date)s'
        and ut='uid'
   ) as b 
   on a.ouid=b.ouid
) t
group by usertype, channel, appver, model, res, os, nettype, carrier
'''


def main():
    kdc = kingnet.kdc()
    args = {
        'run_date': kdc.workDate,
        'targettab': 'wutiao.adl_wutiao_click_statistics',
        'bdl_table': 'wutiao.bdl_wutiao_event',
        'idl_table': 'wutiao.idl_wutiao_user',
    }
    sql = SQL % args
    kdc.debug = True
    print(sql)
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
