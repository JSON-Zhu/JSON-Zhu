package com.atguigu.gmall.activity.mapper;

import com.atguigu.gmall.activity.pojo.SeckillOrder;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * SeckillOrderMapper 同步订单到数据库的mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/4/9 17:11
 **/
@Mapper
public interface SeckillOrderMapper extends BaseMapper<SeckillOrder> {
}
