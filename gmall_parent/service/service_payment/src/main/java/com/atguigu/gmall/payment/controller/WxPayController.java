package com.atguigu.gmall.payment.controller;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.payment.service.WxPayService;
import com.github.wxpay.sdk.WXPayUtil;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * WxPayController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/23 17:48
 **/
@RestController
@RequestMapping(value = "/api/pay/wx")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;

    /**
     * 获取微信支付的二维码
     * @param money
     * @param orderId
     * @param desc
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/getPayUrl")
    public Result getPayUrl(String money,String orderId,String desc){
        return Result.ok(wxPayService.getPayUrl(money, orderId, desc));
    }

    /**
     * 主动查询支付的结果
     * @param orderId
     * @return
     */
    @GetMapping(value = "/getPayResult")
    public Result getPayResult(String orderId){
        return Result.ok(wxPayService.getPayResult(orderId));
    }

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 微信回调的方法
     * @param request
     * @return : java.lang.String
     */
    @RequestMapping(value = "/callback/notify")
    public String callbackNotify(HttpServletRequest request) throws Exception{
//        ServletInputStream inputStream = request.getInputStream();
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        byte[] buffer = new byte[1024];
//        int len=0;
//        while((len=inputStream.read(buffer))!= -1) {
//            byteArrayOutputStream.write(buffer,0,len);
//        }
//        //数据流转换为字符串
//        byte[] bytes = byteArrayOutputStream.toByteArray();
//        String resultXml = new String(bytes);
//        //字符串转换为map
//        Map<String, String> resultMap = WXPayUtil.xmlToMap(resultXml);
//        //转换为jsoN字符串
//        String s = JSONObject.toJSONString(resultMap);
//        System.out.println("s = " + s);
        String s="{\"transaction_id\":\"4200001313202203248422892183\",\"nonce_str\":\"6664534fd5d74fbd8a21b51efcd74637\",\"bank_type\":\"OTHERS\",\"openid\":\"oHwsHuNuj90DnQpXzLxmm5Vq9YEY\",\"sign\":\"95D9B4563867810E4D86BE251EC8C5BB\",\"fee_type\":\"CNY\",\"mch_id\":\"1558950191\",\"cash_fee\":\"1\",\"out_trade_no\":\"198\",\"appid\":\"wx74862e0dfcf69954\",\"total_fee\":\"1\",\"trade_type\":\"NATIVE\",\"result_code\":\"SUCCESS\",\"time_end\":\"20220324151209\",\"is_subscribe\":\"N\",\"return_code\":\"SUCCESS\"}";
        //发送到mq
        rabbitTemplate.convertAndSend("order_pay_exchange", "pay.order.wx", s);
        //返回微信,表示已经收到
        HashMap<String, String> returnToWxMap = new HashMap<>();
        returnToWxMap.put("return_code", "SUCCESS");
        returnToWxMap.put("return_msg", "OK");
        return WXPayUtil.mapToXml(returnToWxMap);
    }

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    @GetMapping(value = "/closePay")
    public Result closePay(String orderId){
        return Result.ok(wxPayService.closePay(orderId));
    }

}
