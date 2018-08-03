package com.itcast.service;

import com.itcast.bean.JDSkuSearchInfo;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/27
 * Time: 11:00
 * Work contact: Astion_Leo@163.com
 */


public interface IEsService {

    List<JDSkuSearchInfo> esSearch(String searchContent, String sortType, Integer start, Integer size);
}
