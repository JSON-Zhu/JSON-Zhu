package com.atguigu.gmall.order.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.PayWayConst;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * OrderPayListener
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/24 15:21
 **/
@Component
@Log4j2
public class OrderPayListener {

    @Autowired
    private OrderService orderService;

    /**
     * 监听订单的支付结果
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "wx_pay_order_queue")
    public void orderPayMessageWx(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        String s = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            Map map = JSONObject.parseObject(s, Map.class);
            //修改订单的支付结果
            orderService.updateOrderPayStatus(map,PayWayConst.WXPAY);
            System.out.println("s = " + s);
            System.out.println("map = " + map);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //消费消息失败,判断是否第一次
                if(messageProperties.getRedelivered()){
                    //若非第一次,则将消息拒绝消费,不放回队列
                    channel.basicReject(deliveryTag, false);
                    log.error("订单支付结果修改失败,请复核,详细内容为:" + s);
                }else{
                    //若第一次消费,则返回队列再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("订单支付结果消息修改时,拒绝消息失败,订单的信息为:" + s + ",错误的内容为:" + e.getMessage());
            }
        }
    }

    /**
     * 监听订单的支付结果,修改订单的状态---支付宝的
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "zfb_pay_order_queue")
    public void orderPayMessageZfb(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        String s = new String(body);
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            Map<String, String> map = JSONObject.parseObject(s, Map.class);
            //修改订单的支付结果
            orderService.updateOrderPayStatus(map, PayWayConst.ALIPAY);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //消费消息失败,判断是否第一次
                if(messageProperties.getRedelivered()){
                    //若非第一次,则将消息拒绝消费,不放回队列
                    channel.basicReject(deliveryTag, false);
                    log.error("订单支付结果修改失败,请复核,完整支付内容为:" + s);
                }else{
                    //若第一次消费,则返回队列再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("订单支付结果修改时,拒绝消息失败,订单的信息为:" + s + ",错误的内容为:" + e.getMessage());
            }
        }
    }
}
