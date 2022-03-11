package com.atguigu.gmall.oauth.mapper;

import com.atguigu.gmall.model.user.UserInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * UserInfoMapper 查询密码的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/11 19:07
 **/
@Mapper
public interface UserInfoMapper extends BaseMapper<UserInfo> {
}
