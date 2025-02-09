package com.hmall.item.test;

import org.apache.http.HttpHost;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.metrics.Stats;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class ElasticSearchQueryTests {
    private RestHighLevelClient restHighLevelClient;

    @BeforeEach
    void setUp() {
        restHighLevelClient = new RestHighLevelClient(RestClient.builder(
                HttpHost.create("http://192.168.19.130:9200")
        ));
    }

    @AfterEach
    void tearDown() throws IOException {
        restHighLevelClient.close();
    }

    // 测试查询匹配所有文档的方法
    @Test
    void testSearchMatchAll() throws IOException {
        // 创建一个针对“items”索引的搜索请求
        SearchRequest searchRequest = new SearchRequest("items");

        // 配置搜索请求的源，使用匹配所有文档的查询
        searchRequest.source()
                .query(QueryBuilders.matchAllQuery());

        // 执行搜索请求并获取搜索响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 获取搜索响应中的所有搜索命中
        SearchHits searchHits = searchResponse.getHits();

        // 打印总命中数
        System.out.println(searchHits.getTotalHits());

        // 遍历并打印每个命中的源文档
        searchHits.forEach(searchHit -> System.out.println(searchHit.getSourceAsString()));
    }

    // 测试使用布尔查询进行搜索的方法
    @Test
    void testSearchBoolQuery() throws IOException {
        // 创建一个针对"items"索引的搜索请求
        SearchRequest searchRequest = new SearchRequest("items");

        // 配置搜索请求的源
        searchRequest.source()
                .query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("name", "脱脂牛奶"))
                        .filter(QueryBuilders.termQuery("brand.keyword", "德亚"))
                        .filter(QueryBuilders.rangeQuery("price")
                                .lt(30000)));
        // ^-- 构建一个布尔查询，包含以下条件：
        //     1. 商品名称必须匹配"脱脂牛奶"
        //     2. 品牌必须为"德亚"
        //     3. 价格必须小于等于30000

        // 执行搜索请求并获取响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 获取搜索结果中的所有命中项
        SearchHits searchHits = searchResponse.getHits();

        // 打印总命中数
        System.out.println(searchHits.getTotalHits());

        // 遍历并打印每个命中的源文档
        searchHits.forEach(searchHit -> System.out.println(searchHit.getSourceAsString()));
    }

    @Test
    void testSearchSortQuery() throws IOException {
        int page = 2;
        int pageSize = 10;

        SearchRequest searchRequest = new SearchRequest("items");
        searchRequest.source()
                .query(QueryBuilders.matchQuery("name", "手机"))
                .sort("price", SortOrder.ASC)
                .from((--page) * pageSize)
                .size(pageSize);

        // 执行搜索请求并获取响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 获取搜索结果中的所有命中项
        SearchHits searchHits = searchResponse.getHits();

        // 打印总命中数
        System.out.println(searchHits.getTotalHits());

        // 遍历并打印每个命中的源文档
        searchHits.forEach(searchHit -> System.out.println(searchHit.getSourceAsString()));
    }

    @Test
    void testSearchHighlightQuery() throws IOException {
        int page = 2;
        int pageSize = 10;

        SearchRequest searchRequest = new SearchRequest("items");
        searchRequest.source()
                .query(QueryBuilders.matchQuery("name", "手机"))
                .highlighter(SearchSourceBuilder.highlight()
                        .field("name"));

        // 执行搜索请求并获取响应
        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        // 获取搜索结果中的所有命中项
        SearchHits searchHits = searchResponse.getHits();

        // 打印总命中数
        System.out.println(searchHits.getTotalHits());

        // 遍历并打印每个命中的源文档
        searchHits.forEach(searchHit -> {
            System.out.println(searchHit.getSourceAsString());
            System.out.println(searchHit.getHighlightFields());
        });
    }

    @Test
    void testSearchAggregationQuery() throws IOException {
        SearchRequest searchRequest = new SearchRequest("items");
        searchRequest.source()
                .size(0)
                .query(QueryBuilders.termQuery("category.keyword", "手机"))
                .aggregation(AggregationBuilders.terms("brand_agg")
                        .size(10)
                        .field("brand.keyword")
                        .subAggregation(AggregationBuilders.stats("price_stats")
                                .field("price")));

        SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);

        Aggregations aggregations = searchResponse.getAggregations();
        Terms brandAgg = aggregations.get("brand_agg");
        brandAgg.getBuckets()
                .forEach(bucket -> {
                    System.out.println(bucket.getKeyAsString());
                    System.out.println(bucket.getDocCount());

                    Stats priceStats = bucket.getAggregations().get("price_stats");
                    System.out.println(priceStats.getAvg());
                    System.out.println(priceStats.getMax());
                    System.out.println(priceStats.getMin());
                });
    }
}
