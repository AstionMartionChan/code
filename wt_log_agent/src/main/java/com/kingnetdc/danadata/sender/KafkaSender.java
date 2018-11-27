package com.kingnetdc.danadata.sender;

import com.alibaba.fastjson.JSONObject;
import com.kingnetdc.danadata.util.Common;
import org.apache.kafka.clients.producer.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class KafkaSender extends Sender {

    private String topic = "";
    //保存读取文件offset信息的文件名
    private String write_offset_info_file_name;
    // kafka producer 配置参数
    private Properties props = new Properties();

    private final static Logger logger = LoggerFactory.getLogger(KafkaSender.class);


    @Override
    public boolean configure(Map<String, String> params) {

        topic = params.get("topic");
        write_offset_info_file_name = params.get("offsetFileName");

        //TODO 暂时写死
        props.put("bootstrap.servers", "172.16.32.24:9092");
        props.put("acks", "all");
        props.put("retries", 0);
        props.put("batch.size", 16384);
        props.put("linger.ms", 1);
        props.put("buffer.memory", 33554432);
        props.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        props.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");

        return true;
    }

    @Override
    public boolean send(List<String> data, Map<String, String> params) {

        // 往Kafka里写入数据
        Producer<String, String> producer = new KafkaProducer<>(props);
        for (String d : data){
            ProducerRecord<String, String> msg = new ProducerRecord<>(topic, d);
            producer.send(msg, new Callback() {
                @Override
                public void onCompletion(RecordMetadata recordMetadata, Exception e) {
                    logger.info("数据存入Kafka {} {} {}", recordMetadata.topic(), recordMetadata.partition(), recordMetadata.offset());
                }
            });
        }

        producer.close();

        //发送完毕写入offset
        writeOffset(params);

        return true;
    }

    @Override
    public boolean close() {
        return true;
    }

    //将offset写入文件
    private void writeOffset(Map<String, String> params){
        String fileName = params.get(Common.file_name_key);
        String offset = params.get(Common.offset_key);
        JSONObject json = new JSONObject();
        json.put("fileName", fileName);
        json.put("offset", offset);
        String jsonStr = json.toJSONString();
        OutputStreamWriter osw = null;
        try {
            File f = new File("./config/" + write_offset_info_file_name);
            FileOutputStream fos = new FileOutputStream(f);
            osw = new OutputStreamWriter(fos);
            osw.write(jsonStr);
        } catch (Exception e) {
            logger.error("写入offset失败，参数：" + jsonStr, e);
        } finally {
            if(null != osw) {
                try {
                    osw.close();
                } catch (Exception e) {
                    logger.error("关闭流失败", e);
                }
            }
        }
    }
}
