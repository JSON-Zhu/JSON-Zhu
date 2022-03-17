package com.atguigu.gmall.cart.service;

import com.atguigu.gmall.model.cart.CartInfo;

import java.util.List;

/**
 * CartInfoService 购物车的接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/13 14:42
 **/
public interface CartInfoService {

    /**
     * 添加购物车
     * @param skuId
     * @param num
     * @return : void
     */
    void addCartInfo(Long skuId, Integer num);

    /**
     * 根据用户名查询购物车数据
     * @return
     */
    List<CartInfo> getCartInfo();

    /**
     * 修改选中状态
     * @param id
     * @param status
     */
    void updateCartInfo(Long id, Short status);

    /**
     * 删除购物车
     * @param id
     */
    void delCartInfo(Long id);

    /**
     * 合并购物车
     * @param cartInfos
     */
    void mergeCartInfo(List<CartInfo> cartInfos);
}
