package com.atguigu.gmall.user.service.impl;

import com.atguigu.gmall.model.user.UserAddress;
import com.atguigu.gmall.user.mapper.UserAddressMapper;
import com.atguigu.gmall.user.service.UserAddressService;
import com.atguigu.gmall.user.utils.UserThreadLocalUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * UserAddressServiceImpl 用户收货地址接口的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/11 21:48
 **/
@Service
public class UserAddressServiceImpl implements UserAddressService {

    @Resource
    private UserAddressMapper userAddressMapper;

    /**
     * 查询用户的地址信息
     *
     * @return : java.util.List<com.atguigu.gmall.model.user.UserAddress>
     */
    @Override
    public List<UserAddress> getUserAddress() {
        String username = UserThreadLocalUtils.get();
        return userAddressMapper.selectList(
                new LambdaQueryWrapper<UserAddress>().eq(UserAddress::getUserId,username));
    }
}
