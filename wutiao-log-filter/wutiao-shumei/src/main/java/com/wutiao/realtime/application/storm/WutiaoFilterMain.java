package com.wutiao.realtime.application.storm;

import com.wutiao.realtime.application.storm.bolt.KafkaSenderBolt;
import com.wutiao.realtime.application.storm.bolt.PhoneFilterBolt;
import com.wutiao.realtime.application.storm.bolt.DeviceFilterBolt;
import com.wutiao.realtime.application.storm.bolt.OuidFilterBolt;
import com.wutiao.realtime.application.storm.spout.SpoutFromKafka;
import com.wutiao.realtime.application.storm.util.PropertyLoader;
import org.apache.storm.Config;
import org.apache.storm.StormSubmitter;
import org.apache.storm.generated.AlreadyAliveException;
import org.apache.storm.generated.AuthorizationException;
import org.apache.storm.generated.InvalidTopologyException;
import org.apache.storm.topology.TopologyBuilder;
import org.apache.storm.tuple.Fields;
import java.io.IOException;
import java.util.Properties;

/**
 * Wutiao日志过滤不合法的用户或者设备
 */
public class WutiaoFilterMain {

    public WutiaoFilterMain() {}

    public static void main(String[] args) throws AlreadyAliveException, AuthorizationException, InvalidTopologyException, IOException {
        TopologyBuilder builder = new TopologyBuilder();
        Config conf = new Config();

        Properties props = PropertyLoader.loadFromFile(args[0]);

        String name = props.getProperty("app.name");
        String sourceTopic = props.getProperty("source.topic");
        Properties kafkaConsumerProps = PropertyLoader.loadFromFile(props.getProperty("kafka.consumer.props.path"));

        String destinationTopic = props.getProperty("destination.topic");
        // 过滤掉的日志
        String destinationFilterTopic = props.getProperty("destination.filter.topic");
        Properties kafkaProducerProps = PropertyLoader.loadFromFile(props.getProperty("kafka.producer.props.path"));

        Properties cacheConfigProps = PropertyLoader.loadFromFile(props.getProperty("risklevel.cache.config.path"));

        Integer workNum = Integer.valueOf(props.getProperty("worker.num", "1"));
        conf.setNumWorkers(workNum);

        Integer spoutNum = Integer.valueOf(props.getProperty("spout.num", "1"));
        Integer boltNum = Integer.valueOf(props.getProperty("bolt.num", "1"));

        builder.setSpout("kafkaSpout", new SpoutFromKafka(sourceTopic, kafkaConsumerProps), spoutNum);
        builder.setBolt("phoneFilterBolt", new PhoneFilterBolt(cacheConfigProps), boltNum).fieldsGrouping("kafkaSpout", new Fields("phone"));
        builder.setBolt("didFilterBolt", new DeviceFilterBolt(cacheConfigProps), boltNum).fieldsGrouping("phoneFilterBolt", new Fields("did"));
        builder.setBolt("ouidFilterBolt", new OuidFilterBolt(cacheConfigProps), boltNum).fieldsGrouping("didFilterBolt", new Fields("ouid"));
        builder.setBolt("kafkaSenderBolt", new KafkaSenderBolt(destinationTopic, destinationFilterTopic, kafkaProducerProps)).shuffleGrouping("ouidFilterBolt");

        StormSubmitter.submitTopology(name, conf, builder.createTopology());
    }

}
