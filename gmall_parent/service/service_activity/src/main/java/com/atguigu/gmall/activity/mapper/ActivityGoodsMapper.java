package com.atguigu.gmall.activity.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * ActivityGoodsMapper 秒杀的持久层mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/5 0:46
 **/

@Mapper
public interface ActivityGoodsMapper extends BaseMapper<SeckillGoods> {
}
