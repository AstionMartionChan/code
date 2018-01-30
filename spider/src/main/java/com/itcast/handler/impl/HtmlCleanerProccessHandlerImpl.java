package com.itcast.handler.impl;

import com.itcast.handler.ProccessHandler;
import com.itcast.po.Page;
import com.itcast.utils.ProccessUtil;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HtmlCleanerProccessHandlerImpl implements ProccessHandler {


    @Override
    public Page proccess(Page page) throws XPatherException {
        String title = ProccessUtil.proccessTextContent(page.getContext(), "//*[@id=\"maincontent\"]/h1");
        page.addParams("title", title);

        return page;
    }
}
