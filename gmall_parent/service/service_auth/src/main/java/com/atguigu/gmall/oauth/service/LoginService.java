package com.atguigu.gmall.oauth.service;

import com.atguigu.gmall.oauth.util.AuthToken;

/**
 * LoginService 自定义登录的服务接口
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/9 19:39
 **/

public interface LoginService {

    /**
     * 登录
     * @param username
     * @param password
     * @return : AuthToken
     */
    public AuthToken login(String username, String password);
}
