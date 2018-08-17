package com.cfy.udaf;

import org.apache.spark.sql.Row;
import org.apache.spark.sql.expressions.MutableAggregationBuffer;
import org.apache.spark.sql.expressions.UserDefinedAggregateFunction;
import org.apache.spark.sql.types.DataType;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructType;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/7/11
 * Time: 21:17
 * Work contact: Astion_Leo@163.com
 */


public class GroupConcatUDAF extends UserDefinedAggregateFunction {

    private StructType inputSchema = DataTypes.createStructType(Arrays.asList(DataTypes.createStructField("cityInfo", DataTypes.StringType, true)));
    private StructType bufferSchema = DataTypes.createStructType(Arrays.asList(DataTypes.createStructField("bufferCityInfo", DataTypes.StringType, true)));

    @Override
    public StructType inputSchema() {
        return inputSchema;
    }

    @Override
    public StructType bufferSchema() {
        return bufferSchema;
    }

    @Override
    public DataType dataType() {
        return DataTypes.StringType;
    }

    @Override
    public boolean deterministic() {
        return true;
    }

    @Override
    public void initialize(MutableAggregationBuffer buffer) {
        buffer.update(0, "");
    }

    @Override
    public void update(MutableAggregationBuffer buffer, Row input) {
        String bufferCityInfo = buffer.getString(0);
        String cityInfo = input.getString(0);
        if (!bufferCityInfo.contains(cityInfo)){
            if ("".equals(bufferCityInfo)){
                bufferCityInfo += cityInfo;
            } else {
                bufferCityInfo += "," + cityInfo;
            }

            // 更新回buffer1中 切记
            buffer.update(0, bufferCityInfo);
        }
    }

    @Override
    public void merge(MutableAggregationBuffer buffer1, Row buffer2) {
        String bufferCityInfo1 = buffer1.getString(0);
        String bufferCityInfo2 = buffer2.getString(0);

        for (String singleCityInfo : bufferCityInfo2.split(",")){
            if (!bufferCityInfo1.contains(singleCityInfo)){
                if ("".equals(bufferCityInfo1)){
                    bufferCityInfo1 += singleCityInfo;
                } else {
                    bufferCityInfo1 += "," + singleCityInfo;
                }
            }
        }
        // 更新回buffer1中 切记
        buffer1.update(0, bufferCityInfo1);

    }

    @Override
    public Object evaluate(Row buffer) {
        return buffer.getString(0);
    }
}
