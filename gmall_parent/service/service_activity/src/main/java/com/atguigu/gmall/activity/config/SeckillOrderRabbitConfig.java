package com.atguigu.gmall.activity.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀订单的消息队列创建
 */
@Configuration
public class SeckillOrderRabbitConfig {

    /**
     * 正常交换机
     */
    @Bean("seckillOrderExchange")
    public Exchange seckillOrderExchange(){
        return ExchangeBuilder.directExchange("seckill_order_exchange").build();
    }

    /**
     * 死信交换机
     */
    @Bean("seckillOrderDeadExchange")
    public Exchange seckillOrderDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_order_dead_exchange").build();
    }

    /**
     * 同步订单的死信队列
     */
    @Bean("seckillOrderDeadQueue")
    public Queue seckillOrderDeadQueue(){
        return QueueBuilder.durable("seckill_order_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","seckill.order.data")
                .build();
    }

    /**
     * 接收死信交换机的正常队列
     */
    @Bean("seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return QueueBuilder.durable("seckill_order_queue").build();
    }

    /**
     * 正常交换机和死信队列绑定
     */
    @Bean
    public Binding seckillOrderBinding(@Qualifier("seckillOrderExchange") Exchange seckillOrderExchange,
                                       @Qualifier("seckillOrderDeadQueue") Queue seckillGoodDeadsQueue){
        return BindingBuilder.bind(seckillGoodDeadsQueue)
                .to(seckillOrderExchange)
                .with("seckill.order.dead")
                .noargs();
    }

    /**
     * 死信交换机和正常队列绑定
     */
    @Bean
    public Binding seckillOrderDeadBinding(@Qualifier("seckillOrderDeadExchange") Exchange seckillOrderDeadExchange,
                                       @Qualifier("seckillOrderQueue") Queue seckillOrderQueue){
        return BindingBuilder.bind(seckillOrderQueue)
                .to(seckillOrderDeadExchange)
                .with("seckill.order.data")
                .noargs();
    }
}
