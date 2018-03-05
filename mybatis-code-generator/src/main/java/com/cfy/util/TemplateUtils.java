package com.cfy.util;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

/**
 * Created by Leo_Chan on 2016/12/8.
 */
public class TemplateUtils {

    private static Configuration cfg;            //模版配置对象

    static {
        //初始化FreeMarker配置
        //创建一个Configuration实例
        cfg = new Configuration();
        cfg.setClassicCompatible(true);
        cfg.setDefaultEncoding("UTF-8");
        //设置FreeMarker的模版文件夹位置
        try {
//            cfg.setDirectoryForTemplateLoading(new File("template/"));
//            cfg.setDirectoryForTemplateLoading(new File(new File("").getCanonicalPath() + "\\template\\"));
            cfg.setDirectoryForTemplateLoading(new File("E:\\auto-test\\template"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }



    /**
     * 根据传入的参数和模板路径，生成对应的数据
     * @param paramMap  传入模板参数
     * @param tempPath  模板路径
     * @return
     * @throws java.io.IOException
     * @throws TemplateException
     */
    public static String toString(Map<String, Object> paramMap, String tempPath) throws IOException, TemplateException {
        //创建模版对象
        Template template = cfg.getTemplate(tempPath);
        //在模版上执行插值操作，并输出到制定的输出流中
        StringWriter writer = new StringWriter();
        template.process(paramMap, writer);
        String result = writer.toString();
        writer.flush();
        return result;
    }



}
