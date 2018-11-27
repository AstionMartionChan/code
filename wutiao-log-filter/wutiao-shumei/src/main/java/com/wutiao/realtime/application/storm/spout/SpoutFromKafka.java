package com.wutiao.realtime.application.storm.spout;

import com.alibaba.fastjson.JSONObject;
import com.wutiao.realtime.application.storm.model.WtLog;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.spout.SpoutOutputCollector;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.topology.base.BaseRichSpout;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.Random;

import static java.util.Objects.requireNonNull;

/**
 * @author zhouml 13/09/2018
 */
public class SpoutFromKafka extends BaseRichSpout {

    public static Logger logger = LoggerFactory.getLogger(SpoutFromKafka.class);

    private String topics;
    private Properties kafkaConsumerProperties;
    private SpoutOutputCollector collector;
    private KafkaConsumer<String, String> consumer;
    private long pool_timeout;
    private long offset;
    private int partition;
    private String topic;

    private List<String> whiteListEvents;

    public SpoutFromKafka(String topic, Properties kafkaConsumerProperties) {
        this.topics = topic;
        this.kafkaConsumerProperties = kafkaConsumerProperties;
        pool_timeout = Long.parseLong(kafkaConsumerProperties.getProperty("consumer.pool.timeout", "100"));
        whiteListEvents = getWhiteListEvents();
    }

    @Override
    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        consumer = new KafkaConsumer<>(kafkaConsumerProperties);
        consumer.subscribe(Arrays.asList(requireNonNull(topics, "topics is null").split(",")));
        this.collector = collector;
    }

    public List<String> getWhiteListEvents() {
        String eventWhiteList = kafkaConsumerProperties.getProperty("event.whitelist");
        requireNonNull(eventWhiteList, "Event whitelist should not be null");

        List<String> events = Arrays.asList(eventWhiteList.split(","));
        return events;
    }

    public WtLog parse(String log, List<String> whiteListEvents) {
        JSONObject jsonObject = JSONObject.parseObject(log);

        String event = jsonObject.getString("event");

        if (whiteListEvents.contains(event)) {
            WtLog wtLog = new WtLog();
            JSONObject propsObject = jsonObject.getJSONObject("properties");

            wtLog.setLog(log);
            wtLog.setEvent(event);

            String did = Objects.toString(jsonObject.getString("did"), "");
            wtLog.setDid(did);

            String riskLevel = Objects.toString(propsObject.getString("riskLevel"), "");
            wtLog.setRiskLevel(riskLevel);

            // 数美日志的ouid实际上就是phone, 其它日志直接从properties下面的phone取值
            if (
                event.equals("shumei_login") || event.equals("shumei_register") ||
                event.equals("shumei_fission") || event.equals("shumei_withdraw")
            ) {
                String phone = Objects.toString(jsonObject.getString("ouid"), "");
                wtLog.setPhone(phone);
                wtLog.setShumei_flag(true);
                wtLog.setOuid("");
            } else {
                String phone = Objects.toString(propsObject.getString("phone"), "");
                wtLog.setPhone(phone);
                String ouid = Objects.toString(jsonObject.getString("ouid"), "");
                wtLog.setOuid(ouid);
                wtLog.setShumei_flag(false);
            }

            // 由于phone为""的日志可能会比较多, 所以进行随机设置, 以缓解数据倾斜; 随机生成1000以内的数字
            if (StringUtils.isBlank(wtLog.getPhone())) {
                Integer randomPhoneSeed = Integer.valueOf(kafkaConsumerProperties.getProperty("phone.random.seed", "1000"));
                Integer generatedFakeRandomPhone = new Random().nextInt(randomPhoneSeed);
                wtLog.setPhone(generatedFakeRandomPhone.toString());
            }

            return wtLog;
        }

        return null;
    }

    @Override
    public void nextTuple() {
        try {
            ConsumerRecords<String, String> records = this.consumer.poll(pool_timeout);

            if (records.count() > 0) {
                logger.info("SpoutFromKafka start process , records size =  " + records.count());
            }

            // 获取遍历kafka数据集
            for (ConsumerRecord<String, String> record : records) {
                String log = record.value();
                offset = record.offset();
                partition = record.partition();
                topic = record.topic();

                // log不为空，下发日志
                if (StringUtils.isNotEmpty(log)) {
                    logger.info(String.format("send message = %s ", log));

                    WtLog wtLog = parse(log, whiteListEvents);

                    // 过滤指定日志发送到下游
                    if (wtLog != null) {
                        this.collector.emit(new Values(wtLog.getPhone(), wtLog));
                    }
                } else {
                    logger.warn("SpoutFromKafka get log empty ，topic=" + record.topic() + ",partition=" + record.partition() + ",offset=" + record.offset());
                }
            }
            // 取到日志 , 提交offset
            if (records != null && records.count() > 0) {
                logger.info("kafka spout submit topic={}, partition={}, offset={}", topic, partition, offset);
                this.consumer.commitSync();
            } else {
                Thread.sleep(100);
            }
        } catch (Throwable t) {
            logger.error("SpoutFromKafka read kafka failed ", t);
        }
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("phone", "wtLog"));
    }

    @Override
    public void close() {
        if (consumer != null) {
            logger.info("Closing kafka consumer");
            consumer.close();
        }
    }

}
