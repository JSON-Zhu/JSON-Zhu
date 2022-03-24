package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * OrderInfoMapper 订单信息的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/19 1:09
 **/

@Mapper
public interface OrderInfoMapper extends BaseMapper<OrderInfo> {
}
