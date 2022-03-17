package com.atguigu.gmall.cart.service.impl;

import com.atguigu.gmall.cart.utils.CartThreadLocalUtils;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.product.SkuInfo;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.atguigu.gmall.cart.mapper.CartInfoMapper;
import com.atguigu.gmall.cart.service.CartInfoService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.segments.MatchSegment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.swing.tree.TreeNode;
import java.io.Externalizable;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * CartInfoServiceImpl 购物车管理的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/13 14:44
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class CartInfoServiceImpl implements CartInfoService {

    @Resource
    private CartInfoMapper cartInfoMapper;

    @Autowired
    private ProductFeign productFeign;

    /**
     * 添加购物车
     *
     * @param skuId
     * @param num
     * @return : void
     */
    @Override
    public void addCartInfo(Long skuId, Integer num) {
        //参数校验
        if(skuId==null||num==null){
            throw new RuntimeException("skuId或购买数量参数错误");
        }
        //查询商品的基本信息,并判断
        SkuInfo skuInfo = productFeign.getSkuInfo(skuId);
        if(skuInfo==null||skuInfo.getId()==null){
            throw new RuntimeException("商品信息查询失败");
        }
        //获取用户名
        String username = CartThreadLocalUtils.get();
        //判断当前商品是否存在购物车
        CartInfo cartInfo = cartInfoMapper.selectOne(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId, username)
                .eq(CartInfo::getSkuId, skuId));
        //判断当前商品是否存在于数据库
        if(cartInfo==null||cartInfo.getId()==null){
            //直接添加,判断num>0
            if(num<=0){
                return;
            }
            cartInfo = new CartInfo();
            //说明当前用户的当前购物车没有这个商品,可以做新增
            cartInfo.setImgUrl(skuInfo.getSkuDefaultImg());
            //商品名字
            cartInfo.setSkuName(skuInfo.getSkuName());
            //设置用户
            cartInfo.setUserId(username);
            //设置商品id
            cartInfo.setSkuId(skuId);
            //远程查询商品的当前价格
            BigDecimal price = productFeign.getPrice(skuId);
            cartInfo.setCartPrice(price);
            //设置购买数量
            cartInfo.setSkuNum(num);
            //新增
            int insert = cartInfoMapper.insert(cartInfo);
            if(insert <= 0){
                throw new RuntimeException("参数错误,新增购物车失败");
            }
        }else {
            //合并数量
            num = cartInfo.getSkuNum() + num;
            if(num>0){
                //合并数据
                cartInfo.setSkuNum(num);
                int update = cartInfoMapper.updateById(cartInfo);
                if(update<=0){
                    throw new RuntimeException("新增购物车失败");
                }
            }else {
                //删除
                cartInfoMapper.deleteById(cartInfo.getId());
            }
        }
    }

    /**
     * 根据用户名查询购物车数据
     *
     * @return
     */
    @Override
    public List<CartInfo> getCartInfo() {
        //获取用户名
        String username = CartThreadLocalUtils.get();
        return cartInfoMapper.selectList(new LambdaQueryWrapper<CartInfo>()
                .eq(CartInfo::getUserId,username));
    }

    /**
     * 修改选中状态
     *
     * @param id
     * @param status
     */
    @Override
    public void updateCartInfo(Long id, Short status) {
        //获取名字
        String username = CartThreadLocalUtils.get();
        int i=0;
        if(id==null){
            i = cartInfoMapper.updateCartInfo(status, username);
        }else{
            i = cartInfoMapper.updateCartInfoOne(status,id);
        }
        if(i<0){
            throw new RuntimeException("修改购物车选中状态失败");
        }
    }

    /**
     * 删除购物车
     *
     * @param id
     */
    @Override
    public void delCartInfo(Long id) {
        if(id==null){
            return;
        }
        cartInfoMapper.deleteById(id);
    }

    /**
     * 合并购物车
     *
     * @param cartInfos
     */
    @Override
    public void mergeCartInfo(List<CartInfo> cartInfos) {
        //批量新增购物车的数据
        cartInfos.stream().forEach(cartInfo -> {
            addCartInfo(cartInfo.getSkuId(),cartInfo.getSkuNum());
        });
    }
}
