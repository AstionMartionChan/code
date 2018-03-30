package com.rltx.dao;

import com.rltx.truck.broker.dao.IOrgInfDao;
import com.rltx.truck.broker.result.DummyResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by wubin on 17/4/20.
 */
@ContextConfiguration(locations = {
        "classpath*:/applicationContext.xml",
        "classpath*:/applicationContext-datasource.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = "localhost")
public class OrgInfDaoTest {

    @Resource(name = "orgInfDao")
    private IOrgInfDao orgInfDao;

    @Test
    public void test() {
        assert orgInfDao.getOrgInf(551l) != null;
    }

}
