package com.atguigu.gmall.activity.task;

import com.atguigu.gmall.activity.mapper.ActivityGoodsMapper;
import com.atguigu.gmall.activity.util.DateUtil;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * ActivityGoodsFromDbToRedisTask 定时写入数据到redis
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/5 0:44
 **/
@Component
@Log4j2
public class ActivityGoodsFromDbToRedisTask{

    @Resource
    private ActivityGoodsMapper activityGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 写入数据到redis
     * @return : void
     * 每20秒执行一次
     */
    @Scheduled(cron = "1/20 * * * * *")
    public void activityGoodsFromDbToRedis() throws  Exception {
        //获取当前系统时间所在的时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历数据
        for (Date dateMenu : dateMenus) {
            //获取时间段的起始时间
            String startTime =
                    DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //获取时间段key的过期时间
            Date date = DateUtil.addDateHour(dateMenu, 2);
            //获取结束时间
            String endTime =
                    DateUtil.data2str(DateUtil.addDateHour(dateMenu,2),
                            DateUtil.PATTERN_YYYY_MM_DDHHMM);
            String redisKey = DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYYMMDDHH);
            //查询database
            LambdaQueryWrapper<SeckillGoods> wrapper = new LambdaQueryWrapper<>();
            wrapper.ge(SeckillGoods::getStartTime,startTime);
            wrapper.le(SeckillGoods::getEndTime,endTime);
            wrapper.eq(SeckillGoods::getStatus,"1");
            wrapper.gt(SeckillGoods::getStockCount, 0);
            //判断是否存在redis
            Set keys = redisTemplate.opsForHash().keys(redisKey);
            if(!keys.isEmpty()||keys.size()>0){
                wrapper.notIn(SeckillGoods::getId,keys);
            }
            List<SeckillGoods> activityGoods = activityGoodsMapper.selectList(wrapper);
            //分别存入redis
            for (SeckillGoods activityGood : activityGoods) {
                Long id = activityGood.getId();
                //存入时间段
                redisTemplate.opsForHash().put(redisKey,id+"",activityGood);
                //为商品构建队列,队列的每个元素
                Integer stockCount = activityGood.getStockCount();
                String[] ids = getIds(stockCount, id);
                redisTemplate.opsForList().leftPushAll("seckill_goods_stock_queue_"+id,ids);
                //创建商品的库存展示key
                redisTemplate.opsForHash().increment("seckill_goods_stock_count"+redisKey,id+"",stockCount);
            }
            //存入完成后,设置过期时间
            setSeckillGoodsExpirationTime(redisKey,date);
        }
    }

    /**
     * 创建一个库存长度的数组
     * @param stockCount
     * @param id
     * @return
     */
    private String[] getIds(Integer stockCount, Long id) {
        //剩余多少库存,数组就有多长
        String[] ids = new String[stockCount];
        for (int i = 0; i < ids.length; i++) {
            ids[i]=id+"";
        }
        return ids;
    }

    /**
     * 每个时间段设置过期时间
     * @param redisKey  时间段的key
     * @param date 时间段活动的结束时间
     * @return : void
     */
    private void setSeckillGoodsExpirationTime(String redisKey, Date date) {
        //每个时间段只设置一次过期时间,利用redis中的increment做标识位
        Long increment =
                redisTemplate.opsForHash().increment("seckill_goods_expire_count", redisKey, 1);
        if (increment>1) {
            return;
        }
        //计算时间段的剩余时间
        long timeToLive = date.getTime() - System.currentTimeMillis();
        //设置key的过期时间
        redisTemplate.expire(redisKey,timeToLive, TimeUnit.MILLISECONDS);
    }
}
