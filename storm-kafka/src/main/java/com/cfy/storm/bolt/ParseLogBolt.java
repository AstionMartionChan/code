package com.cfy.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;

import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/13
 * Time: 21:11
 * Work contact: Astion_Leo@163.com
 */


public class ParseLogBolt extends BaseRichBolt {

    private Map stormConf;
    private TopologyContext context;
    private OutputCollector collector;

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.stormConf = stormConf;
        this.context = context;
        this.collector = collector;
    }

    @Override
    public void execute(Tuple input) {
        try {
            byte[] bytes = input.getBinaryByField("bytes");
            String log = new String(bytes);
            String flag = null;
            if (log.contains("页面爬取成功")){
                flag = "成功";
            } else if (log.contains("页面爬取失败")) {
                flag = "失败";
            }

            if (null != flag){
                collector.emit(new Values(flag));
            }

            collector.ack(input);
        } catch (Exception e) {
            e.printStackTrace();
            collector.fail(input);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("flag"));
    }
}
