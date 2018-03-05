package com.cfy.handler;

import com.cfy.entity.ColumnEntity;
import com.cfy.entity.WriteDateEntity;
import com.cfy.util.NameUtils;
import com.cfy.util.TemplateUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * Created by Leo_Chan on 2016/9/27.
 */
public class AutoCreateHandler {

    /**
     * 连接数据库
     */
    static {
        try {
            Properties prop = new Properties();
            String url = null;
            String username = null;
            String password = null;


            InputStream inputStream = AutoCreateHandler.class.getResourceAsStream("/systemConfig.properties");
//            InputStream inputStream = new FileInputStream(new File("").getCanonicalPath() + File.separator + "systemConfig.properties");

            try {
                prop.load(inputStream);
//                prop.load(resourceAsStream);
                url = prop.getProperty("jdbc.url").trim();
                username = prop.getProperty("jdbc.username").trim();
                password = prop.getProperty("jdbc.password").trim();
                packagePath = prop.getProperty("package.path").trim();
                outputPath = prop.getProperty("output.path").trim();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Class.forName("com.mysql.jdbc.Driver");
            connection = DriverManager.getConnection(url, username, password);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /* 数据库连接 */
    private static Connection connection;
    /* 包名全路径 */
    private static String packagePath;
    /* 输出路径 */
    private static String outputPath;



    public static void main(String[] args) throws Exception {

        List<String> tableNames = getTableNames(connection);
//        List<String> list = new ArrayList<>();
//        list.add("gps_alarm_log");
//        list.add("gps_arrest_log");
//        list.add("gps_device");
//        list.add("gps_parameter");
//        list.add("gps_polygon_access_log");
//        list.add("gps_truck_device_relation");
        start(connection, tableNames, outputPath);

        System.out.println("Auto Create Success!");
    }


    /**
     * 其他数据库不需要这个方法 oracle和db2需要
     * @param conn
     * @return
     * @throws Exception
     */
    private static String getSchema(Connection conn) throws Exception {
        String schema;
        schema = conn.getMetaData().getUserName();
        if ((schema == null) || (schema.length() == 0)) {
            throw new Exception("ORACLE数据库模式不允许为空");
        }
        return schema.toUpperCase().toString();

    }


    /**
     * 获取数据库所有表名称
     * @param connection
     * @return
     * @throws java.sql.SQLException
     */
    private static List<String> getTableNames(Connection connection) throws SQLException {
        List<String> tableNames = new ArrayList<String>();
        ResultSet resultSet = connection.getMetaData().getTables(connection.getCatalog(), "root", null, new String[]{"TABLE"});
        while (resultSet.next()){
            tableNames.add(resultSet.getString("TABLE_NAME"));

        }
        return tableNames;
    }


    /**
     * 入口
     * @param connection
     * @param tableNames
     * @throws java.sql.SQLException
     */
    public static void start(Connection connection, List<String> tableNames, String filePath) throws Exception {
        for (String tableName : tableNames){
            //获取表数据内容
            ResultSet resultSet = connection.getMetaData().getColumns(connection.getCatalog(), getSchema(connection), tableName, null);
            //获取表中数据 并装入ColumnEntity
            List<ColumnEntity> columnToEntity = getColumnToEntity(resultSet);

            //封装FreeMarker 写入参数
            Map<String, Object> map = new HashMap<>();
            map.put("packagePath", packagePath);
            map.put("entityList", columnToEntity);

            //写入Entity
            writeFile(filePath + File.separator + "auto_create" + File.separator + "entity", NameUtils.entityClassName(columnToEntity.get(0).getTableName()), TemplateUtils.toString(map, "entity.ftl"));
            //写入Dao
            writeFile(filePath + File.separator + "auto_create" + File.separator + "dao", NameUtils.daoClassName(columnToEntity.get(0).getTableName()), TemplateUtils.toString(map, "dao.ftl"));
            //写入Mapper
            writeFile(filePath + File.separator + "auto_create" + File.separator + "mapper", NameUtils.xmlName(columnToEntity.get(0).getTableName()), TemplateUtils.toString(map, "mapping.ftl"));
            //写入controller
            writeFile(filePath + File.separator + "auto_create" + File.separator + "controller", NameUtils.entityClassName(columnToEntity.get(0).getTableName()), TemplateUtils.toString(map, "controller.ftl"));
            //写入result
            writeFile(filePath + File.separator + "auto_create" + File.separator + "result", NameUtils.resultClassName(columnToEntity.get(0).getTableName()), TemplateUtils.toString(map, "result.ftl"));
            //写入converter
            writeFile(filePath + File.separator + "auto_create" + File.separator + "converter", NameUtils.converterClassName(columnToEntity.get(0).getTableName()), TemplateUtils.toString(map, "converter.ftl"));

        }

    }


    /**
     * 获取数据库 表名 字段名 以及 字段注释
     * @param resultSet
     * @return
     * @throws java.sql.SQLException
     */
    private static List<ColumnEntity> getColumnToEntity(ResultSet resultSet) throws SQLException {
        List<ColumnEntity> list = new ArrayList<ColumnEntity>();
        while (resultSet.next()){
            ColumnEntity columnEntity = new ColumnEntity();
            String tableName = resultSet.getString("TABLE_NAME"); //表名称
            String columnName = resultSet.getString("COLUMN_NAME"); //字段名称
            String columnType = resultSet.getString("TYPE_NAME"); //字段类型
            String remark = resultSet.getString("REMARKS"); //字段注释

            columnEntity.setTableName(tableName);
            columnEntity.setColumnName(columnName);
            columnEntity.setColumnType(columnType);
            columnEntity.setRemark(remark);

            list.add(columnEntity);
        }
        return list;
    }


    /**
     * 写入文件
     * @param filePath
     * @param fileName
     * @param date
     */
    private static void writeFile(String filePath, String fileName, String date){
        File file = new File(filePath);
        FileOutputStream fileOutputStream = null;
        if (!file.exists()){
            file.mkdirs();
        }

        try {
            fileOutputStream = new FileOutputStream(new File(filePath, fileName));
            byte[] bytes = date.getBytes();
            fileOutputStream.write(bytes);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


}
