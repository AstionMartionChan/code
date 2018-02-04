package com.itcast.utils;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.dom4j.tree.DefaultAttribute;
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


    public static Map<String, Object> proccessElement(String context, String xpath) throws XPatherException, IOException, DocumentException {
        HtmlCleaner htmlCleaner = new HtmlCleaner();
        TagNode rootNode = htmlCleaner.clean(context);
        Map<String, Object> result = new HashMap<>();

        if (rootNode != null){
            Document doc = castTagNodeToDom4jDocument(htmlCleaner, rootNode);
            Element rootE = doc.getRootElement();
            Object obj = rootE.selectObject(xpath);
            if (obj instanceof ArrayList && obj != null){
                List list = (ArrayList) obj;
                if (list.get(0) instanceof DefaultElement){
                    List<DefaultElement> elementList = (ArrayList<DefaultElement>) list;
                    result.put("elementList", elementList);
                    return result;
                } else if (list.get(0) instanceof DefaultAttribute){
                    List<DefaultAttribute> attributeList = (ArrayList<DefaultAttribute>) list;
                    result.put("attributeList", attributeList);
                    return result;
                }
            } else if (obj instanceof DefaultElement && obj != null){
                result.put("elementText", ((DefaultElement) obj).getText());
            } else if (obj instanceof DefaultAttribute && obj != null){
                result.put("elementAttribute", ((DefaultAttribute) obj).getValue());
            }
            return result;
        }

        return result;
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
