# !/usr/bin/env python
# -*-coding: UTF-8 -*-

#**********************程序说明*********************************#
#*模块: etl
#*功能: 五条odl层文件合并
#*作者: zhoujiongyu
#*时间: 2018-09-05
#*备注: 五条odl层文件合并-执行文件中的sql
#***************************************************************#

import kingnetdc
import os
import sys


def main():
    print("************************* begin script **************************")
    path = sys.argv[1]
    kdc = kingnetdc.kdc
    read_handle = open(path)
    sql = """
    set hive.merge.mapfiles=false;
    set hive.merge.smallfiles.avgsize=128000000;
    set hive.merge.size.per.task=256000000;
    set hive.merge.mapredfiles=false;
    set hive.merge.tezfiles=true;
    set mapreduce.map.memory.mb=4000;
    set hive.exec.dynamic.partition.mode=nonstrict;
    """
    for line in read_handle.readlines():
        sql += line
    print(sql)
    kdc.debug = True
    res = kdc.doHive(sql, True, True)

    if res != 0:
        raise Exception('excute sql error!')
    else:
        print("success!")
    read_handle.close()


if __name__ == "__main__":
    main()
