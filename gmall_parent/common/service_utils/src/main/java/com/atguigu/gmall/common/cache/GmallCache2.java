package com.atguigu.gmall.common.cache;

import java.lang.annotation.*;

/**
 * GmallCache2
 *
 * @author XQ.Zhu
 * @version 1.0
 * 2022/2/20 21:09
 **/
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface GmallCache2 {
    String prefix() default "cache";
}
