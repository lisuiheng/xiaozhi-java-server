package com.github.lisuiheng.astra.server.common.handler;

import com.github.lisuiheng.astra.server.speech.model.dto.MediaProcessor;
import com.github.lisuiheng.astra.server.speech.service.MediaProcessorManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class ApplicationShutdownHandler {

    @Autowired
    private MediaProcessorManager mediaProcessorManager;

    @EventListener(ContextClosedEvent.class)
    public void onApplicationClosed(ContextClosedEvent event) {
        log.info("Application shutdown started, waiting for MediaProcessors to finish...");

        // 获取所有活跃的MediaProcessor
        Map<String, MediaProcessor> allProcessors = mediaProcessorManager.getAll();
        log.info("Found {} active MediaProcessors", allProcessors.size());

        // 为每个MediaProcessor启动优雅关闭
        for (Map.Entry<String, MediaProcessor> entry : allProcessors.entrySet()) {
            String callId = entry.getKey();
            MediaProcessor processor = entry.getValue();

            log.info("Starting graceful shutdown for MediaProcessor: {}", callId);
            processor.shutdown();
        }

        log.info("Application shutdown completed");
    }
}