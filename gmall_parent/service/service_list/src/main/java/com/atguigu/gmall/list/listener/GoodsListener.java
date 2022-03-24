package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.GoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * GoodsListener 商品数据同步的消费者
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/20 22:17
 **/
@Component
@Log4j2
public class GoodsListener {

    @Autowired
    private GoodsService goodsService;

    /**
     * 监听消息: 写入es
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "up_queue")
    public void goodsMessage(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        long skuId = Long.parseLong(new String(body));
        //获取消息tag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //上架操作
            goodsService.addGoodsIntoES(skuId);
            //确认消息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            try {
                //消费失败,重新消费
                if(message.getMessageProperties().getRedelivered()){
                    channel.basicReject(deliveryTag,false);
                }else {
                    channel.basicReject(deliveryTag,true);
                }
                log.error("商品数据同步时,连续两次失败,请检查,skuId:"+skuId);
            }catch (Exception e1){
                log.error("商品数据同步时,商品skuId:"+skuId+"拒绝消息失败,信息为:"+e1.getMessage());
            }
        }
    }

    /**
     * 监听消息: 从es删除
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "down_queue")
    public void goodsMessageRemoveFromES(Channel channel, Message message){
        //获取消息
        byte[] body = message.getBody();
        long skuId = Long.parseLong(new String(body));
        //获取消息tag
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        try {
            //上架操作
            goodsService.delGoodsFromEs(skuId);
            //确认消息
            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            try {
                //消费失败,重新消费
                if(message.getMessageProperties().getRedelivered()){
                    channel.basicReject(deliveryTag,false);
                }else {
                    channel.basicReject(deliveryTag,true);
                }
                log.error("商品数据删除时,连续两次失败,请检查,skuId:"+skuId);
            }catch (Exception e1){
                log.error("商品数据删除时,商品skuId:"+skuId+"拒绝消息失败,信息为:"+e1.getMessage());
            }
        }
    }
}
