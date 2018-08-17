package com.cfy.accumulator;

import com.cfy.constants.Constant;
import com.cfy.utils.StringUtils;
import org.apache.spark.AccumulatorParam;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/2
 * Time: 15:48
 * Work contact: Astion_Leo@163.com
 */


public class SesstionAccumulator implements AccumulatorParam<String> {


    @Override
    public String addAccumulator(String t1, String t2) {
        return add(t1, t2);
    }

    @Override
    public String addInPlace(String r1, String r2) {
        return add(r1, r2);
    }

    @Override
    public String zero(String initialValue) {
        return Constant.SESSION_COUNT + "=0|"
                + Constant.TIME_PERIOD_1s_3s + "=0|"
                + Constant.TIME_PERIOD_4s_6s + "=0|"
                + Constant.TIME_PERIOD_7s_9s + "=0|"
                + Constant.TIME_PERIOD_10s_30s + "=0|"
                + Constant.TIME_PERIOD_30s_60s + "=0|"
                + Constant.TIME_PERIOD_1m_3m + "=0|"
                + Constant.TIME_PERIOD_3m_10m + "=0|"
                + Constant.TIME_PERIOD_10m_30m + "=0|"
                + Constant.TIME_PERIOD_30m + "=0|"
                + Constant.STEP_PERIOD_1_3 + "=0|"
                + Constant.STEP_PERIOD_4_6 + "=0|"
                + Constant.STEP_PERIOD_7_9 + "=0|"
                + Constant.STEP_PERIOD_10_30 + "=0|"
                + Constant.STEP_PERIOD_30_60 + "=0|"
                + Constant.STEP_PERIOD_60 + "=0";
    }

    private String add(String v1, String v2) {
        if (StringUtils.isEmpty(v1)){
            return v2;
        }

        String oldValue = StringUtils.getFieldFromConcatString(v1, "\\|", v2);
        if (oldValue != null){
            Integer newValue = Integer.valueOf(oldValue) + 1;
            return StringUtils.setFieldInConcatString(v1, "\\|", v2, newValue.toString());
        }
        return v1;
    }
}
