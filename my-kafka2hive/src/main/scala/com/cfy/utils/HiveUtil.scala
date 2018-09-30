package com.cfy.utils

import com.cfy.constants.ConfigurationKey._
import org.apache.hadoop.hive.metastore.api.{FieldSchema, ThriftHiveMetastore}
import org.apache.thrift.protocol.TBinaryProtocol
import org.apache.thrift.transport.TSocket

object HiveUtil {

  def getMetaDataAndPartition(ip: String, port: Int, tableName: String) = {
    val dbName :: tbName :: Nil = tableName.split(COMMA).toList
    val transport = new TSocket(ip, port)
    val protocol = new TBinaryProtocol(transport)
    val client = new ThriftHiveMetastore.Client(protocol)
    transport.open()
    val table = client.get_table(dbName, tbName)
    val fields = List()[FieldSchema]
    val partitions = List()[FieldSchema]
    table.getSd.getCols.forEach(field => {
      fields.+:(field)
    })
    table.getPartitionKeys.forEach(partition => {
      partitions.+:(partition)
    })
    (fields, partitions)
  }

}
