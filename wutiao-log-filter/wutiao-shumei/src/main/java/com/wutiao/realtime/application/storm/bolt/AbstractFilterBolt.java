package com.wutiao.realtime.application.storm.bolt;

import com.kingnetdc.blueberry.cache.KdcCache;
import com.kingnetdc.blueberry.cache.base.Tuple3;
import com.wutiao.realtime.application.storm.model.UserIdentifier;
import com.wutiao.realtime.application.storm.model.WtLog;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.base.BaseBasicBolt;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Random;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author zhouml 13/09/2018.
 */
public abstract class AbstractFilterBolt extends BaseBasicBolt {

    public static Logger logger = LoggerFactory.getLogger(AbstractFilterBolt.class);

    protected Properties props;

    protected Long lastFlushAt = System.currentTimeMillis();

    protected Map<String, String> riskLevelBuffer = new HashMap<>();

    protected KdcCache kdcCache = null;

    private String ABNORMAL_VALUE = "-1";

    private int VALID_PHONE_LEN = 11;

    @Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
        initKdcCache();
    }

    public AbstractFilterBolt(Properties props) {
        this.props = props;
    }

    /**
     *  prefix wt:shumei-risklevel:prod --> 24
     *  phone 11 --> 1千万 * (24 + 11) ---> 333m
     *  ouid 7 --> 1千万 * (24 + 7) ---> 295m
     *  did 36 --> 1千万 * (24 + 36) ---> 572m
     * @param key
     * @return
     */
    protected String buildKey(String key) {
        String cachePrefix = props.getProperty("risklevel.cache.config.prefix");

        if (StringUtils.isNotBlank(cachePrefix)) {
            return cachePrefix + key;
        } else {
            return key;
        }
    }

    protected void initKdcCache() {
        String cacheConfigPath = props.getProperty("risklevel.cache.config");
        logger.info("Initializing KdcCache");
        kdcCache = KdcCache.builder(getClass().getClassLoader().getResourceAsStream(cacheConfigPath));
    }

    /**
     * @param forcible 是否强制刷新
     */
    protected void tryFlushBuffer(boolean forcible) {
        // 默认达到2000了刷新一次
        Integer capacity = Integer.valueOf(props.getProperty("risklevel.buffer.capacity", "2000"));
        // 默认2min刷新一次(millis)
        Long flushInterval = Long.valueOf(props.getProperty("risklevel.buffer.flush.interval", "120000"));
        // Cache默认1天过期(second)
        Integer cacheExpireSeconds = Integer.valueOf(props.getProperty("risklevel.cache.expire", "86400"));
        // 随机加入的过期时间Seed(second), 默认1小时, 防止缓存雪崩
        Integer cacheExpireRandomSeed = Integer.valueOf(props.getProperty("risklevel.cache.random.expire.seed", "3600"));

        Long current = System.currentTimeMillis();

        if (riskLevelBuffer.size() > capacity || (current - lastFlushAt >= flushInterval) || forcible) {
            if (!riskLevelBuffer.isEmpty()) {
                Integer finalCacheExpire = cacheExpireSeconds + new Random().nextInt(cacheExpireRandomSeed);

                Collection<Tuple3> result =
                    riskLevelBuffer.entrySet().stream().map((Map.Entry<String, String> keyValue) ->
                        new Tuple3(
                            keyValue.getKey(), keyValue.getValue(), finalCacheExpire
                        )
                    ).collect(Collectors.toList());

                kdcCache.multiSet(result);

                logger.info("Flush risk level buffer");
                riskLevelBuffer.clear();
                lastFlushAt = current;
            }
        } else {}
    }

    /**
     * @param wtLog 设置phone|user|device的状态, PASS | REJECT, 设置之前确认是否在缓存中以及是否为正常值
     */
    protected void setRiskLevelByIdentifier(WtLog wtLog, UserIdentifier identifier) {
        String riskLevel = wtLog.getRiskLevel();

        if (StringUtils.isNotBlank(riskLevel)) {
            if (StringUtils.isBlank(getRiskLevelByIdentifier(wtLog, identifier))) {
                switch (identifier) {
                    case PHONE:
                        String phone = wtLog.getPhone();
                        if (StringUtils.isNotBlank(phone) && phone.length() == VALID_PHONE_LEN) {
                            riskLevelBuffer.put(buildKey(phone), riskLevel);
                        }
                        break;
                    case OUID:
                        String ouid = wtLog.getOuid();
                        if (StringUtils.isNotBlank(ouid) && !ABNORMAL_VALUE.equals(ouid)) {
                            riskLevelBuffer.put(buildKey(ouid), riskLevel);
                        }
                        break;
                    case DID:
                        String did = wtLog.getDid();
                        if (StringUtils.isNotBlank(did) && !ABNORMAL_VALUE.equals(did)) {
                            riskLevelBuffer.put(buildKey(did), riskLevel);
                        }
                        break;
                    default:
                        logger.warn(String.format("Not valid identifier {}", identifier.name()));
                        break;
                }
            }
        }

        tryFlushBuffer(false);
    }

    /**
     *  根据WtLog中的相应的属性去取值, 需要进行判断, 避免异常值("", -1)等落入Redis, MySQL中查询
     * @param wtLog
     * @param identifier
     * @return
     */
    protected String getRiskLevelByIdentifier(WtLog wtLog, UserIdentifier identifier) {
        String result = null;
        switch (identifier) {
            case PHONE:
                String phone = wtLog.getPhone();
                if (StringUtils.isNotBlank(phone) && phone.length() == VALID_PHONE_LEN) {
                    String prefixedPhone = buildKey(wtLog.getPhone());
                    result = riskLevelBuffer.getOrDefault(prefixedPhone, kdcCache.get(prefixedPhone));
                }
                break;
            case OUID:
                String ouid = wtLog.getOuid();
                if (StringUtils.isNotBlank(ouid) && !ABNORMAL_VALUE.equals(ouid)) {
                    String prefixedOuid = buildKey(ouid);
                    result = riskLevelBuffer.getOrDefault(prefixedOuid, kdcCache.get(prefixedOuid));
                }
                break;
            case DID:
                String did = wtLog.getDid();
                if (StringUtils.isNotBlank(did) && !ABNORMAL_VALUE.equals(did)) {
                    String prefixedDid = buildKey(did);
                    result = riskLevelBuffer.getOrDefault(prefixedDid, kdcCache.get(prefixedDid));
                }
                break;
            default:
                logger.warn(String.format("Not valid identifier {}", identifier.name()));
                break;
        }
        return result;
    }

    @Override
    public void cleanup() {
        if (kdcCache != null) {
            // 关闭强制进行刷新, 忽略buffer size以及interval
            tryFlushBuffer(true);
            logger.info("Closing KdcCache");
            kdcCache.close();
        }
    }

}
