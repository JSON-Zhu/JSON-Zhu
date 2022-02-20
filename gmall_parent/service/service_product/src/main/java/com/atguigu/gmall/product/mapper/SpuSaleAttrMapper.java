package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SpuInfo;
import com.atguigu.gmall.model.product.SpuSaleAttr;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * spuSaleAttr Mapper
 * @author XQ.Zhu
 */
@Mapper
public interface SpuSaleAttrMapper extends BaseMapper<SpuSaleAttr> {

    /**
     * 根据spuId获取销售属性值的列表
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuSaleAttrValueBySpuId(@Param(value = "spuId") Long spuId);

    /**
     * 根据spuId和skuId查询销售属性和值,同时表示当前的sku所属的销售属性值
     * @param skuId
     * @param spuId
     * @return : java.util.List<com.atguigu.gmall.model.product.SpuSaleAttr>
     */
    List<SpuSaleAttr> getSpuSaleAttrBySpuIdAndSkuId(@Param(value = "skuId")Long skuId,@Param(value = "spuId") Long spuId);
}
