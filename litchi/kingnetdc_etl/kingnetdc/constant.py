# -*-coding:utf-8 -*-

# ------ 集群相关配置
# 最大的系统负载，当系统负载大于这个数值时，不启动新的任务，默认是20，如果系统资源比较多，可以适量增大
SYSTEM_MAX_LOAD_AVERAGE = 20

# 最小的空闲内存的大小，单位是GB，留给系统的内存
SYSTEM_MIN_FREE_MEMORY = 5

# 最大的hive同时执行的个数，建议是根据内存设置
SYSTEM_MAX_HIVE_JOB_COUNT = 10

# ------ 目录相关配置
LOG_PATH = '/tmp'

# ------ 日期格式相关配置
DATE_FORMAT_STR = '%Y-%m-%d'
DATE_TIME_FORMAT_STR = "%Y-%m-%d %H:%M:%S"
DATE_FORMAT_YMD = '%Y%m%d'

# 系统发送邮件使用的邮箱名称
KINGNET_MAIL = 'kingnetdc36@mail.kingnetdc.com'

# ------ wget参数设置
WGET_CONN_TRY = 3  # 重试次数
WGET_TIMEOUT = 120  # 超时秒数
WGET_WAITRETRY = 1  # 仅当下载失败时等待指定时间,单位秒
WGET_LIMIT_RATE = 0  # unit:KB 0表示不限速
