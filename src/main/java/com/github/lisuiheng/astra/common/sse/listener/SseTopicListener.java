package com.github.lisuiheng.astra.common.sse.listener;

import com.github.lisuiheng.astra.common.sse.core.SseEmitterManager;
import com.github.lisuiheng.astra.common.sse.dto.SseMessageDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.Ordered;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Component;

/**
 * SSE主题订阅监听器
 */
@Slf4j
@Component
public class SseTopicListener implements ApplicationRunner, Ordered {

    @Autowired
    private SseEmitterManager sseEmitterManager;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisMessageListenerContainer;

    /**
     * 在Spring Boot应用程序启动时初始化SSE主题订阅监听器
     *
     * @param args 应用程序参数
     * @throws Exception 初始化过程中可能抛出的异常
     */
    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 创建一个ChannelTopic来监听SSE_TOPIC
        ChannelTopic topic = new ChannelTopic(SseEmitterManager.getSseTopic());

        // 添加消息监听器
        redisMessageListenerContainer.addMessageListener(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] pattern) {
                try {
                    // 从RedisTemplate获取序列化器来反序列化消息
                    Object rawMessage = redisTemplate.getValueSerializer().deserialize(message.getBody());
                    
                    if (rawMessage instanceof SseMessageDto) {
                        SseMessageDto sseMessageDto = (SseMessageDto) rawMessage;
                        
                        // 使用SseEmitterManager处理接收到的消息
                        sseEmitterManager.handleReceivedMessage(sseMessageDto);
                    } else {
                        log.warn("接收到的消息不是SseMessageDto类型: {}", rawMessage != null ? rawMessage.getClass() : "null");
                    }
                } catch (Exception e) {
                    log.error("处理SSE消息时出错", e);
                }
            }
        }, topic);

        log.info("初始化SSE主题订阅监听器成功，监听主题: {}", SseEmitterManager.getSseTopic());
    }

    @Override
    public int getOrder() {
        return -1;
    }
}