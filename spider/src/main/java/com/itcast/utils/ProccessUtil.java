package com.itcast.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultElement;
import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.DomSerializer;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.Serializer;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathFactory;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Leo_Chan on 2018/1/30.
 */
public class ProccessUtil {

    public static Document castTagNodeToDom4jDocument(HtmlCleaner cleaner, TagNode tagNode) throws IOException, DocumentException {
        StringWriter sw = new StringWriter();
        Serializer serializer = new SimpleXmlSerializer(cleaner.getProperties());
        serializer.write(tagNode, sw, "UTF-8");
        SAXReader reader = new SAXReader();
        StringReader strReader = new StringReader(sw.toString());
        Document doc = reader.read(strReader);
        return doc;
    }

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

    public static Object proccessTextContentMore(String context, String xpath) throws XPatherException, IOException, DocumentException {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(context);

        if (rootNode != null){
            Document doc = castTagNodeToDom4jDocument(htmlCleaner, rootNode);
            Element rootE = doc.getRootElement();
            Object obj = rootE.selectObject(xpath);

            List<DefaultElement> elementList =  (ArrayList<DefaultElement>) obj;

            for (DefaultElement element : elementList){
                System.out.println(element.getText());
            }
        }


//        if (objects != null && objects.length > 0){
//            for (Object obj : objects){
//                TagNode tagNode = (TagNode) obj;
//                String textContent = tagNode.getText().toString();
//                System.out.println(textContent);
//            }
//            return null;
//        } else {
//            return null;
//        }
        return null;
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
}
