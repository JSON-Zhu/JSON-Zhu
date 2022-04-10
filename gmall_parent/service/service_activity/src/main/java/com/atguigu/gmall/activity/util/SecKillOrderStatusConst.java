package com.atguigu.gmall.activity.util;

/**
 * SecKillOrderStatusConst
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 17:35
 **/
public class SecKillOrderStatusConst {

    /**
     *
     * 订单状态，0未支付，1已支付,2 取消订单,3超时未支付
     */
    //未支付
    public static final Integer SECKILL_ORDER_STATUS_UNPAID =0;
    //支付成功
    public static final Integer SECKILL_ORDER_STATUS_PAID =1;
    //取消订单
    public static final Integer SECKILL_ORDER_STATUS_CANCELED =2;
    //秒杀失败
    public static final Integer SECKILL_ORDER_STATUS_TIMEOUT_UNPAID =3;
}
