package com.itcast.utils;

import com.alibaba.fastjson.JSONObject;
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


    public static Map<String, Object> proccessElement(String context, String xpath) {
        Map<String, Object> result = new HashMap<>();
        try {
            HtmlCleaner htmlCleaner = new HtmlCleaner();
            TagNode rootNode = htmlCleaner.clean(context);

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
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return result;
    }


    public static Map<String, List<String>> proccessElement2(String context, String xpath) {
        Map<String, List<String>> result = new HashMap<>();
        try {
            HtmlCleaner htmlCleaner = new HtmlCleaner();
            TagNode rootNode = htmlCleaner.clean(context);
            List<String> resultList = null;

            if (rootNode != null){
                Document doc = castTagNodeToDom4jDocument(htmlCleaner, rootNode);
                Element rootE = doc.getRootElement();
                Object obj = rootE.selectObject(xpath);
                if (obj instanceof ArrayList && obj != null){
                    List list = (ArrayList) obj;
                    if (list.get(0) instanceof DefaultElement){
                        List<DefaultElement> elementList = (ArrayList<DefaultElement>) list;
                        resultList = new ArrayList<>(elementList.size());
                        for (DefaultElement defaultElement : elementList){
                            resultList.add(defaultElement.getTextTrim());
                        }
                        result.put("elementList", resultList);
                    } else if (list.get(0) instanceof DefaultAttribute){
                        List<DefaultAttribute> attributeList = (ArrayList<DefaultAttribute>) list;
                        resultList = new ArrayList<>(attributeList.size());
                        for (DefaultAttribute defaultAttribute : attributeList){
                            resultList.add(defaultAttribute.getValue());
                        }
                        result.put("attributeList", resultList);
                    }
                } else if (obj instanceof DefaultElement && obj != null){
                    resultList = new ArrayList<>();
                    resultList.add(((DefaultElement) obj).getTextTrim());
                    result.put("elementText", resultList);
                } else if (obj instanceof DefaultAttribute && obj != null){
                    resultList = new ArrayList<>();
                    resultList.add(((DefaultAttribute) obj).getValue());
                    result.put("elementAttribute", resultList);
                }
                return result;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
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
