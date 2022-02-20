package com.atguigu.gmall.item.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.HashMap;

/**
 * ItemFeign
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/21 0:49
 **/
@FeignClient(name = "service-item",path = "/admin/item")
public interface ItemFeign {

    /**
     *商品详情的feign接口
     * @param skuId
     * @return : java.util.HashMap<java.lang.String,java.lang.Object>
     */
    @GetMapping(value = "/getItemInfo/{skuId}")
    HashMap<String, Object> getItemInfo(@PathVariable(value = "skuId")Long skuId);
}
