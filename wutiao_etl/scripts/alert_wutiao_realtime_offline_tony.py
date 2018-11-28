# -*- coding:utf-8 -*-

#**********************程序说明*********************************#
#*模块: ADL
#*功能: 五条实时和文件生成报警
#*作者:gant
#*时间:2018-07-23
#*备注:五条实时排行榜hdfs检查和文件生成检查报警
#***************************************************************#

import socket
import time
import os
import kingnetdc
import pymysql

#def send_error(data_name):
#    local_host = socket.gethostbyname(socket.gethostname())
#    kafka_config = KafkaConfig()
#    adapter = SwordfishKafkaProducerAdapter.getInstance(kafka_config)
#    data = SwordFishData()
#    print data_name
#    properties = {'category': 'danakpi_check', 'type': data_name,'flag':'yes'}
#    data.host = local_host
#    data.event = '81'
#
#    data.timestamp = long(time.time())
#    data.properties = properties
#    adapter.send(data)
#    adapter.close()

def getMysqlConn(host='172.17.2.91', user='root', passwd='123456', db='danakpi', port=3306, charset='utf8'):
    conn = None
    try:
        conn = MySQLdb.connect(host=host, user=user, passwd=passwd, db=db, port=port, charset=charset)
    except MySQLdb.Error(e):
        pass
    return conn

def __mysqlQuery (sql, getAll=True):
    persistConn = False
    ret = [[[]]]
    conn = getMysqlConn()
    cursor = conn.cursor()
    try:
        cursor.execute(sql)
        if getAll:
            ret = cursor.fetchall()
            if not ret:
                ret = [[[]]]
        else:
            ret = [[[]]]
    except MySQLdb.Error(e):
        conn.rollback()
        ret = False
        pass
    cursor.close()
    if not persistConn:
        conn.close()
    return ret

def filecheck (filepath):
    check_path = '''test -e '{filepath}' '''
    checkpath_cmd = check_path.format(filepath=filepath)
    res = kdc.doCommand(checkpath_cmd,True,False)
    return res

def hdfsfilecheck (hdfsfilepath):
    check_path = '''hadoop fs -test -e '{filepath}' '''
    checkpath_cmd = check_path.format(filepath=hdfsfilepath)
    res = kdc.doCommand(checkpath_cmd,True,False)
    return res

query_mysql = '''
select tablename,cnt from(
select '' as tablename,count(1) as cnt from active_user_xyplathz_1301158 where fds='%(fds)s'
)t
where cnt=0
'''

def file_check():
    ds = kdc.workDate
    file1 = '/data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_rank_{ds}.txt'.format(ds=ds)
    file2 = '/data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_rank_{ds}_info.txt'.format(ds=ds)
    file3 = '/data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_10_{ds}.txt'.format(ds=ds)
    file4 = '/data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_last_10_{ds}_info.txt'.format(ds=ds)
    file5 = '/data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_{ds}.txt'.format(ds=ds)
    file6 = '/data/wwwroot/wutiao-hadooplog.kyhub.cn/getmoney_{ds}_info.txt'.format(ds=ds)
    res1 = filecheck(file1)
    res2 = filecheck(file2)
    res3 = filecheck(file3)
    res4 = filecheck(file4)
    res5 = filecheck(file5)
    res6 = filecheck(file6)
    if (res1 or res2 or res3 or res4 or res5 or res6) != 0:
        kingnetdc.send_phone('五条排行榜文件生成问题',['15921854019','17621068377'])
    else:
        pass

def offline_file_check():
    ds = kdc.workDate
    hdfsfile1 = '/user/hive/warehouse/wutiao.db/idl_wutiao_user/ds={ds}'.format(ds=ds)
    hdfsfile2 = '/user/hive/warehouse/wutiao.db/bdl_wutiao_event/eventtype=super/ds={ds}'.format(ds=ds)
    hdfsfile3 = '/user/hive/warehouse/wutiao.db/idl_wutiao_itemid_wb/ds={ds}'.format(ds=ds)
    res1 = hdfsfilecheck(hdfsfile1)
    res2 = hdfsfilecheck(hdfsfile2)
    res3 = hdfsfilecheck(hdfsfile3)
    if (res1 or res2 or res3) != 0:
        kingnetdc.send_phone('五条离线中间表问题',['15921854019','17621068377'])
    else:
        pass

def realtime_rank():
    last_hour = time.strftime('%Y%m%d%H', time.localtime(time.time()-3600))
    last_hour_ds = time.strftime('%Y-%m-%d', time.localtime(time.time()-3600))
    hdfsfile1 = '/user/hive/warehouse/wutiao.db/adl_wutiao_discover_moneygain_rank_realtime/ds={ds}/hour={hour}'.format(ds=last_hour_ds,hour=last_hour)
    hdfsfile2 = '/user/hive/warehouse/wutiao.db/adl_wutiao_money_realtime/ds={ds}/hour={hour}'.format(ds=last_hour_ds,hour=last_hour)
    hdfsfile3 = '/user/hive/warehouse/wutiao.db/adl_wutiao_family_contribute_realtime/ds={ds}/hour={hour}'.format(ds=last_hour_ds,hour=last_hour)
    res1 = hdfsfilecheck(hdfsfile1)
    res2 = hdfsfilecheck(hdfsfile2)
    res3 = hdfsfilecheck(hdfsfile3)
    if (res1 or res2 or res3) != 0:
        kingnetdc.send_phone('五条实时排行榜数据生成问题',['15921854019','17621068377'])
    else:
        pass

if __name__ == '__main__':
    kdc = kingnetdc.kdc
    current_minute = time.strftime('%M', time.localtime(time.time()))
    current_hour = time.strftime('%H', time.localtime(time.time()))
    if int(current_minute) >= 30 and current_hour != '04':
        realtime_rank()
    elif int(current_minute) >= 30 and current_hour == '04':
        realtime_rank()
        file_check()
        offline_file_check()
    else:
        print('Not the appropriate time!')
