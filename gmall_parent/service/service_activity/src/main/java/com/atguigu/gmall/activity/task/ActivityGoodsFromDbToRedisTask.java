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
import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * ActivityGoodsFromDbToRedisTask 定时写入数据到redis
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/5 0:44
 **/
@Component
@Log4j2
public class ActivityGoodsFromDbToRedisTask {

    @Resource
    private ActivityGoodsMapper activityGoodsMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 写入数据到redis
     * @return : void
     */
    @Scheduled(cron = "1/20 * * * * *")
    public void activityGoodsFromDbToRedis() throws  Exception{
        //获取当前系统时间所在的时间段
        List<Date> dateMenus = DateUtil.getDateMenus();
        //遍历数据
        for (Date dateMenu : dateMenus) {
            //获取时间段的起始时间
            String startTime =
                    DateUtil.data2str(dateMenu, DateUtil.PATTERN_YYYY_MM_DDHHMM);
            //获取结束事件
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
                redisTemplate.opsForHash().put(redisKey,id+"",activityGood);
            }

        }
    }

}
