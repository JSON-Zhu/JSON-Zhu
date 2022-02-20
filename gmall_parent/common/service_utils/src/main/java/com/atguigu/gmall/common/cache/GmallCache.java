package com.atguigu.gmall.common.cache;


import java.lang.annotation.*;

/**
 * @author XQ.Zhu
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache {
    String prefix() default "cache";
}
