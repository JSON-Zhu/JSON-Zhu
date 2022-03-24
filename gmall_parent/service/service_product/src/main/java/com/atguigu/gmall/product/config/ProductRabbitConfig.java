package com.atguigu.gmall.product.config;

import com.sun.org.apache.regexp.internal.RE;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ProductRabbitConfig 数据同步: 和es和数据库的rabbit配置
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/20 20:37
 **/
@Configuration
public class ProductRabbitConfig {

    /**
     * 创建数据同步的交换机
     * @return : org.springframework.amqp.core.Exchange
     */
    @Bean("listExchange")
    public Exchange listExchange(){
        return ExchangeBuilder.directExchange("list_exchange").build();
    }

    /**
     * 创建上架队列
     * @return : org.springframework.amqp.core.Queue
     */
    @Bean("upQueue")
    public Queue upQueue(){
        return QueueBuilder.durable("up_queue").build();
    }

    /**
     * 创建下架队列
     */
    @Bean("downQueue")
    public Queue downQueue(){
        return QueueBuilder.durable("down_queue").build();
    }

    /**
     * 上架队列和交换机绑定
     * @param upQueue
     * @param listExchange
     * @return : org.springframework.amqp.core.Binding
     */
    @Bean
    public Binding upBinding(@Qualifier("upQueue") Queue upQueue,
                             @Qualifier("listExchange") Exchange listExchange){
        return BindingBuilder.bind(upQueue).to(listExchange).with("sku.up").noargs();
    }

    /**
     * 下架队列和交换机绑定
     */
    @Bean
    public Binding downBinding(@Qualifier("downQueue") Queue downQueue,
                               @Qualifier("listExchange") Exchange listExchange){
        return BindingBuilder.bind(downQueue).to(listExchange).with("sku.down").noargs();
    }
}
