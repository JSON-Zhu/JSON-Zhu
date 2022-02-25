package com.atguigu.gmall.list;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * listApplication 搜索微服务的启动类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/22 10:42
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.atguigu.gmall")
@EnableFeignClients(basePackages = "com.atguigu.gmall.product.feign")
public class ListApplication {
    public static void main(String[] args) {
        SpringApplication.run(ListApplication.class,args);
    }
}
