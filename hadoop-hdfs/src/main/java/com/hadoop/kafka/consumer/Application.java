package com.hadoop.kafka.consumer;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/8/18
 * Time: 21:05
 * Work contact: Astion_Leo@163.com
 */


public class Application {



    public static void main(String[] args) throws IOException {
        Properties prop = new Properties();
        prop.load(Application.class.getClassLoader().getResourceAsStream("system.properties"));

        ActivationConsumer consumer = new ActivationConsumer(prop);

        consumer.consumer();
    }
}
