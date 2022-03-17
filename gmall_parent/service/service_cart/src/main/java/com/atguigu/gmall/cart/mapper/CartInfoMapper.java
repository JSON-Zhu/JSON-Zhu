package com.atguigu.gmall.cart.mapper;

import com.atguigu.gmall.model.cart.CartInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * CartInfoMapper 购物车信息的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/13 14:39
 **/

@Mapper
public interface CartInfoMapper extends BaseMapper<CartInfo> {

    /**
     * 修改购物车选中状态
     * @param status
     * @param username
     * @return : int
     */
    @Update({"update cart_info set is_checked=#{status} where user_id=#{username}"})
    int updateCartInfo(@Param("status") Short status,  @Param("username") String username);

    /**
     * 修改单条购物车数据的选中状态
     * @param status
     * @param id
     * @return
     */
    @Update("update cart_info set is_checked = #{status} where id = #{id}")
    int updateCartInfoOne(@Param("status") Short status, @Param("id") Long id);
}
