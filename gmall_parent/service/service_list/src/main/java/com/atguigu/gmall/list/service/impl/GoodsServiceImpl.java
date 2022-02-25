package com.atguigu.gmall.list.service.impl;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.list.dao.GoodsDao;
import com.atguigu.gmall.list.service.GoodsService;
import com.atguigu.gmall.model.list.Goods;
import com.atguigu.gmall.model.list.SearchAttr;
import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.atguigu.gmall.model.product.BaseCategoryView;
import com.atguigu.gmall.model.product.BaseTrademark;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * GoodsServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/23 15:11
 **/
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private GoodsDao goodsDao;

    @Autowired
    private ProductFeign productFeign;
    /**
     * 将商品同步到es
     *
     * @param skuId
     * @return : void
     */
    @Override
    public void addGoodsIntoES(Long skuId) {
        //参数校验
        if(skuId==null){
            throw new RuntimeException("参数错误");
        }
        //调productFeign,并判断返回结果是否为空
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if(skuInfo==null||skuInfo.getId()==null){
            throw new RuntimeException("商品不存在");
        }
        //skuInfo包装为Goods对象
        Goods goods = new Goods();
        //id
        goods.setId(skuInfo.getId());
        //image
        goods.setDefaultImg(skuInfo.getSkuDefaultImg());
        //title
        goods.setTitle(skuInfo.getSkuName());
        //price
        BigDecimal price= productFeign.getPrice(skuInfo.getId());
        goods.setPrice(price.doubleValue());
        //create date
        goods.setCreateTime(new Date());
        //trademark
        BaseTrademark baseTrademark = productFeign.getBaseTrademark(skuInfo.getTmId());
        goods.setTmId(baseTrademark.getId());
        goods.setTmName(baseTrademark.getTmName());
        goods.setTmLogoUrl(baseTrademark.getLogoUrl());
        //category info
        Long category3Id = skuInfo.getCategory3Id();
        BaseCategoryView category = productFeign.getCategory(category3Id);
        goods.setCategory1Id(category.getCategory1Id());
        goods.setCategory1Name(category.getCategory1Name());
        goods.setCategory2Id(category.getCategory2Id());
        goods.setCategory2Name(category.getCategory2Name());
        goods.setCategory3Id(category.getCategory3Id());
        goods.setCategory3Name(category.getCategory3Name());
        //设置平台属性->根据skuId查询对应的平台属性的id,名字,值
        List<BaseAttrInfo> baseAttrInfos = productFeign.selectBaseAttrInfoBySkuId(skuInfo.getId());
        List<SearchAttr> searchAttrs = baseAttrInfos.stream().map(baseAttrInfo -> {
            //初始化
            SearchAttr searchAttr = new SearchAttr();
            searchAttr.setAttrId(baseAttrInfo.getId());
            searchAttr.setAttrName(baseAttrInfo.getAttrName());
            searchAttr.setAttrValue(baseAttrInfo.getAttrValueList().get(0).getValueName());
            //返回
            return searchAttr;
        }).collect(Collectors.toList());
        goods.setAttrs(searchAttrs);
        //将goods保存到es
        goodsDao.save(goods);
    }

    /**
     * 从es中删除商品
     *
     * @param skuId
     * @return : void
     */
    @Override
    public void delGoodsFromEs(Long skuId) {
        //参数校验
        if(skuId==null){
            throw new RuntimeException("参数错误");
        }
        //删除数据
        goodsDao.deleteById(skuId);
    }
}
