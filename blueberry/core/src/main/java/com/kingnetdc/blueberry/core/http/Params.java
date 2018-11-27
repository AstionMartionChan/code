package com.kingnetdc.blueberry.core.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class Params {

    /**
     * 从 map 中获取构造参数
     * @param map
     * @return
     */
    public static String buildFromMap(Map<String, Object> map) {
        if (null == map) {
            return "";
        }
        List<String> params = new ArrayList<String>();
        map.forEach((key, val) -> {
            params.add(key + "=" + val);
        });
        return String.join("&", params);
    }

}
