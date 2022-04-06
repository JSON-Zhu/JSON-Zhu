package com.atguigu.gmall.activity.controller;

import com.atguigu.gmall.activity.service.SeckillGoodsService;
import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.activity.util.DateUtil;
import com.atguigu.gmall.common.result.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * SeckillController
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 11:45
 **/

@RestController
@RequestMapping(value = "/api/seckill/goods")
public class SecondKillController{
    @Autowired
    private SeckillOrderService seckillOrderService;

    @Autowired
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询指定时间段的秒杀商品列表
     * @param time
     * @return
     */
    @GetMapping(value = "/getSeckillGoods")
    public Result getSeckillGoods(String time){
        return Result.ok(seckillGoodsService.getSeckillGoods(time));
    }

    /**
     * 获取时间段菜单信息
     * @return
     */
    @GetMapping(value = "/getTimeList")
    public Result getTimeList(){
        return Result.ok(DateUtil.getDateMenus());
    }

    /**
     * 获取指定时间段的指定商品
     * @param time
     * @param goodsId
     * @return
     */
    @GetMapping(value = "/getSeckillGoodsDetail")
    public Result getSeckillGoodsDetail(String time, String goodsId){
        return Result.ok(seckillGoodsService.getSeckillGoodsDetail(time, goodsId));
    }

    /**
     * 测试同名参数
     * @param map
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/test")
//    public Result test(@RequestBody Map<String,String> map){
//        for (Map.Entry<String, String> entry : map.entrySet()) {
//            System.out.println(entry);
//        }
//        String s = map.get("var");
//        return Result.ok();
//    }

    public Result test(HttpServletRequest request, HttpServletResponse response){
        String var = request.getParameter("var");
        return Result.ok();
    }
}
