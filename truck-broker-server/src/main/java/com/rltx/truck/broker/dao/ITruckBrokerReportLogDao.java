
package com.rltx.truck.broker.dao;


import com.rltx.truck.broker.po.TruckBrokerReportLogEntity;
import org.springframework.stereotype.Repository;

/**
 * 无车承运人上报日志接口<br>
 */
@Repository("truckBrokerReportLogDao")
public interface ITruckBrokerReportLogDao {


    /**
     * 插入 无车承运人上报日志
     * @param entity
     * @return
     */
    Long save(TruckBrokerReportLogEntity entity);

}
