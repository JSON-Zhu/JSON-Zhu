package com.atguigu.gmall.activity.mapper;

import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * ActivityGoodsMapper 秒杀的持久层mapper
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/5 0:46
 **/

@Mapper
public interface ActivityGoodsMapper extends BaseMapper<SeckillGoods> {

    /**
     * 同步过期的商品库存到数据库
     * @param id
     * @param stockNum
     * @return : int
     */
    @Update("update seckill_goods set stock_count=${num} where id=${id}")
    int updateActivityGoodsStock(@Param("id")long id,@Param("num") Integer stockNum);
}
