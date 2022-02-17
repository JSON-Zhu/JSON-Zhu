package com.atguigu.gmall.product.service;

import com.atguigu.gmall.model.product.*;

import java.util.List;

/**
 * 管理控制台的service接口
 */
public interface ManageService {
    /**
     * 查询所有的一级分类
     * @return
     */
    List<BaseCategory1> getCategory1();

    /**
     * 根据一级id 查询二级分类
     * @return
     * @param category1Id
     */
    List<BaseCategory2> getCategory2(Long category1Id);

    /**
     * 根据二级id 查询三级分类
     * @param category2Id
     * @return
     */
    List<BaseCategory3> getCategory3(Long category2Id);


    /**
     * 保存平台属性
     * @param baseAttrInfo
     */
    void saveBaseAttrInfo(BaseAttrInfo baseAttrInfo);

    /**
     * 根据分类查询平台属性信息
     * @param category1Id
     * @param category2Id
     * @param category3Id
     * @return
     */
    List<BaseAttrInfo> attrInfoList(Long category1Id, Long category2Id, Long category3Id);

    /**
     * 获取属性值列表
     * @param attrId
     * @return
     */
    List<BaseAttrValue> getAttrValueList(Long attrId);

    /**
     * 获取全部商标数据
     * @return
     */
    List<BaseTrademark> getTrademarkList();
}
