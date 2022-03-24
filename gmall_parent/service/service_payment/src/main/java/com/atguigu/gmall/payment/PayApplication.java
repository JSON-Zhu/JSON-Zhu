package com.atguigu.gmall.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * PayApplication
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/22 17:37
 **/
@SpringBootApplication
@ComponentScan(basePackages = "com.atguigu.gmall")
@EnableDiscoveryClient
public class PayApplication {
    public static void main(String[] args) {
        SpringApplication.run(PayApplication.class, args);
    }
}
