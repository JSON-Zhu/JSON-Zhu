package com.atguigu.gmall.oauth.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.common.util.IpUtil;
import com.atguigu.gmall.oauth.service.LoginService;
import com.atguigu.gmall.oauth.util.AuthToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

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

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private HttpServletRequest httpServletRequest;
    /**
     * 自定义登录
     * @param username
     * @param password
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping
    public Result login(String username, String password){
        AuthToken authToken = loginService.login(username, password);
        //获取ip地址
        String ipAddress = IpUtil.getIpAddress(httpServletRequest);
        //存入redis key:ipAddress, value: token, ip和token进行绑定
        stringRedisTemplate.opsForValue().set(ipAddress,authToken.getAccessToken());
        return Result.ok(authToken);
    }
}
