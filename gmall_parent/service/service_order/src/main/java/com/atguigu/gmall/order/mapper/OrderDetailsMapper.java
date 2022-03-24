package com.atguigu.gmall.order.mapper;

import com.atguigu.gmall.model.order.OrderDetail;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * OrderDetailsMapper 订单详情的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/19 1:10
 **/
@Mapper
public interface OrderDetailsMapper extends BaseMapper<OrderDetail> {
}
