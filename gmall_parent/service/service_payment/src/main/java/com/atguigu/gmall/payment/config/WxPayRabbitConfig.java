package com.atguigu.gmall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 支付相关的消息通知的交换机 队列 创建
 * @author XQ.Zhu
 */
@Configuration
public class WxPayRabbitConfig {

    /**
     * 创建交换机
     */
    @Bean("OrderPayExchange")
    public Exchange OrderPayExchange(){
        return ExchangeBuilder.directExchange("order_pay_exchange").build();
    }

    /**
     * 创建队列:微信
     */
    @Bean("wxPayOrderQueue")
    public Queue wxPayOrderQueue(){
        return QueueBuilder.durable("wx_pay_order_queue").build();
    }

    /**
     * 创建队列:支付宝
     */
    @Bean("zfbPayOrderQueue")
    public Queue zfbPayOrderQueue(){
        return QueueBuilder.durable("zfb_pay_order_queue").build();
    }

    /**
     * 创建队列:微信-秒杀
     */
    @Bean("wxPaySeckillOrdeQueue")
    public Queue wxPaySeckillOrdeQueue(){
        return QueueBuilder.durable("wx_pay_seckill_order_queue").build();
    }

    /**
     * 创建队列:支付宝-秒杀
     */
    @Bean("zfbPaySeckillOrdeQueue")
    public Queue zfbPaySeckillOrdeQueue(){
        return QueueBuilder.durable("zfb_pay_seckill_order_queue").build();
    }

    /**
     * 创建绑定:微信绑定
     */
    @Bean
    public Binding wxPayOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                     @Qualifier("wxPayOrderQueue") Queue wxPayOrderQueue){
        return BindingBuilder.bind(wxPayOrderQueue).to(OrderPayExchange).with("pay.order.wx").noargs();
    }

    /**
     * 创建绑定:支付宝绑定
     */
    @Bean
    public Binding zfbPayOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                      @Qualifier("zfbPayOrderQueue") Queue zfbPayOrderQueue){
        return BindingBuilder.bind(zfbPayOrderQueue).to(OrderPayExchange).with("pay.order.zfb").noargs();
    }

    /**
     * 创建绑定:微信绑定-秒杀
     */
    @Bean
    public Binding wxPaySeckillOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                     @Qualifier("wxPaySeckillOrdeQueue") Queue wxPaySeckillOrdeQueue){
        return BindingBuilder.bind(wxPaySeckillOrdeQueue).to(OrderPayExchange).with("pay.seckill.order.wx").noargs();
    }

    /**
     * 创建绑定:支付宝绑定-秒杀
     */
    @Bean
    public Binding zfbPaySeckillOrderBinding(@Qualifier("OrderPayExchange") Exchange OrderPayExchange,
                                      @Qualifier("zfbPaySeckillOrdeQueue") Queue zfbPaySeckillOrdeQueue){
        return BindingBuilder.bind(zfbPaySeckillOrdeQueue).to(OrderPayExchange).with("pay.seckill.order.zfb").noargs();
    }
}
