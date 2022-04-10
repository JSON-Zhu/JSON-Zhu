package com.atguigu.gmall.activity.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.activity.util.PayWayConst;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * SeckillOrderPayListener
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/4/10 1:45
 **/
@Component
@Log4j2
public class SeckillOrderPayListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监控秒杀订单的微信支付的队列
     *
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "wx_pay_seckill_order_queue")
    public void secKillOrderExpiration(Channel channel, Message message) {
        //获取消息
        byte[] body = message.getBody();
        String s = new String(body);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //获取秒杀订单的支付结果
            Map map = JSONObject.parseObject(s, Map.class);
            //同步订单数据
            seckillOrderService.updateOrderPayStatus(map, PayWayConst.WXPAY);
            //确认消息
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            e.printStackTrace();
            try {
                //判断消息是否被消费过
                if (messageProperties.getRedelivered()) {
                    //消费了2次: 下单失败
                    log.error("获取秒杀订单支付结果失败,失败的订单为:"+s);
                    //拒绝消费,从队列移除消息
                    channel.basicReject(deliveryTag, false);
                } else {
                    //消费了一次:再试一次
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                e1.printStackTrace();
                log.error("拒绝消费秒杀订单信息失败, 原因为: " + e1.getMessage() + ",详情为:" + s);
            }
        }
    }
}
