package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ItemService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ItemServiceImpl 商品详情页面使用的内部调用的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/19 23:38
 **/
@Service
public class ItemServiceImpl implements ItemService {

    @Resource
    private SkuInfoMapper skuInfoMapper;
    @Resource
    private BaseCategoryViewMapper baseCategoryViewMapper;
    /**
     * 查询sku的详细信息
     *
     * @param skuId
     * @return : com.atguigu.gmall.model.product.SkuInfo
     */
    @Override
    public SkuInfo getSkuInfo(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo;
    }

    /**
     * 根据三级分类id 查询一级二级三级分类的详细信息
     *
     * @param category3Id
     * @return : com.atguigu.gmall.model.product.BaseCategoryView
     */
    @Override
    public BaseCategoryView getCategory(Long category3Id) {
        return baseCategoryViewMapper.selectById(category3Id);
    }

    @Resource
    private SkuImageMapper skuImageMapper;
    /**
     * 根据skuId查询所属的图片列表
     *
     * @param skuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SkuImage>
     */
    @Override
    public List<SkuImage> getSkuImageList(Long skuId) {

        return skuImageMapper.selectList(new LambdaQueryWrapper<SkuImage>()
                .eq(SkuImage::getSkuId,skuId));
    }

    /**
     * 查询商品的价格
     *
     * @param skuId
     * @return : java.math.BigDecimal
     */
    @Override
    public BigDecimal getPrice(Long skuId) {
        SkuInfo skuInfo = skuInfoMapper.selectById(skuId);
        return skuInfo.getPrice();
    }

    @Resource
    private SpuSaleAttrMapper spuSaleAttrMapper;
    /**
     * 根据spuId和skuId查询销售属性和值,同时表示当前的sku所属的销售属性值
     *
     * @param skuId
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    @Override
    public List<SpuSaleAttr> getSpuSaleAttr(Long skuId, Long spuId) {
        return spuSaleAttrMapper.getSpuSaleAttrBySpuIdAndSkuId(skuId, spuId);
    }

    @Resource
    private SkuSaleAttrValueMapper skuSaleAttrValueMapper;
    /**
     * 根据spuId查询所属的所有的Sku及其对应的销售属性值
     *
     * @param spuId
     * @return : java.util.Map
     */
    @Override
    public Map getSkuIdAndSaleAttrValues(Long spuId) {
        Map result = new ConcurrentHashMap<>();
        List<Map> maps = skuSaleAttrValueMapper.selectSkuIdAndSaleAttrValues(spuId);
        //遍历每条数据
//        for (Map map : maps) {
//            //串行
//            Object skuId = map.get("sku_id");
//            Object valuesId = map.get("valuesId");
//            //save
//            result.put(skuId,valuesId);
//        }
        maps.stream().forEach(map -> {
            //并行
            Object skuId = map.get("sku_id");
            Object valuesId = map.get("valuesId");
            //save
            result.put(skuId,valuesId);
        });
        return result;
    }

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;
    /**
     * 根据id获取商标信息
     *
     * @param id
     * @return : com.atguigu.gmall.model.product.BaseTrademark
     */
    @Override
    public BaseTrademark getBaseTrademark(Long id) {
        BaseTrademark baseTrademark = baseTrademarkMapper.selectById(id);
        return baseTrademark;
    }
    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;
    /**
     * 根据skuId获取平台属性和值
     *
     * @param skuId
     * @return : java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    @Override
    public List<BaseAttrInfo> selectBaseAttrInfoBySkuId(Long skuId) {
        return baseAttrInfoMapper.selectBaseAttrInfoBySkuId(skuId);
    }

    /**
     * 扣减库存
     *
     * @param decreaseMap
     * @return : boolean
     */
    @Override
    public boolean decreaseStock(Map<String, Object> decreaseMap) {
        //遍历map
        decreaseMap.entrySet().stream().forEach(entry->{
            //获取商品id
            String key = entry.getKey();
            long skuId = Long.parseLong(key);
            Object value = entry.getValue();
            Integer num = Integer.parseInt(value.toString());
            int i = skuInfoMapper.decreaseStock(skuId, num);
            if (i<=0) {
                throw new RuntimeException("扣减库存失败");
            }
        });
        return true;
    }

    /**
     * 回滚库存
     *
     * @param rollbackMap
     * @return : boolean
     */
    @Override
    public boolean rollbackStock(Map<String, Object> rollbackMap) {
        //遍历map
        rollbackMap.entrySet().stream().forEach(entry->{
            //获取商品id
            String key = entry.getKey();
            long skuId = Long.parseLong(key);
            Object value = entry.getValue();
            Integer num = Integer.parseInt(value.toString());
            int i = skuInfoMapper.rollbackStock(skuId, num);
            if (i<=0) {
                throw new RuntimeException("回滚库存失败");
            }
        });
        return true;
    }
}
