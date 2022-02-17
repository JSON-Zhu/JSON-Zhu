package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.BaseCategory1;
import com.atguigu.gmall.product.mapper.BaseCategory1Mapper;
import com.atguigu.gmall.product.service.BaseCategory1Service;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 获取所有的数据
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getAll() {
        List<BaseCategory1> baseCategory1List =
                baseCategory1Mapper.selectList(null);
        return baseCategory1List;
    }

    /**
     * 新增数据
     *
     * @param baseCategory1
     */
    @Override
    public void add(BaseCategory1 baseCategory1) {
        int insert = baseCategory1Mapper.insert(baseCategory1);
        if(insert<=0){
            throw new RuntimeException("新增失败");
        }
    }

    /**
     * 修改数据
     *
     * @param baseCategory1
     */
    @Override
    public void update(BaseCategory1 baseCategory1) {
        int update = baseCategory1Mapper.updateById(baseCategory1);
        if(update<=0){
            throw new RuntimeException("修改失败");
        }
    }

    /**
     * 删除数据
     *
     * @param id
     */
    @Override
    public void delete(Long id) {
        int delete = baseCategory1Mapper.deleteById(id);
        if(delete<=0){
            throw new RuntimeException("删除失败");
        }
    }

    /**
     * 条件查询
     *
     * @param baseCategory1
     * @return
     */
    @Override
    public List<BaseCategory1> query(BaseCategory1 baseCategory1) {
        //构建条件构造器,拼接条件
        LambdaQueryWrapper<BaseCategory1> wrapper= this.getWrapper(baseCategory1);
        List<BaseCategory1> baseCategory1List = baseCategory1Mapper.selectList(wrapper);
        return baseCategory1List;
    }

    /**
     * 分页查询
     *
     * @param page  当前页面
     * @param limit 每页数量
     * @return
     */
    @Override
    public IPage page(Integer page, Integer limit) {
        return baseCategory1Mapper.selectPage(new Page<>(page,limit),null);
    }

    /**
     * 带条件分页查询
     *
     * @param page
     * @param limit
     * @param baseCategory1
     * @return
     */
    @Override
    public IPage query(Integer page, Integer limit, BaseCategory1 baseCategory1) {
        //构建条件构造器,拼接条件
        LambdaQueryWrapper<BaseCategory1> wrapper= this.getWrapper(baseCategory1);
        IPage<BaseCategory1> pageList = baseCategory1Mapper.selectPage(new Page<>(page, limit), wrapper);
        return pageList;
    }

    /**
     * 构造查询条件的方法
     * @param baseCategory1
     * @return
     */
    private LambdaQueryWrapper<BaseCategory1> getWrapper(BaseCategory1 baseCategory1) {
        //条件构造器初始化
        LambdaQueryWrapper<BaseCategory1> wrapper = new LambdaQueryWrapper<>();
        //设置条件id ,只有id不为空的时候才加上这个条件
        if(baseCategory1.getId()!=null){
            wrapper.eq(BaseCategory1::getId,baseCategory1.getId());
        }
        //设置name,like,只有name!=null,add the following condition
        if (baseCategory1.getName()!=null){
            wrapper.like(BaseCategory1::getName,baseCategory1.getName());
        }
        return wrapper;
    }
}
