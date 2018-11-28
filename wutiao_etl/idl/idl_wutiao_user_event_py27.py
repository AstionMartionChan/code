# coding: utf-8

#**********************程序说明*********************************#
#*模块：idl
#*功能：区块链-用户事件中间表
#*作者：Leo
#*时间：2018-06-28
#*备注：区块链-用户事件中间表
#***************************************************************#

import kingnet

SQL_DAY = '''
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT os,
       appver,
       channel,
       ouid,
       event AS curr_event,
       ts AS curr_time,
       lead(event)over(partition BY ouid ORDER BY ouid,ts) AS next_event,
       lead(ts)over(partition BY ouid ORDER BY ouid,ts) AS next_time,
       lag(event)over(partition BY ouid ORDER BY ouid,ts) AS pre_event,
       lag(ts)over(partition BY ouid ORDER BY ouid,ts) AS pre_time,
       pos,
       target,
       itemtype,
       sortid
FROM
  (SELECT os,
          appver,
          channel,
          ouid,
          ts,
          event,
          pos,
          target,
          itemtype,
          sortid
   FROM {sourcetab}
   WHERE event IN ('openclient',
                   'click',
                   'logout',
                   'enterbackground',
                   'readlist')
     AND ds='{ds}' ) AS m ;
'''


def main():
    kdc = kingnet.kdc()
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.idl_wutiao_user_event'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
