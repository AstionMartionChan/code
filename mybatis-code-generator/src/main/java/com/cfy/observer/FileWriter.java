package com.cfy.observer;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/2
 * Time: 17:21
 * Work contact: Astion_Leo@163.com
 */


public interface FileWriter {

    String PROJECT_NAME = "mybatis-code-generator";

    void write(Object data);
}
