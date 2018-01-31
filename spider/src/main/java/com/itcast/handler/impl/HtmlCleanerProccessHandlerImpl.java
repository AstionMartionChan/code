package com.itcast.handler.impl;

import com.itcast.handler.ProccessHandler;
import com.itcast.po.Page;
import com.itcast.utils.ProccessUtil;
import org.dom4j.DocumentException;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public class HtmlCleanerProccessHandlerImpl implements ProccessHandler {


    @Override
    public Page proccess(Page page) throws XPatherException, IOException, DocumentException {
        String title = ProccessUtil.proccessTextContent(page.getContext(), "//*[@id=\"maincontent\"]/h1");
        page.addParams("title", title);

        return page;
    }
}
