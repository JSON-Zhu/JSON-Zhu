package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CartInfoController 新增购物车
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/13 17:21
 **/

@RestController
@RequestMapping(value = "/api/cart")
public class CartInfoController {

    @Autowired
    private CartInfoService cartInfoService;

    /**
     * 新增商品到购物车
     * @param skuId
     * @param num
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/addCart")
    public Result addCart(Long skuId, Integer num){
        cartInfoService.addCartInfo(skuId,num);
        return Result.ok();
    }

    /**
     * 查询购物车数据
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping("/getCartInfo")
    public Result getCartInfo(){
        return Result.ok(cartInfoService.getCartInfo());
    }

    /**
     * 选中: 全部选中/选中某一个
     * @param id: 不为null就是选中某一个, 为null选中全部
     * @return
     */
    @GetMapping(value = "/checked")
    public Result checked(Long id){
        cartInfoService.updateCartInfo(id, CartConst.CART_CHECKED);
        return Result.ok();
    }

    /**
     * 取消选中: 取消全部选中/取消选中某一个
     * @param id: 不为null就是取消选中某一个, 为null取消选中全部
     * @return
     */
    @GetMapping(value = "/cancelChecked")
    public Result cancelChecked(Long id){
        cartInfoService.updateCartInfo(id, CartConst.CART_CANCEL_CHECKED);
        return Result.ok();
    }

    @PostMapping(value = "/mergeCart")
    public Result mergeCart(@RequestBody List<CartInfo> cartInfos){
        cartInfoService.mergeCartInfo(cartInfos);
        return Result.ok();
    }
}
