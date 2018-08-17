package com.cfy.dao.factory;

import com.cfy.dao.*;
import com.cfy.dao.impl.*;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/1
 * Time: 12:32
 * Work contact: Astion_Leo@163.com
 */


public class DaoFactory {

    public static ITaskDao getTaskDao(){
        return new TaskDaoImpl();
    }

    public static ISesstionAggrStatDao getSesstionAggrStatDao() {return new SesstionAggrStatDaoImpl(); }

    public static ISesstionRandomExtractDao getSesstionRandomExtractDao() {return new SesstionRandomExtractDaoImpl(); }

    public static ISesstionDetailDao getSessionDetailDao() {return new SessionDetailDaoImpl(); }

    public static ITop10categoryDao getTop10categoryDao() {return new Top10categoryDaoImpl(); }

    public static ITop10CategorySessionDao getTop10CategorySesstionDao() {return new Top10CategorySessionDaoImpl(); }

    public static IPageRateDao getPageRateDao() {return new PageRateDapImpl(); }

    public static IAreaTop3ProductDao getAreaTop3ProductDao() {return new AreaTop3ProductDaoImpl(); }
}
