package com.atguigu.gmall.activity.util;

/**
 * SecKillOrderStatusConst
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 17:35
 **/
public class SecKillOrderStatusConst {

    //秒杀排队中
    /**
     *
     */
    public static final Integer SECKILL_ORDER_STATUS_QUEUING =1;
    //秒杀成功等待支付
    public static final Integer SECKILL_ORDER_STATUS_WAITING_PAY =2;
    //秒杀失败
    public static final Integer SECKILL_ORDER_STATUS_FAIL =3;
    //支付成功
    public static final Integer SECKILL_ORDER_STATUS_PAY_SUCCESS =4;

}
