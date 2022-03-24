package com.atguigu.gmall.cart.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;

/**
 * CartFeign
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/19 1:21
 **/
@FeignClient(name = "service-cart",path = "/api/cart")
public interface CartFeign {

    /**
     * 生成订单
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/getOrderAddInfo")
    Map<String, Object> getOrderAddInfo();

    /**
     * 清空购物车
     * @return : boolean
     */
    @GetMapping(value = "/removeCart")
    boolean removeCart();
}
