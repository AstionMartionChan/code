package com.test;

import com.cfy.bean.TaskEntity;
import com.cfy.dao.ITaskDao;
import com.cfy.dao.factory.DaoFactory;
import org.junit.Test;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/1
 * Time: 12:26
 * Work contact: Astion_Leo@163.com
 */


public class TaskDaoTest {

    @Test
    public void testFindById() {
        ITaskDao taskDao = DaoFactory.getTaskDao();
        TaskEntity task = taskDao.findById(1);
        return;
    }
}
