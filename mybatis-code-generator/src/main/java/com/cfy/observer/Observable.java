package com.cfy.observer;

import com.cfy.observer.impl.DaoFileWriter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/4/2
 * Time: 17:20
 * Work contact: Astion_Leo@163.com
 */


public class Observable {


    private List<FileWriter> observers;

    public void registerObserver(FileWriter fileWriter){
        if (null == observers){
            observers = new ArrayList<>();
        }
        observers.add(fileWriter);
    }

    public void removeObserver(FileWriter fileWriter){
        if (null != observers){
            observers.remove(fileWriter);
        }
    }

    public void write(Object data){
        for (FileWriter fileWriter : observers){
            fileWriter.write(data);
        }
    }
}
