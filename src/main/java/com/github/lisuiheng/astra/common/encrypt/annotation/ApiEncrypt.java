package com.github.lisuiheng.astra.common.encrypt.annotation;

import java.lang.annotation.*;

/**
 * API加密注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ApiEncrypt {

    /**
     * 响应加密忽略，默认不加密，为 true 时加密
     */
    boolean response() default false;

}