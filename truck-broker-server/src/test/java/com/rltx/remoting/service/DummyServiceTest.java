package com.rltx.remoting.service;

import com.rltx.truck.broker.result.DummyResult;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Created by zhuyi on 17/4/17.
 */
@ContextConfiguration(locations = {
        "classpath*:/applicationContext-client.xml"
})
@RunWith(SpringJUnit4ClassRunner.class)
@ActiveProfiles(value = "localhost")
public class DummyServiceTest {

//    @Resource(name = "dummyServiceClient")
//    private IDummyService dummyService;

//    @Test
//    public void test() {
//        DummyResult dummyResult = dummyService.test(1L);
//        System.out.println(ToStringBuilder.reflectionToString(dummyResult));
//    }

}
