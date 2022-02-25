package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * ItemService 商品详情页面使用的内部调用的接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/19 23:36
 **/
public interface ItemService {

    /**
     * 查询sku的详细信息
     * @param skuId
     * @return : com.atguigu.gmall.model.product.SkuInfo
     */
    SkuInfo getSkuInfo(Long skuId);

    /**
     * 根据三级分类id 查询一级二级三级分类的详细信息
     * @param category3Id
     * @return : com.atguigu.gmall.model.product.BaseCategoryView
     */
    BaseCategoryView getCategory(Long category3Id);

    /**
     * 根据skuId查询所属的图片列表
     * @param skuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    List<SkuImage> getSkuImageList(Long skuId);

    /**
     * 查询商品的价格
     * @param skuId
     * @return : java.math.BigDecimal
     */
    BigDecimal getPrice(Long skuId);

    /**
     * 根据spuId和skuId查询销售属性和值,同时表示当前的sku所属的销售属性值
     * @param skuId
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuSaleAttr(Long skuId,Long spuId);

    /**
     * 根据spuId查询所属的所有的Sku及其对应的销售属性值
     * @param spuId
     * @return : java.util.Map
     */
    Map getSkuIdAndSaleAttrValues(Long spuId);

    /**
     * 根据id获取商标信息
     * @param id
     * @return : com.atguigu.gmall.model.product.BaseTrademark
     */
    BaseTrademark getBaseTrademark(Long id);

    /**
     * 根据skuId获取平台属性和值
     * @param skuId
     * @return : java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    List<BaseAttrInfo> selectBaseAttrInfoBySkuId(Long skuId);
}
