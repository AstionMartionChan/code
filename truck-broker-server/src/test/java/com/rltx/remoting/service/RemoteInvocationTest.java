package com.rltx.remoting.service;

import com.alibaba.fastjson.JSON;
import org.springframework.remoting.support.RemoteInvocation;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhuyi on 17/4/17.
 */
public class RemoteInvocationTest {

    public static void main(String[] args) {

        RemoteInvocation remoteInvocation = new RemoteInvocation();
        remoteInvocation.setMethodName("test");
        remoteInvocation.setParameterTypes(new Class[]{Long.class});
        remoteInvocation.setArguments(new Object[]{1L});
        Map<String, Serializable> extraParams = new HashMap<>();
        remoteInvocation.setAttributes(extraParams);

        System.out.println(JSON.toJSONString(remoteInvocation));


    }

}
