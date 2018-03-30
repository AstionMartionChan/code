package com.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Leo_Chan on 2017/3/10.
 */
public class UploadUtils {

    public static String getFormatTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date()).toString();
    }


    public static String getUuid() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
