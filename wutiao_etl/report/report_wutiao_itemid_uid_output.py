# coding: utf-8

#**********************程序说明*********************************#
#*模块：REPORT
#*功能：区块链-内容收入分析
#*作者：gant
#*时间：2018-06-25
#*备注：区块链-内容收入分析
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

INSERT overwrite TABLE {targettab} partition (ds='{ds}',type='itemid')
select ds as fds                           
,itemid as id                           
,itemtype                     
,day_showinpage_cnt           
,day_read_cnt                 
,day_itemid_like_cnt          
,day_itemid_discover_vote_cnt 
,day_addcomment_cnt           
,day_clickshare_cnt           
,day_addfavour_cnt            
,day_income                   
,his_showinpage_cnt           
,his_read_cnt                 
,his_itemid_like_cnt          
,his_itemid_discover_vote_cnt 
,his_addcomment_cnt           
,his_clickshare_cnt           
,his_addfavour_cnt            
,his_income
,day_play_cnt
,his_play_cnt   
from {sourcetab}
where ds='{ds}'; 

INSERT overwrite TABLE {targettab} partition (ds='{ds}',type='uid')
select ds as fds                           
,uid as id                           
,itemtype                     
,sum(day_showinpage_cnt) as day_showinpage_cnt         
,sum(day_read_cnt) as day_read_cnt                 
,sum(day_itemid_like_cnt) as day_itemid_like_cnt         
,sum(day_itemid_discover_vote_cnt) as day_itemid_discover_vote_cnt 
,sum(day_addcomment_cnt) as day_addcomment_cnt          
,sum(day_clickshare_cnt) as day_clickshare_cnt           
,sum(day_addfavour_cnt) as day_addfavour_cnt            
,sum(day_income) as day_income                    
,sum(his_showinpage_cnt) as his_showinpage_cnt            
,sum(his_read_cnt) as his_read_cnt                  
,sum(his_itemid_like_cnt) as his_itemid_like_cnt           
,sum(his_itemid_discover_vote_cnt) as his_itemid_discover_vote_cnt  
,sum(his_addcomment_cnt) as his_addcomment_cnt            
,sum(his_clickshare_cnt) as his_clickshare_cnt            
,sum(his_addfavour_cnt) as his_addfavour_cnt             
,sum(his_income) as his_income 
,sum(day_play_cnt) as day_play_cnt
,sum(his_play_cnt) as his_play_cnt    
from {sourcetab}
where ds='{ds}'
group by ds,uid,itemtype;                  
'''

delete1=r'''echo "use wutiao; delete from report_wutiao_itemid_output where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync1=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_itemid_uid_output/ds='{ds}'/type=itemid --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_itemid_output" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

delete2=r'''echo "use wutiao; delete from report_wutiao_uid_output where fds ='{ds}'; " |mysql -h {host} -u {user} -p{password} '''
sync2=r'''/usr/bin/sqoop-export  --export-dir /user/hive/warehouse/wutiao.db/report_wutiao_itemid_uid_output/ds='{ds}'/type=uid --connect jdbc:mysql://{host}:{port}/wutiao --username "{user}" --password '{password}' --table "report_wutiao_uid_output" --input-fields-terminated-by "|" --input-null-string "\\\N" --input-null-non-string "\\\N" '''

def main():
    kdc = kingnetdc.kdc
    args = {
        'ds': kdc.workDate,
        'sourcetab': 'wutiao.idl_wutiao_itemid_wb',
        'targettab': 'wutiao.report_wutiao_itemid_uid_output',
        'host': DB_PARAMS['host'],
        'user': DB_PARAMS['user'],
        'password': DB_PARAMS['password'],
        'port': DB_PARAMS['port']
    }
    sql = SQL_DAY.format(ds=args['ds'], targettab=args['targettab'], sourcetab=args['sourcetab'])

    kdc.debug = True
    res = kdc.doHive(sql,True,True)

    if res !=0 :
        raise Exception('report_wutiao_itemid_wb hive table insert execute failure!!')

    res1 = os.system(delete1.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res2 = os.system(sync1.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))
    res3 = os.system(delete2.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password']))
    res4 = os.system(sync2.format(ds=args['ds'],host=args['host'],user=args['user'],password=args['password'],port=args['port']))

    if (res1 or res2 or res3 or res4) !=0 :
        print(res1,res2,res3,res4)
        raise Exception('report_wutiao_itemid_uid_output mysql table delete&insert execute failure!!')
    else :
        print(res1,res2,res3,res4)
        print('complete')



if __name__ == '__main__':
    main()
