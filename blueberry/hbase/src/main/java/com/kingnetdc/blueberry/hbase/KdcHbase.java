package com.kingnetdc.blueberry.hbase;

import com.kingnetdc.blueberry.core.util.Common;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Durability;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;


/**
 *
 *
 */
public class KdcHbase {

	private static Logger logger = LoggerFactory.getLogger(KdcHbase.class);

	public HbaseAccess hbase;

	public KdcHbase(String quorum, String user, String nameSpace, String writeToWalFlag) {
		hbase = new HbaseAccess(quorum, user, nameSpace, writeToWalFlag);
	}

	/**
	 * 保存数据到表
	 *
	 * @param tableName 表名
	 * @param values    kv集合
	 * @return 保存状态，true:正常保存，false:保存异常
	 */
	public boolean save(String tableName, List<Qualifier> values) {
		return save(tableName, values, null);
	}

	/**
	 * 保存数据到表
	 *
	 * @param tableName 表名
	 * @param values    kv集合
	 * @param walFlag   写wal标识，默认是打开，如果传空则根据配置中的设置文件来设置
	 * @return 保存状态，true:正常保存，false:保存异常
	 */
	public boolean save(String tableName, List<Qualifier> values, Boolean walFlag) {
		if (StringUtils.isNotBlank(tableName)) {
			if (values != null && values.size() > 0) {
				Table table = null;
				try {
					table = hbase.getTable(tableName);
					List<Put> puts = new ArrayList<>();
					for (Qualifier kv : values) {
						Put put = new Put(Bytes.toBytes(kv.getRowKey()));
						if (kv.getTs() != null && kv.getTs() > 0) {
							put.addColumn(Bytes.toBytes(kv.getFamily()), Bytes.toBytes(kv.getQualifier()), kv.getTs(), kv.getValue());
						} else {
							put.addColumn(Bytes.toBytes(kv.getFamily()), Bytes.toBytes(kv.getQualifier()), kv.getValue());
						}
						if (kv.getTtl() != null && kv.getTtl() > 0) {
							put.setTTL(kv.getTtl());
						}

						if (walFlag != null) {
							if (!walFlag) {
								put.setWriteToWAL(walFlag);
								//put.setDurability(Durability.SKIP_WAL);
							}
						} else if (!hbase.writeWal) {
							//关闭写wal才需要设置，默认是打开的
							put.setWriteToWAL(hbase.writeWal);
							//put.setDurability(Durability.SKIP_WAL);
						}
						puts.add(put);
					}
					table.put(puts);
				} catch (Throwable e) {
					logger.error("save values error.", e);
					return false;
				} finally {
					hbase.closeTable(table);
				}
			} else {
				logger.error("values is empty.");
			}
		} else {
			logger.error("table name is blank.");
		}
		return true;
	}

	/**
	 * 通过rowkey取得所有簇列数据
	 *
	 * @param tableName 表名
	 * @param rowkeys   rowkey
	 * @return 结果集，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	public Map<String, Map<String, byte[]>> get(String tableName, List<String> rowkeys) {
		Map<String, Map<String, byte[]>> result = new HashMap<>();
		Table table = null;
		try {
			table = hbase.getTable(tableName);
			List<Get> gets = new ArrayList<>();
			for (String key : rowkeys) {
				Get get = new Get(Bytes.toBytes(key));
				gets.add(get);
			}
			Result[] results = table.get(gets);
			result = resultToMap(results);
		} catch (Exception e) {
			logger.error("get data error.", e);
		} finally {
			hbase.closeTable(table);
		}
		return result;
	}

	/**
	 * 通过rowkey取得指定簇下所有列数据
	 *
	 * @param tableName 表名
	 * @param rowkeys   rowkey
	 * @param familys   簇
	 * @return 结果集，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	public Map<String, Map<String, byte[]>> get(String tableName, List<String> rowkeys, List<String> familys) {
		Map<String, Map<String, byte[]>> result = new HashMap<>();
		Table table = null;
		try {
			table = hbase.getTable(tableName);
			List<Get> gets = new ArrayList<>();
			for (String key : rowkeys) {
				Get get = new Get(Bytes.toBytes(key));
				if (familys != null && !familys.isEmpty()) {
					for (String family : familys) {
						get.addFamily(Bytes.toBytes(family));
					}
				}
				gets.add(get);
			}
			Result[] results = table.get(gets);
			result = resultToMap(results);
		} catch (Exception e) {
			logger.error("get data error.", e);
		} finally {
			hbase.closeTable(table);
		}
		return result;
	}

	/**
	 * 通过rowkey取得指定簇列数据
	 *
	 * @param tableName 表名
	 * @param rowkeys   rowkey
	 * @param qualifier map key为簇，list中数据为列
	 * @return 结果集，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	public Map<String, Map<String, byte[]>> get(String tableName, List<String> rowkeys, Map<String, List<String>> qualifier) {
		Map<String, Map<String, byte[]>> result = new HashMap<>();
		Table table = null;
		try {
			table = hbase.getTable(tableName);
			List<Get> gets = new ArrayList<>();
			for (String key : rowkeys) {
				Get get = new Get(Bytes.toBytes(key));
				if (qualifier != null && !qualifier.isEmpty()) {
					for (Map.Entry e : qualifier.entrySet()) {
						byte[] family = Bytes.toBytes(e.getKey().toString());
						for (String column : (List<String>) e.getValue()) {
							get.addColumn(family, Bytes.toBytes(column));
						}
					}
				}
				gets.add(get);
			}
			Result[] results = table.get(gets);
			result = resultToMap(results);
		} catch (Exception e) {
			logger.error("get data error.", e);
		} finally {
			hbase.closeTable(table);
		}
		return result;
	}

	/**
	 * 从startKey开始扫描表，获取num条数据
	 *
	 * @param tableName 表名
	 * @param startKey  开始的rowkey，结果包含改rowkey
	 * @param num       数据条数
	 * @return 结果集，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	public LinkedHashMap<String, Map<String, byte[]>> scan(String tableName, String startKey, Integer num) {
		return scan(tableName, startKey, null, num);
	}

	/**
	 * 获取从startKey到stopKey的数据
	 *
	 * @param tableName 表名
	 * @param startKey  开始的rowkey，结果包含改rowkey
	 * @param stopKey   结束的rowkey，结果不包含该rowkey
	 * @return 结果集，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	public LinkedHashMap<String, Map<String, byte[]>> scan(String tableName, String startKey, String stopKey) {
		return scan(tableName, startKey, stopKey, null);
	}

	/**
	 * 获取从startKey到stopKey的数据，结果按照rowkey顺序存放，如果设置了num则最多取到num条数据
	 *
	 * @param tableName 表名
	 * @param startKey  开始的rowkey，结果包含改rowkey
	 * @param stopKey   结束的rowkey，结果不包含该rowkey
	 * @param num       数据条数
	 * @return 结果集，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	public LinkedHashMap<String, Map<String, byte[]>> scan(String tableName, String startKey, String stopKey, Integer num) {
		LinkedHashMap<String, Map<String, byte[]>> result = new LinkedHashMap<>();
		if (num == null) {
			num = 0;
		}
		Table table = null;
		try {
			table = hbase.getTable(tableName);
			Scan scan = new Scan();
			scan.setStartRow(Bytes.toBytes(startKey));
			if (!Common.isBlank(stopKey)) {
				scan.setStopRow(Bytes.toBytes(stopKey));
			}
			if (num > 0) {
				scan.setMaxResultSize(num);
			}
			ResultScanner resultScanner = table.getScanner(scan);
			int n = 0;
			for (Result res : resultScanner) {
				if (res != null && res.getRow() != null) {
					n++;
					Map<String, byte[]> map = new HashMap<>();
					String rowKey = Bytes.toString(res.getRow());
					Cell[] cells = res.rawCells();
					for (Cell cell : cells) {
						map.put(Bytes.toString(CellUtil.cloneFamily(cell)) + ":" + Bytes.toString(CellUtil.cloneQualifier(cell)), CellUtil.cloneValue(cell));
					}
					result.put(rowKey, map);
					if (num != 0 && n >= num) {
						break;
					}
				}
			}
		} catch (Exception e) {
			logger.error("scan data error.", e);
		} finally {
			hbase.closeTable(table);
		}
		return result;
	}

	/**
	 * 将result结果转换为map，第一层map的key为rowkey，第二层map的key为 簇+:+列名
	 *
	 * @param results 结果数组
	 * @return map，外层Map key是rowkey,里层Map key是列，value是具体数据
	 */
	private Map<String, Map<String, byte[]>> resultToMap(Result[] results) {
		Map<String, Map<String, byte[]>> result = new HashMap<>();
		if (results != null) {
			for (Result res : results) {
				if (res != null && res.getRow() != null) {
					Map<String, byte[]> map = new HashMap<>();
					String rowKey = Bytes.toString(res.getRow());
					Cell[] cells = res.rawCells();
					for (Cell cell : cells) {
						map.put(Bytes.toString(CellUtil.cloneFamily(cell)) + ":" + Bytes.toString(CellUtil.cloneQualifier(cell)), CellUtil.cloneValue(cell));
					}
					result.put(rowKey, map);
				}
			}
		}
		return result;
	}

	/**
	 * 删除整行数据
	 *
	 * @param tableName 表名
	 * @param rowKeys   rowkey
	 * @return 是否操作成功
	 */
	public boolean deleteRow(String tableName, List<String> rowKeys) {
		Table table = null;
		try {
			table = hbase.getTable(tableName);
			List<Delete> list = new ArrayList<>();
			for (String rowkey : rowKeys) {
				Delete delete = new Delete(Bytes.toBytes(rowkey));
				list.add(delete);
			}
			table.delete(list);
		} catch (Exception e) {
			logger.error("delete row error.", e);
			return false;
		} finally {
			hbase.closeTable(table);
		}
		return true;
	}

	/**
	 * 删除指定列
	 *
	 * @param tableName       表名
	 * @param rowKeys         rowkey
	 * @param familyQualifier 簇列
	 * @return 是否操作成功
	 */
	public boolean deleteQualifier(String tableName, List<String> rowKeys, Map<String, List<String>> familyQualifier) {
		Table table = null;
		try {
			table = hbase.getTable(tableName);
			List<Delete> list = new ArrayList<>();
			for (String rowkey : rowKeys) {
				Delete delete = new Delete(Bytes.toBytes(rowkey));
				for (Map.Entry e : familyQualifier.entrySet()) {
					for (String qualifier : (List<String>) e.getValue()) {
						delete.addColumn(Bytes.toBytes((String) e.getKey()), Bytes.toBytes(qualifier));
					}
				}
				list.add(delete);
			}
			table.delete(list);
		} catch (Exception e) {
			logger.error("delete row qualifier error.", e);
			return false;
		} finally {
			hbase.closeTable(table);
		}
		return true;
	}
}
