package com.github.lisuiheng.astra.server.speech.service;

import com.github.lisuiheng.astra.server.speech.OkHttpTtsClient;
import com.github.lisuiheng.astra.server.speech.OkHttpTtsConfig;
import com.github.lisuiheng.astra.server.speech.config.TtsProperties;
import com.github.lisuiheng.astra.server.speech.model.dto.MediaProcessor;
import com.github.lisuiheng.astra.server.speech.model.dto.TtsEvent;
import com.github.lisuiheng.astra.server.speech.protocol.EventType;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Slf4j
@Service
public class TtsService {

    @Autowired
    private TtsProperties ttsProperties;

    private OkHttpTtsClient ttsClient;
    private boolean initialized = false;

    @PostConstruct
    public void init() {
        try {
            // 使用TtsProperties构造OkHttpTtsClient
            ttsClient = new OkHttpTtsClient(ttsProperties);
            initialized = true;
            log.info("TTS service initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize TTS service", e);
        }
    }

    @PreDestroy
    public void destroy() {
        try {
            if (ttsClient != null) {
                ttsClient.shutdown().join();
                log.info("TTS service shutdown completed");
            }
        } catch (Exception e) {
            log.error("Error during TTS service shutdown", e);
        }
    }

    /**
     * 合成文本为语音
     *
     * @param text 要合成的文本
     * @return 音频数据字节数组
     */
    public byte[] synthesizeText(String text) {
        return synthesizeText(text, createDefaultConfig());
    }

    /**
     * 使用自定义配置合成文本为语音
     *
     * @param text   要合成的文本
     * @param config TTS配置
     * @return 音频数据字节数组
     */
    public byte[] synthesizeText(String text, OkHttpTtsConfig config) {
        if (!initialized) {
            throw new IllegalStateException("TTS service not initialized");
        }

        try {
            CompletableFuture<byte[]> future = ttsClient.synthesize(text, config);
            return future.join(); // 等待合成完成
        } catch (Exception e) {
            log.error("Failed to synthesize text: {}", text, e);
            throw new RuntimeException("Failed to synthesize text", e);
        }
    }

    /**
     * 创建默认TTS配置
     *
     * @return 默认TTS配置
     */
    public OkHttpTtsConfig createDefaultConfig() {
        return OkHttpTtsConfig.builder()
            .speaker(ttsProperties.getDefaultSpeaker())
            .audioFormat(ttsProperties.getDefaultFormat())
            .sampleRate(ttsProperties.getDefaultSampleRate())
            .build();
    }

    /**
     * 创建带有情感的TTS配置
     *
     * @param emotion 情感类型
     * @return 带有情感的TTS配置
     */
    public OkHttpTtsConfig createEmotionConfig(String emotion) {
        return OkHttpTtsConfig.builder()
            .speaker(ttsProperties.getDefaultSpeaker())
            .audioFormat(ttsProperties.getDefaultFormat())
            .sampleRate(ttsProperties.getDefaultSampleRate())
            .build();
    }

    /**
     * 创建声音复刻配置
     *
     * @param customVoiceId 自定义声音ID
     * @return 声音复刻配置
     */
    public OkHttpTtsConfig createCloningConfig(String customVoiceId) {
        return OkHttpTtsConfig.builder()
            .speaker(customVoiceId)
            .audioFormat("wav")
            .sampleRate(48000)
            .build();
    }

    /**
     * 检查服务是否已初始化
     *
     * @return true如果服务已初始化，否则false
     */
    public boolean isInitialized() {
        return initialized;
    }

    /**
     * 创建流式TTS会话
     *
     * @param config TTS配置
     * @return 流式会话实例
     */
    public OkHttpTtsClient.StreamingSession createStreamingSession(OkHttpTtsConfig config) {
        if (!initialized) {
            throw new IllegalStateException("TTS service not initialized");
        }

        try {
            return ttsClient.createStreamingSession(config);
        } catch (Exception e) {
            log.error("Failed to create streaming session", e);
            throw new RuntimeException("Failed to create streaming session", e);
        }
    }

    /**
     * 创建流式TTS会话并连接到MediaProcessor
     *
     * @param config TTS配置
     * @param mediaProcessor 媒体处理器
     * @return 流式会话实例
     */
    public OkHttpTtsClient.StreamingSession createStreamingSessionWithMediaProcessor(
            OkHttpTtsConfig config, MediaProcessor mediaProcessor) {
        if (!initialized) {
            throw new IllegalStateException("TTS service not initialized");
        }

        try {
            // 1. 创建TTS会话
            OkHttpTtsClient.StreamingSession session = ttsClient.createStreamingSession(config);

            // 2. 设置组合监听器
            Consumer<TtsEvent> combinedListener = createCombinedListener(session, mediaProcessor);
            mediaProcessor.setTtsEventListener(combinedListener);

            // 3. 启动会话并注册
            startSessionAndRegister(session, mediaProcessor);

            return session;
        } catch (Exception e) {
            log.error("Failed to create streaming session with media processor", e);
            throw new RuntimeException("Failed to create streaming session with media processor", e);
        }
    }


    /**
     * 创建组合监听器
     */
    private Consumer<TtsEvent> createCombinedListener(
            OkHttpTtsClient.StreamingSession session,
            MediaProcessor mediaProcessor) {

        return new Consumer<TtsEvent>() {
            private volatile String sessionId = null;
            private final Consumer<TtsEvent> originalListener = mediaProcessor.getTtsEventListener();

            @Override
            public void accept(TtsEvent event) {
                // 1. 尝试获取会话ID
                if (sessionId == null) {
                    sessionId = extractSessionId(event, session);
                    if (sessionId != null) {
                        // 注册MediaProcessor
                        ttsClient.registerMediaProcessor(sessionId, mediaProcessor);
                        log.info("MediaProcessor registered via event: {}", sessionId);
                    }
                }

                // 2. 处理事件
                if (event.getSessionId() == null && sessionId != null) {
                    // 如果事件缺少sessionId，设置它
                    event = TtsEvent.builder()
                            .eventType(event.getEventType())
                            .sessionId(sessionId)
                            .connectId(event.getConnectId())
                            .data(event.getData())
                            .rawMessage(event.getRawMessage())
                            .system(event.isSystem())
                            .errorCode(event.getErrorCode())
                            .errorMessage(event.getErrorMessage())
                            .timestamp(event.getTimestamp())
                            .build();
                }

                // 3. 调用原始监听器
                if (originalListener != null) {
                    originalListener.accept(event);
                }
            }

            private String extractSessionId(TtsEvent event, OkHttpTtsClient.StreamingSession session) {
                // 多种方式获取sessionId
                if (event.getSessionId() != null) {
                    return event.getSessionId();
                }

                try {
                    String sid = session.getSessionId();
                    if (sid != null) {
                        return sid;
                    }
                } catch (Exception e) {
                    // 忽略
                }

                return null;
            }
        };
    }

    /**
     * 启动会话并注册MediaProcessor
     */
    private void startSessionAndRegister(
            OkHttpTtsClient.StreamingSession session,
            MediaProcessor mediaProcessor) {

        session.start().thenRun(() -> {
            // 尝试立即获取sessionId
            String sessionId = session.getSessionId();
            if (sessionId != null) {
                ttsClient.registerMediaProcessor(sessionId, mediaProcessor);
                log.info("MediaProcessor registered after session start: {}", sessionId);
            } else {
                // 如果sessionId为null，启动一个重试机制
                retryRegistration(session, mediaProcessor);
            }
        }).exceptionally(ex -> {
            log.error("Failed to start TTS session", ex);
            return null;
        });
    }

    /**
     * 重试注册机制
     */
    private void retryRegistration(
            OkHttpTtsClient.StreamingSession session,
            MediaProcessor mediaProcessor) {

        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        Runnable registrationTask = new Runnable() {
            private int attempts = 0;
            private final int maxAttempts = 5;

            @Override
            public void run() {
                try {
                    String sessionId = session.getSessionId();
                    if (sessionId != null) {
                        ttsClient.registerMediaProcessor(sessionId, mediaProcessor);
                        log.info("MediaProcessor registered after {} retries: {}", attempts, sessionId);
                        scheduler.shutdown();
                    } else if (++attempts >= maxAttempts) {
                        log.warn("Failed to register MediaProcessor after {} attempts", maxAttempts);
                        scheduler.shutdown();
                    } else {
                        // 继续重试
                        scheduler.schedule(this, 500, TimeUnit.MILLISECONDS);
                    }
                } catch (Exception e) {
                    log.error("Error during registration retry", e);
                    scheduler.shutdown();
                }
            }
        };

        scheduler.schedule(registrationTask, 0, TimeUnit.MILLISECONDS);
    }
}