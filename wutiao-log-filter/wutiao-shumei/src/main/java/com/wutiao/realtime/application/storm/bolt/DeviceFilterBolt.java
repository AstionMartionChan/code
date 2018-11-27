package com.wutiao.realtime.application.storm.bolt;

import com.wutiao.realtime.application.storm.model.UserIdentifier;
import com.wutiao.realtime.application.storm.model.WtLog;
import org.apache.storm.shade.org.apache.commons.lang.StringUtils;
import org.apache.storm.topology.BasicOutputCollector;
import org.apache.storm.topology.OutputFieldsDeclarer;
import org.apache.storm.tuple.Fields;
import org.apache.storm.tuple.Tuple;
import org.apache.storm.tuple.Values;

import java.util.Properties;

/**
 * @author zhouml 13/09/2018.
 */
public class DeviceFilterBolt extends AbstractFilterBolt {

	public DeviceFilterBolt(Properties props) {
		super(props);
	}

	@Override
	public void execute(Tuple input, BasicOutputCollector collector) {
		WtLog wtLog = (WtLog) input.getValueByField("wtLog");
		if (wtLog.getShumei_flag()) {
			// 设置设备ID判断标识
			setRiskLevelByIdentifier(wtLog, UserIdentifier.DID);
		} else {
			// 非数美日志
			if (StringUtils.isNotBlank(wtLog.getRiskLevel())) {
				// 已经有判断标识，则更新设备ID的判断标识
				setRiskLevelByIdentifier(wtLog, UserIdentifier.DID);
			} else {
				//没有判断标识，根据设备取得判断标识
				wtLog.setRiskLevel(getRiskLevelByIdentifier(wtLog, UserIdentifier.DID));
			}
		}
		collector.emit(new Values(wtLog.getOuid(), wtLog));
	}

	@Override
	public void declareOutputFields(OutputFieldsDeclarer declarer) {
		declarer.declare(new Fields("ouid", "wtLog"));
	}

}
