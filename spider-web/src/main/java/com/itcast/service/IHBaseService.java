package com.itcast.service;

import com.itcast.bean.JDSkuDetailInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/27
 * Time: 14:13
 * Work contact: Astion_Leo@163.com
 */


public interface IHBaseService {

    JDSkuDetailInfo getById(String id);

    Map<String, Object> getPriceHistory(String id);
}
