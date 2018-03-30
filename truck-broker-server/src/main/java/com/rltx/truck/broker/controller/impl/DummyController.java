package com.rltx.truck.broker.controller.impl;

import com.rltx.truck.broker.service.IDummyService;
import com.rltx.truck.broker.result.DummyResult;
import org.springframework.stereotype.Controller;

/**
 * Created by zhuyi on 17/4/14.
 */
@Controller("dummyController")
public class DummyController implements IDummyService {

    public DummyResult test(Long id) {

        DummyResult dummyResult = new DummyResult();
        dummyResult.setCode("200");
        dummyResult.setValue("dummy test");

        return dummyResult;
    }

    public DummyResult testWithError() throws Exception {
        throw new Exception("test error");
    }











}
