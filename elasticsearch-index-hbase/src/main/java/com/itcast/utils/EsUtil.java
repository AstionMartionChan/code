package com.itcast.utils;

import com.alibaba.fastjson.JSONObject;
import com.itcast.bean.JDSkuInfo;
import org.elasticsearch.ElasticsearchException;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/24
 * Time: 21:24
 * Work contact: Astion_Leo@163.com
 */


public class EsUtil {

    public static TransportClient getTransportClient() {
        Settings setting = ImmutableSettings
                .settingsBuilder()
                .put("cluster.name", "elasticsearch") // 指定集群名称
//                .put("client.transport.sniff", true)  // 开启集群嗅探功能，可以自动发现集群内的所有节点信息
                .build();

        // 创建客户端连接
        TransportClient transportClient = new TransportClient(setting);
        // 设置节点信息
        TransportAddress transportAddress = new InetSocketTransportAddress("leochan3", 9300);
        // 添加节点信息
        transportClient.addTransportAddress(transportAddress);


        return transportClient;
    }



    public static void addIndex(String index, String document, String id, Object source) {
        TransportClient transportClient = null;
        try {
            transportClient = getTransportClient();
            transportClient.prepareIndex(index, document, id).setSource(JSONObject.toJSONString(source)).get();
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        } finally {
            transportClient.close();
        }
    }


    public static Map<String, Object> getIndex(String index, String document, String id) {
        TransportClient transportClient = null;
        try {
            transportClient = getTransportClient();
            GetResponse response = transportClient.prepareGet(index, document, id).get();
            Map<String, Object> source = response.getSource();
            return source;
        } catch (ElasticsearchException e) {
            e.printStackTrace();
        } finally {
            transportClient.close();
        }
        return null;
    }


    public static void updateIndex(String index, String document, String id, Object source) {
        TransportClient transportClient = null;
        try {
            transportClient = getTransportClient();
            transportClient.prepareUpdate(index, document, id);
            transportClient.prepareUpdate().setSource(JSONObject.toJSONString(source).getBytes()).get();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            transportClient.close();
        }
    }


    public static<T> List<T> search(String index, String document, String searchContent, Class<T> clz) {
        TransportClient transportClient = getTransportClient();
        SearchRequestBuilder builder = transportClient.prepareSearch(index)
                .setTypes(document)
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setFrom(0)
                .setSize(20)
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", searchContent)));

        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        long count = hits.getTotalHits();
        SearchHit[] searchHits = hits.getHits();
        List<T> resultList = new ArrayList<>(searchHits.length);

        for (SearchHit hit : searchHits){
            String json = hit.getSourceAsString();
            T t = JSONObject.parseObject(json, clz);
            resultList.add(t);
        }
        return resultList;
    }




}
