package com.github.lisuiheng.astra.common.json.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import cn.hutool.extra.spring.SpringUtil;

/**
 * JSON工具类
 */
public class JsonUtils {

    private static final ObjectMapper objectMapper = SpringUtil.getBean(ObjectMapper.class);

    /**
     * 将对象转换为JSON字符串
     *
     * @param obj 待转换对象
     * @return JSON字符串
     */
    public static String toJsonString(Object obj) {
        try {
            return objectMapper.writeValueAsString(obj);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON序列化失败", e);
        }
    }

    /**
     * 将JSON字符串转换为对象
     *
     * @param json JSON字符串
     * @param clazz 目标类
     * @param <T> 泛型类型
     * @return 转换后的对象
     */
    public static <T> T parseObject(String json, Class<T> clazz) {
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("JSON反序列化失败", e);
        }
    }
}