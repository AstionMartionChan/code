package com.cfy.trait;

import com.cfy.entity.Column;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/2
 * Time: 16:56
 * Work contact: Astion_Leo@163.com
 */


public interface Processable {

    Map<String, String> process(Column column);

}
