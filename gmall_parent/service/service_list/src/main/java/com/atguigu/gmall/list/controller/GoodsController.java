package com.atguigu.gmall.list.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * GoodsController es商品管理的控制层
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/23 15:08
 **/

@RestController
@RequestMapping(value = "/api/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     *创建索引,创建映射
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/create")
    public Result createIndexAndMapping(){
        //createIndex
        elasticsearchRestTemplate.createIndex(Goods.class);
        //create mapping
        elasticsearchRestTemplate.putMapping(Goods.class);
        return Result.ok();
    }

    /**
     * 新增es中的数据
     * @param skuId
     * @return
     */
    @GetMapping(value = "/add/{skuId}")
    public Result add(@PathVariable(value = "skuId") Long skuId){
        goodsService.addGoodsIntoES(skuId);
        return Result.ok();
    }

    /**
     * 删除es中的数据
     * @param skuId
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/delete/{skuId}")
    public Result delete(@PathVariable(value = "skuId") Long skuId){
        goodsService.delGoodsFromEs(skuId);
        return Result.ok();
    }

}
