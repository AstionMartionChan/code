# coding: utf-8

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 五条平台统计率
#*作者:gant
#*时间:2018-05-30
#*备注:五条最近30天平台平均有效观看率、内容投票率、内容评论率、内容分享率
#***************************************************************#

import kingnetdc

sql_day = '''
set mapreduce.map.memory.mb=3000;
set mapreduce.reduce.memory.mb=3000;
set hive.merge.mapredfiles=false;
set hive.merge.mapfiles=false;

insert overwrite table {targettab} partition (ds='{ds}')
select total_palyread_usercnt,
validread_usercnt,
like_usercnt,
comment_usercnt,
share_usercnt,
round(validread_usercnt/total_palyread_usercnt,4) as validread_rate,
round(like_usercnt/total_palyread_usercnt,4) as like_rate,
round(comment_usercnt/total_palyread_usercnt,4) as comment_rate,
round(share_usercnt/total_palyread_usercnt,4) as share_rate
from(
    select ds,
    sum(if(from_unixtime(last_read_time,'yyyy-MM-dd')>=date_sub('{ds}',29) or from_unixtime(last_play_time,'yyyy-MM-dd')>=date_sub('{ds}',29) 
        or from_unixtime(last_validread_time,'yyyy-MM-dd')>=date_sub('{ds}',29),1,0)) as total_palyread_usercnt,
    sum(if(from_unixtime(last_validread_time,'yyyy-MM-dd')>=date_sub('{ds}',29),1,0)) as validread_usercnt,
    sum(if(from_unixtime(last_like_time,'yyyy-MM-dd')>=date_sub('{ds}',29),1,0)) as like_usercnt,
    sum(if(from_unixtime(last_comment_time,'yyyy-MM-dd')>=date_sub('{ds}',29),1,0)) as comment_usercnt,
    sum(if(from_unixtime(last_share_time,'yyyy-MM-dd')>=date_sub('{ds}',29),1,0)) as share_usercnt
    from {sourcetab}
    where ds='{ds}' and ut='uid'
    group by ds
)t1;
'''


def main():
    kdc = kingnetdc.kdc
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.adl_wutiao_statistics_rate_realtime',
                         sourcetab='wutiao.idl_wutiao_user')
    kdc.debug = True
    print(sql)
    res = kdc.doHive(sql, True, True)
    if res != 0:
        raise Exception('wutiao.adl_wutiao_statistics_rate_realtime hive table insert error!')


if __name__ == '__main__':
    main()
