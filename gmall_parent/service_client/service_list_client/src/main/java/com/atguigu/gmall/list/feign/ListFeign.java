package com.atguigu.gmall.list.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * ListFeign
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/2 16:57
 **/
@FeignClient(name = "service-list",path = "/api/list")
public interface ListFeign {

    /**
     * 商品搜索
     * @param searchData
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/search")
    Map<String, Object> search(@RequestParam Map<String,String> searchData);
}
