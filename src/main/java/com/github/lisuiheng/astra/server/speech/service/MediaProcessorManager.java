package com.github.lisuiheng.astra.server.speech.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.asr.model.dto.GenericConnection;
import com.github.lisuiheng.astra.server.asr.service.VoiceChatService;
import com.github.lisuiheng.astra.server.speech.model.dto.MediaProcessor;
import com.github.lisuiheng.astra.server.speech.pool.OpusResourcePool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@Service
public class MediaProcessorManager {
    private final Map<String, MediaProcessor> processors;
    private final int MAX_CACHE_SIZE = 100;

    @Autowired
    private OpusResourcePool opusPool;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private VoiceChatService voiceChatService;

    public MediaProcessorManager() {
        this.processors = Collections.synchronizedMap(
                new LinkedHashMap<>(16, 0.75f, true) {
                    @Override
                    protected boolean removeEldestEntry(Map.Entry eldest) {
                        if (size() > MAX_CACHE_SIZE) {
                            log.info("淘汰MediaProcessor: callId={}, 当前大小={}", eldest.getKey(), size());
                            ((MediaProcessor) eldest.getValue()).shutdown();
                            return true;
                        }
                        return false;
                    }
                }
        );
    }

    public MediaProcessor getOrCreate(GenericConnection connection) {
        String callId = connection.getSessionId();
        return processors.computeIfAbsent(callId, id -> {
            // 只有新建 MediaProcessor 时才会进入此代码块
            log.info("新建 MediaProcessor: callId={}, 当前缓存大小={}/{}",
                    callId, processors.size(), MAX_CACHE_SIZE);


            MediaProcessor processor = new MediaProcessor(
                    id, objectMapper, connection, opusPool
            );

            log.debug("MediaProcessor 初始化完成: {}", processor);
            return processor;
        });
    }


    /**
     * 根据 sessionId 获取 MediaProcessor 实例
     */
    public MediaProcessor get(String callId) {
        return processors.get(callId);
    }

    public void remove(String callId) {
        MediaProcessor processor = processors.remove(callId);
        if (processor != null) processor.shutdown();
    }
    
    /**
     * 获取所有活跃的MediaProcessor
     */
    public Map<String, MediaProcessor> getAll() {
        return new java.util.HashMap<>(processors);
    }
    
    /**
     * 获取活跃处理器数量
     */
    public int getActiveCount() {
        return (int) processors.values().stream()
            .filter(MediaProcessor::isHealthy)
            .count();
    }
    
    /**
     * 优雅关闭所有处理器
     */
    public void shutdownAll() {
        log.info("Shutting down all {} MediaProcessors", processors.size());
        
        for (Map.Entry<String, MediaProcessor> entry : processors.entrySet()) {
            try {
                entry.getValue().shutdown();
            } catch (Exception e) {
                log.error("Error shutting down MediaProcessor: {}", entry.getKey(), e);
            }
        }
        processors.clear();
    }
}