package com.itcast.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/3
 * Time: 19:36
 * Work contact: Astion_Leo@163.com
 */


public class PhantomJSUtil {

    public static String download(String url, String exec) {
        StringBuffer sbf = new StringBuffer();;
        try {
            Runtime runtime = Runtime.getRuntime();
            if (exec == null){
                exec = "phantomjs D:\\phantomjs-2.1.1-windows\\bin\\load.js ";
            }
            exec = exec+ url;
            Process process = runtime.exec(exec);
            InputStream inputStream = process.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String tmp = "";
            while((tmp = br.readLine())!=null){
                sbf.append(tmp);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sbf.toString();
    }
}
