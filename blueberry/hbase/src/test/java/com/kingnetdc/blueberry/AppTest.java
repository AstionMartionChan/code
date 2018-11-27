package com.kingnetdc.blueberry;

import com.kingnetdc.blueberry.core.util.Common;
import com.kingnetdc.blueberry.hbase.KdcHbase;
import com.kingnetdc.blueberry.hbase.Qualifier;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Unit test for simple App.
 */
public class AppTest {

	String hosts = "192.168.77.29:2181,192.168.77.28:2181,192.168.77.25:2181";
	String user = "root";
	String nameSpace = "wutiao_adv_android";
	String writeToWalFlag = "true";
	@Test
	public void testSave(){
		KdcHbase hbase = new KdcHbase(hosts,user,nameSpace,writeToWalFlag);

		String table = "activation_info";
		String rowKey = "adv_data4";
		String family = "base";
		String f2 = "base_info";
		Qualifier adv_id = new Qualifier(rowKey, f2, "adv_id", null, Bytes.toBytes("12"), null);
		Qualifier client = new Qualifier(rowKey, family, "client", null, Bytes.toBytes("client"), null);
		Qualifier idfa = new Qualifier(rowKey, f2, "idfa", null, Bytes.toBytes("idfa"), null);
		Qualifier ip = new Qualifier(rowKey, family, "ip", null, Bytes.toBytes("127.0.0.2"), null);
		Qualifier real_remote = new Qualifier(rowKey, f2, "real_remote", null, Bytes.toBytes("real_remote"), null);
		Qualifier referer = new Qualifier(rowKey, family, "referer", null, Bytes.toBytes("refer"), null);
		Qualifier uri = new Qualifier(rowKey, family, "uri", null, null, null);

		List<Qualifier> list = new ArrayList<>();
		list.add(adv_id);
		list.add(client);
		list.add(idfa);
		list.add(ip);
		list.add(real_remote);
		list.add(referer);
		list.add(uri);
		if(hbase.save(table, list, null)){
			System.out.println("save ok");
		} else {
			System.out.println("save error.");
		}
	}

	@Test
	public void testGet(){
		KdcHbase hbase = new KdcHbase(hosts,user,nameSpace,writeToWalFlag);

		String table = "activation_info";
		String rowKey = "adv_data1";
		List<String> rowkeys = new ArrayList<>();
		rowkeys.add(rowKey);
		rowkeys.add("adv_data3");
		Map<String, Map<String, byte[]>> r1 = hbase.get(table, rowkeys);
		for (String key: r1.keySet()) {
			System.out.println("==========rowkey=" + key);
			for(String column: r1.get(key).keySet()) {
				System.out.println("column= " + column + " value=" + Bytes.toString(r1.get(key).get(column)));
			}
		}

		System.out.println("==============get by family===========");
		List<String> familys = new ArrayList<>();
		familys.add("base");
		Map<String, Map<String, byte[]>> r2 = hbase.get(table, rowkeys, familys);
		for (String key: r2.keySet()) {
			System.out.println("==========rowkey=" + key);
			for(String column: r2.get(key).keySet()) {
				System.out.println("column= " + column + " value=" + Bytes.toString(r2.get(key).get(column)));
			}
		}

		System.out.println("==============get by qualifier============");
		List<String> columns = new ArrayList<>();
		columns.add("client");
		columns.add("ip");
		columns.add("referer");
		Map<String, List<String>> qualifiers = new HashMap<>();
		qualifiers.put("base", columns);
		Map<String, Map<String, byte[]>> r3 = hbase.get(table, rowkeys, qualifiers);
		for (String key: r3.keySet()) {
			System.out.println("==========rowkey=" + key);
			for(String column: r3.get(key).keySet()) {
				System.out.println("column= " + column + " value=" + Bytes.toString(r3.get(key).get(column)));
			}
		}
	}

	@Test
	public void testScan(){
		KdcHbase hbase = new KdcHbase(hosts,user,nameSpace,writeToWalFlag);

		String table = "adv_data";

		String startKey = "adv_data1";
		String stopKey = "adv_data4";

		Map<String, Map<String, byte[]>> result = hbase.scan(table, startKey, stopKey, null);
		for (String key: result.keySet()) {
			System.out.println("==========rowkey=" + key);
			for(String column: result.get(key).keySet()) {
				System.out.println("column= " + column + " value=" + Bytes.toString(result.get(key).get(column)));
			}
		}

		Map<String, Map<String, byte[]>> result2 = hbase.scan(table, startKey, null,2);
		for (String key: result2.keySet()) {
			System.out.println("==========rowkey=" + key);
			for(String column: result2.get(key).keySet()) {
				System.out.println("column= " + column + " value=" + Bytes.toString(result2.get(key).get(column)));
			}
		}

		Map<String, Map<String, byte[]>> result3 = hbase.scan(table, startKey, null,0);
		for (String key: result3.keySet()) {
			System.out.println("==========rowkey=" + key);
			for(String column: result3.get(key).keySet()) {
				System.out.println("column= " + column + " value=" + Bytes.toString(result3.get(key).get(column)));
			}
		}
	}

	@Test
	public void testDelete(){
		KdcHbase hbase = new KdcHbase(hosts,user,nameSpace,writeToWalFlag);

		String table = "adv_data";

		String rowkey = "adv_data3";

		Map<String, List<String>> qualifiers = new HashMap<>();
		List<String> list = new ArrayList<>();
		list.add("uri");
		list.add("idfa");
		qualifiers.put("base_info", list);

		List<String> rowkeys = new ArrayList<>();
		rowkeys.add(rowkey);
		//hbase.deleteQualifier(table, rowkeys, qualifiers);

		hbase.deleteRow(table, rowkeys);
	}

	@Test
	public void testMd5(){
		System.out.println(Common.md5("client_ip=223.104.3.250, 172.17.0.234&device_id=3E70233C-62F2-4AE3-881E-2B22872584DC&user_id=14090131&app_id=20&key=c890809bohoangjunzheec34jian63201104136b"));
	}
}
