package com.atguigu.gmall.order.controller;

import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.service.OrderService;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

/**
 * 订单的控制层
 * @author XQ.Zhu
 */
@RestController
@RequestMapping(value = "/api/order")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 新增订单
     * @param orderInfo
     * @return
     */
    @PostMapping(value = "/addOrder")
    public Result addOrder(@RequestBody OrderInfo orderInfo){
        orderService.addOrder(orderInfo);
        return Result.ok();
    }

    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 主动取消订单
     * @param orderId
     * @return
     */
    @GetMapping(value = "/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable(value = "orderId") Long orderId){
        //使用redis标识位,防止用户重复请求
        Long increment = redisTemplate.opsForValue().increment(orderId + "_cancel_order_increment", 1);
        if(increment>1){
            return Result.fail("正在取消这个订单,请不要重复取消!!");
        }
        orderService.cancelOrder(orderId,OrderStatus.CANCEL.getComment());
        //删除标识位--可以不删除
        redisTemplate.delete(orderId + "_cancel_order_increment");
        return Result.ok();
    }

}
