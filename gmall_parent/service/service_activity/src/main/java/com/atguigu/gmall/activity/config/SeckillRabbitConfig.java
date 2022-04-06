package com.atguigu.gmall.activity.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 秒杀下单的消息队列创建
 */
@Configuration
public class SeckillRabbitConfig {

    /**
     * 交换机
     */
    @Bean("seckillOrderExchange")
    public Exchange seckillOrderExchange(){
        return ExchangeBuilder.directExchange("seckill_order_exchange").build();
    }

    /**
     * 队列
     */
    @Bean("seckillOrderQueue")
    public Queue seckillOrderQueue(){
        return QueueBuilder.durable("seckill_order_queue").build();
    }

    /**
     * 绑定
     */
    @Bean
    public Binding seckillOrderBinding(@Qualifier("seckillOrderExchange") Exchange seckillOrderExchange,
                                       @Qualifier("seckillOrderQueue") Queue seckillOrderQueue){
        return BindingBuilder.bind(seckillOrderQueue).to(seckillOrderExchange).with("seckill.order.add").noargs();
    }
}
