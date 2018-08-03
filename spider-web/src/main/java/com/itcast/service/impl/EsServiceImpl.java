package com.itcast.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.itcast.bean.JDSkuSearchInfo;
import com.itcast.constants.EsConstant;
import com.itcast.service.IEsService;
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
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: Leo_Chan
 * Date: 2018/6/27
 * Time: 11:07
 * Work contact: Astion_Leo@163.com
 */

@Service("esService")
public class EsServiceImpl implements IEsService {

    private TransportClient transportClient;

    public EsServiceImpl (){
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

        this.transportClient = transportClient;
    }



    @Override
    public List<JDSkuSearchInfo> esSearch(String searchContent, String sortType, Integer start, Integer size) {
        SearchRequestBuilder builder = transportClient.prepareSearch(EsConstant.ES_INDEX)
                .setTypes(EsConstant.ES_DOCUMENT)
                .setSearchType(SearchType.DFS_QUERY_AND_FETCH)
                .setFrom(start)
                .setSize(size)
                .setQuery(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("title", searchContent)));

        if (null != sortType){
            if (sortType.equals(EsConstant.ES_SORT_ASC)){
                builder.addSort("p_price", SortOrder.ASC);
            } else if (sortType.equals(EsConstant.ES_SORT_DESC)){
                builder.addSort("p_price", SortOrder.DESC);
            }
        }

        SearchResponse response = builder.get();
        SearchHits hits = response.getHits();
        long count = hits.getTotalHits();
        List<JDSkuSearchInfo> resultList = new ArrayList<JDSkuSearchInfo>(hits.getHits().length);
        for (SearchHit hit : hits.getHits()){
            String json = hit.getSourceAsString();
            JDSkuSearchInfo jdSkuSearchInfo = JSONObject.parseObject(json, JDSkuSearchInfo.class);
            jdSkuSearchInfo.setId(hit.getId());
            resultList.add(jdSkuSearchInfo);
        }

        return resultList;
    }
}
