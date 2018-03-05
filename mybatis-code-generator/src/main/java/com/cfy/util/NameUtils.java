package com.cfy.util;

/**
 * Created by Leo_Chan on 2016/9/30.
 */
public class NameUtils {


    /**
     * 获取Java Entity类名
     * @param tableName
     * @return
     */
    public static String entityName(String tableName){
        String humpName = toHumpName(tableName);
        return humpName.substring(0, 1).toUpperCase() + humpName.substring(1) + "Entity";
    }

    /**
     * 获取Java Result类名
     * @param tableName
     * @return
     */
    public static String resultName(String tableName){
        String humpName = toHumpName(tableName);
        return humpName.substring(0, 1).toUpperCase() + humpName.substring(1) + "Result";
    }

    /**
     * 获取Result文件名
     * @param tableName
     * @return
     */
    public static String resultClassName(String tableName){
        return resultName(tableName) + ".java";
    }

    /**
     * 获取Entity文件名
     * @param tableName
     * @return
     */
    public static String entityClassName(String tableName){
        return entityName(tableName) + ".java";
    }

    /**
     * 获取Converter文件名
     * @param tableName
     * @return
     */
    public static String converterClassName(String tableName){
        return entityName(tableName) + "Converter.java";
    }
    /**
     * 获取Java Dao类名
     * @param tableName
     * @return
     */
    public static String daoName(String tableName){
        String humpName = toHumpName(tableName);
        return "I" + humpName.substring(0, 1).toUpperCase() + humpName.substring(1) + "Dao";
    }


    /**
     * 获取dao文件名
     * @param tableName
     * @return
     */
    public static String daoClassName(String tableName){
        return daoName(tableName) + ".java";
    }


    /**
     * 获取Java Dao 注解名
     * @param tableName
     * @return
     */
    public static String repositoryDaoName(String tableName){
        String humpName = toHumpName(tableName);
        return humpName + "Dao";
    }


    /**
     * 获取XML文件名
     * @param tableName
     * @return
     */
    public static String xmlName(String tableName){
        return tableName.replaceAll("_", ".") + ".mapping.xml";
    }


    /**
     * 获取Java类 字段名
     * @param columnName
     * @return
     */
    public static String fieldName(String columnName){
        return toHumpName(columnName);
    }


    /**
     * 获取Java类 字段名 头大写
     * @param columnName
     * @return
     */
    public static String upperFieldName(String columnName){
        String fieldName = fieldName(columnName);
        return fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
    }



    /**
     * 获取Java字段类型
     * @param columnType
     * @return
     */
    public static String fieldType(String columnType){
        String javaType = javaType(columnType);
        return javaType.substring(javaType.lastIndexOf(".") + 1, javaType.length());
    }



    /**
     * 获取Java字段类型 全路径
     * @param columnType
     * @return
     */
    public static String javaType(String columnType){
        columnType = columnType.toUpperCase();
        switch (columnType){
            case "VARCHAR":
            case "VARCHAR2":
            case "CHAR":
                return "java.lang.String";
            case "TINYINT":
                return "java.lang.Integer";
            case "BIT":
                return "java.lang.Boolean";
            case "INT":
            case "SMALLINT":
            case "INTEGER":
                return "java.lang.Integer";
            case "BIGINT":
                return "java.lang.Long";
            case "DATETIME":
            case "TIMESTAMP":
            case "DATE":
                return "java.util.Date";
            case "DOUBLE":
            case "DECIMAL":
                return "java.lang.Double";
            default:
                return "java.lang.String";
        }
    }




    /**
     * 数据库 表名字段名 替换为 Java驼峰命名
     * @param name
     * @return
     */
    private static String toHumpName(String name){
        String[] split = name.split("_");
        String humpName = "";
        for (int x = 0; x < split.length; x++){
            if (x == 0){
                humpName += split[x];
            } else {
                humpName += split[x].substring(0, 1).toUpperCase() + split[x].substring(1);
            }
        }
        return humpName;
    }
}
