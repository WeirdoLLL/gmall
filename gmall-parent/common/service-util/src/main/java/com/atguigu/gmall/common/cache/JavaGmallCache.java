package com.atguigu.gmall.common.cache;

import java.lang.annotation.*;

/**
 * 包名:com.atguigu.gmall.common.cache
 *
 * @author Weirdo
 * 日期2023/3/30 15:39
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface JavaGmallCache {
    String prefix() default "cache";
}
