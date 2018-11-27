#!/usr/bin/env python
# -*- coding: UTF-8 -*-

# ***************************************************************#
# 描述: hive调用公共加载模块
# 模块: kingdc
# 功能: 提供hive通过python调用相关的功能
# 作者: Jake.zhang(原) sunyu(改)
# 日期: 2014-06-20(原) 2018-05-22(改)
# 备注: kingnetdc datacenter hive 公共类库
#      改动: 适配python3.6及方法调整
# 路径: /usr/lib/python3.6/site-packages/
# ***************************************************************#
import os
import sys
import random
import subprocess
from optparse import OptionParser
from .time_utils import (date_sub_by_date, date_add_by_date, get_now_format, get_time_format, sleep,
                         get_current_time, to_timestamp)
from .utils import exec_cmd
from .constant import (
    DATE_FORMAT_STR, DATE_TIME_FORMAT_STR, SYSTEM_MAX_LOAD_AVERAGE, SYSTEM_MIN_FREE_MEMORY,
    SYSTEM_MAX_HIVE_JOB_COUNT, LOG_PATH)
from .config_utils import custom_config


class Kdc:
    debug = False
    logPath = LOG_PATH
    # 任务类型
    workType = ''
    # 任务时间
    workDate = ''
    # 默认日志的句柄
    logHandle = None

    # 脚本初始化
    def __init__(self):
        self.__init_env()
        self.__init_log_handle()
        if self.workType:
            self.__check_system_load()
        self.__init_task_log_handle()
        self.log(self.__taskScheduleInfoString + ' task start!', self.__taskLogHandle)

    def __init_env(self):
        options = self.__get_options()
        self.workType = options.workType
        work_date_tmp = options.workDate
        if not work_date_tmp or work_date_tmp == 'yesterday':
            self.workDate = get_time_format()
        elif work_date_tmp == 'today':
            self.workDate = get_now_format()
        else:
            self.workDate = get_time_format(to_timestamp(str(work_date_tmp), DATE_FORMAT_STR))
        if options.debug and options.debug == 'true':
            self.debug = True

    def log(self, msg, handle=None):
        if not handle:
            handle = self.logHandle
        handle.write(get_now_format(DATE_TIME_FORMAT_STR) + ' ' + str(msg) + "\n")

    def __init_log_handle(self):
        log_file = self.__get_log_handle_file()
        try:
            self.logHandle = open(log_file, 'a+')
        except PermissionError:
            log_file = '/tmp/__kingnetdcKDCDefaultLogFile_' + self.workType + '_' + self.workDate + '.log'
            self.logHandle = open(log_file, 'a+')

    def __get_log_handle_file(self):
        log_path = self.__get_task_schedule_path(self.logPath, self.workDate)
        if self.workType:
            script_name = 'output_' + self.__get_script_name() + '.log'
        else:
            script_name = 'output_' + self.__get_script_name() + '.' + self.workDate + '.log'
        if not os.path.exists(log_path):
            os.makedirs(log_path)
        return log_path + script_name

    def getLogPath(self):
        return self.__get_task_schedule_path(self.logPath, self.workDate)

    def getLogFile(self):
        return self.__get_log_handle_file()

    # init task run schedule loger
    def __init_task_log_handle(self):
        self.__taskScheduleInfoString = self.__get_script_name() + ' / workType:' + str(self.workType)\
                                        + ' / workDate:' + str(self.workDate) + ' / '
        log_file = self.logPath + '/common/output_taskRunScheduleLog.' + self.workDate + '.log'
        try:
            self.__taskLogHandle = open(log_file, 'a+')
        except PermissionError:
            self.log('create task run schedule log error. use default /tmp path.')
            log_file = '/tmp/__taskRunScheduleLog_' + self.workDate + '.log'
            self.__taskLogHandle = open(log_file, 'a+')

    # 获取 schedule 目录
    def __get_task_schedule_path(self, priority_path, work_date):
        if self.workType and not work_date:
            path = priority_path + '/' + self.workType + '/'
        elif self.workType and work_date:
            path = priority_path + '/' + self.workType + '/' + work_date + '/'
        else:
            path = priority_path + '/common/'
        return path

    # 执行参数解析
    @staticmethod
    def __get_options():
        parser = OptionParser()
        parser.add_option('-d', action='store', dest='workDate', default='yesterday', help='date[yesterday|2014-06-06]')
        parser.add_option('-t', action='store', dest='workType', default='', help='type[webgame|xyplat]')
        parser.add_option('--debug', action='store', dest='debug', default='false', help='false[true]')
        (options, args) = parser.parse_args()
        return options

    def __check_system_load(self):
        """检查系统负载,如果负载很高,则默认随机等待30~60秒
        """
        flag = False
        while not flag:
            free_memory_cmd = "/usr/bin/free -g | /bin/sed -n '3p' | /bin/awk '{print $4}'"
            now_free_memory = int(exec_cmd(free_memory_cmd, False))
            load_avgrage_cmd = "/usr/bin/uptime | /bin/awk -F 'load average: ' '{print $2}' | /bin/awk '{print $1}' | /bin/sed 's/,//g'"
            now_load_average = int(round(float(exec_cmd(load_avgrage_cmd, False))))
            hive_count_cmd = "/usr/java/default/bin/jps | grep 'RunJar\|Sqoop' | wc -l"
            hive_job_count = int(exec_cmd(hive_count_cmd, False))
            self.log('now free memory:' + str(now_free_memory) + ', now load average:' + str(
                now_load_average) + ', now hiveJobCount Count:' + str(hive_job_count))
            if now_free_memory > SYSTEM_MIN_FREE_MEMORY \
                    and now_load_average < SYSTEM_MAX_LOAD_AVERAGE and hive_job_count < SYSTEM_MAX_HIVE_JOB_COUNT:
                flag = True
            else:
                sleep(random.randint(30, 60))

    # 获取当前 python 的脚本名称
    @staticmethod
    def __get_script_name():
        script_name = os.path.split(os.path.realpath(sys.argv[0]))[1]
        return script_name

    # 操作 ETL
    def __do_etl(self, command, is_return):
        """ 进行etl操作
        :param command: 需要执行的命令
        :param is_return: 是否返回执行结果; flase时执行完成后发送sys.exit信号
        :return: int() or sys.exit 信号
        """
        msg = "workType::" + self.workType + ", workDate::" + self.workDate
        self.log('doCommand: ' + msg)
        self.log('doCommand: ' + command)
        start_time = get_current_time()
        if self.debug:
            print(msg)
            print(command)
            status = subprocess.Popen(command, stdin=None, stdout=None, stderr=None, shell=True).wait()
        else:
            handle = subprocess.Popen(command, stdout=self.logHandle, stderr=subprocess.STDOUT, shell=True)
            self.logHandle.close()
            status = handle.wait()
            # 重新打开文件句柄
            self.__init_log_handle()

        consume_time = get_current_time() - start_time
        self.log("command consume time: %s" % consume_time)

        if status == 0:
            self.log("command execute success!")
        else:
            self.log("command execute failure!")

        if is_return:
            return status

        if status == 0:
            exit(0)
        else:
            exit(1)

    def doHive(self, sql, isReutrn=True, is_try=True):
        command = custom_config.get('hive', 'command_hive') + ' -e "' + sql + '"'
        return self.doCommand(command, isReutrn, is_try)

    def doSparkSql(self, sql, isReutrn=True, is_try=True):
        command = custom_config.get('spark', 'command_spark_sql') + ' -e "' + sql + '"'
        return self.doCommand(command, isReutrn, is_try)

    def doCommand(self, command, isReutrn=True, is_try=True):
        ret_status = self.__do_etl(command, isReutrn)
        if is_try and ret_status != 0:
            self.log('execute: %s is error!!!' % command)
            raise Exception('execute: %s is error!!!' % command)
        return ret_status

    def dateSub(self, day_num, date=None):
        if not date:
            date = self.workDate
        return date_sub_by_date(day_num, date)

    def dateAdd(self, day_num, date=None):
        if not date:
            date = self.workDate
        return date_add_by_date(day_num, date)

    def is_hdfs_location_exists(self, hdfs_location):
        """
        check hdfs location is exists
        :param hdfs_location: the hdfs path string
        :return: bool(True) or bool(False)
        """
        command = '''%s fs -ls -d %s ''' % (custom_config.get('hive', 'command_hdfs'), hdfs_location)
        ret_status = self.__do_etl(command, True)
        return True if ret_status == 0 else False

    def put_tag_on_hdfs(self, tag):
        command = '''%s fs -touchz %s ''' % (custom_config.get('hive', 'command_hdfs'), tag)
        return self.doCommand(command)
