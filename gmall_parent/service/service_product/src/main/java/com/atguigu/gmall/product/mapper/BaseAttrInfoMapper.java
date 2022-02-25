package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseAttrInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 平台属性Mapper
 * @author XQ.Zhu
 */
@Mapper
public interface BaseAttrInfoMapper extends BaseMapper<BaseAttrInfo> {

    /**
     * 根据分类Id  查询平台属性
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> selectBaseAttrInfoByCategoryId(
            @Param("category1Id")Long category1Id,
            @Param("category2Id")Long category2Id,
            @Param("category3Id")Long category3Id
                                    );
    /**
     * 根据skuId获取平台属性和值
     * @param skuId
     * @return : java.util.List<com.atguigu.gmall.model.product.BaseAttrInfo>
     */
    List<BaseAttrInfo> selectBaseAttrInfoBySkuId(
            @Param("skuId")Long skuId
    );

}
