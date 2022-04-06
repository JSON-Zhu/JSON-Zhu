package com.atguigu.gmall.activity.service;

import com.atguigu.gmall.model.activity.SeckillGoods;

import java.util.List;

/**
 *
 * 秒杀商品的接口类
 * @author XQ.Zhu
 *
 */
public interface SeckillGoodsService {
    /**
     * 根据时间段获取秒杀商品的列表信息
     * @param time
     * @return
     */
    public List<SeckillGoods> getSeckillGoods(String time);

    /**
     * 查询指定时间段的某个商品
     * @param time
     * @param goodsId
     * @return
     */
    public SeckillGoods getSeckillGoodsDetail(String time, String goodsId);
}
