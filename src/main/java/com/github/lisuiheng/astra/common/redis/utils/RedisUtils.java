package com.github.lisuiheng.astra.common.redis.utils;

import com.github.lisuiheng.astra.common.json.utils.JsonUtils;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * redis 工具类
 */
@Component
@SuppressWarnings(value = {"unchecked", "rawtypes"})
public class RedisUtils {

    public static RedisTemplate redisTemplate;

    public RedisUtils(RedisTemplate redisTemplate) {
        RedisUtils.redisTemplate = redisTemplate;
    }

    /**
     * 设置缓存对象
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    public static void setCacheObject(final String key, final Object value, final long timeout, final TimeUnit unit) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, timeout, unit);
    }

    /**
     * 设置缓存对象，默认过期时间
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param timeout 过期时间
     */
    public static void setCacheObject(final String key, final Object value, final long timeout) {
        setCacheObject(key, value, timeout, TimeUnit.MINUTES);
    }
    
    /**
     * 设置缓存对象，使用Duration作为过期时间
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param duration 过期时间
     */
    public static void setCacheObject(final String key, final Object value, final Duration duration) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value, duration);
    }
    
    /**
     * 删除缓存对象
     *
     * @param key 缓存键
     * @return 删除成功返回true，否则返回false
     */
    public static Boolean deleteObject(final String key) {
        return redisTemplate.delete(key);
    }
    
    /**
     * 删除匹配的缓存键
     *
     * @param pattern 匹配模式
     * @return 删除的键的数量
     */
    public static Long deleteKeys(final String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            return redisTemplate.delete(keys);
        }
        return 0L;
    }
    
    /**
     * 获取缓存对象
     *
     * @param key 缓存键
     * @return 缓存值
     */
    public static Object getCacheObject(final String key) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        return ops.get(key);
    }
    
    /**
     * 获取缓存对象，带类型转换
     *
     * @param key 缓存键
     * @param clazz 缓存对象类型
     * @return 缓存值
     */
    public static <T> T getCacheObject(final String key, Class<T> clazz) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        Object obj = ops.get(key);
        if (obj != null) {
            try {
                if (clazz.isInstance(obj)) {
                    return clazz.cast(obj);
                } else {
                    // 尝试类型转换
                    return JsonUtils.parseObject(JsonUtils.toJsonString(obj), clazz);
                }
            } catch (Exception e) {
                // 如果转换失败，返回null
                return null;
            }
        }
        return null;
    }
    
    /**
     * 设置缓存对象，默认永不过期
     *
     * @param key   缓存键
     * @param value 缓存值
     */
    public static void setCacheObject(final String key, final Object value) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        ops.set(key, value);
    }
    
    /**
     * 设置缓存对象，可选择是否覆盖已存在的值
     *
     * @param key   缓存键
     * @param value 缓存值
     * @param isUpdateIfExists 是否更新已存在的值
     */
    public static void setCacheObject(final String key, final Object value, final boolean isUpdateIfExists) {
        ValueOperations<String, Object> ops = redisTemplate.opsForValue();
        if (isUpdateIfExists) {
            ops.set(key, value);
        } else {
            ops.setIfAbsent(key, value);
        }
    }
    
    /**
     * 检查缓存键是否存在
     *
     * @param key 缓存键
     * @return 存在返回true，否则返回false
     */
    public static Boolean hasKey(final String key) {
        return redisTemplate.hasKey(key);
    }
    
    /**
     * 获取缓存键的剩余过期时间
     *
     * @param key 缓存键
     * @return 剩余过期时间（毫秒）
     */
    public static Long getTimeToLive(final String key) {
        return redisTemplate.getExpire(key);
    }
    
    /**
     * 设置缓存键的过期时间
     *
     * @param key 缓存键
     * @param duration 过期时间
     * @return 设置成功返回true，否则返回false
     */
    public static Boolean expire(final String key, final Duration duration) {
        return redisTemplate.expire(key, duration);
    }
    
    /**
     * 获取匹配的缓存键
     *
     * @param pattern 匹配模式
     * @return 匹配的键集合
     */
    public static Set<String> keys(final String pattern) {
        return redisTemplate.keys(pattern);
    }

    /**
     * 发布通道消息
     *
     * @param channelKey 通道key
     * @param msg        发送数据
     * @param consumer   自定义处理
     */
    public static <T> void publish(String channelKey, T msg, Consumer<T> consumer) {
        redisTemplate.convertAndSend(channelKey, msg);
        consumer.accept(msg);
    }

    /**
     * 发布消息到指定的频道
     *
     * @param channelKey 通道key
     * @param msg        发送数据
     */
    public static <T> void publish(String channelKey, T msg) {
        redisTemplate.convertAndSend(channelKey, msg);
    }

    /**
     * 订阅通道接收消息
     *
     * @param channelKey 通道key
     * @param clazz      消息类型
     * @param consumer   自定义处理
     */
    public static <T> void subscribe(String channelKey, Class<T> clazz, Consumer<T> consumer) {
        // 这里需要通过 Spring 上下文获取 RedisMessageListenerContainer 来实现订阅
        // 由于直接在工具类中实现复杂，我们建议在具体的服务类中实现订阅逻辑
        // 或者通过 Spring 上下文获取必要的组件来实现
        throw new UnsupportedOperationException("订阅功能需要在具体的监听器中实现");
    }
}