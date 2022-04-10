package com.atguigu.gmall.activity.util;

/**
 * SecKillOrderStatusConst
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 17:35
 **/
public class SecKillQueuingStatusConst {

    //秒杀排队中
    /**
     * //秒杀状态  1:排队中，2:秒杀等待支付,3:秒杀失败,4:支付完成
     *
     */
    public static final Integer SECKILL_QUEUING_STATUS_QUEUING =1;
    //秒杀成功等待支付
    public static final Integer SECKILL_QUEUING_STATUS_WAITING_PAY =2;
    //秒杀失败
    public static final Integer SECKILL_QUEUING_STATUS_FAIL =3;
    //支付成功
    public static final Integer SECKILL_QUEUING_STATUS_PAY_SUCCESS =4;

}
