package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.baomidou.mybatisplus.core.metadata.IPage;

import java.util.List;

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

    /**
     * 获取所有的数据
     * @return
     */
    List<BaseCategory1> getAll();

    /**
     * 新增数据
     * @param baseCategory1
     */
    void add(BaseCategory1 baseCategory1);

    /**
     * 修改数据
     * @param baseCategory1
     */
    void update(BaseCategory1 baseCategory1);

    /**
     * 删除数据
     * @param id
     */
    void delete(Long id);

    /**
     * 条件查询
     * @param baseCategory1
     * @return
     */
    List<BaseCategory1> query(BaseCategory1 baseCategory1);

    /**
     * 分页查询
     * @param page 当前页面
     * @param limit 每页数量
     * @return
     */
    IPage page(Integer page, Integer limit);

    /**
     * 带条件分页查询
     * @param page
     * @param limit
     * @param baseCategory1
     * @return
     */
    IPage query(Integer page, Integer limit, BaseCategory1 baseCategory1);
}
