package com.cfy.storm.topology;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.StormSubmitter;
import backtype.storm.generated.AlreadyAliveException;
import backtype.storm.generated.InvalidTopologyException;
import backtype.storm.generated.StormTopology;
import backtype.storm.topology.TopologyBuilder;
import com.cfy.storm.bolt.ParseLogBolt;
import com.cfy.storm.bolt.StatSpiderSuccessBolt;
import storm.kafka.BrokerHosts;
import storm.kafka.KafkaSpout;
import storm.kafka.SpoutConfig;
import storm.kafka.ZkHosts;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/13
 * Time: 20:47
 * Work contact: Astion_Leo@163.com
 */


public class SpiderTopology {

    private static final String SPIDER_TOPOLOGY = SpiderTopology.class.getSimpleName();
    private static final String KAFKA_SPOUT = KafkaSpout.class.getSimpleName();
    private static final String PARSE_LOG_BOLT = ParseLogBolt.class.getSimpleName();
    private static final String STAT_SPIDER_SUCCESS_BOLT = StatSpiderSuccessBolt.class.getSimpleName();

    private static final String TOPIC = "storm-kafka-spider";

    public static void main(String[] args) {
        TopologyBuilder topologyBuilder = new TopologyBuilder();

        BrokerHosts hosts = new ZkHosts("leochan:2181,leochan1:2181,leochan2:2181");
        String zkRoot = "/kafkaSpout";
        String id = "cfy";
        SpoutConfig spoutConfig = new SpoutConfig(hosts, TOPIC, zkRoot, id);

        topologyBuilder.setSpout(KAFKA_SPOUT, new KafkaSpout(spoutConfig));
        topologyBuilder.setBolt(PARSE_LOG_BOLT, new ParseLogBolt()).shuffleGrouping(KAFKA_SPOUT);
        topologyBuilder.setBolt(STAT_SPIDER_SUCCESS_BOLT, new StatSpiderSuccessBolt()).shuffleGrouping(PARSE_LOG_BOLT);



        Config config = new Config();
        StormTopology topology = topologyBuilder.createTopology();

        if (args.length == 0){
            LocalCluster localCluster = new LocalCluster();
            localCluster.submitTopology(SPIDER_TOPOLOGY, config, topology);

        } else {
            try {
                StormSubmitter.submitTopology(SPIDER_TOPOLOGY, config, topology);
            } catch (AlreadyAliveException e) {
                e.printStackTrace();
            } catch (InvalidTopologyException e) {
                e.printStackTrace();
            }
        }

    }
}
