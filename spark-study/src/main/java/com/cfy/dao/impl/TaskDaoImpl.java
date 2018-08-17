package com.cfy.dao.impl;

import com.cfy.bean.TaskEntity;
import com.cfy.dao.ITaskDao;
import com.cfy.utils.JDBCHelper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/1
 * Time: 12:14
 * Work contact: Astion_Leo@163.com
 */


public class TaskDaoImpl implements ITaskDao {


    @Override
    public TaskEntity findById(Integer taskId) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        final TaskEntity taskEntity = new TaskEntity();
        String sql = "select * from task where task_id = ?";
        Object[] params = new Object[]{taskId};
        jdbcHelper.prepareQuery(sql, params, new JDBCHelper.ResultProccess() {
            @Override
            public void proccess(ResultSet resultSet) throws SQLException {
                if (resultSet.next()){
                    Integer taskId = resultSet.getInt("task_id");
                    String taskName = resultSet.getString("task_name");
                    String createTime = resultSet.getString("create_time");
                    String startTime = resultSet.getString("start_time");
                    String finishTime = resultSet.getString("finish_time");
                    String taskType = resultSet.getString("task_type");
                    String taskStatus = resultSet.getString("task_status");
                    String taskParam = resultSet.getString("task_param");

                    taskEntity.setTaskId(taskId);
                    taskEntity.setTaskName(taskName);
                    taskEntity.setCreateTime(createTime);
                    taskEntity.setStartTime(startTime);
                    taskEntity.setFinishTime(finishTime);
                    taskEntity.setTaskType(taskType);
                    taskEntity.setTaskStatus(taskStatus);
                    taskEntity.setTaskParam(taskParam);

                    System.out.println(taskEntity.toString());
                }
            }
        });
        return taskEntity;
    }

}
