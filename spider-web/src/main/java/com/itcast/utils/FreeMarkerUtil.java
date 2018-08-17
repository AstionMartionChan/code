package com.itcast.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/2
 * Time: 22:33
 * Work contact: Astion_Leo@163.com
 */


public class FreeMarkerUtil {

    //模版配置对象
    private static Configuration cfg;

    static {
        //初始化FreeMarker配置
        //创建一个Configuration实例
        cfg = new Configuration();
        cfg.setClassicCompatible(true);
        cfg.setDefaultEncoding("UTF-8");
        //设置FreeMarker的模版文件夹位置
        try {
            cfg.setClassForTemplateLoading(FreeMarkerUtil.class, "/templates");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String build(Map<String, Object> paramMap, String tempPath){
        String result = null;
        try {
            //创建模版对象
            Template template = cfg.getTemplate(tempPath);
            //在模版上执行插值操作，并输出到制定的输出流中
            StringWriter writer = new StringWriter();
            template.process(paramMap, writer);
            result = writer.toString();
            writer.flush();

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }
        return result;
    }

}
