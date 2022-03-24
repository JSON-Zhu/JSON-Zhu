package com.atguigu.gmall.pay.feign;

import com.atguigu.gmall.common.result.Result;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * PayFeign  支付微服务的feign接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/24 22:42
 **/
@FeignClient(name = "service-payment",path = "/api/pay/wx")
public interface PayFeign {

    /**
     * 关闭交易
     * @param orderId
     * @return
     */
    @GetMapping(value = "/closePay")
    Result closePay(String orderId);
}
