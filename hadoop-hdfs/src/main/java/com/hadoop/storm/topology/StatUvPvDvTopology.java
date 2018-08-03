package com.hadoop.storm.topology;

import backtype.storm.Config;
import backtype.storm.Constants;
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
import backtype.storm.utils.Utils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/5/30
 * Time: 21:06
 * Work contact: Astion_Leo@163.com
 */


public class StatUvPvDvTopology {

    public static class ReadSpout extends BaseRichSpout {

        Map conf;
        TopologyContext context;
        SpoutOutputCollector collector;
        BufferedReader bufferedReader;

        @Override
        public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
            this.conf = conf;
            this.context = context;
            this.collector = collector;
            try {
                this.bufferedReader = new BufferedReader(new FileReader("d:\\access_copy.txt"));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void nextTuple() {
            try {
                String line = bufferedReader.readLine();
                if (null != line){
                    collector.emit(new Values(line), line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("line"));
        }

        @Override
        public void ack(Object msgId) {
            System.out.println("处理成功：" + msgId);
        }

        @Override
        public void fail(Object msgId) {
            System.err.println("处理失败：" + msgId);
        }
    }

    public static class SplitBolt extends BaseRichBolt {

        Map stormConf;
        TopologyContext context;
        OutputCollector collector;

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.stormConf = stormConf;
            this.context = context;
            this.collector = collector;
        }

        @Override
        public void execute(Tuple input) {
            try {
                String line = input.getStringByField("line");
                if (StringUtils.isNotBlank(line)){
                    String[] splited = line.split(" ");
                    collector.emit(input, new Values(splited[6], splited[0]));
                }
                collector.ack(input);
            } catch (Exception e) {
                e.printStackTrace();
                collector.fail(input);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("url", "ip"));
        }
    }


    public static class LocalStatBolt extends BaseRichBolt {

        Map stormConf;
        TopologyContext context;
        OutputCollector collector;
        Map<String, Integer> pvMap = new HashMap();
        Map<String, HashSet<String>> uvMap = new HashMap<>();

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.stormConf = stormConf;
            this.context = context;
            this.collector = collector;
        }

        @Override
        public void execute(Tuple input) {
            try {
                String url = input.getStringByField("url");
                String ip = input.getStringByField("ip");

                // 处理PV
                if (pvMap.containsKey(url)){
                    pvMap.put(url, (pvMap.get(url) + 1));
                } else {
                    pvMap.put(url, 1);
                }

                // 处理UV
                if (uvMap.containsKey(url)){
                    HashSet<String> set = uvMap.get(url);
                    set.add(ip);
                    uvMap.put(url, set);
                } else {
                    HashSet<String> set = new HashSet<>();
                    set.add(ip);
                    uvMap.put(url, set);
                }

//                System.out.println("Task" + context.getThisTaskId());
                for (String key : pvMap.keySet()) {
//                    System.out.println(key);
//                    System.out.println("UV：" + uvMap.get(key).size());
//                    System.out.println("PV：" + pvMap.get(key));
                    collector.emit(new Values(key, new Integer[] {uvMap.get(key).size(), pvMap.get(key)}));

                }

//                System.out.println("----------------------");
//                Utils.sleep(1000);
                collector.ack(input);
            } catch (Exception e) {
                e.printStackTrace();
                collector.fail(input);
            }
        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {
            declarer.declare(new Fields("url", "UV,PV"));
        }
    }


    public static class GlobalStatBolt extends BaseRichBolt {

        OutputCollector collector;
        Map<String, Integer[]> globalStatMap = new HashMap<>();
        Connection connection;
        PreparedStatement preparedStatement;
        private static final String QUERY_SQL = "select 1 from t_url_stat where url = ?";
        private static final String INSERT_SQL = "insert into t_url_stat (url,uv,pv) values(?,?,?)";
        private static final String UPDATE_SQL = "update t_url_stat set uv=?,pv=?,update_time=DATE_FORMAT(NOW(), '%Y-%m-%d %h:%i:%s') where url=?";

        @Override
        public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
            this.collector = collector;
            try {
                Class.forName("com.mysql.jdbc.Driver");
                this.connection = DriverManager.getConnection("jdbc:mysql://47.98.46.234:3306/spider?useUnicode=true&characterEncoding=utf-8", "root", "123456");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void execute(Tuple input) {
            try {
                if (input.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)){
                    for (Map.Entry<String, Integer[]> entry : globalStatMap.entrySet()){
                        preparedStatement = connection.prepareStatement(QUERY_SQL);
                        preparedStatement.setString(1, entry.getKey());
                        ResultSet resultSet = preparedStatement.executeQuery();
                        if (resultSet.next()){
                            // 更新UV,PV
                            preparedStatement = connection.prepareStatement(UPDATE_SQL);
                            preparedStatement.setInt(1, entry.getValue()[0]);
                            preparedStatement.setInt(2, entry.getValue()[1]);
                            preparedStatement.setString(3, entry.getKey());
                            preparedStatement.execute();
                        } else {
                            // 插入UV,PV
                            preparedStatement = connection.prepareStatement(INSERT_SQL);
                            preparedStatement.setString(1, entry.getKey());
                            preparedStatement.setInt(2, entry.getValue()[0]);
                            preparedStatement.setInt(3, entry.getValue()[1]);
                            preparedStatement.execute();
                        }
                    }
                    preparedStatement.close();
                } else {
                    String url = input.getStringByField("url");
                    Integer[] stats = (Integer[]) input.getValueByField("UV,PV");
                    globalStatMap.put(url, stats);
                }
                collector.ack(input);
            } catch (Exception e) {
                e.printStackTrace();
                collector.fail(input);
            }

        }

        @Override
        public void declareOutputFields(OutputFieldsDeclarer declarer) {

        }

        @Override
        public Map<String, Object> getComponentConfiguration() {
            Map<String, Object> map = new HashMap<>();
            map.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 5);
            return map;
        }
    }

    public static void main(String[] args) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();
        topologyBuilder.setSpout("ReadSpout", new ReadSpout());
        topologyBuilder.setBolt("SplitBlot", new SplitBolt(), 3).shuffleGrouping("ReadSpout");
        topologyBuilder.setBolt("LocalStatBolt", new LocalStatBolt(), 3).fieldsGrouping("SplitBlot", new Fields("url"));
        topologyBuilder.setBolt("GlobalStatBolt", new GlobalStatBolt()).globalGrouping("LocalStatBolt");

        Config config = new Config();

        StormTopology topology = topologyBuilder.createTopology();
        String topologyName = StatUvPvDvTopology.class.getSimpleName();

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
