package com.atguigu.gmall.list.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.list.service.SearchService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchResponseAttrVo;
import com.atguigu.gmall.model.list.SearchResponseTmVo;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * SearchServiceImpl 商品搜索的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/26 16:20
 **/
@Service
public class SearchServiceImpl implements SearchService {

    @Autowired
    private RestHighLevelClient restHighLevelClient;

    /**
     * 商品搜索
     * @param searchData
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     */
    @Override
    public Map<String,Object> search(Map<String,String> searchData) {
        try {
            //拼接查询条件
            SearchRequest searchRequest = buildQueryParams(searchData);
            //执行查询
            SearchResponse searchResponse =
                    restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
            //解析结果(反序列化)
            Map<String, Object> result = getSearchResult(searchResponse);
            //返回结果
            return result;
        } catch (IOException e) {
            e.printStackTrace();
        }
        //返回空
        return null;
    }

    /**
     * 构建查询请求参数
     * @param searchData
     * @return : org.elasticsearch.action.search.SearchRequest
     */
    private SearchRequest buildQueryParams(Map<String,String> searchData){
        //拼接搜索条件
        SearchRequest searchRequest = new SearchRequest("goods_java0107");
        //初始化条件构造器
        SearchSourceBuilder builder = new SearchSourceBuilder();
        //构建组合查询条件的对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        //当关键不为空的时候,将关键字设置为查询条件
        String keyword = searchData.get("keyword");
        if(!StringUtils.isEmpty(keyword)){
            //            builder.query(QueryBuilders.matchQuery("title", keyword));
            boolQueryBuilder.must(QueryBuilders.matchQuery("title",keyword));
        }
        //品牌查询
        String trademark = searchData.get("trademark");
        if(!StringUtils.isEmpty(trademark)){
            //获取品牌id
            String[] split = trademark.split(":");
            //根据品牌的id查询
            boolQueryBuilder.must(QueryBuilders.termQuery("tmId",split[0]));
        }
        //平台属性构建 attr_系列=326:变频空调& attr_匹数=429:1匹
        for (Map.Entry<String, String> entry : searchData.entrySet()) {
            //获取每个查询条件的key
            String key = entry.getKey();
            //若key以attr开头,则就是平台属性的条件
            if(key.startsWith("attr_")){
                //326:变频空调
                String value = entry.getValue();
                //切分字符串
                String[] split = value.split(":");
                //构建bool查询
                BoolQueryBuilder nestedBoolQuery = QueryBuilders.boolQuery();
                //平台属性id
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrId",split[0]));
                //平台属性的值
                nestedBoolQuery.must(QueryBuilders.termQuery("attrs.attrValue",split[1]));
                //设置nested的查询条件
                boolQueryBuilder.must(
                        QueryBuilders.nestedQuery("attrs",nestedBoolQuery, ScoreMode.None));
            }
        }
        //查询价格 price=1000元以上 500-1000元
        String price = searchData.get("price");
        if(!StringUtils.isEmpty(price)){
            //价格字符串处理
            price = price.replace("元", "").replace("以上", "");
            //切分字符串
            String[] split = price.split("-");
            //大于第一个值
            boolQueryBuilder.must(QueryBuilders.rangeQuery("price").gte(split[0]));
            //判断是否有第二个值
            if(split.length>1){
                boolQueryBuilder.must(QueryBuilders.rangeQuery("price").lt(split[1]));
            }
        }
        //设置全部的查询条件
        builder.query(boolQueryBuilder);
        //设置排序
        String sortRule = searchData.get("sortRule");
        String sortField = searchData.get("sortField");
        if(!StringUtils.isEmpty(sortRule)&&!StringUtils.isEmpty(sortField)){
            //指定排序
            builder.sort(sortField, SortOrder.valueOf(sortRule));
        }else {
            //default sort order
            builder.sort("id", SortOrder.DESC);
        }
        //设置每页返回的数据量;固定返回100条数据
        builder.size(10);
        //获取页码
        String pageNum = searchData.get("pageNum");
        //计算页码
        Integer page = getPageNum(pageNum);
        builder.from((page-1)*100);
        //设置品牌聚合查询
        builder.aggregation(
                AggregationBuilders.terms("aggTmId").field("tmId")
                    .subAggregation(AggregationBuilders.terms("aggTmName").field("tmName"))
                    .subAggregation(AggregationBuilders.terms("aggTmLogoUrl").field("tmLogoUrl"))
        );
        //设置平台属性的聚合
        builder.aggregation(AggregationBuilders.nested("aggAttrs","attrs").subAggregation(
                AggregationBuilders.terms("aggAttrId").field("attrs.attrId")
                    .subAggregation(AggregationBuilders.terms("aggAttrName").field("attrs.attrName"))
                    .subAggregation(AggregationBuilders.terms("aggAttrValue").field("attrs.attrValue"))
                    .size(1000)
                )
        );
        //设置高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.field("title");
        highlightBuilder.preTags("<font style=color:red>");
        highlightBuilder.postTags("</font>");
        builder.highlighter(highlightBuilder);
        //设置条件
        searchRequest.source(builder);
        return searchRequest;
    }

    /**
     * 计算页码
     * @param pageNum
     * @return : java.lang.Integer
     */
    private Integer getPageNum(String pageNum) {
        try {
            int i = Integer.parseInt(pageNum);
            return i>0?i:1;
        }catch (Exception e){
            //默认显示第一页
            return 1;
        }
    }

    /**
     * 解析结果
     * @param searchResponse
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     */
    private Map<String,Object> getSearchResult(SearchResponse searchResponse) {
        //返回结果初始化
        Map<String, Object> result = new HashMap<>();
        //返回商品初始化
        List<Goods> goodsList = new ArrayList<>();
        //获取数据
        SearchHits hits = searchResponse.getHits();
        //获取数据总量
        long totalHits = hits.getTotalHits();
        result.put("totalHits",totalHits);
        //获取迭代器
        Iterator<SearchHit> iterator = hits.iterator();
        //遍历获取数据
        while (iterator.hasNext()){
            SearchHit next = iterator.next();
            //获取json类型的数据
            String sourceAsString = next.getSourceAsString();
            //反序列化
            Goods goods = JSONObject.parseObject(sourceAsString, Goods.class);
            //获取高亮的数据
            HighlightField highlightField = next.getHighlightFields().get("title");
            if(highlightField!=null){
                Text[] fragments = highlightField.getFragments();
                if(fragments!=null && fragments.length > 0){
                    //遍历获取所有的高亮内容
                    String title="";
                    for (Text fragment : fragments) {
                        title +=fragment;
                    }
                    //使用高亮的数据替换原始的数据
                    goods.setTitle(title);
                }
            }
            //保存数据
            goodsList.add(goods);
        }
        result.put("goodsList",goodsList);
        //获取全部聚合的结果
        Aggregations aggregations = searchResponse.getAggregations();
        //获取品牌的聚合结果
        List<SearchResponseTmVo> searchResponseTmVos= getTrademarkAggResult(aggregations);
        result.put("searchResponseTmVos",searchResponseTmVos);
        //获取平台属性的聚合结果
        List<SearchResponseAttrVo> searchResponseAttrVos=getAttrAggResult(aggregations);
        result.put("searchResponseAttrVos",searchResponseAttrVos);
        return result;
    }

    /**
     * 获取平台属性的聚合结果
     * @param aggregations
     * @return : java.util.List<com.atguigu.gmall.model.list.SearchResponseAttrVo>
     */
    private List<SearchResponseAttrVo> getAttrAggResult(Aggregations aggregations) {
        //获取nested属性的聚合结果
        ParsedNested aggAttrs = aggregations.get("aggAttrs");
        //获取子聚合平台属性的id的聚合结果
        ParsedLongTerms aggAttrId = aggAttrs.getAggregations().get("aggAttrId");
        //遍历平台属性id的聚合结果获取子聚合中的平台属性的名字和值列表
        return aggAttrId.getBuckets().stream().map(aggAttrIdBucket->{
            SearchResponseAttrVo searchResponseAttrVo = new SearchResponseAttrVo();
            //获取平台属性id
            long attrId = aggAttrIdBucket.getKeyAsNumber().longValue();
            searchResponseAttrVo.setAttrId(attrId);
            //获取子聚合全部结果
            Aggregations subAggregations = aggAttrIdBucket.getAggregations();
            //获取平台属性名字
            ParsedStringTerms aggAttrName = subAggregations.get("aggAttrName");
            if(!aggAttrName.getBuckets().isEmpty()){
                String attrName = aggAttrName.getBuckets().get(0).getKeyAsString();
                searchResponseAttrVo.setAttrName(attrName);
            }
            //获取平台属性的值
            ParsedStringTerms aggAttrValue = subAggregations.get("aggAttrValue");
            if(!aggAttrValue.getBuckets().isEmpty()){
                List<String> attrValues = aggAttrValue.getBuckets().stream().map(bucket -> {
                    //获取值的名字
                    String attrValue = bucket.getKeyAsString();
                    //返回结果
                    return attrValue;
                }).collect(Collectors.toList());
                searchResponseAttrVo.setAttrValueList(attrValues);
            }
            return searchResponseAttrVo;
        }).collect(Collectors.toList());
    }

    /**
     * 获取品牌的集合结果
     * @param aggregations   
     * @return : java.util.List<com.atguigu.gmall.model.list.SearchResponseTmVo>
     */
    private List<SearchResponseTmVo> getTrademarkAggResult(Aggregations aggregations) {
        //通过别名获取品牌的id的聚合结果
        ParsedLongTerms aggTmId = aggregations.get("aggTmId");
        //遍历获取聚合的每条结果
        return aggTmId.getBuckets().stream().map(bucket->{
            SearchResponseTmVo searchResponseTmVo = new SearchResponseTmVo();
            //获取品牌id
            long tmId = bucket.getKeyAsNumber().longValue();
            searchResponseTmVo.setTmId(tmId);
            //获取子聚合的结果
            Aggregations subAggregations = bucket.getAggregations();
            //获取品牌名字
            ParsedStringTerms aggTmName = subAggregations.get("aggTmName");
            if(!aggTmName.getBuckets().isEmpty()){
                String tmName = aggTmName.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmName(tmName);
            }
            //获取品牌logo
            ParsedStringTerms aggTmLogoUrl = subAggregations.get("aggTmLogoUrl");
            if(!aggTmLogoUrl.getBuckets().isEmpty()){
                String tmLogoUrl = aggTmLogoUrl.getBuckets().get(0).getKeyAsString();
                searchResponseTmVo.setTmLogoUrl(tmLogoUrl);
            }
            //返回结果
            return searchResponseTmVo;
        }).collect(Collectors.toList());
    }
}
