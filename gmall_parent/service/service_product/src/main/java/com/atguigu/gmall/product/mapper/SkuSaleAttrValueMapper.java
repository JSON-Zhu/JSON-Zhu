package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.SkuSaleAttrValue;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * Sku销售属性值 Mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/19 1:07
 **/
@Mapper
public interface SkuSaleAttrValueMapper extends BaseMapper<SkuSaleAttrValue> {

    /**
     * 根据spuId查询所属的所有的Sku及其对应的销售属性值
     * @param spuId
     * @return : java.util.List<java.util.Map>
     */
    @Select("SELECT\n" +
            "\tsku_id,GROUP_CONCAT(DISTINCT sale_attr_value_id ORDER BY sale_attr_value_id ASC SEPARATOR '|') as valuesId \n" +
            "FROM\n" +
            "\tsku_sale_attr_value\n" +
            "WHERE \n" +
            "\tspu_id=1\n" +
            "GROUP BY\n" +
            "\tsku_id")
    List<Map> selectSkuIdAndSaleAttrValues(@Param(value = "spuId")Long spuId);
}
