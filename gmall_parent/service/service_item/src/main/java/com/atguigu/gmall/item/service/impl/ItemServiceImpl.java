package com.atguigu.gmall.item.service.impl;

import com.atguigu.gmall.item.service.ItemService;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.SkuImage;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ItemServiceImpl 商品详情数据整合的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/19 23:23
 **/
@Service
public class ItemServiceImpl implements ItemService {

    @Autowired
    private ProductFeign productFeign;

    /**
     * 获取商品详情页中的所有数据的接口
     *
     * @param skuId
     * @return : map
     */
    @Override
    public HashMap<String, Object> getSkuItem(Long skuId) {
        //参数校验
        if(skuId==null){
            throw new RuntimeException("商品不存在");
        }
        //新建map
        HashMap<String, Object> result = new HashMap<>();
        //查询skuInfo,并校验
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if(skuInfo==null||skuInfo.getId()==null){
            throw new RuntimeException("商品不存在!!");
        }
        result.put("skuInfo",skuInfo);
        //根据category3Id,查询category2和category3,平台属性
        BaseCategoryView baseCategoryView
                = productFeign.getCategory(skuInfo.getCategory3Id());
        result.put("baseCategoryView",baseCategoryView);
        //查询图片
        List<SkuImage> imageList = productFeign.getImages(skuId);
        result.put("imageList",imageList);
        //查询价格
        BigDecimal price = productFeign.getPrice(skuId);
        result.put("price",price);
        //查询销售属性的名称和值
        Long spuId = skuInfo.getSpuId();
        List<SpuSaleAttr> spuSaleAttrs = productFeign.getSpuSaleAttr(skuId, spuId);
        result.put("spuSaleAttrs",spuSaleAttrs);
        //跳转信息
        Map skuAndValuesMap = productFeign.getSkuIdAndSaleAttrValues(spuId);
        result.put("skuAndValuesMap",skuAndValuesMap);
        return result;
    }
}
