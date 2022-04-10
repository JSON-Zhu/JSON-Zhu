package com.atguigu.gmall.activity.listener;

import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SecKillOrderExpirationListener 秒杀订单超时的监听类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/4/8 23:32
 **/
@Component
@Log4j2
public class SecKillOrderExpirationListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监控秒杀订单超时的未支付订单
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "seckill_order_queue")
    public void secKillOrderExpiration(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        String s = new String(body);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //同步redis消息到数据库
            seckillOrderService.cancelSecKillOrder(s,"超时取消");
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //判断消息是否被消费过
                if (messageProperties.getRedelivered()) {
                    //消费了2次: 下单失败
                    log.error("秒杀订单超时取消时失败,失败的订单为:");
                    //拒绝消费,从队列移除消息
                    channel.basicReject(deliveryTag, false);
                } else {
                    //消费了一次:再试一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                log.error("秒杀订单超时取消时失败, 原因为: " + e1.getMessage() + ",详情为:" + s);
            }
        }
    }

}
