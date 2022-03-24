package com.atguigu.gmall.user.service;

import com.atguigu.gmall.model.user.UserAddress;

import java.util.List;

/**
 * UserAddressService 用户收货地址的service
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/11 21:46
 **/
public interface UserAddressService {

    /**
     * 查询用户的地址信息
     * @return : java.util.List<com.atguigu.gmall.model.user.UserAddress>
     */
    List<UserAddress> getUserAddress();
}
