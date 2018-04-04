package com.cfy.trait;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/1
 * Time: 15:06
 * Work contact: Astion_Leo@163.com
 */


public class HumpProcess {

    /**
     * 数据库 表名字段名 替换为 Java驼峰命名
     * @param name
     * @return
     */
    protected String toHumpName(String name) {
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
