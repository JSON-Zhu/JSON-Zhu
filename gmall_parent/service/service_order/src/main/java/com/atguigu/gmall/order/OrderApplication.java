package com.atguigu.gmall.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * OrderApplication
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/18 14:22
 **/
@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.atguigu.gmall")
@EnableFeignClients(basePackages = {"com.atguigu.gmall.cart.feign","com.atguigu.gmall.product.feign","com.atguigu.gmall.pay.feign"})
@ServletComponentScan(basePackages = "com.atguigu.gmall.order.filter")
public class OrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class,args);
    }
}
