package com.atguigu.gmall.order.service;

import com.atguigu.gmall.model.order.OrderInfo;

import java.util.Map;

/**
 * OrderService 订单相关的接口类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/19 1:11
 **/
public interface OrderService {

    /**
     * 新增订单
     * @param orderInfo
     * @return : void
     */
    OrderInfo addOrder(OrderInfo orderInfo);

    /**
     * 取消订单
     * @param orderId
     * @param msg
     * @return : void
     */
    void cancelOrder(Long orderId, String msg);

    /**
     * 修改订单的支付结果
     * @param map
     * @return : void
     */
    void updateOrderPayStatus(Map<String, String> map, Integer payWay);
}
