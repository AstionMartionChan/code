#!/usr/bin/env python
# -*- coding: UTF-8 -*-

import configparser
import os


class CustomConfigRead:

    __user_config_file = '/etc/kingnetdc.conf'
    pwd_path_str = '/'.join(os.path.abspath(__file__).split('/')[:-1])
    __default_config_file = "%s/config/kingnetdc.conf" % pwd_path_str
    __conf = configparser.ConfigParser()

    def __init__(self):
        has_usr_conf = os.path.exists(self.__user_config_file)
        has_def_conf = os.path.exists(self.__default_config_file)

        if not has_def_conf and not has_usr_conf:
            raise configparser.Error('缺失配置文件: %s 或 %s' % (
                    self.__user_config_file, self.__default_config_file))

        if has_usr_conf:
            self.__conf.read(self.__user_config_file)

        if (not has_usr_conf) and has_def_conf:
            self.__conf.read(self.__default_config_file)

    def get_conf(self):
        return self.__conf

custom_config = CustomConfigRead().get_conf()
