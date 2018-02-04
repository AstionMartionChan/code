package com.itcast.handler.impl;

import com.itcast.handler.StormHandler;
import com.itcast.po.Page;
import com.itcast.utils.MySqlUtil;

import java.sql.SQLException;

/**
 * Created by Leo_Chan on 2018/2/2.
 */
public class MysqlStormHandlerImpl implements StormHandler {



    @Override
    public void saveDB(Page page) throws SQLException {
        System.out.println("正在存入数据");
        MySqlUtil.insert(page.getParams());
    }
}
