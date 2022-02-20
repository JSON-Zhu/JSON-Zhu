package com.atguigu.gmall.product.mapper;

import com.atguigu.gmall.model.product.BaseCategoryView;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * BaseCategoryViewMapper 根据三级分类id 查询一级二级三级分类的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 1:11
 **/
@Mapper
public interface BaseCategoryViewMapper extends BaseMapper<BaseCategoryView> {
}
