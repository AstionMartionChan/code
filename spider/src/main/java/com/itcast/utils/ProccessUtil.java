package com.itcast.utils;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

/**
 * Created by Leo_Chan on 2018/1/30.
 */
public class ProccessUtil {

    public static String proccessTextContent(String context, String xpath) throws XPatherException {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(context);
        Object[] objects = rootNode.evaluateXPath(xpath);

        if (objects != null && objects.length > 0){
            TagNode tagNode = (TagNode) objects[0];
            String textContent = tagNode.getText().toString();
            return textContent;
        } else {
            return null;
        }
    }


    public static String proccessAttributeContent(String context, String xpath, String attributeName) throws XPatherException {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(context);
        Object[] objects = rootNode.evaluateXPath(xpath);

        if (objects != null && objects.length > 0){
            TagNode tagNode = (TagNode) objects[0];
            String attributeContent = tagNode.getAttributeByName(attributeName).toString();
            return attributeContent;
        } else {
            return null;
        }
    }
}
