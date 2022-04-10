package com.atguigu.gmall.payment.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.payment.service.WxPayService;
import com.atguigu.gmall.payment.utils.HttpClient;
import com.github.wxpay.sdk.WXPayConstants;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * WxPayServiceImpl 微信支付的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/23 23:23
 **/
@Service
public class WxPayServiceImpl implements WxPayService {

    /**
     * 公众号id
     */
    @Value("${weixin.pay.appid}")
    private String appId;
    /**
     * 商户号
     */
    @Value("${weixin.pay.partner}")
    private String partner;
    /**
     * 商户的秘钥
     */
    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    /**
     * 回调地址
     */
    @Value("${weixin.pay.notifyUrl}")
    private String notifyUrl;

    /**
     * 调用微信获取支付二维码
     *
     * @param parameterMap  220410 更新参数为map类型
     * @return : java.util.Map<java.lang.String,java.lang.String>
     */
    @Override
    public Map<String, String> getPayUrl(Map<String,String> parameterMap) {
        //获取请求的统一下单的url
        String url = "https://api.mch.weixin.qq.com/pay/unifiedorder";
        HashMap<String, String> paramMap = new HashMap<>();
        //将参数转换为xml格式的数据
        paramMap.put("appid", appId);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("body", parameterMap.get("desc"));
        paramMap.put("out_trade_no", parameterMap.get("orderId"));
        paramMap.put("total_fee", parameterMap.get("money"));
        paramMap.put("spbill_create_ip", "192.168.200.1");
        paramMap.put("notify_url", notifyUrl);
        paramMap.put("trade_type", "NATIVE");
        //防止附加参数过大,删除部分参数
        parameterMap.remove("desc");
        parameterMap.remove("orderId");
        parameterMap.remove("money");
        paramMap.put("attach", JSONObject.toJSONString(parameterMap));
        try {
            //数据转换为xml格式,并签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发送post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.post();
            //获取xml格式的数据
            String contentXML = httpClient.getContent();
            //解析xm格式数据
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXML);
            //判断通讯是否成功
            if(("SUCCESS").equals(resultMap.get("return_code"))&&
                    ("SUCCESS").equals(resultMap.get("result_code"))){
                //工作时跳转到url
                return resultMap;
            }else {
                return resultMap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 查询订单支付结果
     *
     * @param orderId
     * @return : java.util.Map<java.lang.String,java.lang.String>
     */
    @Override
    public Map<String, String> getPayResult(String orderId) {
        //获取请求的查询订单支付结果的url
        String url = "https://api.mch.weixin.qq.com/pay/orderquery";
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        //将参数转换为xml格式的数据
        paramMap.put("appid", appId);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no", orderId);
        try {
            //将参数转换为xml格式,同时生成签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发起post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.post();
            //获取结果:xml格式的数据
            String contentXml = httpClient.getContent();
            //解析xml格式数据
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml);
            //判断通讯是否成功
            if(resultMap.get("return_code").equals("SUCCESS") &&
                    resultMap.get("result_code").equals("SUCCESS")){
                return resultMap;
            }else{
                return resultMap;
            }
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
    public Map<String, String> closePay(String orderId) {
        //获取请求的查询订单支付结果的url
        String url = "https://api.mch.weixin.qq.com/pay/closeorder";
        //包装参数
        Map<String,String> paramMap = new HashMap<>();
        //将参数转换为xml格式的数据
        paramMap.put("appid", appId);
        paramMap.put("mch_id", partner);
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr());
        paramMap.put("out_trade_no", orderId);
        try {
            //将参数转换为xml格式,同时生成签名
            String paramXml = WXPayUtil.generateSignedXml(paramMap, partnerkey);
            //发起post请求
            HttpClient httpClient = new HttpClient(url);
            httpClient.setXmlParam(paramXml);
            httpClient.setHttps(true);
            httpClient.post();
            //获取结果:xml格式的数据
            String contentXml = httpClient.getContent();
            //解析xml格式数据
            Map<String, String> resultMap = WXPayUtil.xmlToMap(contentXml);
            //判断通讯是否成功
            if(resultMap.get("return_code").equals("SUCCESS") &&
                    resultMap.get("result_code").equals("SUCCESS")){
                return resultMap;
            }else{
                return resultMap;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
