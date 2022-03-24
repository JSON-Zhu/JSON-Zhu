package com.atguigu.gmall.cart.controller;

import com.atguigu.gmall.common.constant.CartConst;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.atguigu.gmall.model.cart.CartInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    /**
     * 删除购物车
     * @param id
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/delCart")
    public Result delCart(Long id){
        cartInfoService.delCartInfo(id);
        return Result.ok();
    }

    /**
     * 合并购物车
     * @param cartInfos
     * @return : com.atguigu.gmall.common.result.Result
     */
    @PostMapping(value = "/mergeCart")
    public Result mergeCart(@RequestBody List<CartInfo> cartInfos){
        cartInfoService.mergeCartInfo(cartInfos);
        return Result.ok();
    }

    /**
     * 查询订单确认页面的信息
     * @return : com.atguigu.gmall.common.result.Result
     */
    @GetMapping(value = "/getOrderConfirm")
    public Result getOrderConfirm(){
        return Result.ok(cartInfoService.getOrderConfirm());
    }

    /**
     * 下单时查询共生成订单
     *
     */
    @GetMapping(value = "/getOrderAddInfo")
    public Map<String, Object> getOrderAddInfo(){
        return cartInfoService.getOrderConfirm();
    }

    /**
     * 清空购物车
     *
     */
    @GetMapping(value = "/removeCart")
    public boolean removeCart(){
        return cartInfoService.removeCart();
    }

}
