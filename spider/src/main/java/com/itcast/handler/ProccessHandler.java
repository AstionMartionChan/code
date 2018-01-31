package com.itcast.handler;

import com.itcast.po.Page;
import org.dom4j.DocumentException;
import org.htmlcleaner.XPatherException;

import java.io.IOException;

/**
 * Created by Leo_Chan on 2018/1/25.
 */
public interface ProccessHandler {

    Page proccess(Page page) throws XPatherException, IOException, DocumentException;
}
