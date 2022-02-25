package com.atguigu.gmall.list.service;

/**
 * GoodsSer vice es商品管理的服务层接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/23 15:10
 **/
public interface GoodsService {

    /**
     * 将商品同步到es
     * @param skuId
     * @return : void
     */
    void addGoodsIntoES(Long skuId);

    /**
     *  从es中删除商品
     * @param skuId
     * @return : void
     */
    void delGoodsFromEs(Long skuId);
}
