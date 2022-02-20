package com.atguigu.gmall.product.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.product.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * TestController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 16:04
 **/
@RestController
@RequestMapping(value = "/api/product")
public class TestController {

    @Autowired
    private TestService testService;

    //ab -n 5000 -c 100 http://192.168.200.1:8206/api/product/test
    /**
     * redis测试的方法
     * @return
     */
    @GetMapping(value = "/test")
    public Result test(){
        testService.setRedisByRedssion();
        return Result.ok();
    }

    @GetMapping(value = "/test2")
    public Result test2(){
        testService.setRedis();
        return Result.ok();
    }
}
