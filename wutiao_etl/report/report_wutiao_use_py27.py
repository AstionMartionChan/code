# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-用户使用分析
#*作者：Leo
#*时间：2018-05-04
#*备注：区块链-用户使用分析
#***************************************************************#
import kingnet
import os

sql_day = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds,
       nvl(os,'allos') AS os,
       nvl(appver,'allappver') AS appver,
       nvl(channel,'allchannel') AS channel,
       nvl(new_flag,'alldidflag') AS didflag,
       sum(appUV) AS appUV,
       sum(appPV) AS appPV,
       sum(usetime) AS usetime,
       sum(enterbackground_cnt) AS enterbackground_cnt
FROM
  (SELECT nvl(last_os,'-1') AS os,
          nvl(last_appver,'-1') AS appver,
          nvl(last_channel,'-1') AS channel,
          nvl(ouid,'-1') AS did,
          if(from_unixtime(first_login_time,'yyyy-MM-dd')=ds,1,0) AS new_flag,
          if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,last_login_cnt,0) AS appPV,
          if(from_unixtime(last_login_time,'yyyy-MM-dd')=ds,1,0) AS appUV,
          nvl(enterbackground_cnt,0)-nvl(enterfront_under30_cnt,0) as enterbackground_cnt,
          nvl(usetime,0) as usetime
   FROM {sourcetab}
   WHERE ds='{ds}'
     AND ut='did') AS m
GROUP BY os,
         appver,
         channel,
         new_flag WITH CUBE
;
'''

delete='''echo "use wutiao; delete from report_wutiao_use where fds ='{ds}'; " |mysql -h 172.17.2.91 -u root -p123456 '''
sync='''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/report_wutiao_use/ds='{ds}' --connect jdbc:mysql://172.17.2.91:3306/wutiao --username "root" --password '123456' --table "report_wutiao_use" --input-fields-terminated-by "|" --input-null-string "\\\\\N" --input-null-non-string "\\\\\N" '''


def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.report_wutiao_use', sourcetab='wutiao.idl_wutiao_user')
    kdc.debug = True

    # print sql
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('wutiao.report_wutiao_use insert execute failure!!')

    res1 = os.system(delete.format(ds=ds))
    res2 = os.system(sync.format(ds=ds))

    if (res1 or res2) !=0 :
        print res1,res2
        raise Exception('wutiao.report_wutiao_use mysql delete&insert execute failure!!')
    else :
        print res1,res2
        print 'complete'


if __name__ == '__main__':
    main()
