package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;

/**
 * 一级分类的接口类
 */
public interface BaseCategory1Service {
    /**
     * 根据主键id查询
     * @param id
     * @return
     */
    BaseCategory1 getById(Long id);
}
