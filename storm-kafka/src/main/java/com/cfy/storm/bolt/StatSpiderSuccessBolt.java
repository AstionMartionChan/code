package com.cfy.storm.bolt;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.cfy.storm.utils.MySqlUtil;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/15
 * Time: 16:34
 * Work contact: Astion_Leo@163.com
 */


public class StatSpiderSuccessBolt extends BaseRichBolt {

    private Map stormConf;
    private TopologyContext context;
    private OutputCollector collector;
    Connection connection;
    PreparedStatement preparedStatement;
    private static final String QUERY_SQL = "select * from t_spider_capacity_stat where flag = ? and time = ?";
    private static final String INSERT_SQL = "insert into t_spider_capacity_stat (flag,count,time) values(?,?,?)";
    private static final String UPDATE_SQL = "update t_spider_capacity_stat set count=? where flag=? and time = ?";

    private Map<String, Integer> statMap = new HashMap<>();


    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.stormConf = stormConf;
        this.context = context;
        this.collector = collector;

    }

    @Override
    public void execute(Tuple input) {

        if (input.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID)){
            String date = parseDate();
            try {
                connection = MySqlUtil.getConnection();
                for (Map.Entry<String, Integer> entry : statMap.entrySet()) {
                    preparedStatement = this.connection.prepareStatement(QUERY_SQL);
                    preparedStatement.setString(1, entry.getKey());
                    preparedStatement.setString(2, date);
                    ResultSet resultSet = preparedStatement.executeQuery();
                    if (resultSet.next()) {
                        int count = resultSet.getInt("count");
                        preparedStatement = this.connection.prepareStatement(UPDATE_SQL);
                        preparedStatement.setInt(1, entry.getValue() + count);
                        preparedStatement.setString(2, entry.getKey());
                        preparedStatement.setString(3, date);
                        preparedStatement.execute();
                    } else {
                        preparedStatement = this.connection.prepareStatement(INSERT_SQL);
                        preparedStatement.setString(1, entry.getKey());
                        preparedStatement.setInt(2, entry.getValue());
                        preparedStatement.setString(3, date);
                        preparedStatement.execute();
                    }

                    statMap.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null){
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }

        } else {
            String flag = input.getStringByField("flag");
            Integer count = statMap.get(flag);
            if (null == count){
                count = 0;
            }
            count ++;
            statMap.put(flag, count);
        }

    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {

    }

    @Override
    public Map<String, Object> getComponentConfiguration() {
        Map<String, Object> map = new HashMap<>();
        map.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, 10);
        return map;
    }

    private String parseDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        java.util.Date date = new java.util.Date();
        String result = format.format(date);
        return result;
    }
}
