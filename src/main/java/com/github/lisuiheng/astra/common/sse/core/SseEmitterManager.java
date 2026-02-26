package com.github.lisuiheng.astra.common.sse.core;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.map.MapUtil;
import com.github.lisuiheng.astra.common.redis.utils.RedisUtils;
import com.github.lisuiheng.astra.common.sse.dto.SseMessageDto;
import com.github.lisuiheng.astra.common.utils.SpringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * 管理 Server-Sent Events (SSE) 连接
 *
 * @author 
 */
@Slf4j
public class SseEmitterManager {

    /**
     * 订阅的频道
     */
    private final static String SSE_TOPIC = "global:sse";

    private final static Map<Long, Map<String, SseEmitter>> USER_TOKEN_EMITTERS = new ConcurrentHashMap<>();

    public SseEmitterManager() {
        // 定时执行 SSE 心跳检测
        SpringUtils.getBean(ScheduledExecutorService.class)
            .scheduleWithFixedDelay(this::sseMonitor, 60L, 60L, TimeUnit.SECONDS);
    }

    /**
     * 建立与指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符，用于区分不同用户的连接
     * @param token  用户的唯一令牌，用于识别具体的连接
     * @return 返回一个 SseEmitter 实例，客户端可以通过该实例接收 SSE 事件
     */
    public SseEmitter connect(Long userId, String token) {
        // 从 USER_TOKEN_EMITTERS 中获取或创建当前用户的 SseEmitter 映射表（ConcurrentHashMap）
        // 每个用户可以有多个 SSE 连接，通过 token 进行区分
        Map<String, SseEmitter> emitters = USER_TOKEN_EMITTERS.computeIfAbsent(userId, k -> new ConcurrentHashMap<>());

        // 关闭已存在的SseEmitter，防止超过最大连接数
        SseEmitter oldEmitter = emitters.remove(token);
        if (oldEmitter != null) {
            oldEmitter.complete();
        }

        // 创建一个新的 SseEmitter 实例，超时时间设置为一天 避免连接之后直接关闭浏览器导致连接停滞
        SseEmitter emitter = new SseEmitter(86400000L);

        emitters.put(token, emitter);

        // 当 emitter 完成、超时或发生错误时，从映射表中移除对应的 token
        emitter.onCompletion(() -> {
            log.debug("SSE连接完成 userId:{} token:{}", userId, token);
            disconnect(userId, token);
        });
        emitter.onTimeout(() -> {
            log.debug("SSE连接超时 userId:{} token:{}", userId, token);
            disconnect(userId, token);
        });
        emitter.onError(throwable -> {
            log.warn("SSE连接错误 userId:{} token:{}", userId, token, throwable);
            disconnect(userId, token);
        });

        log.debug("建立SSE连接 userId:{} token:{}", userId, token);
        return emitter;
    }

    /**
     * 断开与指定用户的 SSE 连接
     *
     * @param userId 用户的唯一标识符
     * @param token  用户的唯一令牌
     */
    public void disconnect(Long userId, String token) {
        Map<String, SseEmitter> emitters = USER_TOKEN_EMITTERS.get(userId);
        if (MapUtil.isNotEmpty(emitters)) {
            try {
                SseEmitter sseEmitter = emitters.get(token);
                sseEmitter.send(SseEmitter.event().comment("disconnected"));
                sseEmitter.complete();
            } catch (Exception ignore) {
            }
            emitters.remove(token);
        } else {
            USER_TOKEN_EMITTERS.remove(userId);
        }
    }

    /**
     * SSE 心跳检测，关闭无效连接
     */
    public void sseMonitor() {
        final SseEmitter.SseEventBuilder heartbeat = SseEmitter.event().comment("heartbeat");
        // 记录需要移除的用户ID
        List<Long> toRemoveUsers = new ArrayList<>();

        USER_TOKEN_EMITTERS.forEach((userId, emitterMap) -> {
            if (CollUtil.isEmpty(emitterMap)) {
                toRemoveUsers.add(userId);
                return;
            }

            emitterMap.entrySet().removeIf(entry -> {
                try {
                    entry.getValue().send(heartbeat);
                    return false;
                } catch (Exception ex) {
                    try {
                        entry.getValue().complete();
                    } catch (Exception ignore) {
                        // 忽略重复关闭异常
                    }
                    log.debug("SSE心跳检测移除失效连接 userId:{} token:{}", userId, entry.getKey());
                    return true;
                }
            });

            if (CollUtil.isEmpty(emitterMap)) {
                toRemoveUsers.add(userId);
            }
        });

        // 移除空的用户映射
        toRemoveUsers.forEach(USER_TOKEN_EMITTERS::remove);
    }

    /**
     * 向指定的SSE会话发送消息
     *
     * @param userId  要发送消息的用户id
     * @param message 要发送的消息内容
     */
    public void sendMessage(Long userId, String message) {
        Map<String, SseEmitter> emitters = USER_TOKEN_EMITTERS.get(userId);
        if (MapUtil.isNotEmpty(emitters)) {
            Iterator<Map.Entry<String, SseEmitter>> iterator = emitters.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, SseEmitter> entry = iterator.next();
                try {
                    entry.getValue().send(SseEmitter.event()
                        .name("message")
                        .data(message));
                } catch (Exception e) {
                    SseEmitter remove = emitters.remove(entry.getKey());
                    if (remove != null) {
                        remove.complete();
                    }
                    iterator.remove();
                }
            }
        } else {
            USER_TOKEN_EMITTERS.remove(userId);
        }
    }

    /**
     * 本机全用户会话发送消息
     *
     * @param message 要发送的消息内容
     */
    public void sendMessage(String message) {
        for (Long userId : USER_TOKEN_EMITTERS.keySet()) {
            sendMessage(userId, message);
        }
    }

    /**
     * 发布SSE订阅消息
     *
     * @param sseMessageDto 要发布的SSE消息对象
     */
    public void publishMessage(SseMessageDto sseMessageDto) {
        SseMessageDto broadcastMessage = new SseMessageDto();
        broadcastMessage.setMessage(sseMessageDto.getMessage());
        broadcastMessage.setUserIds(sseMessageDto.getUserIds());
        RedisUtils.publish(SSE_TOPIC, broadcastMessage, consumer -> {
            log.info("SSE发送主题订阅消息topic:{} session keys:{} message:{}",
                SSE_TOPIC, sseMessageDto.getUserIds(), sseMessageDto.getMessage());
        });
    }

    /**
     * 处理接收到的SSE消息
     * 
     * @param message 接收到的SSE消息
     */
    public void handleReceivedMessage(SseMessageDto message) {
        log.info("SSE主题订阅收到消息session keys={} message={}", message.getUserIds(), message.getMessage());
        // 如果key不为空就按照key发消息 如果为空就群发
        if (CollUtil.isNotEmpty(message.getUserIds())) {
            message.getUserIds().forEach(key -> {
                sendMessage(key, message.getMessage());
            });
        } else {
            sendMessage(message.getMessage());
        }
    }
    
    /**
     * 获取SSE主题名称
     */
    public static String getSseTopic() {
        return SSE_TOPIC;
    }
}