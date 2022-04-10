package com.atguigu.gmall.activity.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀商品的消息队列创建
 */
@Configuration
public class SeckillGoodsRabbitConfig {

    /**
     * 正常交换机
     */
    @Bean("seckillGoodsExchange")
    public Exchange seckillGoodsExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_exchange").build();
    }

    /**
     * 死信交换机
     */
    @Bean("seckillGoodsDeadExchange")
    public Exchange seckillGoodsDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_dead_exchange").build();
    }

    /**
     * 同步库存的死信队列
     */
    @Bean("seckillGoodsDeadQueue")
    public Queue seckillGoodsDeadQueue(){
        return QueueBuilder.durable("seckill_goods_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_goods_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.goods.data")
                .build();
    }

    /**
     * 接收死信交换机的正常队列
     */
    @Bean("seckillGoodsQueue")
    public Queue seckillGoodsQueue(){
        return QueueBuilder.durable("seckill_goods_queue").build();
    }

    /**
     * 正常交换机和死信队列绑定
     */
    @Bean
    public Binding seckillGoodsBinding(@Qualifier("seckillGoodsExchange") Exchange seckillGoodsExchange,
                                       @Qualifier("seckillGoodsDeadQueue") Queue seckillGoodDeadsQueue){
        return BindingBuilder.bind(seckillGoodDeadsQueue)
                .to(seckillGoodsExchange)
                .with("seckill.goods.dead")
                .noargs();
    }

    /**
     * 死信交换机和正常队列绑定
     */
    @Bean
    public Binding seckillGoodsDeadBinding(@Qualifier("seckillGoodsDeadExchange") Exchange seckillGoodsDeadExchange,
                                       @Qualifier("seckillGoodsQueue") Queue seckillGoodsQueue){
        return BindingBuilder.bind(seckillGoodsQueue)
                .to(seckillGoodsDeadExchange)
                .with("seckill.goods.data")
                .noargs();
    }
}
