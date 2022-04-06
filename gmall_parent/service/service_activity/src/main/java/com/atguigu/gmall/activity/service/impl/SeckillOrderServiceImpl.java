package com.atguigu.gmall.activity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.activity.pojo.UserRecord;
import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.activity.util.SecKillOrderStatusConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * SeckillOrderServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 18:14
 **/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀下单: 真实为排队,并没有真实下单
     *
     * 从前端传过来的数据 ,1 时间段, 2 商品id ,3 数量
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecord addSeckillOrder(String time, String goodsId, Integer num) {
        //包装用户的排队信息
        UserRecord userRecord = new UserRecord();
        //设置时间段
        userRecord.setTime(time);
        //设置商品的id
        userRecord.setGoodsId(goodsId);
        //设置购买的数量
        userRecord.setNum(num);
        //设置生成时间
        userRecord.setCreateTime(new Date());
        //设置用户
        String username="xianqiang";
        userRecord.setUsername(username);
        //防止重复排队
        Long increment = redisTemplate.opsForValue()
                .increment("user_record_count_"+username, 1);
        if (increment>1) {
            //说明用户已经排队
            //设置状态
            userRecord.setMsg("重复排队");
            userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_FAIL);
            return userRecord;
        }
        //设置状态
        userRecord.setMsg("秒杀排队中");
        userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_QUEUING);
        //存储redis
        redisTemplate.opsForHash().put("user_record",username,userRecord);
        //发送秒杀下单的消息
        rabbitTemplate.convertAndSend("seckill_order_exchange",
                "seckill.order.add", JSONObject.toJSONString(userRecord));
        //返回结果,同步返回排队的结果

        return userRecord;
    }

    /**
     * 查询用户的排队状态
     *
     * @return
     */
    @Override
    public UserRecord getUserRecode() {
        String username="xianqiang";
        return (UserRecord)redisTemplate.opsForHash().get("user_record",username);
    }
}
