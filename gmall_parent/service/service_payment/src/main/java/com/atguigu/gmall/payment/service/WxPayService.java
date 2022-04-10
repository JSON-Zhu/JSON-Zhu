package com.atguigu.gmall.payment.service;

import java.util.Map;

/**
 * WxPayService 微信支付的接口类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/23 20:33
 **/

public interface WxPayService {

    /**
     * 调用微信获取支付二维码
     * @param paramMap
     * @return : java.util.Map<java.lang.String,java.lang.String>
     */
    Map<String,String> getPayUrl(Map<String,String> paramMap);

    /**
     * 查询订单支付结果
     * @param orderId
     * @return : java.util.Map<java.lang.String,java.lang.String>
     */
    Map<String,String> getPayResult(String orderId);

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    Map<String, String> closePay(String orderId);

}
