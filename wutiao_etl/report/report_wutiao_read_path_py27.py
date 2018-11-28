# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-访问路径分析
#*作者：Leo
#*时间：2018-05-28
#*备注：区块链-访问路径分析
#***************************************************************#
import kingnet
import os

sql_day = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}'AS fds,
       os,
       channel,
       itemtype,
       sum(homepage_pv) AS homepage_pv,
       sum(homepage_uv) AS homepage_uv,
       sum(second_pv) AS second_pv,
       sum(second_uv) AS second_uv,
       sum(content_pv) AS content_pv,
       sum(content_uv) AS content_uv,
       sum(readlist_pv) AS readlist_pv,
       sum(readlist_uv) AS readlist_uv,
       sum(exit_pv) AS exit_pv,
       sum(exit_uv) AS exit_uv
FROM
  (SELECT nvl(os,'allos') AS os,
          nvl(channel,'allchannel') AS channel,
          nvl(itemtype,'allitemtype') as itemtype,
          ouid,
          sum(if((curr_event='click' AND target='A') OR (curr_event='openclient'),1,0))
          - sum(if(curr_event='click' AND pos='S' AND target='S_1',1,0)) AS homepage_pv,
          max(if((curr_event='click' AND target='A') OR (curr_event='openclient'),1,0)) AS homepage_uv,
          sum(if(curr_event='click' AND pos='A' AND target IS NOT NULL,1,0)) AS second_pv,
          max(if(curr_event='click' AND pos='A' AND target IS NOT NULL,1,0)) AS second_uv,
          sum(if(curr_event='click' AND target='P',1,0)) AS content_pv,
          max(if(curr_event='click' AND target='P',1,0)) AS content_uv,
          sum(if(curr_event='readlist',1,0)) AS readlist_pv,
          max(if(curr_event='readlist',1,0)) AS readlist_uv,
          sum(if(curr_event='openclient' AND (next_event IS NULL OR next_event<>'click'),1,0)) AS exit_pv,
          max(if(curr_event='openclient',1,0))
          - max(if(curr_event='openclient' AND next_event='click',1,0)) AS exit_uv
   FROM
     (SELECT nvl(os,'-1') AS os,
             nvl(channel,'-1') AS channel,
             nvl(itemtype,'-1') as itemtype,
             ouid,
             curr_event,
             next_event,
             pos,
             target
      FROM {sourcetab}
      WHERE ds='{ds}') AS a
   GROUP BY os,
            channel,
            itemtype,
            ouid
   GROUPING sets((os,channel,itemtype,ouid)
                ,(os,channel,ouid)
                ,(os,itemtype,ouid)
                ,(channel,itemtype,ouid)
                ,(os,ouid)
                ,(channel,ouid)
                ,(itemtype,ouid)
                ,(ouid))) AS t
GROUP BY os,
         channel,
         itemtype;
'''

delete='''echo "use wutiao; delete from report_wutiao_read_path where fds ='{ds}'; " |mysql -h 172.17.2.91 -u root -p123456 '''
sync='''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao_db/report_wutiao_read_path/ds='{ds}' --connect jdbc:mysql://172.17.2.91:3306/wutiao --username "root" --password '123456' --table "report_wutiao_read_path" --input-fields-terminated-by "|" --input-null-string "\\\\\N" --input-null-non-string "\\\\\N" '''


def main():
    kdc = kingnet.kdc()
    ds = kdc.workDate
    sql = sql_day.format(ds=ds, targettab='wutiao.report_wutiao_read_path', sourcetab='wutiao.idl_wutiao_user_event')
    kdc.debug = True

    # print sql
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('wutiao.report_wutiao_read_path insert execute failure!!')

    res1 = os.system(delete.format(ds=ds))
    res2 = os.system(sync.format(ds=ds))

    if (res1 or res2) !=0 :
        print res1,res2
        raise Exception('wutiao.report_wutiao_read_path mysql delete&insert execute failure!!')
    else :
        print res1,res2
        print 'complete'


if __name__ == '__main__':
    main()
