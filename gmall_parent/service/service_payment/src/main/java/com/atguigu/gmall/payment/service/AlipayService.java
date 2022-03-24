package com.atguigu.gmall.payment.service;

/**
 * AlipayService 支付宝的支付接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/24 21:30
 **/
public interface AlipayService {

    /**
     * 获取支付宝下单页面
     * @param money
     * @param orderId
     * @param desc
     * @return
     */
    String getPayUrl(String money, String orderId, String desc);

    /**
     * 查询订单的支付结果
     * @param orderId
     * @return
     */
    String getPayResult(String orderId);

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    void closePay(String orderId);

}
