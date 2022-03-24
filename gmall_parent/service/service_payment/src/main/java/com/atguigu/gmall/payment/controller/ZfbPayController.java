package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.payment.service.AlipayService;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * ZfbPayController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/24 23:03
 **/
@RestController
@RequestMapping(value = "/api/pay/zfb")
public class ZfbPayController {

    @Autowired
    private AlipayService zfbService;

    /**
     * 支付宝统一下单
     * @param money
     * @param orderId
     * @param desc
     * @return
     */
    @GetMapping(value = "/getPayUrl")
    public String getPayUrl(String money, String orderId, String desc){
        return zfbService.getPayUrl(money, orderId, desc);
    }

    /**
     * 主动查询支付的结果
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayResult")
    public Result getPayResult(String orderId){
        return Result.ok(zfbService.getPayResult(orderId));
    }

    /**
     * 同步回调
     * @return
     */
    @RequestMapping(value = "/callback/return")
    public String returnCallback(@RequestParam Map<String, String> retrunData){
        System.out.println("同步回调成功,返回的参数为:" + retrunData);
        return "用户付钱完成后,跳转到我们商城了!!";
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;
    /**
     * 异步通知: 不及时告知支付宝收到了结果,反复的调用
     * @param retrunData
     * @return
     */
    @RequestMapping(value = "/callback/notify")
    public String notifyCallback(@RequestParam Map<String, String> retrunData){
        System.out.println("异步通知成功,通知的参数为:" + retrunData);
        //String s = JSONObject.toJSONString(retrunData);
        String s="{\"code\":\"10000\",\"msg\":\"Success\",\"buyer_logon_id\":\"141***@qq.com\",\"buyer_pay_amount\":\"0.00\",\"buyer_user_id\":\"2088502211621705\",\"invoice_amount\":\"0.00\",\"out_trade_no\":\"197\",\"point_amount\":\"0.00\",\"receipt_amount\":\"0.00\",\"send_pay_date\":\"2022-03-24 23:25:30\",\"total_amount\":\"0.01\",\"trade_no\":\"2022032422001421701419135906\",\"trade_status\":\"TRADE_SUCCESS\"}";
        //发支付结果的消息
        rabbitTemplate.convertAndSend("order_pay_exchange", "pay.order.zfb", s);
        return "success";
    }
}
