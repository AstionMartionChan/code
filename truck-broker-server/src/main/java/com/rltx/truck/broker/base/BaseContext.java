package com.rltx.truck.broker.base;


import com.wl.framework.session.context.SessionContext;
import com.wl.framework.support.IFrameworkParamsGetter;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;


/**
 * 基础上下文
 */
public class BaseContext {

    /**
     * 获取国际化语言
     *
     * @return Locale 国际化语言
     */
    protected Locale getLocale() {
        // todo
        return Locale.CHINA;
    }

    /**
     * 获取request对象
     *
     * @return HttpServletRequest request对象
     */
    protected final HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    /**
     * 获取User-Agent
     *
     * @return String User Agent
     */
    protected final String getUserAgent() {
        return this.getRequest().getHeader("User-Agent");
    }

}
