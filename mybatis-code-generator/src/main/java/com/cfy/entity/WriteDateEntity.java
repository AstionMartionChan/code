package com.cfy.entity;

/**
 * Created by Leo_Chan on 2016/9/28.
 */
public class WriteDateEntity {

    /* 表名 */
    private String tableName;
    /* Java Entity文件生成内容 */
    private String javaDate;
    /* Xml Mapper文件生成内容 */
    private String xmlDate;


    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getJavaDate() {
        return javaDate;
    }

    public void setJavaDate(String javaDate) {
        this.javaDate = javaDate;
    }

    public String getXmlDate() {
        return xmlDate;
    }

    public void setXmlDate(String xmlDate) {
        this.xmlDate = xmlDate;
    }

    public String getJavaEntityName(){
        String newString = tableNameToJavaName();
        return newString.substring(0, 1).toUpperCase() + newString.substring(1) + "Entity.java";
    }

    public String getXmlFileName(){
        return this.tableName.replaceAll("_", ".") + ".mapping.xml";
    }

    public String getJavaDaoName(){
        String newString = tableNameToJavaName();
        return "I" + newString.substring(0, 1).toUpperCase() + newString.substring(1) + "Dao.java";
    }

    private String tableNameToJavaName(){
        String[] split = this.tableName.split("_");
        String newString = "";
        for (int x = 0; x < split.length; x++){
            if (x == 0){
                newString += split[x];
            }else {
                newString += split[x].substring(0, 1).toUpperCase() + split[x].substring(1);
            }
        }
        return newString;
    }
}
