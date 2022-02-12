package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 一级分类的接口类的实现
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class BaseCategory1ServiceImpl implements BaseCategory1Service {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    /**
     * 根据主键id查询
     * @param id
     * @return
     */
    @Override
    public BaseCategory1 getById(Long id) {
        BaseCategory1 baseCategory1 = baseCategory1Mapper.selectById(id);
        return baseCategory1;
    }
}
