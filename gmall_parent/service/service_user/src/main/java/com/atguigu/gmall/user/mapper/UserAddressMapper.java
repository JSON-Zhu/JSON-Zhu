package com.atguigu.gmall.user.mapper;

import com.atguigu.gmall.model.user.UserAddress;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserAddressMapper 用户收货地址的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/11 21:37
 **/
@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {
}
