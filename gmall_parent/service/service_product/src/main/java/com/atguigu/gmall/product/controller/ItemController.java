package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.service.ItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public BaseCategoryView getCategory(@PathVariable("category3Id") Long category3Id){
        return itemService.getCategory(category3Id);
    }

    /**
     * 根据sku的id查询图片列表
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getImages/{skuId}")
    public List<SkuImage> getImages(@PathVariable(value = "skuId") Long skuId){
        return itemService.getSkuImageList(skuId);
    }

    /**
     * 根据sku的id查询价格的信息
     * @param skuId
     * @return
     */
    @GetMapping(value = "/getPrice/{skuId}")
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
    public Map getSkuIdAndSaleAttrValues(@PathVariable(value = "spuId") Long spuId){
        return itemService.getSkuIdAndSaleAttrValues(spuId);
    }
}
