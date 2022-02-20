package com.atguigu.gmall.product.service.impl;

import com.atguigu.gmall.product.service.TestService;
import lombok.extern.log4j.Log4j2;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * TestServiceImpl
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 18:40
 **/
@Service
@Log4j2
public class TestServiceImpl implements TestService {


    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * redis的测试案例
     */
    @Override
    public void setRedis() {
        //生成一个随机的字符串
        String uuid = UUID.randomUUID().toString().replace("-", "");
        //使用redis的setnx加锁: 锁的过期时间
        Boolean lock = redisTemplate.opsForValue().setIfAbsent("lock", uuid, 10, TimeUnit.SECONDS);
        if(lock){
            //从redis中获取一个key=java0823
            Integer i = (Integer)redisTemplate.opsForValue().get("java0823");
            //若这个key的value不为空,则+1
            if(i != null){
                //加完成了以后,将值写回redis中去
                i++;
                redisTemplate.opsForValue().set("java0823", i);
            }
//            //把锁的value取出来
//            String redisuuid = (String)redisTemplate.opsForValue().get("lock");
//            if(uuid.equals(redisuuid)){
//                //自己释放自己的锁,防止锁误删
//                redisTemplate.delete("lock");
//            }
            //定义脚本对象
            DefaultRedisScript defaultRedisScript = new DefaultRedisScript();
            defaultRedisScript.setScriptText("if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end");
            //取值 判断 释放锁能一步完成!--->lua表达式,脚本语言
            //防止报错illegalState
            defaultRedisScript.setResultType(Long.class);
            /**
             * 1.脚本
             * 2.key
             * 3.值
             */
            redisTemplate.execute(defaultRedisScript, Arrays.asList("lock"), uuid);
        }else{
            try {
                Thread.sleep(100);
                //加锁失败
                setRedis();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    @Autowired
    private RedissonClient redissonClient;

    /**
     * redssion的解决方案
     */
    @Override
    public void setRedisByRedssion() {
        //获取锁
        RLock lock = redissonClient.getLock("lock");
        try {
            //本质依然使用的是setnx的操作
            if (lock.tryLock(10, 10, TimeUnit.SECONDS)) {
                try {
                    //加锁成功从redis中获取一个key=java0823
                    Integer i = (Integer) redisTemplate.opsForValue().get("java0823");
                    //若这个key的value不为空,则+1
                    if (i != null) {
                        //加完成了以后,将值写回redis中去
                        i++;
                        redisTemplate.opsForValue().set("java0823", i);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    log.error("获取锁成功以后,出现了异常");
                } finally {
                    //释放锁: lua表达式
                    lock.unlock();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("只是在尝试加锁的时候出现了异常");
            log.error("test");
        }
    }
}
