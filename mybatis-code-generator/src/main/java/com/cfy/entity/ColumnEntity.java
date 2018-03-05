package com.cfy.entity;

import com.cfy.util.NameUtils;

/**
 * Created by Leo_Chan on 2016/9/27.
 */
public class ColumnEntity {

    /* 表名 */
    private String tableName;
    /* 表字段名 */
    private String columnName;
    /* 表字段类型 */
    private String columnType;
    /* 表字段 */
    private String remark;


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getColumnType() {
        return columnType;
    }

    public void setColumnType(String columnType) {
        this.columnType = columnType;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getEntityName(){
        return NameUtils.entityName(this.tableName);
    }

    public String getResultName() {
        return NameUtils.resultName(this.tableName);
    }

    public String getDaoName(){
        return NameUtils.daoName(this.tableName);
    }

    public String getRepositoryDaoName(){
        return NameUtils.repositoryDaoName(this.tableName);
    }

    public String getFieldName(){
        return NameUtils.fieldName(this.columnName);
    }

    public String getUpperFieldName(){
        return NameUtils.upperFieldName(this.columnName);
    }

    public String getFieldType(){
        return NameUtils.fieldType(this.columnType);
    }

    public String getJavaType(){
        return NameUtils.javaType(this.columnType);
    }
}
