package com.hadoop.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/29
 * Time: 16:21
 * Work contact: Astion_Leo@163.com
 */


public class AckerTestTopology {


    public static class ReadSpout extends BaseRichSpout {

        private Map map;

        private TopologyContext topologyContext;

        private SpoutOutputCollector spoutOutputCollector;

        @Override
        public void open(Map map, TopologyContext topologyContext, SpoutOutputCollector spoutOutputCollector) {
            this.map = map;
            this.topologyContext = topologyContext;
            this.spoutOutputCollector = spoutOutputCollector;
        }

        @Override
        public void nextTuple() {
            // 读文件
            Collection<File> files = FileUtils.listFiles(new File("d:\\test\\"), new String[]{"txt"}, true);
            for (File file : files){
                try {
                    // 读行
                    List<String> lines = FileUtils.readLines(file);
                    for (String line : lines){
                        spoutOutputCollector.emit(new Values(line), line);
                    }
                    // 修改已读文件
                    FileUtils.moveFile(file, new File(file.getAbsolutePath() + ".bak"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("line"));
        }

        @Override
        public void ack(Object msgId) {
            String line = (String) msgId;
            System.out.println("处理成功：" + line);
        }

        @Override
        public void fail(Object msgId) {
            String line = (String) msgId;
            System.err.println("处理失败：" + line);
        }
    }

    public static class SplitBlot extends BaseRichBolt {

        private Map map;

        private TopologyContext topologyContext;

        private OutputCollector outputCollector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.map = map;
            this.topologyContext = topologyContext;
            this.outputCollector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            try {
                String line = tuple.getStringByField("line");
                String[] splited = line.split("\t");
                for (String str : splited){
                    outputCollector.emit(tuple, new Values(str));
                }
                outputCollector.ack(tuple);
            } catch (Exception e) {
                e.printStackTrace();
                outputCollector.fail(tuple);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("str"));
        }
    }


    public static class LocalCountBlot extends BaseRichBolt {

        private Map map;

        private TopologyContext topologyContext;

        private OutputCollector outputCollector;

        private Map<String, Integer> countMap = new HashMap();


        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.map = map;
            this.topologyContext = topologyContext;
            this.outputCollector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            try {
                String str = tuple.getStringByField("str");
                if (str.equals("zsr")){
                    outputCollector.fail(tuple);
                }
                Integer count = 0;
                if (countMap.containsKey(str)){
                    count = countMap.get(str);
                }
                count ++;
                countMap.put(str, count);

                outputCollector.emit(tuple, new Values(str, count));
                outputCollector.ack(tuple);
            } catch (Exception e) {
                e.printStackTrace();
                outputCollector.fail(tuple);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("word", "count"));
        }
    }

    public static class GlobalCountBlot extends BaseRichBolt {

        private Map<String, Integer> countMap = new HashMap();
        private OutputCollector outputCollector;

        @Override
        public void prepare(Map map, TopologyContext topologyContext, OutputCollector outputCollector) {
            this.outputCollector = outputCollector;
        }

        @Override
        public void execute(Tuple tuple) {
            try {
                String word = tuple.getStringByField("word");
                Integer count = tuple.getIntegerByField("count");
                countMap.put(word, count);
                for (Map.Entry<String, Integer> entry : countMap.entrySet()){
                    System.out.println("单个单词总数 " + entry.getKey() + ": " + entry.getValue());
                }
                System.out.println("单词去重总数：" + countMap.size());

                System.out.println("--------------------------");
                outputCollector.ack(tuple);
            } catch (Exception e) {
                e.printStackTrace();
                outputCollector.fail(tuple);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {

        }
    }

    public static void main(String[] args) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("ReadSpout", new ReadSpout());
        topologyBuilder.setBolt("SplitBlot", new SplitBlot(),2).shuffleGrouping("ReadSpout");
        topologyBuilder.setBolt("LocalCountBlot", new LocalCountBlot(),2).fieldsGrouping("SplitBlot", new Fields("str"));
        topologyBuilder.setBolt("GlobalCountBlot", new GlobalCountBlot()).globalGrouping("LocalCountBlot");

        Config config = new Config();
        StormTopology topology = topologyBuilder.createTopology();
        String topologyName = AckerTestTopology.class.getSimpleName();

        if (args.length == 0){
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(topologyName, config, topology);
        } else {
            try {
                StormSubmitter.submitTopology(topologyName, config, topology);
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            }
        }
    }
}
