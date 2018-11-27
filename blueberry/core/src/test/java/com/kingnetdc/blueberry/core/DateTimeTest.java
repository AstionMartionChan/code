package com.kingnetdc.blueberry.core;

import com.kingnetdc.blueberry.core.util.DateTime;
import org.junit.Test;


public class DateTimeTest {

    @Test
    public void testDatetime() {
        System.out.println(DateTime.dateTime(1531991872));
        System.out.println(DateTime.dateDiff("2018-07-19", 2));
        System.out.println(DateTime.dateDiff("2018-07-19", -2));
        System.out.println(DateTime.timestamp("2018-07-19 12:00:00"));
    }

}
