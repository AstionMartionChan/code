# coding: utf-8

#**********************程序说明*********************************#
#*模块：idl
#*功能：区块链-用户家庭中间表
#*作者：Leo
#*时间：2018-08-01
#*备注：区块链-用户家庭中间表
#***************************************************************#

import kingnetdc

SQL_DAY = '''
INSERT overwrite TABLE {targettab} partition (ut='uid',ds='{ds}')
SELECT nvl(m.ouid,n.ouid) AS ouid,
       nvl(m.familyid,n.familyid) AS familyid,
       nvl(n.createid,m.createid) AS createid,
       nvl(n.createdate,m.createdate) AS createdate,
       nvl(n.inviterid,m.inviterid) AS inviterid,
       nvl(n.invitedate,m.invitedate) AS invitedate,
       nvl(n.first_os,m.first_os) AS first_os,
       nvl(m.last_os,n.last_os) AS last_os,
       nvl(n.first_appver,m.first_appver) AS first_appver,
       nvl(m.last_appver,n.last_appver) AS last_appver,
       nvl(n.first_channel,m.first_channel) AS first_channel,
       nvl(m.last_channel,n.last_channel) AS last_channel
FROM
  (SELECT ouid,
          familyid ,
          maxbytimenotnull(if(event='createfamily' AND ouid<>0,ouid,null),-1*ts) AS createid ,
          min(if(event='createfamily',ts,NULL)) AS createdate ,
          maxbytimenotnull(if(event='joinfamily' AND inviterid<>0,inviterid,NULL),-1*ts) AS inviterid ,
          min(if(event='joinfamily',ts,NULL)) AS invitedate ,
          maxbytimenotnull(os,-1*ts) AS first_os ,
          maxbytimenotnull(os,ts) AS last_os ,
          maxbytimenotnull(appver,-1*ts) AS first_appver ,
          maxbytimenotnull(appver,ts) AS last_appver ,
          maxbytimenotnull(channel,-1*ts) AS first_channel ,
          maxbytimenotnull(channel,ts) AS last_channel
   FROM {sourcetab}
   WHERE ds='{ds}'
     AND event in('createfamily' ,'joinfamily')
     AND eventtype='medium'
   GROUP BY ouid,
            familyid) AS m
FULL OUTER JOIN
  (SELECT *
   FROM {targettab}
   WHERE ds=date_sub('{ds}',1)
     AND ut='uid') AS n ON m.ouid=n.ouid
AND m.familyid=n.familyid;

'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.bdl_wutiao_event',
        'targettab': 'wutiao.idl_wutiao_user_family'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)


if __name__ == '__main__':
    main()
