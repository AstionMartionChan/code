package com.itcast.handler;

import com.itcast.po.Page;
import org.htmlcleaner.XPatherException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public interface ProccessHandler {

    Page proccess(Page page) throws XPatherException;
}
