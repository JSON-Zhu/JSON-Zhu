package com.atguigu.gmall.common.cache;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

/**
 * GmallCacheAspect2 商城项目的缓存的aop切面类
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 21:12
 **/
@Component
@Aspect
public class GmallCacheAspect2 {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedissonClient redissonClient;

    /**
     * 增强方法
     * @param point
     * @return : java.lang.Object
     */
    @Around("@annotation(com.atguigu.gmall.common.cache.GmallCache2)")
    public Object cacheAroundAdvice(ProceedingJoinPoint point){
        //返回结果初始化
        Object result=null;
        try {
            //获取方法参数
            Object[] args = point.getArgs();
            //获取方法签名
            MethodSignature signature = (MethodSignature) point.getSignature();
            //获取注解
            GmallCache2 gmallCache2 = signature.getMethod().getAnnotation(GmallCache2.class);
            //获取指定prefix
            String prefix = gmallCache2.prefix();
            //拼接存储需要的key
            String key = prefix + Arrays.asList(args).toString();
            //从缓存获取数据
            result= cacheHit(signature,key);
            if(result!=null){
                return result;
            }
            //getLock= sku:[1]:lock
            String lockKey = key + ":lock";
            //获取锁
            RLock lock = redissonClient.getLock(lockKey);
            //尝试加锁
            boolean flag = lock.tryLock(100, 100, TimeUnit.SECONDS);
            if(flag){
                try {
                    try {
                        //查询数据
                        result = point.proceed(point.getArgs());
                        //数据库也没有数据
                        if(result==null){
                        //初始化一个对象存入缓存,有效期5分钟
                            Object o = new Object();
                            redisTemplate.opsForValue().set(key,
                                    JSONObject.toJSONString(o),300,TimeUnit.SECONDS);
                            return null;
                        }else {
                            //有数据则存入redis,有效期1天
                            redisTemplate.opsForValue().set(key,
                                    JSONObject.toJSONString(result),24*60*60,TimeUnit.SECONDS);
                        }
                    }catch (Throwable throwable){
                        throwable.printStackTrace();
                    }
                    return result;
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    //释放锁
                    lock.unlock();
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 从redis获取数据
     * @param signature
     * @param key
     * @return : java.lang.Object
     */
    private Object cacheHit(MethodSignature signature, String key) {
        String cache = (String)redisTemplate.opsForValue().get(key);
        //判断是否为空,若为空则说明没有数据
        if(StringUtils.isNotBlank(cache)){
            //有,则反序列化,直接返回
            Class returnType = signature.getReturnType();
            return JSONObject.parseObject(cache,returnType);
        }
        //没有数据直接返回Null
        return null;
    }

}
