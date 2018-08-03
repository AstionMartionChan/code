package com.hadoop.elasticsearch;

import org.elasticsearch.action.bulk.BulkRequestBuilder;
import org.elasticsearch.action.bulk.BulkResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.search.SearchType;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.cluster.node.DiscoveryNode;
import org.elasticsearch.common.collect.ImmutableList;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.common.xcontent.XContent;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/20
 * Time: 16:00
 * Work contact: Astion_Leo@163.com
 */


public class ElasticsearchDemo {

    public static void main(String[] args) throws Exception {
        TransportClient client = connect();
//        addIndexJsonInfo(client);
//        addIndexMapInfo(client);
//        addIndexHelperInfo(client);
//        getIndexInfo(client);
//        updateIndexInfo(client);
//        deleteIndexInfo(client);
//        countIndexInfo(client);
//        bulkIndexInfo(client);
        query(client);
    }

    static final String index = "spider";
    static final String type = "person";

    /**
     * 连接elasticsearch
     */
    public static TransportClient connect(){
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

        ImmutableList<DiscoveryNode> discoveryNodes = transportClient.connectedNodes();
        for (DiscoveryNode discoveryNode : discoveryNodes){
            println(discoveryNode.getHostAddress());
        }

        return transportClient;
    }

    /**
     * json 插入索引
     * @param transportClient
     */
    public static void addIndexJsonInfo(TransportClient transportClient){
        String json = "{\"title\":\"中国上海浦东塘桥陈赋赟\",\"price\":99999999999}";
        transportClient.prepareIndex(index, type, "1").setSource(json).get();
    }


    /**
     * map 插入索引
     * @param transportClient
     */
    public static void addIndexMapInfo(TransportClient transportClient){
        Map<String, Object> map = new HashMap<>();
        map.put("title", "vivo find");
        map.put("price", 12000);
        transportClient.prepareIndex(index, type, "3").setSource(map).get();
    }

    /**
     * helper 插入索引
     * @param transportClient
     * @throws IOException
     */
    public static void addIndexHelperInfo(TransportClient transportClient) throws IOException {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", "iphone7")
                .field("price", 5899)
                .endObject();
        transportClient.prepareIndex(index, type, "3").setSource(builder).get();
    }


    /**
     * 根据ID获取索引
     * @param transportClient
     * @throws IOException
     */
    public static void getIndexInfo(TransportClient transportClient) throws IOException {
        GetResponse response = transportClient.prepareGet(index, type, "AWQcMUH7bOs0g5dl3Oqt").get();
        println(response.getSource().toString());
    }

    /**
     * 修改索引
     * @param transportClient
     * @throws Exception
     */
    public static void updateIndexInfo(TransportClient transportClient) throws Exception {
        XContentBuilder builder = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", "iphone7")
                .field("price", 5999)
                .endObject();
        transportClient.prepareUpdate(index, type, "3").setDoc(builder).get();
    }

    /**
     * 删除索引
     * @param transportClient
     * @throws Exception
     */
    public static void deleteIndexInfo(TransportClient transportClient) throws Exception {
        transportClient.prepareDelete(index, type, "2").get();
    }

    /**
     * 获取索引总数
     * @param transportClient
     * @throws Exception
     */
    public static void countIndexInfo(TransportClient transportClient) throws Exception {
        long count = transportClient.prepareCount(index).setTypes(type).get().getCount();
        println(count);
    }

    /**
     * 批量操作
     * @param transportClient
     * @throws IOException
     */
    public static void bulkIndexInfo(TransportClient transportClient) throws IOException {
        BulkRequestBuilder bulkRequestBuilder = transportClient.prepareBulk();
        XContentBuilder builder1 = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", "iphone6s")
                .field("price", 3699)
                .endObject();
        XContentBuilder builder2 = XContentFactory.jsonBuilder()
                .startObject()
                .field("title", "iphone6")
                .field("price", 2188)
                .endObject();
        IndexRequest indexRequest1 = new IndexRequest(index, type, "4");
        indexRequest1.source(builder1);
        IndexRequest indexRequest2 = new IndexRequest(index, type, "5");
        indexRequest2.source(builder2);

        bulkRequestBuilder.add(indexRequest1);
        bulkRequestBuilder.add(indexRequest2);
        BulkResponse bulkItemResponses = bulkRequestBuilder.get();

        if (bulkItemResponses.hasFailures()){
            println(bulkItemResponses.buildFailureMessage());
        } else {
            println("ok");
        }
    }


    /**
     * 搜索
     * @param transportClient
     */
    public static void query(TransportClient transportClient) {
        SearchRequestBuilder builder = transportClient.prepareSearch(index)
                .setTypes(type)
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH);
//        builder.setQuery(QueryBuilders.matchAllQuery());
//        builder.setQuery(QueryBuilders.matchQuery("name", "cfy"))
//        builder.setQuery(QueryBuilders.queryString("name: c* OR age: 26"))
        builder.setQuery(QueryBuilders.boolQuery().must((QueryBuilders.matchQuery("age", "25"))).must(QueryBuilders.matchQuery("name", "lym")));
        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        println("总长：" + hits.getTotalHits());
        for (SearchHit hit : hits.getHits()){
            println(hit.getSourceAsString());
        }
    }

    public static void println(Object obj){
        System.out.println(obj);
    }
}
