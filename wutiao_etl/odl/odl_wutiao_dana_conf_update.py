# coding: utf-8

# **********************程序说明*********************************#
# *模块： odl_wutiao_dana_conf_update.py -> odl
# *功能： 增量更新五条dana配置
# *作者： sunyu
# *时间： 2018-08-14
# *备注：
# ***************************************************************#

import kingnetdc
import sys
import os
sys.path.append('/'.join(os.path.abspath(__file__).split('/')[:-2]))
from project_constant import DB_PARAMS_SH_DANA, DB_PARAMS_SH_SDK_ADVERT


def get_day_data(args):
    sql = r'''
        select regexp_replace(appver,'[\s]','') as appver
        from (
            select distinct lower(nvl(_appver,'-1')) as appver
            from wutiao.odl_event_qkl
            where event in ('comment','favour','like','share','read','click','nointerest','report','attention','leaveread','play','search','login','active','enterfront','openclient')
            and ds = '%(run_date)s'
            and eventtype in ('super','high')
        ) t
    ''' % args
    ret = kingnetdc.presto_execsqlr(sql)
    ret = set([x[0].replace('\\t', '').replace('\\n', '') for x in ret])
    return ret


def get_his_data(args):
    sql = r''' set io.sort.mb=32;
        set mapreduce.map.memory.mb=4000;
        set mapreduce.reduce.memory.mb=4000;
        select 'swy' as tag, regexp_replace(appver, '[\t|\n]', '') as appver
        from (
            select distinct lower(nvl(\`_appver\`,'-1')) as appver
            from wutiao.odl_event_qkl
            where event in ('comment','favour','like','share','read','click','nointerest','report','attention','leaveread','play','search','login','active','enterfront','openclient')
            and ds <= '%(run_date)s'
            and eventtype in ('super','high')
        ) t
    ''' % args
    ret = kingnetdc.do_hql_exec(sql)
    ret = set([x.split('\t')[1] for x in ret.replace('\\n', '').replace('\\t', '').split('\n') if x[:3] == 'swy'])
    return ret


def get_mysql_conf(sql):
    ret = kingnetdc.original_execsqlr(DB_PARAMS_SH_DANA, sql)
    try:
        conf_d = eval(ret[0][0])
    except Exception as e:
        print(e)
        conf_d = {}
    return conf_d


def filter_data(ret, mysql_conf_d):
    new_d = {}
    keys = [x.strip().replace('\n', '').replace('\t', '') for x in mysql_conf_d.keys()]

    if isinstance(ret, dict):
        for key in ret.keys():
            if key in keys:
                continue
            else:
                new_d[key] = ret[key]
    else:
        for key in ret:
            if key in keys:
                continue
            else:
                new_d[key] = key
    return new_d


def update_appver(args, task):
    ret = eval('get_%s_data(args)' % task)
    print(type(ret), len(ret), ret)
    # 获取已有配置
    sql = ''' select info from dana.meta_config_info where config_id = 395 '''
    mysql_conf_d = get_mysql_conf(sql)
    # 如果已经存在则忽视更新
    diff_data_d = filter_data(ret, mysql_conf_d)
    diff_data_d.update(mysql_conf_d)
    all_k = sorted(diff_data_d.keys(), reverse=True)
    new_info = '{"allappver":"全部版本",'
    for k in all_k:
        if k in ('allappver', '-1'):
            continue
        new_info += '"%s":"%s",' % (k, diff_data_d[k])
    new_info = new_info + '"-1":"未知"}'
    if eval(new_info) != mysql_conf_d:
        sql = r'''
                    update dana.meta_config_info 
                    set info = '%s'
                    where config_id = 395
                ''' % new_info
        kingnetdc.original_execsqlr(DB_PARAMS_SH_DANA, sql)


def get_channel_data(sql):
    ret = kingnetdc.original_execsqlr(DB_PARAMS_SH_SDK_ADVERT, sql)
    try:
        channel = []
        for c in ret:
            channel.append('"'+str(c[0])+'":"'+c[1]+'"')
        conf_d = '{'+','.join(channel)+'}'
        conf_d = eval(conf_d)
    except Exception as e:
        print(e)
        conf_d = {}
    return conf_d


def update_channel():
    sql_src = ''' select id,name from sdk_advert.sdk_channel '''
    ret = get_channel_data(sql_src)
    print(type(ret), len(ret), ret)
    # 获取已有配置
    sql_conf = ''' select info from dana.meta_config_info where config_id = 394 '''
    mysql_conf_d = get_mysql_conf(sql_conf)
    # 如果已经存在则忽视更新
    diff_data_d = filter_data(ret, mysql_conf_d)
    diff_data_d.update(mysql_conf_d)
    all_k = sorted(diff_data_d.keys(), reverse=True)
    new_info = '{"allchannel":"全部渠道",'
    for k in all_k:
        if k in ('allchannel', '-1'):
            continue
        new_info += '"%s":"%s",' % (k, diff_data_d[k])
    new_info = new_info + '"-1":"未知"}'
    if eval(new_info) != mysql_conf_d:
        sql = r'''
                    update dana.meta_config_info 
                    set info = '%s'
                    where config_id = 394
                ''' % new_info
        kingnetdc.original_execsqlr(DB_PARAMS_SH_DANA, sql)


def main(task):
    kdc = kingnetdc.kdc
    args = {
        'run_date': kdc.workDate
    }
    if task == 'day':
        args['run_date'] = kdc.dateAdd(1)
    try:
        update_appver(args, task)
        update_channel()

        from json import loads
        from urllib import request
        # 请求url重新加载数据
        url_str = 'http://172.27.6.83:8316/kingnetiokpiservice/rest/nav/restart'
        ret_data = request.urlopen(url_str, timeout=60).read()
        ret_data = loads(ret_data)
        assert ret_data['code'] == 200, 'shanghai dana conf overload is faild!!! return code :%s' % ret_data['code']
    except Exception as e:
        print(e)
        kingnetdc.send_wechat('上海dana版本配置更新失败', 'sunyu@kingnet.com')
        raise e


if __name__ == '__main__':
    if len(sys.argv) == 2:
        task = sys.argv[1]
    else:
        task = 'day'
    main(task)
