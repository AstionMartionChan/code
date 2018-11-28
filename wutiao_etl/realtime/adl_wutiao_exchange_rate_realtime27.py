# coding: utf-8

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 五条汇率计算表
#*作者: gant
#*时间: 2018-06-13
#*备注: 五条汇率计算表
#***************************************************************#

import kingnet
import os

sql_day = '''
insert overwrite table {targettab} partition (ds='{ds}')
select ds as fds,
sum(if(from_unixtime(registe_time,'yyyy-MM-dd')=ds,1,0)) as total_register,
sum(if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_rmb,0)) as total_withdraw_rmb,
sum(if(from_unixtime(last_withdraw_time,'yyyy-MM-dd')=ds,last_day_withdraw_coin,0)) as total_withdraw_coin,
sum(if(from_unixtime(last_money_gain_time,'yyyy-MM-dd')=ds,last_day_money_gain,0)) as total_money_gain
from {sourcetab}
where ds='{ds}' and ut='uid'
group by ds;
'''

delete='''echo "use wutiao; delete from adl_wutiao_exchange_rate_realtime where fds ='{ds}'; " |mysql -h 172.17.2.91 -u root -p123456 '''
sync='''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/adl_wutiao_exchange_rate_realtime/ds='{ds}' --connect jdbc:mysql://172.17.2.91:3306/wutiao --username "root" --password '123456' --table "adl_wutiao_exchange_rate_realtime" --input-fields-terminated-by "|" --input-null-string "\\\\\N" --input-null-non-string "\\\\\N" '''

def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.adl_wutiao_exchange_rate_realtime',
                         sourcetab='wutiao.idl_wutiao_user')
    kdc.debug = True
    print(sql)
    res = kdc.doHive(sql,True,True)
    if res != 0:
        raise Exception('wutiao.adl_wutiao_exchange_rate_realtime hive table insert error!')

    res1 = os.system(delete.format(ds=ds))
    res2 = os.system(sync.format(ds=ds))

    if (res1 or res2) !=0 :
        print(res1,res2)
        raise Exception('adl_wutiao_exchange_rate_realtime mysql table delete&insert execute failure!!')
    else :
        print(res1,res2)
        print('complete')


if __name__ == '__main__':
    main()
