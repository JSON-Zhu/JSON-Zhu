package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 监听30分后订单的消息,看订单的支付状态
 */
@Component
@Log4j2
public class OrderTimeoutListener {

    @Autowired
    private OrderService orderService;

    /**
     * 取消超时未支付的订单
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "order_queue")
    public void orderTimeoutCancel(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        long orderId = Long.parseLong(new String(body));
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        try {
            //执行订单的取消逻辑
            orderService.cancelOrder(orderId, OrderStatus.TIMEOUT.getComment());
            //确认消息
            channel.basicAck(deliveryTag, false);
            int i=1/0;
        }catch (Exception e){
            try {
                //消费消息失败,判断是否第一次
                if(messageProperties.getRedelivered()){
                    //若非第一次,则将消息拒绝消费,不放回队列
                    channel.basicReject(deliveryTag, false);
                    log.error("订单超时取消失败,请复核,订单号的id为:" + orderId);
                }else{
                    //若第一次消费,则返回队列再来一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("订单超时取消时,订单号:" + orderId + ",拒绝消息失败,错误的内容为:" + e.getMessage());
            }
        }
    }
}
