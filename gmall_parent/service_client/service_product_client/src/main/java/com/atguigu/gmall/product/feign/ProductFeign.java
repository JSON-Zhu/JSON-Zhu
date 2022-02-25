package com.atguigu.gmall.product.feign;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.product.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ProductFeign 商品微服务feign接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 0:31
 **/
@FeignClient(name = "service-product",path = "/api/product")
public interface ProductFeign {

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return : com.atguigu.gmall.model.product.SkuInfo
     */
    @GetMapping(value = "/getSkuInfo/{skuId}")
     SkuInfo getSkuInfo(@PathVariable("skuId")Long skuId);

    /**
     * 根据三级分类id 查询一级二级三级分类的详细信息
     * @param category3Id
     * @return : com.atguigu.gmall.model.product.BaseCategoryView
     */
    @GetMapping(value = "getCategory/{category3Id}")
    BaseCategoryView getCategory(@PathVariable("category3Id") Long category3Id);

    /**
     * 根据skuId查询所属的图片列表
     * @param skuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    @GetMapping(value = "/getImages/{skuId}")
    List<SkuImage> getImages(@PathVariable(value = "skuId") Long skuId);

    /**
     * 查询商品的价格
     * @param skuId
     * @return : java.math.BigDecimal
     */
    @GetMapping(value = "/getPrice/{skuId}")
    BigDecimal getPrice(@PathVariable(value = "skuId") Long skuId);

    /**
     * 根据spuId和skuId查询销售属性和值,同时表示当前的sku所属的销售属性值
     * @param skuId
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    @GetMapping(value = "/getSpuSaleAttr/{skuId}/{spuId}")
    List<SpuSaleAttr> getSpuSaleAttr(@PathVariable(value = "skuId") Long skuId,
                                            @PathVariable(value = "spuId") Long spuId);

    /**
     * 根据spuId查询所属的所有的Sku及其对应的销售属性值
     * @param spuId
     * @return : java.util.Map
     */
    @GetMapping(value = "/getSkuIdAndSaleAttrValues/{spuId}")
    Map getSkuIdAndSaleAttrValues(@PathVariable(value = "spuId") Long spuId);

    /**
     * 根据id获取商标信息
     * @param id
     * @return
     */
    @GetMapping(value = "/getBaseTrademark/{id}")
    BaseTrademark getBaseTrademark(@PathVariable(value = "id") Long id);

    /**
     * 根据skuId获取平台属性和值
     * @param skuId
     * @return :
     */
    @GetMapping(value = "getBaseAttr/{skuId}")
    List<BaseAttrInfo> selectBaseAttrInfoBySkuId(@PathVariable(value = "skuId")Long skuId);
}
