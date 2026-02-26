package com.github.lisuiheng.astra.common.ratelimiter.annotation;

import com.github.lisuiheng.astra.common.ratelimiter.enums.LimitType;

import java.lang.annotation.*;

/**
 * 限流注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RateLimiter {

    /**
     * 限流key
     */
    String key() default "";

    /**
     * 限流时间,单位秒
     */
    int time() default 60;

    /**
     * 限流次数
     */
    int count() default 10;

    /**
     * 限流类型
     */
    LimitType limitType() default LimitType.DEFAULT;
}