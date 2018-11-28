#!/usr/bin/python3.6
# -*- coding: UTF-8 -*-

import socket

def retrun_mysql_db():
    hostname = socket.gethostname()
    if hostname == 'hwwg-bigdata-hadooprm-prod-1' or hostname == 'hwwg-bigdata-hadooprm-prod-2':
        DB_PARAMS_TEST = {'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}
    else:
        DB_PARAMS_TEST = {'host': '172.16.32.8', 'user': 'datac', 'password': 'E9QSVyj26gw/Q', 'port': 3306, 'charset': 'utf8'}
    return DB_PARAMS_TEST

DB_PARAMS_TEST = retrun_mysql_db()

    # 这是正式地址
#DB_PARAMS_TEST = {'host': '172.27.0.255', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}

# 这才是测试的
DB_PARAMS_TB = {'host': '172.27.2.251', 'user': 'data_test', 'password': 'data_test_pass', 'port': 4310, 'charset': 'utf8'}

# audit_system db connection
DB_PARAMS_AUDIT_SYSTEM = {'host': '172.27.1.39', 'user': 'data_system', 'password': '"rea1NVarW&GWY"', 'port': 3306, 'charset': 'utf8'}
DB_PARAMS_AUDIT_SYSTEM_HOSTS = ['172.27.1.39']

AUDIT_SYSTEM_TAB_SYNC_INFO = [{"db": "audit_system_0000", "table": "audit_status", "byfield": "", "fields": ""},
                              {"db": "audit_system_0000", "table": "audit_contents", "byfield": "last_edit_time", "fields": "content_id,title,tags,user_name,audit_content_type,video_content,audit_status,content_type,target_ids,sensitive_groups,sensitive_levels,sensitive_categories,publish_time,ip,device_id,media_name,media_id,media_property,match_sensitive_words,match_content_words,refuse_reason,audit_user,last_edit_time,picture,pre_audit_group,comment_status,click_step_status,tipoff_status,is_pre_audit,match_content_words_json,audit_criteria,content_count,group_status"}]

# wutiao_discoverer db connection
DB_PARAMS_WUTIAO_USER = {'host': '172.27.2.95', 'user': 'data_chain', 'password': 'data..kingnet', 'port': 5066, 'charset': 'utf8'}
DB_PARAMS_WUTIAO_USER_HOSTS = ['172.27.2.95', '172.27.2.85', '172.27.2.67', '172.27.2.55', '172.27.2.142', '172.27.1.82', '172.27.0.211', '172.27.0.179']

WUTIAO_USER_TAB_SYNC_INFO = [{"db": "wutiao_user", "table": "users", "byfield": "update_dt", "fields": "uid,realname,family_id,sex,phone,province,city,district,field,id_card_num,alipay_account,wallet_address,accsesstoken,source,status,role,create_dt,update_dt,face_auth_dt"},
                             {"db": "wutiao_user_relate", "table": "user_authentication", "byfield": "update_dt", "fields": "uid,type,status,create_dt,update_dt,public_welfare"},
                             {"db": "wutiao_user_relate", "table": "user_operators", "byfield": "update_dt", "fields": ""},
                             {"db": "wutiao_user_relate", "table": "user_organization", "byfield": "update_dt", "fields": ""},
                             {"db": "wutiao_user_relate", "table": "user_family_invite_log", "byfield": "update_dt", "fields": ""},
                             {"db": "wutiao_discoverer", "table": "discoverer", "byfield": "update_dt", "fields": ""},
                             {"db": "wutiao_mp", "table": "mp_article", "byfield": "update_time", "fields": "item_id,item_type,uid,title,summary,thumbnails,img_type,category,imgs,videos,tag,province,timing,status,reason,create_time,update_time,insert_time"},
                             {"db": "wutiao", "table": "field", "byfield": "", "fields": ""},
                             {"db": "wutiao", "table": "city", "byfield": "", "fields": ""},
                             {"db": "wutiao_admin", "table": "wutiao_rate", "byfield": "dt", "fields": ""}]

# 上链获取记录存储数据库信息
DB_PARAMS_TX = {'host': '172.27.3.170', 'user': 'block_data', 'password': 'Ux5bxBpTd/Hbg', 'port': 3307, 'charset': 'utf8'}

# 营销中心
DB_PARAMS_YX = {'host': '172.17.4.23', 'user': 'user_market_center', 'password': '8QK6usermarketcenter3@Va9wR6', 'port': 3306, 'charset': 'utf8'}

# 上海dana数据库
DB_PARAMS_SH_DANA = {'host': '172.27.3.143', 'user': 'datac', 'password': 'g3Z2zHF6uTxK6#rnlZ7', 'port': 3306, 'charset': 'utf8'}


# wutiao db connection
DB_PARAMS_WUTIAO = {'host': '172.17.2.91', 'user': 'root1', 'password': 'yunying.kingnet', 'port': 3306, 'charset': 'utf8'}
DB_PARAMS_WUTIAO_HOSTS = ['172.17.2.91']
WUTIAO_TAB_SYNC_INFO = [{"db": "wutiao", "table": "mp_article", "byfield": "update_time", "fields": "item_id,item_type,uid,title,summary,thumbnails,img_type,category,imgs,videos,tag,province,timing,status,reason,create_time,update_time"},
                        {"db": "wutiao", "table": "field", "byfield": "", "fields": ""},
                        {"db": "wutiao", "table": "audit_contents", "byfield": "last_edit_time", "fields": "content_id,title,tags,user_name,audit_content_type,video_content,audit_status,content_type,target_ids,sensitive_groups,sensitive_levels,sensitive_categories,publish_time,ip,device_id,media_name,media_id,media_property,match_sensitive_words,match_content_words,refuse_reason,audit_user,last_edit_time,picture,pre_audit_group,comment_status,click_step_status,tipoff_status,is_pre_audit,match_content_words_json,audit_criteria,content_count,group_status"}]


# 上海sdk_advert数据库
DB_PARAMS_SH_SDK_ADVERT = {'host': '172.27.0.92', 'user': 'chian_data', 'password': 'X9aRcx8_me84vk', 'port': 3306, 'charset': 'utf8'}
DB_PARAMS_SDK_ADVERT_HOSTS = ['172.27.0.92']
SDK_ADVERT_TAB_SYNC_INFO = [{"db": "sdk_advert", "table": "sdk_channel", "byfield": "", "fields": ""},
                            {"db": "sdk_advert", "table": "sdk_channel_media_type", "byfield": "", "fields": ""}]
