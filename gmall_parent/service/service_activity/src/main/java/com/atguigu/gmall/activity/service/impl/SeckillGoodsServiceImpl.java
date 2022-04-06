package com.atguigu.gmall.activity.service.impl;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.model.activity.SeckillGoods;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * SeckillGoodsServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 18:12
 **/
@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 根据时间段获取秒杀商品的列表信息
     *
     * @param time
     * @return
     */
    @Override
    public List<SeckillGoods> getSeckillGoods(String time) {
        //获取指定时间段秒杀商品的列表
        return (List<SeckillGoods>)redisTemplate.opsForHash().values(time);
    }

    /**
     * 查询指定时间段的某个商品
     *
     * @param time
     * @param goodsId
     * @return
     */
    @Override
    public SeckillGoods getSeckillGoodsDetail(String time, String goodsId) {
        return (SeckillGoods)redisTemplate.opsForHash().get(time, goodsId);
    }
}
