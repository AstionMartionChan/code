package com.wutiao.realtime.application.storm.bolt;

import com.wutiao.realtime.application.storm.model.WtLog;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseBasicBolt;
import org.apache.storm.tuple.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

/**
 * @author yangjun on 15/09/2018.
 */
public class KafkaSenderBolt extends BaseBasicBolt {

    public static Logger logger = LoggerFactory.getLogger(KafkaSenderBolt.class);

    private Properties properties;
    private KafkaProducer<String, String> producer;

    private String topic;
    private long printTime = 0;
    //被过滤的数据
    private String filterTopic;

    public KafkaSenderBolt(String topic, String filterTopic, Properties properties) {
        this.topic = topic;
        this.filterTopic = filterTopic;
        this.properties = properties;
    }

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        Properties props = new Properties();
        props.put("bootstrap.servers", properties.get("bootstrap.servers"));
        props.put("key.serializer", properties.getOrDefault("key.serializer", "org.apache.kafka.common.serialization.StringSerializer"));
        props.put("value.serializer", properties.getOrDefault("value.serializer", "org.apache.kafka.common.serialization.StringSerializer"));
        props.put("acks", properties.getOrDefault("acks", "all"));
        props.put("buffer.memory", properties.getOrDefault("buffer.memory", 67108864L));
        props.put("compression.type", properties.getOrDefault("compression.type", "gzip"));
        props.put("retries", properties.getOrDefault("retries", 1));
        props.put("batch.size", properties.getOrDefault("batch.size", 16384));
        props.put("linger.ms", properties.getOrDefault("linger.ms", 20L));
        props.put("max.block.ms", properties.getOrDefault("max.block.ms", 3000L));
        props.put("max.request.size", properties.getOrDefault("max.request.size", 1048576));
        props.put("partitioner.class", properties.getOrDefault("partitioner.class", "org.apache.kafka.clients.producer.internals.DefaultPartitioner"));
        props.put("request.timeout.ms", properties.getOrDefault("request.timeout.ms", 30000));
        props.put("retry.backoff.ms", properties.getOrDefault("retry.backoff.ms", 100L));
        producer = new KafkaProducer<>(props);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        WtLog wtLog = (WtLog) input.getValueByField("wtLog");
        try {
            if (wtLog != null) {
                if (this.printTime <= System.currentTimeMillis() - 30 * 1000) {
                    this.printTime = System.currentTimeMillis();
                    logger.info("send data. topic=" + topic + " log=" + wtLog.getLog());
                }

				ProducerRecord<String, String> record;
				if (StringUtils.isNotBlank(wtLog.getRiskLevel()) && "REJECT".equals(wtLog.getRiskLevel().toUpperCase())) {
                    record = new ProducerRecord<>(filterTopic, wtLog.getLog());
				} else {
                    record = new ProducerRecord<>(topic, wtLog.getLog());
				}

                producer.send(record);
            } else {
                logger.warn("wtLog is empty.");
            }
        } catch (Throwable t) {
            logger.error("send log to kafka error，log=" + wtLog.toString(), t);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer outputFieldsDeclarer) {}

}
