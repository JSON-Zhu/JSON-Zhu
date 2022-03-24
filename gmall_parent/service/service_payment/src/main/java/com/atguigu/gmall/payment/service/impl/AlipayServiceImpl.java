package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.alipay.api.AlipayClient;
import com.alipay.api.request.AlipayTradePagePayRequest;
import com.alipay.api.request.AlipayTradeQueryRequest;
import com.alipay.api.response.AlipayTradePagePayResponse;
import com.alipay.api.response.AlipayTradeQueryResponse;
import com.atguigu.gmall.payment.service.AlipayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * AlipayServiceImpl 支付宝支付的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/24 22:54
 **/
@Service
public class AlipayServiceImpl implements AlipayService {

    @Value("${return_payment_url}")
    private String returnPaymentUrl;

    @Value("${notify_payment_url}")
    private String notifyPaymentUrl;

    @Resource
    private AlipayClient alipayClient;

    /**
     * 获取支付宝下单页面
     *
     * @param money
     * @param orderId
     * @param desc
     * @return
     */
    @Override
    public String getPayUrl(String money, String orderId, String desc) {
        //声明请求体
        AlipayTradePagePayRequest request = new AlipayTradePagePayRequest();
        //设置通知地址
        request.setNotifyUrl(notifyPaymentUrl);
        //设置回调地址
        request.setReturnUrl(returnPaymentUrl);
        //设置请求参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        bizContent.put("total_amount", money);
        bizContent.put("subject", desc);
        bizContent.put("product_code", "FAST_INSTANT_TRADE_PAY");
        request.setBizContent(bizContent.toString());
        try {
            //发起请求获取结果
            AlipayTradePagePayResponse response = alipayClient.pageExecute(request);
            if(response.isSuccess()){
                //支付页面
                return response.getBody();
            } else {
                return null;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询订单的支付结果
     *
     * @param orderId
     * @return
     */
    @Override
    public String getPayResult(String orderId) {
        //请求体初始化
        AlipayTradeQueryRequest request = new AlipayTradeQueryRequest();
        //包装参数
        JSONObject bizContent = new JSONObject();
        bizContent.put("out_trade_no", orderId);
        request.setBizContent(bizContent.toString());
        try {
            //请求获取结果
            AlipayTradeQueryResponse response = alipayClient.execute(request);
            //返回结果
            return response.getBody();
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 关闭交易
     *
     * @param orderId
     * @return
     */
    @Override
    public void closePay(String orderId) {

    }
}
