package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SecKillOrderController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 23:16
 **/

@RestController
@RequestMapping(value = "/api/seckill/order")
public class SecKillOrderController {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 秒杀下单: 假下单真排队
     * @return
     */
    @GetMapping(value = "/addSeckillOrder")
    public Result addSeckillOrder(String time, String goodsId, Integer num){
        return Result.ok(seckillOrderService.addSeckillOrder(time, goodsId, num));
    }

    /**
     * 查询用户的排队状态
     * @return
     */
    @GetMapping(value = "/getUserRecord")
    public Result getUserRecord(){
        return Result.ok(seckillOrderService.getUserRecode());
    }

}
