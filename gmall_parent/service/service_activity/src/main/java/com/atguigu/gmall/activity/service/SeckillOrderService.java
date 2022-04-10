package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.activity.pojo.UserRecord;

import java.util.Map;

/**
 * 秒杀商品下单的接口类
 */
public interface SeckillOrderService {

    /**
     * 秒杀下单: 真实为排队,并没有真实下单
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    UserRecord addSeckillOrder(String time, String goodsId, Integer num);

    /**
     * 查询用户的排队状态
     * @return
     */
    UserRecord getUserRecode();

    /**
     * 取消秒杀订单
     * @param username
     * @param message
     * @return : void
     */
    void cancelSecKillOrder(String username, String message);

    /**
     * 修改订单的支付结果
     * @param map
     * @return : void
     */
    void updateOrderPayStatus(Map<String, String> map, Integer payWay);
}
