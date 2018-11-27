package com.kingnetdc.blueberry.hbase;

import java.io.IOException;

import com.kingnetdc.blueberry.core.util.Common;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.security.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HbaseAccess {
	private static Logger logger = LoggerFactory.getLogger(HbaseAccess.class);
	public static Configuration conf;
	public static Connection connection;
	private String nameSpace;
	public boolean writeWal = true;

	public HbaseAccess(String quorum, String user, String nameSpace, String writeToWalFlag) {
		conf = HBaseConfiguration.create();
		conf.set("hbase.zookeeper.quorum", quorum);

		logger.info("hbase init config:  \n" +
			"hbase.zookeeper.quorum=" + quorum + "\n" +
			"namespace.name=" + nameSpace + "\n" +
			"hbase.user.name=" + user
		);
		try {
			if (Common.isBlank(user)) {
				connection = ConnectionFactory.createConnection(conf);
			} else {
				User uCreated = User.createUserForTesting(conf, user, new String[0]);
				connection = ConnectionFactory.createConnection(conf, uCreated);
			}
			if (Common.isBlank(nameSpace)) {
				this.nameSpace = null;
			} else {
				this.nameSpace = nameSpace;
			}
			if(Common.isBlank(writeToWalFlag)) {
				this.writeWal = true;
			} else {
				this.writeWal = Boolean.valueOf(writeToWalFlag);
			}
		} catch (IOException e) {
			logger.error("fatal error because hbase connection failed to be initialized , exit system");
			System.exit(1);
		}
	}

	/**
	 * 取得表名，如果有nameSpace则加上nameSpace
	 *
	 * @param tableName 表名
	 * @return
	 */
	public String getTableName(String tableName) {
		if (StringUtils.isBlank(nameSpace)) {
			return tableName;
		} else {
			return nameSpace + ":" + tableName;
		}
	}

	/**
	 * 获取table对象
	 *
	 * @param tableName 表名
	 * @return table对象
	 * @throws IOException
	 */
	public Table getTable(String tableName) throws IOException {
		return connection.getTable(TableName.valueOf(getTableName(tableName)));
	}

	/**
	 * 关闭表连接
	 *
	 * @param table 表
	 */
	public void closeTable(Table table) {
		try {
			if (table != null) {
				table.close();
			}
		} catch (Exception e) {
			logger.error("close table error.", e);
		}
	}
}
