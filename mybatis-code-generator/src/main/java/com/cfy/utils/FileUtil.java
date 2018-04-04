package com.cfy.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/3
 * Time: 10:28
 * Work contact: Astion_Leo@163.com
 */


public class FileUtil {

    public static void write(String filePath, String fileName, byte[] data) {
        File file = new File(filePath);
        FileOutputStream fileOutputStream = null;
        if (!file.exists()){
            file.mkdirs();
        }

        try {
            fileOutputStream = new FileOutputStream(new File(filePath, fileName));
            fileOutputStream.write(data);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fileOutputStream != null){
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static boolean delete(File file){
        if (null == file || !file.exists()) return false;

        if (file.isDirectory()){
            for (File f : file.listFiles()){
                delete(f);
            }
        } else {
            file.delete();
        }
        return true;
    }
}
