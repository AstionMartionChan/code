package com.itcast.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultElement;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.Serializer;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Leo_Chan on 2018/1/30.
 */
public class ProccessUtil {



    public static String proccessTextContent(String context, String xpath) throws XPatherException, IOException, DocumentException {
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

    public static Map<String, Object> proccessTextContentMore(String context, String xpath) throws XPatherException, IOException, DocumentException {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(context);

        if (rootNode != null){
            Document doc = castTagNodeToDom4jDocument(htmlCleaner, rootNode);
            Element rootE = doc.getRootElement();
            Object obj = rootE.selectObject(xpath);

            List<DefaultElement> elementList =  (ArrayList<DefaultElement>) obj;

            Map<String, Object> result = new HashMap<>();
            String temp = null;
            for (int x = 0; x < elementList.size(); x++){
                if (isOdd(x)){ //偶数
                    result.put(elementList.get(x).getText(), elementList.get(x).getText());
                    temp = elementList.get(x).getText();
                } else {      //奇数
                    result.put(result.get(temp).toString(), elementList.get(x).getText());
                }
            }
            return result;
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
            String attributeContent = tagNode.getAttributeByName(attributeName);
            return attributeContent;
        } else {
            return null;
        }
    }





    private static boolean isOdd(int a){
        if((a & 1) != 1){   //是奇数
            return true;
        }
        return false;
    }


    private static Document castTagNodeToDom4jDocument(HtmlCleaner cleaner, TagNode tagNode) throws IOException, DocumentException {
        StringWriter sw = new StringWriter();
        Serializer serializer = new SimpleXmlSerializer(cleaner.getProperties());
        serializer.write(tagNode, sw, "UTF-8");
        SAXReader reader = new SAXReader();
        StringReader strReader = new StringReader(sw.toString());
        Document doc = reader.read(strReader);
        return doc;
    }
}
