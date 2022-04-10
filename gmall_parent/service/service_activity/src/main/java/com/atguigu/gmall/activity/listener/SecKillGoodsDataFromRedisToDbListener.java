package com.atguigu.gmall.activity.listener;

import com.atguigu.gmall.activity.mapper.ActivityGoodsMapper;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Set;

/**
 * 商品过期同步数据库消息的消费者
 *
 * @author XQ.Zhu
 */

@Component
@Log4j2
public class SecKillGoodsDataFromRedisToDbListener {

    @Autowired
    private RedisTemplate redisTemplate;

    @Resource
    private ActivityGoodsMapper activityGoodsMapper;

    /**
     * 活动结束的时候同步redis和数据库的商品库存数据
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "seckill_goods_queue")
    public void seckillOrderAdd(Channel channel, Message message){
        //获取消息时间段
        byte[] body = message.getBody();
        String s = new String(body);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //从redis获取整个时间段的全部数据
            Set keys = redisTemplate.opsForHash().keys("seckill_goods_stock_count_" + s);
            if (!keys.isEmpty()&&keys.size()>0) {
                keys.stream().forEach(goodsId->{
                    //同步商品数据
                    Integer o = (Integer)redisTemplate.opsForHash().get("seckill_goods_stock_count_" + s, goodsId);
                    activityGoodsMapper.updateActivityGoodsStock(Long.parseLong(goodsId.toString()),o);
                });
            }
            //清除整个时间段的数据
            redisTemplate.delete("seckill_goods_stock_count_" + s);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            e.printStackTrace();
            try {
                //判断消息是否被消费过
                if(messageProperties.getRedelivered()){
                    //消费了2次: 下单失败
                    log.error("在活动结束时同步redis和数据库的商品库存数据失败,时间段为:"+s);
                    //拒绝消费,从队列移除消息
                    channel.basicReject(deliveryTag, false);
                }else{
                    //消费了一次:再试一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                e1.printStackTrace();
                log.error("同步数据库的拒绝消息失败, 原因为: " + e1.getMessage() + ",时间段信息为:" + s);
            }
        }
    }
}
