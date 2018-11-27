package com.wutiao.realtime.application.storm.bolt;

import com.wutiao.realtime.application.storm.model.UserIdentifier;
import com.wutiao.realtime.application.storm.model.WtLog;
import org.apache.storm.task.TopologyContext;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Map;
import java.util.Properties;

/**
 * @author zhouml 13/09/2018.
 */
public class PhoneFilterBolt extends AbstractFilterBolt {

	public PhoneFilterBolt(Properties props) {
		super(props);
	}

	private int boltId;

	@Override
    public void prepare(Map stormConf, TopologyContext context) {
        super.prepare(stormConf, context);
		this.boltId = context.getThisTaskId();
    }

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		WtLog wtLog = (WtLog) input.getValueByField("wtLog");

		if (wtLog.getShumei_flag()) {
			// 存储RiskLevel -- 确保phone是正常的
			setRiskLevelByIdentifier(wtLog, UserIdentifier.PHONE);
		} else {
			// 非数美日志根据手机号取得判断标识
			wtLog.setRiskLevel(getRiskLevelByIdentifier(wtLog, UserIdentifier.PHONE));
		}
		collector.emit(new Values(wtLog.getDid(), wtLog));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("did", "wtLog"));
	}

}
