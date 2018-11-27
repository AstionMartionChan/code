package com.kingnetdc.blueberry.kafka.base;


/**
 * @author jake.zhang <zhangxj@kingnet.com>
 */
public class Constants {

    /**
     * kafka 消费客户端保存到 zookeeper 中的默认的路径
     */
    public static final String DEFAULT_KAFKA_CONSUMER_SAVED_ZK_PATH = "/consumers/${group}/offsets/${topic}/${partition}";

    /**
     * kafka producer 连接池中的个数
     */
    public static final int DEFAULT_KAFKA_PRODUCER_POOL_NUM = 3;


    /**
     * Kafka 发送参数 发送的 Key 的序列化类
     */
    public static final String KAFKA_KEY_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    /**
     * Kafka 发送参数 发送的 Value 的序列化类
     */
    public static final String KAFKA_VALUE_SERIALIZER = "org.apache.kafka.common.serialization.StringSerializer";

    /**
     * 发送的确认方式，默认使用 all，即 kafka 全部写入成功则认为写成功。Kafka broker 需要设置复制因子为 2
     */
    public static final String KAFKA_ACKS = "all";

    /**
     * Kafka 发送端缓存大小，单位为字节，默认大小为 32M，把客户端调整到 64M。总的客户端使用内存是主备集群的数量 16 * 64MB = 1024MB
     */
    public static final long KAFKA_BUFFER_MEMORY = 67108864L;

    /**
     * Kafka 发送端的压缩方式，默认使用 snappy 压缩, 压缩速度比 gzip 快，压缩比比 gzip 小
     */
    public static final String KAFKA_COMPRESSION_TYPE = "snappy";

    /**
     * 发送的重试次数
     */
    public static final int KAFKA_RETRIES = 1;

    /**
     * 每次批量发送的字节大小，单位字节，默认是16 KB
     */
    public static final int KAFKA_BATCH_SIZE = 16384;

    /**
     * 客户端的发送延迟，可以理解为在大数据量的时候，累计到5毫秒批量发，默认是0毫秒延迟
     */
    public static final long KAFKA_LINGER_MS = 5L;

    /**
     * 最大的阻塞时间，单位毫秒，60000L 默认60s，这个会导致在连接到集群有问题是，放大等待的事件，设置为3s，不会拦截很多请求。
     */
    public static final long KAFKA_MAX_BLOCK_MS = 3000L;

    /**
     * 批量发送最大大小，单位字节，默认是1M
     */
    public static final int KAFKA_MAX_REQUEST_SIZE = 1048576;

    /**
     * 分区类名，默认使用
     */
    public static final String KAFKA_PARTITIONER_CLASS = "org.apache.kafka.clients.producer.internals.DefaultPartitioner";

    /**
     * 客户端发送超时时间，默认是 30000，单位是毫秒，30秒，发送超时时间设置为3秒
     * 高并发的场景在异步的发送的业务逻辑，不需要考虑发送超时设置
     */
    public static final int KAFKA_REQUEST_TIMEOUT_MS = 30000;

    /**
     * 消息发送失败的重试时间，单位是毫秒，默认是100ms
     */
    public static final long KAFKA_RETRY_BACKOFF_MS = 100L;

}
