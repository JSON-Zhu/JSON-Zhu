package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.cache.GmallCache2;
import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ItemController 商品详情页面使用的内部调用的控制器
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/19 23:41
 **/
@RestController
@RequestMapping(value = "/api/product")
public class ItemController {

    @Autowired
    private ItemService itemService;

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return : com.atguigu.gmall.model.product.SkuInfo
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
    @GmallCache2(prefix = "skuId:")
    public SkuInfo getSkuInfo(@PathVariable("skuId")Long skuId){
        SkuInfo skuInfo = itemService.getSkuInfo(skuId);
        return skuInfo;
    }

    /**
     * 查询分类根据三级id
     * @param category3Id
     * @return : com.atguigu.gmall.model.product.BaseCategoryView
     */
    @GetMapping(value = "getCategory/{category3Id}")
    @GmallCache2(prefix = "category:")
    public BaseCategoryView getCategory(@PathVariable("category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 根据sku的id查询图片列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getImages/{skuId}")
    @GmallCache2(prefix = "images:")
    public List<SkuImage> getImages(@PathVariable(value = "skuId") Long skuId){
        return itemService.getSkuImageList(skuId);
    }

    /**
     * 根据sku的id查询价格的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
    @GmallCache2(prefix = "price:")
    public BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId){
        return itemService.getPrice(skuId);
    }

    /**
     * 根据spu和sku的id查询销售属性和销售属性值的信息
     * @param skuId
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSpuSaleAttr/{skuId}/{spuId}")
    @GmallCache2(prefix = "spuSaleAttr:")
    public List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId){
        return itemService.getSpuSaleAttr(skuId, spuId);
    }

    /**
     * 根据spu的id查询出这个spu下所有sku的id和销售属性值的键值对
     * @param spuId
     * @return
     */
    @GetMapping(value = "/getSkuIdAndSaleAttrValues/{spuId}")
    @GmallCache2(prefix = "skuIdAndSaleAttrValues:")
    public Map getSkuIdAndSaleAttrValues(@PathVariable(value = "spuId") Long spuId){
        return itemService.getSkuIdAndSaleAttrValues(spuId);
    }

    /**
     * 根据id获取商标信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    @GmallCache2(prefix = "baseTrademark:")
    public BaseTrademark getBaseTrademark(@PathVariable(value = "id") Long id){
        return itemService.getBaseTrademark(id);
    }

    /**
     * 根据skuId获取平台属性和值
     * @param skuId
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "getBaseAttr/{skuId}")
    @GmallCache2(prefix = "baseAttrInfoBySkuId:")
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@PathVariable(value = "skuId")Long skuId) {
        return itemService.selectBaseAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     * @param decreaseMap
     * @return
     */
    @GetMapping(value = "/decreaseStock")
    public Boolean decreaseStock(@RequestParam Map<String, Object> decreaseMap){
        return itemService.decreaseStock(decreaseMap);
    }

    /**
     * 回滚库存
     * @param rollbackMap
     * @return
     */
    @GetMapping(value = "/rollbackStock")
    public Boolean rollbackStock(@RequestParam Map<String, Object> rollbackMap){
        return itemService.rollbackStock(rollbackMap);
    }
}
