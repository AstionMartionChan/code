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

hour_list = ['00', '01', '02', '03', '04', '05', '06', '07', '08', '09', '10', '11', '12', '13', '14', '15', '16', '17', '18', '19', '20', '21', '22', '23']


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


def merge_hive_files(days, day_length, partition, value, table):
    start_ds = get_previous_date(days + day_length)
    end_ds = get_previous_date(days)
    columns, partitions = get_columns_and_partitions(table)
    column_string = reduce(lambda x, y: x + ',' + y, map(lambda x: '\`' + x + '\`', columns))
    print (column_string)
    all_partitions = partitions
    all_partition_string = reduce(lambda x, y: x + ',' + y, all_partitions)
    partitions.remove(partition)
    partition_string = reduce(lambda x, y: x + ',' + y, partitions)
    print(partition_string)
    sql_format = '''insert overwrite table {table} partition ({all_partition_string}) select {column_string},{partition_string},'{value}' as {partition} from {table} where ds >= '{start_ds}' and ds <= '{end_ds}'; \n'''.format(table=table, partition=partition, value=value, column_string=column_string, partition_string=partition_string, start_ds=start_ds, end_ds=end_ds, all_partition_string=all_partition_string)
    print(sql_format)
    drop_partition_format = '''alter table {table} drop partition(ds='{ds}', {partition}='{hour}');\n'''
    output_handle = open("/data/project/hive/wutiao/sql_file.txt", "w")
    output_handle.write(sql_format)
    for day in range(day_length + 1):
        current_ds = get_previous_date(days + day)
        hour_ds = reduce(lambda x, y: x + y, current_ds.split('-'))
        for hour in hour_list:
            drop_partition_sql = drop_partition_format.format(table=table, ds=current_ds, partition=partition, hour=hour_ds + hour)
            output_handle.write(drop_partition_sql)
    output_handle.close()


def main():
    print("************************* begin script **************************")
    table, partition, value, days, length = get_sys_params()
    print(table)
    print(partition)
    print(value)
    merge_hive_files(days, length, partition, value, table)


def get_sys_params():
    length = len(sys.argv)
    if length != 6:
        raise Exception('parmas must be 2\n'
                        '  argv[1]: the hive table name with db [db.table]\n'
                        '  argv[2]: the partition to be merged\n'
                        '  argv[3]: the value of partition to output\n'
                        '  argv[4]: the delta days to merge\n'
                        '  argv[5]: the merge day length')
    table = sys.argv[1]
    partition = sys.argv[2]
    value = sys.argv[3]
    days = int(sys.argv[4])
    length = int(sys.argv[5])
    return table, partition, value, days, length


if __name__ == "__main__":
    main()