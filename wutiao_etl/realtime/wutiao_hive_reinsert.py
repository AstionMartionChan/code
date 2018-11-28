# !/usr/bin/env python
# -*-coding: UTF-8 -*-

#**********************程序说明*********************************#
#*模块: etl
#*功能: 五条odl层文件合并
#*作者: zhoujiongyu
#*时间: 2018-09-05
#*备注: 五条odl层文件合并-输出sql到文件
#***************************************************************#

import sys
from hive_metastore import ttypes
from hive_metastore import ThriftHiveMetastore
from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
# import kingnetdc
import os
import time
from datetime import datetime
from datetime import timedelta


def get_current_date():
    return datetime.now().strftime("%Y-%m-%d")


def get_previous_date(days):
    now = datetime.now()
    delta = timedelta(days=days)
    return (now - delta).strftime("%Y-%m-%d")


def get_columns_and_partitions(table):
    db = table.split(".")[0]
    table_name = table.split(".")[1]
    transport = TSocket.TSocket('127.0.0.1', 9083)
    # transport = TSocket.TSocket('hwwg-bigdata-hadoopnn-prod-1', 9083)
    transport = TTransport.TBufferedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = ThriftHiveMetastore.Client(protocol)
    transport.open()
    hive_table = client.get_table(db, table_name)
    cols = hive_table.sd.cols
    colums = []
    for col in cols:
        colums.append(col.name)
    partitions = hive_table.partitionKeys
    partition_list = []
    for partition in partitions:
        partition_list.append(partition.name)
    transport.close()
    return colums, partition_list


def merge_hive_files(days, table):
    ds = get_previous_date(days)
    columns, partitions = get_columns_and_partitions(table)
    column_string = reduce(lambda x, y: x + ',' + y, map(lambda x: '\`' + x + '\`', columns))
    print (column_string)
    all_partitions = partitions
    all_partition_string = reduce(lambda x, y: x + ',' + y, all_partitions)
    sql_format = '''insert overwrite table {table} partition ({all_partition_string}) select {column_string},{all_partition_string} from {table} where ds = '{ds}'; \n'''.format(table=table, column_string=column_string, ds=ds, all_partition_string=all_partition_string)
    print(sql_format)
    output_handle = open("/data/project/hive/wutiao/reinsert_file.txt", "w")
    output_handle.write(sql_format)
    output_handle.close()


def main():
    print("************************* begin script **************************")
    table, days = get_sys_params()
    merge_hive_files(days, table)


def get_sys_params():
    length = len(sys.argv)
    if length != 3:
        raise Exception('parmas must be 2\n'
                        '  argv[1]: the hive table name with db [db.table]\n'
                        '  argv[2]: the delta days to merge\n')
    table = sys.argv[1]
    days = int(sys.argv[2])
    return table, days


if __name__ == "__main__":
    main()

