package com.kingnetdc.blueberry.hbase;

/**
 * 列对象，包括rowkey、簇名、列名、值、时间戳、ttl
 */
public class Qualifier {
	/**
	 * rowKey
	 */
	private String rowKey;

	/**
	 * 簇名
	 */
	private String family;

	/**
	 * 列名
	 */
	private String qualifier;

	/**
	 * 时间
	 */
	private Long ts;

	/**
	 * 值
	 */
	private byte[] value;

	/**
	 * 有效时长，单位为毫秒
	 */
	private Long ttl;

	public Qualifier(String rowKey, String family, String qualifier, Long ts, byte[] value, Long ttl) {
		this.rowKey = rowKey;
		this.family = family;
		this.qualifier = qualifier;
		this.ts = ts;
		this.value = value;
		this.ttl = ttl;
	}

	public String getRowKey() {
		return rowKey;
	}

	public void setRowKey(String rowKey) {
		this.rowKey = rowKey;
	}

	public String getFamily() {
		return family;
	}

	public void setFamily(String family) {
		this.family = family;
	}

	public String getQualifier() {
		return qualifier;
	}

	public void setQualifier(String qualifier) {
		this.qualifier = qualifier;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	public byte[] getValue() {
		return value;
	}

	public void setValue(byte[] value) {
		this.value = value;
	}

	public Long getTtl() {
		return ttl;
	}

	public void setTtl(Long ttl) {
		this.ttl = ttl;
	}
}
