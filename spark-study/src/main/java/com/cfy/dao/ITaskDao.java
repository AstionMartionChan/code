package com.cfy.dao;

import com.cfy.bean.TaskEntity;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/1
 * Time: 11:55
 * Work contact: Astion_Leo@163.com
 */


public interface ITaskDao {

    TaskEntity findById(Integer taskId);

}
