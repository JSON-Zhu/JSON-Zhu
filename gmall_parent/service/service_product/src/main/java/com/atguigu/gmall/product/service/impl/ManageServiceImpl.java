package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.model.product.*;
import com.atguigu.gmall.product.mapper.*;
import com.atguigu.gmall.product.service.ManageService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
@Transactional(rollbackFor = Exception.class)
public class ManageServiceImpl implements ManageService {

    @Resource
    private BaseCategory1Mapper baseCategory1Mapper;

    @Resource
    private BaseCategory2Mapper baseCategory2Mapper;

    @Resource
    private BaseCategory3Mapper baseCategory3Mapper;

    @Resource
    private BaseAttrInfoMapper baseAttrInfoMapper;

    @Resource
    private BaseAttrValueMapper baseAttrValueMapper;

    @Resource
    private BaseTrademarkMapper baseTrademarkMapper;

    @Resource
    private BaseSaleAttrMapper baseSaleAttrMapper;
    /**
     * 查询所有的一级分类
     *
     * @return
     */
    @Override
    public List<BaseCategory1> getCategory1() {
        return baseCategory1Mapper.selectList(null);
    }

    /**
     * 根据一级id 查询二级分类
     *
     * @param category1Id
     * @return
     */
    @Override
    public List<BaseCategory2> getCategory2(Long category1Id) {
        LambdaQueryWrapper<BaseCategory2> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory2::getCategory1Id, category1Id);
        List<BaseCategory2> baseCategory2List = baseCategory2Mapper.selectList(wrapper);
        return baseCategory2List;
    }

    /**
     * 根据二级id 查询三级分类
     *
     * @param category2Id
     * @return
     */
    @Override
    public List<BaseCategory3> getCategory3(Long category2Id) {
        LambdaQueryWrapper<BaseCategory3> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseCategory3::getCategory2Id, category2Id);
        List<BaseCategory3> baseCategory3List = baseCategory3Mapper.selectList(wrapper);
        return baseCategory3List;
    }

    /**
     * 保存平台属性
     *
     * @param baseAttrInfo
     */
    @Override
    public void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo) {
        //参数校验
        if (baseAttrInfo == null) {
            throw new RuntimeException("参数错误");
        }
        //判断是新增还是修改
        if (baseAttrInfo.getId() != null) {
            //前端传递了id,此时为修改,先修改attr_info表
            int update = baseAttrInfoMapper.updateById(baseAttrInfo);
            if (update <= 0) {
                throw new RuntimeException("修改平台属性失败!");
            }
            //将旧的平台属性的所有的值全部删除
            baseAttrValueMapper.delete(
                    new LambdaQueryWrapper<BaseAttrValue>().
                            eq(BaseAttrValue::getAttrId, baseAttrInfo.getId()));
        } else {
            //新增
            int insert = baseAttrInfoMapper.insert(baseAttrInfo);
            if (insert <= 0) {
                throw new RuntimeException("保存平台属性失败");
            }
        }
        //获取平台属性名称的id
        Long attrInfoId = baseAttrInfo.getId();
        //获取平台属性对应的值的列表
        List<BaseAttrValue> attrValueList = baseAttrInfo.getAttrValueList();
        attrValueList.stream().forEach(baseAttrValue -> {
            //为每一个平台属性补充这个值所属的平台属性名称的id
            baseAttrValue.setAttrId(attrInfoId);
            //保存平台属性值到数据库
            int insert = baseAttrValueMapper.insert(baseAttrValue);
            if (insert <= 0) {
                throw new RuntimeException("保存平台属性值失败");
            }
        });

//        for (BaseAttrValue baseAttrValue : attrValueList) {
//            //为每一个平台属性补充这个值所属的平台属性名称的id
//            baseAttrValue.setAttrId(attrInfoId);
//            //保存平台属性值到数据库
//            int insert = baseAttrValueMapper.insert(baseAttrValue);
//            if (insert <= 0) {
//                throw new RuntimeException("保存平台属性值失败");
//            }
//        }

    }

    /**
     * 根据分类查询平台属性信息
     *
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    @Override
    public List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id) {
        List<BaseAttrInfo> baseAttrInfoList = baseAttrInfoMapper.selectBaseAttrInfoByCategoryId
                (category1Id, category2Id, category3Id);
        return baseAttrInfoList;
    }

    /**
     * 获取属性值列表
     *
     * @param attrId
     * @return
     */
    @Override
    public List<BaseAttrValue> getAttrValueList(Long attrId) {
        List<BaseAttrValue> baseAttrValueList = baseAttrValueMapper.
                selectList(new LambdaQueryWrapper<BaseAttrValue>().eq(BaseAttrValue::getAttrId, attrId));
        return baseAttrValueList;
    }

    /**
     * 获取全部商标数据
     *
     * @return
     */
    @Override
    public List<BaseTrademark> getTrademarkList() {
        return baseTrademarkMapper.selectList(null);
    }

    /**
     * 获取属性列表
     *
     * @return
     */
    @Override
    public List<BaseSaleAttr> baseSaleAttrList() {
        return baseSaleAttrMapper.selectList(null);
    }
}




