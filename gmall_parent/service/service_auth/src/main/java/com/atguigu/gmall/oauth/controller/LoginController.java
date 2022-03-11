package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.oauth.service.LoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * LoginController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/9 19:36
 **/

@RestController
@RequestMapping(value = "/user/login")
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 自定义登录
     * @param username
     * @param password
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping
    public Result login(String username, String password){

        return Result.ok(loginService.login(username, password));
    }
}
