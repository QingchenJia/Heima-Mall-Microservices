package com.hmall.item.test;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hmall.item.domain.po.Item;
import com.hmall.item.domain.po.ItemDoc;
import com.hmall.item.service.IItemService;
import org.apache.http.HttpHost;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;

@SpringBootTest(properties = "spring.profiles.active=local")
public class ElasticSearchDocTests {
    @Autowired
    private IItemService itemService;

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

    @Test
    void testDocumentIndexRequest() throws IOException {
        Item item = itemService.getById(100002644680L);
        ItemDoc itemDoc = BeanUtil.copyProperties(item, ItemDoc.class);

        IndexRequest indexRequest = new IndexRequest("items").id(itemDoc.getId());
        indexRequest.source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON);

        restHighLevelClient.index(indexRequest, RequestOptions.DEFAULT);
    }

    @Test
    void testDocumentGetRequest() throws IOException {
        GetRequest getRequest = new GetRequest("items").id("100002644680");
        GetResponse getResponse = restHighLevelClient.get(getRequest, RequestOptions.DEFAULT);
        String sourceAsString = getResponse.getSourceAsString();
        System.out.println(sourceAsString);
    }

    @Test
    void testDocumentDeleteRequest() throws IOException {
        DeleteRequest deleteRequest = new DeleteRequest("items", "100002644680");
        DeleteResponse deleteResponse = restHighLevelClient.delete(deleteRequest, RequestOptions.DEFAULT);
        System.out.println(deleteResponse);
    }

    @Test
    void testDocumentUpdateRequest() throws IOException {
        UpdateRequest updateRequest = new UpdateRequest("items", "100002644680");
        updateRequest.doc(
                "price", 1000
        );
        UpdateResponse updateResponse = restHighLevelClient.update(updateRequest, RequestOptions.DEFAULT);
        System.out.println(updateResponse);
    }

    @Test
    void testDocumentBulkRequest() throws IOException {
        int page = 1;
        int pageSize = 500;

        while (true) {
            Page<Item> itemPage = Page.of(page, pageSize);
            LambdaQueryWrapper<Item> queryWrapperItem = new LambdaQueryWrapper<>();
            queryWrapperItem.eq(Item::getStatus, 1);

            itemService.page(itemPage, queryWrapperItem);

            List<Item> items = itemPage.getRecords();
            if (CollUtil.isEmpty(items)) {
                break;
            }

            List<ItemDoc> itemDocs = BeanUtil.copyToList(items, ItemDoc.class);

            BulkRequest bulkRequest = new BulkRequest();
            itemDocs.forEach(itemDoc -> {
                bulkRequest.add(new IndexRequest("items")
                        .id(itemDoc.getId())
                        .source(JSONUtil.toJsonStr(itemDoc), XContentType.JSON));
            });

            restHighLevelClient.bulk(bulkRequest, RequestOptions.DEFAULT);

            page++;
        }
    }
}
