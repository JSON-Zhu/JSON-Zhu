package com.atguigu.gmall.cart.utils;

/**
 * CartThreadLocalUtils
 * 购物车服务的本地线程的工具类
 * @author XQ.Zhu
 * @version 1.0
 * 2022/3/14 17:26
 **/
public class CartThreadLocalUtils {

    /**
     * 购物车微服务使用的本地线程对象
     * @param null
     * @return : null
     */
    private final static ThreadLocal<String> THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 保存用户名:
     * 1.存储在各自的线程中,
     * 2.读取速度快
     * 3.安全,线程是隔离的 独立的
     * 4.不能放太大的对象
     * @param username
     * @return : void
     */
    public static void set(String username){
        THREAD_LOCAL.set(username);
    }

    public static String get(){
        return THREAD_LOCAL.get();
    }

}
