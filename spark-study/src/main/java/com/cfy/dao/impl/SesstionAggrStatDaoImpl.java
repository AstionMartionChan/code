package com.cfy.dao.impl;

import com.cfy.bean.SessionAggrStatEntity;
import com.cfy.dao.ISesstionAggrStatDao;
import com.cfy.utils.JDBCHelper;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/2
 * Time: 21:05
 * Work contact: Astion_Leo@163.com
 */


public class SesstionAggrStatDaoImpl implements ISesstionAggrStatDao {
    @Override
    public void insert(SessionAggrStatEntity entity) {
        JDBCHelper jdbcHelper = JDBCHelper.getJdbcHelper();
        String sql = "INSERT INTO session_aggr_stat (" +
                "            task_id, " +
                "            session_count," +
                "            1s_3s," +
                "            4s_6s," +
                "            7s_9s," +
                "            10s_30s," +
                "            30s_60s," +
                "            1m_3m," +
                "            3m_10m," +
                "            10m_30m," +
                "            30m," +
                "            1_3," +
                "            4_6," +
                "            7_9," +
                "            10_30," +
                "            30_60," +
                "            `60`" +
                "        )" +
                "        VALUES (" +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?," +
                "            ?" +
                "        )";

        Object[] params = new Object[17];
        params[0] = entity.getTaskId();
        params[1] = entity.getSessionCount();
        params[2] = entity.getTime_1s3s();
        params[3] = entity.getTime_4s6s();
        params[4] = entity.getTime_7s9s();
        params[5] = entity.getTime_10s30s();
        params[6] = entity.getTime_30s60s();
        params[7] = entity.getTime_1m3m();
        params[8] = entity.getTime_3m10m();
        params[9] = entity.getTime_10m30m();
        params[10] = entity.getTime_30m();
        params[11] = entity.getStep_13();
        params[12] = entity.getStep_46();
        params[13] = entity.getStep_79();
        params[14] = entity.getStep_1030();
        params[15] = entity.getStep_3060();
        params[16] = entity.getStep_60();

        jdbcHelper.prepareUpdate(sql, params);
    }
}
