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
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;

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

    @Autowired
    private ThreadPoolExecutor threadPoolExecutor;

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

        //优化并并行处理,使用supplyAsync因为后面操作都依赖此处查询的结果
        CompletableFuture<SkuInfo> future1 = CompletableFuture.supplyAsync(() -> {
            SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
            return skuInfo;
        }, threadPoolExecutor);

        try {
            SkuInfo skuInfo = future1.get();
            if(skuInfo==null||skuInfo.getId()==null){
                throw new RuntimeException("商品不存在!!");
            }
            result.put("skuInfo",skuInfo);
            //根据category3Id,查询category2和category3,平台属性
            CompletableFuture<Void> future2 = future1.thenRunAsync(() -> {
                BaseCategoryView baseCategoryView = productFeign.getCategory(skuInfo.getCategory3Id());
                result.put("baseCategoryView", baseCategoryView);
            }, threadPoolExecutor);
            //查询图片
            CompletableFuture<Void> future3 = future1.thenRunAsync(() -> {
                List<SkuImage> imageList = productFeign.getImages(skuInfo.getId());
                result.put("imageList", imageList);
            }, threadPoolExecutor);
            //查询价格
            CompletableFuture<Void> future4 = future1.thenRunAsync(() -> {
                BigDecimal price = productFeign.getPrice(skuInfo.getId());
                result.put("price", price);
            }, threadPoolExecutor);
            //查询销售属性的名称和值
            CompletableFuture<Void> future5 = future1.thenRunAsync(() -> {
                Long spuId = skuInfo.getSpuId();
                List<SpuSaleAttr> spuSaleAttrs = productFeign.getSpuSaleAttr(skuInfo.getId(), skuInfo.getSpuId());
                result.put("spuSaleAttrs", spuSaleAttrs);
            }, threadPoolExecutor);
            //跳转信息
            CompletableFuture<Void> future6 = future1.thenRunAsync(() -> {
                Map skuAndValuesMap = productFeign.getSkuIdAndSaleAttrValues(skuInfo.getSpuId());
                result.put("skuAndValuesMap", skuAndValuesMap);
            }, threadPoolExecutor);
            //全部完成后返回
            CompletableFuture.allOf(future1,future2,future3,future4,future5,future6).join();
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
