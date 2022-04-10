package com.atguigu.gmall.activity.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.activity.mapper.SeckillOrderMapper;
import com.atguigu.gmall.activity.pojo.SeckillOrder;
import com.atguigu.gmall.activity.pojo.UserRecord;
import com.atguigu.gmall.activity.service.SeckillOrderService;
import com.atguigu.gmall.activity.util.PayWayConst;
import com.atguigu.gmall.activity.util.SecKillOrderStatusConst;
import com.atguigu.gmall.activity.util.SecKillQueuingStatusConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.atguigu.gmall.model.enums.OrderStatus;
import com.atguigu.gmall.model.enums.ProcessStatus;
import com.atguigu.gmall.model.order.OrderInfo;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * SeckillOrderServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/25 18:14
 **/
@Service
public class SeckillOrderServiceImpl implements SeckillOrderService {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 秒杀下单: 真实为排队,并没有真实下单
     * <p>
     * 从前端传过来的数据 ,1 时间段, 2 商品id ,3 数量
     *
     * @param time
     * @param goodsId
     * @param num
     * @return
     */
    @Override
    public UserRecord addSeckillOrder(String time, String goodsId, Integer num) {
        //包装用户的排队信息
        UserRecord userRecord = new UserRecord();
        //设置时间段
        userRecord.setTime(time);
        //设置商品的id
        userRecord.setGoodsId(goodsId);
        //设置购买的数量
        userRecord.setNum(num);
        //设置生成时间
        userRecord.setCreateTime(new Date());
        //设置用户
        String username = "xianqiang";
        userRecord.setUsername(username);
        //防止重复排队
        Long increment = redisTemplate.opsForValue()
                .increment("user_record_count_" + username, 1);
        if (increment > 1) {
            //说明用户已经排队
            //设置状态
            userRecord.setMsg("重复排队");
            userRecord.setStatus(SecKillQueuingStatusConst.SECKILL_QUEUING_STATUS_FAIL);
            return userRecord;
        }
        //设置状态
        userRecord.setMsg("秒杀排队中");
        userRecord.setStatus(SecKillQueuingStatusConst.SECKILL_QUEUING_STATUS_QUEUING);
        //存储redis
        redisTemplate.opsForHash().put("user_record", username, userRecord);
        //发送秒杀下单的消息
        rabbitTemplate.convertAndSend("seckill_order_exchange",
                "seckill.order.add", JSONObject.toJSONString(userRecord));
        //返回结果,同步返回排队的结果
        return userRecord;
    }

    /**
     * 查询用户的排队状态
     *
     * @return
     */
    @Override
    public UserRecord getUserRecode() {
        String username = "xianqiang";
        return (UserRecord) redisTemplate.opsForHash().get("user_record", username);
    }

    @Resource
    private SeckillOrderMapper seckillOrderMapper;

    /**
     * 取消秒杀订单
     *
     * @param username
     * @param message
     * @return : void
     */
    @Override
    public void cancelSecKillOrder(String username, String message) {
        //从redis获取用户的订单信息
        SeckillOrder userSeckillOrder =
                (SeckillOrder) redisTemplate.opsForHash().get("user_seckill_order", username);
        //判断订单的前置状态是否为--未支付, 幂等性问题
        if (userSeckillOrder != null && (SecKillOrderStatusConst.SECKILL_ORDER_STATUS_UNPAID + "").equals(userSeckillOrder.getStatus())) {
            //将订单的状态修改为-- 主动取消/超时取消
            userSeckillOrder.setStatus(message);
            //订单数据写入数据库--insert
            seckillOrderMapper.insert(userSeckillOrder);
            //获取排队记录
            UserRecord userRecord = (UserRecord) redisTemplate.opsForHash().get("user_record", username);
            //获取购买商品的id,
            String goodsId = userRecord.getGoodsId();
            String time = userRecord.getTime();
            //获取redis中商品的数据
            SeckillGoods seckillGoods = (SeckillGoods) redisTemplate.opsForHash().get(time, goodsId);
            //获取用户购买的数量
            Integer num = userRecord.getNum();
            //更新商品库存值
            Long increment = redisTemplate.opsForHash().increment("seckill_goods_stock_count_" + time, goodsId, num);
            if (seckillGoods != null) {
                //活动未结束,更新三个相关数据
                //更新库存队列
                redisTemplate.opsForList().leftPushAll("seckill_goods_stock_queue_" + goodsId, getIds(num, Long.parseLong(goodsId)));
                //更新库存数量
                seckillGoods.setStockCount(increment.intValue());
                redisTemplate.opsForHash().put(time, goodsId + "", seckillGoods);
            }
            //清理标识位
            redisTemplate.opsForHash().delete("user_record", username);
            //清理排队计数器
            redisTemplate.delete("user_record_count_" + username);
            //清理秒杀订单数据
            redisTemplate.opsForHash().delete("user_seckill_order", username);
        }
    }

    /**
     * 创建一个库存长度的数组
     *
     * @param stockCount
     * @param id
     * @return
     */
    private String[] getIds(Integer stockCount, Long id) {
        //剩余多少库存,数组就有多长
        String[] ids = new String[stockCount];
        for (int i = 0; i < ids.length; i++) {
            ids[i] = id + "";
        }
        return ids;
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
        //获取附加参数
        String s = map.get("attach");
        Map<String, String> attachMap = JSONObject.parseObject(s, Map.class);
        String username = attachMap.get("username");
        //从redis获取订单
        SeckillOrder userSeckillOrder =
                (SeckillOrder) redisTemplate.opsForHash().get("user_seckill_order", username);
        if (userSeckillOrder != null &&
                userSeckillOrder.getStatus().equals(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_UNPAID)
        ) {
            if (payWay.equals(PayWayConst.WXPAY)) {
                updateFromWx(map, userSeckillOrder);
            } else {
                updateFromZfb(map, userSeckillOrder);
            }
        }
        //更新订单完成后,删除标识位
        //清理标识位
        redisTemplate.opsForHash().delete("user_record", username);
        //清理排队计数器
        redisTemplate.delete("user_record_count_" + username);
        //清理秒杀订单数据
        redisTemplate.opsForHash().delete("user_seckill_order", username);
    }

    /**
     * 微信修改逻辑
     *
     * @param map
     * @param seckillOrder
     * @return : void
     */
    private void updateFromWx(Map<String, String> map, SeckillOrder seckillOrder) {
        if ("SUCCESS".equals(map.get("result_code"))
                && "SUCCESS".equals(map.get("return_code"))) {
            //订单支付成功,获取订单号
            String transactionId = map.get("transaction_id");
            seckillOrder.setOutTradeNo(transactionId);
            //状态
            seckillOrder.setStatus("1:支付成功");
        } else {
            //修改状态
            seckillOrder.setStatus("2:支付失败");
        }
        //修改数据
        int i = seckillOrderMapper.insert(seckillOrder);
        if (i <= 0) {
            throw new RuntimeException("同步秒杀订单的状态失败");
        }
    }

    /**
     * 支付宝结果修改
     *  @param map
     * @param seckillOrder
     */
    private void updateFromZfb(Map<String, String> map, SeckillOrder seckillOrder) {
        //获取支付的结果
        if (map.get("trade_status").equals("TRADE_SUCCESS")) {
            //获取支付宝的交易号
            String transactionId = map.get("trade_no");
            //第三方交易的流水号
            seckillOrder.setOutTradeNo(transactionId);
            //状态
            seckillOrder.setStatus("1:支付成功");
        } else {
            //修改状态
            seckillOrder.setStatus("2:支付失败");
        }
        //修改数据
        int i = seckillOrderMapper.insert(seckillOrder);
        if (i <= 0) {
            throw new RuntimeException("同步秒杀订单的状态失败");
        }
    }
}