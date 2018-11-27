package com.wutiao.realtime.application.storm.bolt;

import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * Created by yangjun on 2018/5/29.
 * 用户对内容行为统计bolt
 */
public class ProcBolt extends BaseBasicBolt {

    public static Logger logger;

    private Set<String> events = new HashSet<>();

    /**
     * 计算周期，默认1分钟
     */
    private int period = 1;

    /**
     * 计算/存储层的配置文件
     */
    private Properties redisProperties;
    private Properties hbaseProperties;
    private Properties compute_properties;
    private Properties cacheProperties;
    private String env;
    private String idc;

    //构造方法
    public ProcBolt() {

    }


    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        logger = LoggerFactory.getLogger(ProcBolt.class);
        //TODO 初始化
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        Object data = input.getValueByField("entry");
        //TODO 处理业务逻辑



        //TODO 发送到下一个节点（如果需要就发送，不需要不发送）
        collector.emit(new Values("", data));
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        //定义发送数据
        declarer.declare(new Fields("time", "itemlist"));
    }
}
