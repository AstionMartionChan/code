# coding: utf-8

#**********************程序说明*********************************#
#*模块：ADL
#*功能：区块链-内容分析
#*作者：Leo
#*时间：2018-05-28
#*备注：区块链-内容分析
#***************************************************************#
import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_TEST

DB_PARAMS = DB_PARAMS_TEST


SQL_DAY = '''
set mapred.max.split.size=10000000;
set mapreduce.map.memory.mb=4096;
set mapreduce.reduce.memory.mb=4096;
set hive.new.job.grouping.set.cardinality=2048;
INSERT overwrite TABLE {targettab} partition (ds='{ds}')
SELECT '{ds}' AS fds,
       nvl(os,'allos') AS os,
       nvl(channel,'allchannel') AS channel,
       nvl(sortid,'allsortid') AS sortid,
       nvl(usertype,'allusertype') AS usertype,
       nvl(itemtype,'allitemtype') AS itemtype,
       sum(his_play_cnt) AS his_play_cnt,
       sum(play_cnt) AS play_cnt,
       sum(valid_play_cnt) AS valid_play_cnt,
       sum(finish_play_cnt) AS finish_play_cnt,
       sum(avg_playtime) AS avg_playtime,
       sum(recommend_play_cnt) AS recommend_play_cnt,
       sum(pagenum) AS pagenum,
       sum(his_read_cnt) AS his_read_cnt,
       sum(read_cnt) AS read_cnt,
       sum(valid_read_cnt) AS valid_read_cnt,
       sum(finish_read_cnt) AS finish_read_cnt,
       sum(jumpout_cnt) AS jumpout_cnt,
       sum(recommend_read_cnt) AS recommend_read_cnt,
       sum(comment_cnt) AS comment_cnt,
       sum(reforward_cnt) AS reforward_cnt,
       sum(reward_cnt) AS reward_cnt,
       sum(attention_cnt) AS attention_cnt
FROM
  (SELECT nvl(os,'-1') as os,
          nvl(channel,'-1') as channel,
          nvl(sortid,'-1') as sortid,
          nvl(usertype,'-1') as usertype,
          nvl(itemtype,'-1') as itemtype,
          his_play_cnt,
          play_cnt,
          valid_play_cnt,
          finish_play_cnt,
          0 as avg_playtime,
          recommend_play_cnt,
          pagenum,
          his_read_cnt,
          read_cnt,
          valid_read_cnt,
          finish_read_cnt,
          0 as jumpout_cnt,
          recommend_read_cnt,
          comment_cnt,
          reforward_cnt,
          reward_cnt,
          attention_cnt
   FROM {sourcetab1}
   WHERE ds='{ds}') AS m
GROUP BY m.os,
         m.channel,
         m.sortid,
         m.usertype,
         m.itemtype WITH CUBE
UNION ALL
SELECT '{ds}' AS fds,
       nvl(os,'allos') as os,
       nvl(channel,'allchannel') channel,
       nvl(sortid,'allsortid') sortid,
       'activeuser' AS usertype,
       nvl(itemtype,'allitemtype')itemtype,
       sum(his_play_cnt) his_play_cnt,
       sum(play_cnt)play_cnt,
       sum(valid_play_cnt)valid_play_cnt,
       sum(finish_play_cnt)finish_play_cnt,
       sum(avg_playtime)avg_playtime,
       sum(recommend_play_cnt)recommend_play_cnt,
       sum(pagenum)pagenum,
       sum(his_read_cnt)his_read_cnt,
       sum(read_cnt)read_cnt,
       sum(valid_read_cnt)valid_read_cnt,
       sum(finish_read_cnt)finish_read_cnt,
       sum(jumpout_cnt)jumpout_cnt,
       sum(recommend_read_cnt)recommend_read_cnt,
       sum(comment_cnt)comment_cnt,
       sum(reforward_cnt)reforward_cnt,
       sum(reward_cnt)reward_cnt,
       sum(attention_cnt)attention_cnt
FROM
  (SELECT nvl(os,'-1') as os,
          nvl(channel,'-1') as channel,
          nvl(sortid,'-1') as sortid,
          nvl(itemtype,'-1') as itemtype,
          his_play_cnt,
          play_cnt,
          valid_play_cnt,
          finish_play_cnt,
          0 as avg_playtime,
          recommend_play_cnt,
          pagenum,
          his_read_cnt,
          read_cnt,
          valid_read_cnt,
          finish_read_cnt,
          0 as jumpout_cnt,
          recommend_read_cnt,
          comment_cnt,
          reforward_cnt,
          reward_cnt,
          attention_cnt
   FROM {sourcetab1}
   WHERE ds='{ds}'
     AND usertype in('newuser','olduser')) AS m
GROUP BY m.os,m.channel,m.sortid,m.itemtype WITH CUBE;
'''


def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab1': 'wutiao.adl_wutiao_play_read_statistics',
        'targettab': 'wutiao.report_wutiao_content_analysis',
        'mysqltab': 'wutiao.report_wutiao_content_analysis'
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab1=args['sourcetab1'])
    kdc.debug = True

    # print sql
    kdc.doHive(sql)

    presto_sql = ''' SELECT fds,os,channel,sortid,usertype,itemtype,his_play_cnt,play_cnt,valid_play_cnt
                            ,finish_play_cnt,avg_playtime,recommend_play_cnt,pagenum,his_read_cnt
                            ,read_cnt,valid_read_cnt,finish_read_cnt,jumpout_cnt,recommend_read_cnt,comment_cnt
                            ,reforward_cnt,reward_cnt,attention_cnt
                     FROM %(targettab)s 
                     WHERE ds='%(ds)s' ''' % args
    ret = kingnetdc.hive_execsqlr(presto_sql)

    if ret:
        ret_len = len(ret[0])
        # print(ret)
        args['column_str'] = ','.join(['%s'] * ret_len)
        del_sql = ''' delete from %(mysqltab)s where fds = '%(ds)s' ''' % args
        kingnetdc.new_execsqlr(DB_PARAMS, del_sql)
        insert_sql = ''' insert into %(mysqltab)s values(%(column_str)s) ''' % args
        kingnetdc.executemany_batches(DB_PARAMS, insert_sql, ret)
    else:
        raise Exception('none result')


if __name__ == '__main__':
    main()
