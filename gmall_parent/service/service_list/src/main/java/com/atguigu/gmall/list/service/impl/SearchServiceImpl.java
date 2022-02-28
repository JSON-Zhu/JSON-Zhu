package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * SearchServiceImpl 商品搜索的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/26 16:20
 **/
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     *
     * @param keyword
     * @return : void
     */
    @Override
    public List<Goods> search(String keyword) {
        //参数校验
        if(keyword==null){

        }
        //拼接查询条件
        SearchRequest searchRequest = buildQueryParams(keyword);
        //执行查询
        try {
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果(反序列化)
            List<Goods> searchResult = getSearchResult(searchResponse);
            //返回结果
            return searchResult;

        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回空
        return null;
    }

    /**
     * 解析结果
     * @param searchResponse
     * @return : List<Goods>
     */
    private List<Goods> getSearchResult(SearchResponse searchResponse) {
        List<Goods> goodsList= new ArrayList<>();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //遍历获取数据
        while (iterator.hasNext()){
            SearchHit next = iterator.next();
            //获取json类型的数据
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //保存数据
            goodsList.add(goods);
        }
        return goodsList;
    }

    /**
     * 构建查询请求参数
     * @param searchData
     * @return : org.elasticsearch.action.search.SearchRequest
     */
    private SearchRequest buildQueryParams(Map<String,String> searchData){
        //拼接搜索条件
        SearchRequest searchRequest = new SearchRequest("goods_0107");
        //初始化条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //构建组合查询条件的对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //当关键不为空的时候,将关键字设置为查询条件
        String keyword = searchData.get("keyword");
        if(!StringUtils.isEmpty(keyword)){
//            builder.query(QueryBuilders.matchQuery("title",keyword));
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword));
        }
        //品牌查询
        String trademark = searchData.get("trademark");
        if(!StringUtils.isEmpty(trademark)){
            //获取品牌id
            String[] split = trademark.split(":");

        }
        //设置条件
        searchRequest.source(builder);
        return null;
    }
}
