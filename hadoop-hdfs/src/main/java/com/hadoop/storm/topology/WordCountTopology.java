package com.hadoop.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.cluster.StormClusterState;
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
 * Date: 2018/5/27
 * Time: 22:03
 * Work contact: Astion_Leo@163.com
 */


public class WordCountTopology {


    public static class Spout extends BaseRichSpout {

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
            Collection<File> files = FileUtils.listFiles(new File(File.separator + "opt" + File.separator
            ), new String[]{"txt"}, true);
            for (File file : files){
                try {
                    List<String> lines = FileUtils.readLines(file);
                    for (String line : lines){
                        spoutOutputCollector.emit(new Values(line));
                    }

                    FileUtils.moveFile(file, new File(file.getAbsoluteFile() + ".bak"));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {
            outputFieldsDeclarer.declare(new Fields("line"));
        }
    }

    public static class SplitBlot extends BaseRichBolt {

        private Map stormConf;

        private TopologyContext topologyContext;

        private OutputCollector collector;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.stormConf = stormConf;
            this.topologyContext = context;
            this.collector = collector;
        }

        @Override
        public void execute(Tuple input) {
            String line = input.getStringByField("line");
            if (null != line && !"".equals(line)){
                String[] splited = line.split("\t");
                for (String str : splited){
                    collector.emit(new Values(str));
                }
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("str"));
        }
    }

    public static class CountBlot extends BaseRichBolt {

        private Map<String, Integer> countMap = new HashMap<>();

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {

        }

        @Override
        public void execute(Tuple input) {
            String str = input.getStringByField("str");
            if (null != str && !"".equals(str)) {
                if (countMap.containsKey(str)) {
                    countMap.put(str, countMap.get(str) + 1);
                } else {
                    countMap.put(str, 1);
                }
            }

            for (Map.Entry<String, Integer> entry : countMap.entrySet()){
                System.out.println(entry.getKey() + ": " + entry.getValue());
            }

            System.out.println("---------------------------");
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }
    }


    public static void main(String[] args) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout(Spout.class.getSimpleName(), new Spout());
        topologyBuilder.setBolt(SplitBlot.class.getSimpleName(), new SplitBlot()).shuffleGrouping(Spout.class.getSimpleName());
        topologyBuilder.setBolt(CountBlot.class.getSimpleName(), new CountBlot()).shuffleGrouping(SplitBlot.class.getSimpleName());

        Config config = new Config();
        StormTopology topology = topologyBuilder.createTopology();
        String topologyName = WordCountTopology.class.getSimpleName();

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
