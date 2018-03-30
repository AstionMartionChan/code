
package com.rltx.truck.broker.dao;


import com.rltx.truck.broker.po.OrgInfEntity;
import com.rltx.truck.broker.po.TruckBrokerReportLogEntity;
import org.springframework.stereotype.Repository;

/**
 * 组织信息查询
 */
@Repository("orgInfDao")
public interface IOrgInfDao {

    /**
     * 插入 无车承运人上报日志
     * @param orgId 公司id
     * @return OrgInfEntity
     */
    OrgInfEntity getOrgInf(Long orgId);

}
