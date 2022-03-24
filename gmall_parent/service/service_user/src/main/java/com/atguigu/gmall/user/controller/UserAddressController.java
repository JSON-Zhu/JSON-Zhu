package com.atguigu.gmall.user.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.user.service.UserAddressService;
import com.atguigu.gmall.user.utils.UserThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Hashtable;

/**
 * UserAddressController 用户收货地址的控制层
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/11 21:54
 **/
@RestController
@RequestMapping(value = "/api/user")
public class UserAddressController {

    @Autowired
    private UserAddressService userAddressService;

    /**
     *获取用户的地址
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/getUserAddress")
    public Result getUserAddress(){
        return Result.ok(userAddressService.getUserAddress());
    }
}
