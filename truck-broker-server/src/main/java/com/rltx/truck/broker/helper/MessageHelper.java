package com.rltx.truck.broker.helper;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

/**
 * 获取国际化文言配置帮助类
 */
public class MessageHelper {

    private static String ERROR_RESOURCE_NAME = "error";

    /**
     * 获取api异常国际化文言
     *
     * @param key    国际化key
     * @param locale 国际化语言
     * @return String 返回国际化文言
     */
    public static String getErrorMessage(String key, Locale locale, Object... args) {

        ResourceBundle resourceBundle = ResourceBundle.getBundle(ERROR_RESOURCE_NAME, locale);
        String messageFormat = resourceBundle.getString(key);
        return MessageFormat.format(messageFormat, args);

    }

}
