package com.atguigu.gmall.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.cart.feign.CartFeign;
import com.atguigu.gmall.common.result.Result;
import com.atguigu.gmall.model.cart.CartInfo;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.PaymentWay;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderDetail;
import com.atguigu.gmall.model.order.OrderInfo;
import com.atguigu.gmall.order.mapper.OrderDetailsMapper;
import com.atguigu.gmall.order.mapper.OrderInfoMapper;
import com.atguigu.gmall.order.service.OrderService;
import com.atguigu.gmall.order.util.OrderThreadLocalUtil;
import com.atguigu.gmall.order.util.PayWayConst;
import com.atguigu.gmall.pay.feign.PayFeign;
import com.atguigu.gmall.product.feign.ProductFeign;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OrderServiceImpl 订单管理的实现类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/19 1:13
 **/
@Service
@Transactional(rollbackFor = Exception.class)
@Log4j2
public class OrderServiceImpl implements OrderService {

    @Resource
    private OrderInfoMapper orderInfoMapper;

    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    @Autowired
    private CartFeign cartFeign;

    @Autowired
    private ProductFeign productFeign;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 新增订单
     *
     * @param orderInfo
     * @return : void
     */
    @Override
    public OrderInfo addOrder(OrderInfo orderInfo) {
        if(orderInfo==null){
            throw new RuntimeException("订单信息参数错误");
        }
        String username = OrderThreadLocalUtil.get();
        Long increment = redisTemplate.opsForValue().increment(username + "_add_order_increment", 1);
        if(increment>1){
            throw new RuntimeException("订单重复提交");
        }
        try {
            //获取订单,feign->熔断器->线程隔离
            Map<String, Object> orderAddInfo = cartFeign.getOrderAddInfo();
            //查到的数据为空,直接返回
            if(orderAddInfo.isEmpty()) {
                return null;
            }
            Object totalMoney = orderAddInfo.get("totalMoney");
            //设置总金额
            orderInfo.setTotalAmount(new BigDecimal(totalMoney.toString()));
            //设置订单的状态
            orderInfo.setOrderStatus(OrderStatus.UNPAID.getComment());
            //设置userId
            orderInfo.setUserId(username);
            //设置创建时间和失效时间
            orderInfo.setCreateTime(new Date());
            orderInfo.setExpireTime(new Date(System.currentTimeMillis()+30*60*1000));
            //设置订单的进度状态
            orderInfo.setProcessStatus(ProcessStatus.UNPAID.getComment());
            //保存订单信息,获取订单id
            int insert = orderInfoMapper.insert(orderInfo);
            if(insert<=0){
                throw new RuntimeException("新增订单信息失败");
            }
            //获取订单id
            Long id = orderInfo.getId();
            //强制类型转换异常
            List cartInfoList = (List) orderAddInfo.get("cartInfoList");
            //定义商品库存的扣减对象
            ConcurrentHashMap<String, Object> skuDecreaseStockMap =
                    new ConcurrentHashMap<>();
            cartInfoList.stream().forEach(o->{
                //cartInfo是一个linkHashMap--> cartInfo(需要显式转换)
                String jsonString = JSONObject.toJSONString(o);
                CartInfo cartInfo = JSONObject.parseObject(jsonString, CartInfo.class);
                //新建商品详情对象
                OrderDetail orderDetail = new OrderDetail();
                orderDetail.setOrderId(id);
                orderDetail.setSkuId(cartInfo.getSkuId());
                orderDetail.setSkuName(cartInfo.getSkuName());
                orderDetail.setImgUrl(cartInfo.getImgUrl());
                orderDetail.setOrderPrice(cartInfo.getSkuPrice());
                orderDetail.setSkuNum(cartInfo.getSkuNum());
                //保存扣减商品的数据
                skuDecreaseStockMap.put(cartInfo.getSkuId()+"",cartInfo.getSkuNum());
                int insert1 = orderDetailsMapper.insert(orderDetail);
                if(insert1<=0){
                    throw new RuntimeException("新增订单详情失败");
                }
            });
            //收尾1 清空购物车
            //cartFeign.removeCart();
            //收尾2 扣减库存
            if (!productFeign.decreaseStock(skuDecreaseStockMap)) {
                throw new RuntimeException("新增订单失败.");
            }
            //收尾3 发送延迟消息,如果30分后,仍未支付,取消订单
            rabbitTemplate.convertAndSend("order_exchange","order.dead",
                    orderInfo.getId()+"",(message -> {
                        //过期消息设置20s 测试用
                        message.getMessageProperties().setExpiration("20000");
                        return message;
                    }));
        }catch (Exception e){
            log.error(OrderThreadLocalUtil.get()+"用户订单提交失败,失败原因为:"+e.getMessage());
            throw new RuntimeException("新增订单失败");
        }finally {
            //删除redis标识位
            redisTemplate.delete(username + "_add_order_increment");
        }
        return orderInfo;
    }

    @Autowired
    private PayFeign payFeign;

    /**
     * 取消订单
     *
     * @param orderId
     * @param msg 主动取消/延时自动取消
     * @return : void
     */
    @Override
    public void cancelOrder(Long orderId, String msg) {
        OrderInfo orderInfo = orderInfoMapper.selectById(orderId);
        if(orderInfo!=null &&
                orderInfo.getId()!=null &&
                orderInfo.getOrderStatus().equals(OrderStatus.UNPAID.getComment())){
            //关闭交易
            Result result = payFeign.closePay(orderId+"");
            if(!result.isOk()) {
                return;
            }
            //将订单的状态改为取消
            orderInfo.setOrderStatus(msg);
            orderInfo.setProcessStatus(msg);
            int update = orderInfoMapper.updateById(orderInfo);
            if(update<=0){
                return;
            }
            //库存回滚
            Map<String,Object> rollbackMap= rollback(orderId);
            productFeign.rollbackStock(rollbackMap);
        }
    }

    /**
     * 回滚库存
     * @param orderId
     * @return : java.util.Map<java.lang.String,java.lang.Object>
     */
    private Map<String, Object> rollback(Long orderId) {
        //查询需要回滚的订单详情
        ConcurrentHashMap<String, Object> rollbackMAP = new ConcurrentHashMap<>();
        List<OrderDetail> orderDetails = orderDetailsMapper.selectList(
                new LambdaQueryWrapper<OrderDetail>()
                        .eq(OrderDetail::getOrderId, orderId));
        //获取需要回滚的数据
        orderDetails.stream().forEach(orderDetail -> {
            Long skuId = orderDetail.getSkuId();
            Integer skuNum = orderDetail.getSkuNum();
            rollbackMAP.put(skuId+"",skuNum);
        });
        return rollbackMAP;
    }

    /**
     * 修改订单的支付结果
     *
     * @param map
     * @return : void
     */
    @Override
    public void updateOrderPayStatus(Map<String, String> map, Integer payWay) {
        String tradeNo = map.get("out_trade_no");
        OrderInfo orderInfo = orderInfoMapper.selectById(tradeNo);
        if(orderInfo!=null&&
                orderInfo.getId()!=null&&
                OrderStatus.UNPAID.getComment().equals(orderInfo.getOrderStatus())){
            if(payWay.equals(PayWayConst.WXPAY)){
                updateFromWx(map,orderInfo);
            }else{
                updateFromZfb(map,orderInfo);
            }
        }
    }

    /**
     * 微信修改逻辑
     *
     * @param map
     * @return : void
     */
    private void updateFromWx(Map<String, String> map, OrderInfo orderInfo) {
        if("SUCCESS".equals(map.get("result_code"))
                &&"SUCCESS".equals(map.get("return_code"))){
            //订单支付成功,获取订单号
            String transactionId = map.get("transaction_id");
            orderInfo.setOutTradeNo(transactionId);
            //状态
            orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
            orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        }else{
            //修改状态
            orderInfo.setOrderStatus(OrderStatus.FAIL.getComment());
            orderInfo.setProcessStatus(OrderStatus.FAIL.getComment());
        }
        //记录第三方交易的报文
        orderInfo.setTradeBody(JSONObject.toJSONString(map));
        //修改数据
        int i = orderInfoMapper.updateById(orderInfo);
        if(i <= 0){
            throw new RuntimeException("修改订单的状态失败");
        }
    }

    /**
     * 支付宝结果修改
     * @param map
     * @param orderInfo
     */
    private void updateFromZfb(Map<String, String> map, OrderInfo orderInfo){
        //获取支付的结果
        if(map.get("trade_status").equals("TRADE_SUCCESS")){
            //获取支付宝的交易号
            String transactionId = map.get("trade_no");
            //第三方交易的流水号
            orderInfo.setOutTradeNo(transactionId);
            //状态
            orderInfo.setOrderStatus(OrderStatus.PAID.getComment());
            orderInfo.setProcessStatus(ProcessStatus.PAID.getComment());
        }else{
            //修改状态
            orderInfo.setOrderStatus(OrderStatus.FAIL.getComment());
            orderInfo.setProcessStatus(OrderStatus.FAIL.getComment());
        }
        //记录第三方交易的报文
        orderInfo.setTradeBody(JSONObject.toJSONString(map));
        //修改数据
        int i = orderInfoMapper.updateById(orderInfo);
        if(i <= 0){
            throw new RuntimeException("修改订单的状态失败");
        }
    }
}


