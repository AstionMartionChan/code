package com.wutiao.realtime.application.storm.bolt;

import com.wutiao.realtime.application.storm.model.UserIdentifier;
import com.wutiao.realtime.application.storm.model.WtLog;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * @author zhouml 13/09/2018.
 */
public class OuidFilterBolt extends AbstractFilterBolt {

    private static Logger logger = LoggerFactory.getLogger(OuidFilterBolt.class);

    public OuidFilterBolt(Properties props) {
        super(props);
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
        WtLog wtLog = (WtLog) input.getValueByField("wtLog");
        if (wtLog.getShumei_flag()) {
            //设置ouid判断标识
            setRiskLevelByIdentifier(wtLog, UserIdentifier.OUID);
        } else {
            // 非数美日志
            if (StringUtils.isNotBlank(wtLog.getRiskLevel())) {
                //已经有判断标识，则更新OUID的判断标识
                setRiskLevelByIdentifier(wtLog, UserIdentifier.OUID);
            } else {
                //没有判断标识，根据OUID取得判断标识
                wtLog.setRiskLevel(getRiskLevelByIdentifier(wtLog, UserIdentifier.OUID));
            }
            collector.emit(new Values(wtLog));

        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("wtLog"));
    }

}
