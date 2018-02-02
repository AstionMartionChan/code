package com.itcast.handler;

import com.itcast.po.Page;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public interface ProccessHandler {

    Map<String, Object> proccessList(String content) throws XPatherException, DocumentException, IOException;

    Page proccessDetail(Page page) throws XPatherException, IOException, DocumentException;
}
