package com.atguigu.gmall.activity.listener;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.gmall.activity.pojo.SeckillOrder;
import com.atguigu.gmall.activity.pojo.UserRecord;
import com.atguigu.gmall.activity.util.SecKillOrderStatusConst;
import com.atguigu.gmall.model.activity.SeckillGoods;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.UUID;

/**
 * 秒杀订单的消费者
 *
 * @author XQ.Zhu
 */

@Component
@Log4j2
public class SecKillOrderAddListener {

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 监听排队的消息,完成异步下单
     * @param channel
     * @param message
     * @return : void
     */
    @RabbitListener(queues = "seckill_order_queue")
    public void seckillOrderAdd(Channel channel, Message message){
        byte[] body = message.getBody();
        String s = new String(body);
        MessageProperties messageProperties = message.getMessageProperties();
        long deliveryTag = messageProperties.getDeliveryTag();
        //消息反序列化
        UserRecord userRecord = JSONObject.parseObject(s, UserRecord.class);
        try {
//            int i=1/0;
            //秒杀下单
            realSeckillOrderAdd(userRecord);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            e.printStackTrace();
            try {
                //判断消息是否被消费过
                if(messageProperties.getRedelivered()){
                    //消费了2次: 下单失败
                    updateUserRecord(userRecord);
                    //拒绝消费,从队列移除消息
                    channel.basicReject(deliveryTag, false);
                }else{
                    //消费了一次:再试一次
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                e1.printStackTrace();
                log.error("秒杀下单拒绝消息失败, 原因为: " + e1.getMessage() + ",用户信息为:" + s);
            }
        }
    }

    /**
     * 秒杀下单方法
     * @param userRecord
     * @return : void
     */
    private void realSeckillOrderAdd(UserRecord userRecord) {
        //获取 用户名字,时间段,商品id,购买数量
        String username = userRecord.getUsername();
        String time = userRecord.getTime();
        String goodsId = userRecord.getGoodsId();
        Integer num = userRecord.getNum();
//        此段判断可以删除,因为redis标识位可以起到拦截作用,
//        判断用户是否存在未支付的订单
//        Object o = redisTemplate.opsForHash()
//                .get("user_seckill_order", username);
//        if(o!=null){
//            //修改用户的派对状态:秒杀失败,写入失败的原因
//            userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_FAIL);
//            userRecord.setMsg("您存在未支付的秒杀订单, 秒杀下单失败, 请购完成未支付的订单后再购买!");
//            //修改redis中的信息
//            redisTemplate.opsForHash().put("user_record",userRecord.getUsername(),userRecord);
//            //删除排队的计数器
//            redisTemplate.delete("user_record_count_"+userRecord.getUsername());
//            //存在未支付订单,直接返回
//            return;
//        }
        //根据goodsId获取商品
        SeckillGoods seckillGood = (SeckillGoods)redisTemplate
                .opsForHash().get(time, goodsId + "");
        if(seckillGood!=null){
            //判断库存是否足够,从商品队列右端操作,拿到的不为空就可以下单
            for (int i = 0; i < num; i++) {
                //循环去队列中取元素
                Object o = redisTemplate.opsForList().rightPop("seckill_goods_stock_queue_" + goodsId);
                if (o==null) {
                    //修改用户的排队状态,秒杀失败,写入失败的原因
                    userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_FAIL);
                    userRecord.setMsg("商品库存不足,秒杀下单失败,请购买其他的商品");
                    //修改redis中的信息
                    redisTemplate.opsForHash().put("user_record",userRecord.getUsername(),userRecord);
                    //删除排队的计数器
                    redisTemplate.delete("user_record_count_"+userRecord.getUsername());
                    //取到的元素为空,回滚库存
                    redisTemplate.opsForList().leftPushAll("seckill_goods_stock_queue_"+goodsId
                            ,getIds(i,Long.parseLong(goodsId)));
                    //返回
                    return;
                }
            }
//            //判断库存是否足够,   ----------执行存在时间差,会有超卖问题产生
//            if (seckillGood.getStockCount()-num>=0){
//                Integer stockNum= seckillGood.getStockCount()-num;
                //生成秒杀订单
                SeckillOrder seckillOrder = new SeckillOrder();
                seckillOrder.setId(UUID.randomUUID().toString().replace("-",""));
                seckillOrder.setGoodsId(goodsId);
                seckillOrder.setNum(num);
                seckillOrder.setMoney(num*seckillGood.getCostPrice().doubleValue()+"");
                seckillOrder.setUserId(username);
                seckillOrder.setCreateTime(new Date());
                seckillOrder.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_WAITING_PAY+"");
                /*
                seckillOrder.setPayTime();
                seckillOrder.setAddress();
                seckillOrder.setMobile();
                seckillOrder.setReceiver();
                seckillOrder.setOutTradeNo();
*/
                //保存订单的额消息--- redis, 只有在付款,取消,超时才会写入数据
                redisTemplate.opsForHash().put("user_seckill_order",username,seckillOrder);
                //修改用户派对状态,秒杀成功,等待付款
                userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_WAITING_PAY);
                userRecord.setMsg("秒杀成功,等待付款");
                //补充订单号
                userRecord.setOrderId(seckillOrder.getId());
                //金额
                userRecord.setMoney(seckillOrder.getMoney());
                //修改redis中的信息
                redisTemplate.opsForHash().put("user_record",username,userRecord);
                //商品如果买完了,--售罄-没有卖完,更新库存, -- 超卖问题
                updateSecKillGoodsStockRedis(num,seckillGood,time);
                //结束
        }else {
            //修改用户的排队状态,写入失败的原因
            userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_FAIL);
            userRecord.setMsg("商品活动结束,下单失败,请购买其他的商品!");
            //修改redis中的信息
            redisTemplate.opsForHash().put("user_record",userRecord.getUsername(),userRecord);
            //删除排队计数器
            redisTemplate.delete("user_record_count_"+userRecord.getUsername());
            return;
        }
//        //商品售完的情况
//        updateUserRecord(userRecord);
    }

    /**
     * 下单后,更新redis中的库存
     * @param num 用户购买的数量
     * @param seckillGood
     * @param time
     * @return : void
     */
    private void updateSecKillGoodsStockRedis(Integer num, SeckillGoods seckillGood, String time) {
        Long stockNum =
                redisTemplate.opsForHash().increment("seckill_goods_stock_count" + time, seckillGood.getId() + "", -num);
        //设置剩余的库存
        seckillGood.setStockCount(stockNum.intValue());
        //更新商品库存
        redisTemplate.opsForHash().put(time,seckillGood.getId()+"",seckillGood);
    }

    /**
     * 修改用户的秒杀的派对状态, 秒杀失败
     * @param userRecord
     * @return : void
     */
    private void updateUserRecord(UserRecord userRecord) {
        //修改用户的派对状态,
        userRecord.setStatus(SecKillOrderStatusConst.SECKILL_ORDER_STATUS_FAIL);
        userRecord.setMsg("商品卖完了, 秒杀下单失败, 请购买其他的商品!");
        redisTemplate.opsForHash().put("user_record",userRecord.getUsername(),userRecord);
        //删除排队计数器
        redisTemplate.delete("user_record_count_"+userRecord.getUsername());
    }

    /**
     * 创建一个库存长度的数组
     * @param stockCount
     * @param id
     * @return
     */
    private String[] getIds(Integer stockCount, Long id) {
        //剩余多少库存,数组就有多长
        String[] ids = new String[stockCount];
        for (int i = 0; i < ids.length; i++) {
            ids[i]=id+"";
        }
        return ids;
    }
}
