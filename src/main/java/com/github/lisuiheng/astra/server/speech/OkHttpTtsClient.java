package com.github.lisuiheng.astra.server.speech;

import com.github.lisuiheng.astra.server.speech.config.TtsProperties;
import com.github.lisuiheng.astra.server.speech.model.dto.MediaProcessor;
import com.github.lisuiheng.astra.server.speech.model.dto.TtsEvent;
import com.github.lisuiheng.astra.server.asr.model.dto.outbound.TtsOutbound;
import com.github.lisuiheng.astra.server.speech.protocol.EventType;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.*;

/**
 * 基于OkHttp的高级TTS客户端
 */
@Slf4j
public class OkHttpTtsClient {
    private final String appId;
    private final String accessToken;
    private final String resourceId;
    private final String endpoint;

    private final ExecutorService executor;
    private final Map<String, StreamingContext> streamingContexts;
    private final Map<String, MediaProcessor> mediaProcessors;

    /**
     * 流式上下文
     */
    @lombok.Data
    public static class StreamingContext {
        private volatile String sessionId; // 改为 volatile
        private final OkHttpTtsConfig config;
        private final List<byte[]> audioChunks;
        private final CompletableFuture<byte[]> completionFuture;
        private volatile boolean streaming;
        private OkHttpTtsWebSocketHandler handler;

        public StreamingContext(OkHttpTtsConfig config) {
            this.sessionId = null; // 初始为 null
            this.config = config;
            this.audioChunks = Collections.synchronizedList(new ArrayList<>());
            this.completionFuture = new CompletableFuture<>();
            this.streaming = true;
        }

        // 添加设置 sessionId 的方法
        public void setSessionId(String sessionId) {
            this.sessionId = sessionId;
        }

        // 修改 getSessionId 方法
        public String getSessionId() {
            return sessionId;
        }

        public void addAudioChunk(byte[] chunk) {
            if (chunk != null && chunk.length > 0) {
                audioChunks.add(chunk);
            }
        }

        public byte[] getCompleteAudio() {
            synchronized (audioChunks) {
                int totalSize = audioChunks.stream().mapToInt(chunk -> chunk.length).sum();
                byte[] result = new byte[totalSize];
                int position = 0;

                for (byte[] chunk : audioChunks) {
                    System.arraycopy(chunk, 0, result, position, chunk.length);
                    position += chunk.length;
                }

                return result;
            }
        }
    }

    /**
     * 构造器 - 使用显式参数
     */
    public OkHttpTtsClient(String appId, String accessToken, String resourceId) {
        this.appId = appId;
        this.accessToken = accessToken;
        this.resourceId = resourceId;
        this.endpoint = "wss://openspeech.bytedance.com/api/v3/tts/bidirection";

        this.executor = Executors.newCachedThreadPool();
        this.streamingContexts = new ConcurrentHashMap<>();
        this.mediaProcessors = new ConcurrentHashMap<>();
    }

    /**
     * 构造器 - 使用TtsProperties
     */
    public OkHttpTtsClient(TtsProperties ttsProperties) {
        this(ttsProperties.getAppId(), ttsProperties.getAccessToken(), ttsProperties.getDefaultResourceId());
    }

    /**
     * 创建新的WebSocket处理器
     */
    private OkHttpTtsWebSocketHandler createNewHandler() {
        // 构建请求头
        Map<String, String> headers = new HashMap<>();
        headers.put("X-Api-App-Key", appId);
        headers.put("X-Api-Access-Key", accessToken);
        headers.put("X-Api-Resource-Id", resourceId);
        headers.put("X-Api-Connect-Id", UUID.randomUUID().toString());

        return new OkHttpTtsWebSocketHandler(endpoint, headers);
    }

    /**
     * 注册MediaProcessor到指定会话
     */
    public void registerMediaProcessor(String sessionId, MediaProcessor mediaProcessor) {
        if (sessionId == null || mediaProcessor == null) {
            log.warn("Cannot register MediaProcessor with null sessionId or mediaProcessor");
            return;
        }

        mediaProcessors.put(sessionId, mediaProcessor);
        log.info("MediaProcessor registered for session: {}", sessionId);
    }

    /**
     * 移除会话对应的MediaProcessor
     */
    public void unregisterMediaProcessor(String sessionId) {
        if (sessionId == null) {
            return;
        }

        MediaProcessor removed = mediaProcessors.remove(sessionId);
        if (removed != null) {
            log.info("MediaProcessor unregistered for session: {}", sessionId);
        }
    }

    // 新增：处理特定会话的事件
    private void handleEventForContext(TtsEvent event, StreamingContext context) {
        switch (event.getEventType()) {
            case TTS_SENTENCE_START:
            case TTS_SENTENCE_END:
                handleTtsSentenceEvent(event, context);
                break;
            case SESSION_FINISHED:
                handleSessionFinished(event, context);
                break;
            case SESSION_STARTED:
                // 处理会话开始事件
                log.info("Session started: {}", event.getSessionId());
                context.setSessionId(event.getSessionId()); // 设置 sessionId
                break;
            default:
                log.debug("Unhandled event type: {}", event.getEventType());
        }
    }

    private void handleTtsSentenceEvent(TtsEvent event, StreamingContext context) {
        MediaProcessor mediaProcessor = mediaProcessors.get(event.getSessionId());
        if (mediaProcessor != null) {
            TtsEvent ttsEvent = TtsEvent.builder()
                .eventType(event.getEventType())
                .sessionId(event.getSessionId())
                .connectId(event.getConnectId())
                .data(event.getData())
                .rawMessage(event.getRawMessage())
                .system(event.isSystem())
                .errorCode(event.getErrorCode())
                .errorMessage(event.getErrorMessage())
                .timestamp(event.getTimestamp())
                .build();
            
            mediaProcessor.triggerTtsEvent(ttsEvent);
            
            TtsOutbound ttsMessage = new TtsOutbound();
            ttsMessage.setSessionId(event.getSessionId());
            ttsMessage.setType("tts");

            if (event.getEventType() == EventType.TTS_SENTENCE_START) {
                ttsMessage.setState("sentence_start");
            } else if (event.getEventType() == EventType.TTS_SENTENCE_END) {
                ttsMessage.setState("sentence_end");
            }

            if (event.getData() instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> dataMap = (Map<String, Object>) event.getData();
                ttsMessage.setText((String) dataMap.get("text"));
            } else if (event.getData() != null) {
                ttsMessage.setText(event.getData().toString());
            }

            try {
                mediaProcessor.onTTSReceived(ttsMessage);
            } catch (Exception e) {
                log.error("Error sending TTS event message to client", e);
            }
        }
    }

    private void handleSessionFinished(TtsEvent event, StreamingContext context) {

        MediaProcessor mediaProcessor = mediaProcessors.get(event.getSessionId());
        TtsOutbound ttsMessage = new TtsOutbound();
        ttsMessage.setSessionId(event.getSessionId());
        ttsMessage.setType("tts");
        ttsMessage.setState("stop");
        mediaProcessor.onTTSReceived(ttsMessage);

        context.setStreaming(false);
        byte[] completeAudio = context.getCompleteAudio();
        context.getCompletionFuture().complete(completeAudio);
        streamingContexts.remove(event.getSessionId());

        log.info("Session {} completed", event.getSessionId());
    }

    /**
     * 设置单个连接的回调
     */
    private void setupCallbacks(OkHttpTtsWebSocketHandler handler, StreamingContext context) {
        // 音频数据回调
        handler.setOnAudioData(audioData -> {
            log.debug("Received audio data: {} bytes", audioData.length);

            if (context.isStreaming()) {
                context.addAudioChunk(audioData);
            }
            
            // 同时分发给对应的MediaProcessor
            mediaProcessors.values().forEach(processor -> {
                try {
                    processor.onAudioReceived(audioData);
                } catch (Exception e) {
                    log.error("Error sending audio to MediaProcessor", e);
                }
            });
        });

        // 事件回调 - 使用 sessionId 查找对应的 context
        handler.setOnEvent(event -> {
            String eventSessionId = event.getSessionId();
            
            log.debug("Received event: {} for session: {}", 
                     event.getEventType().getValue(), eventSessionId);

            if (eventSessionId == null) {
                log.warn("Received event without sessionId: {}", event.getEventType());
                return;
            }

            // 方案1A：通过 streamingContexts map 查找
            StreamingContext targetContext = streamingContexts.get(eventSessionId);
            if (targetContext != null) {
                handleEventForContext(event, targetContext);
            } else {
                log.warn("No context found for session: {}", eventSessionId);
            }
        });

        // 错误回调
        handler.setOnError(error -> {
            log.error("TTS Error occurred", error);

            if (context != null && context.isStreaming()) {
                context.getCompletionFuture().completeExceptionally(error);
            }
        });

        // 关闭回调
        handler.setOnClose(reason -> {
            log.warn("TTS connection closed: {}", reason);

            if (context != null && context.isStreaming()) {
                context.getCompletionFuture().completeExceptionally(
                    new RuntimeException("Connection closed: " + reason));
            }
        });
    }

    /**
     * 开始流式TTS会话
     */
    public CompletableFuture<StreamingContext> startStreamingSession(OkHttpTtsConfig config) {
        CompletableFuture<StreamingContext> future = new CompletableFuture<>();

        executor.submit(() -> {
            try {
                // 创建新的WebSocket处理器
                OkHttpTtsWebSocketHandler handler = createNewHandler();
                
                // 设置回调
                StreamingContext context = new StreamingContext(config); // 临时创建，sessionId稍后设置
                setupCallbacks(handler, context);
                
                // 连接到WebSocket
                handler.connect().thenCompose(connected -> {
                    if (connected) {
                        log.info("WebSocket connected, waiting for connection ready...");
                        // 等待连接准备就绪（收到CONNECTION_STARTED事件）
                        return handler.waitForConnectionReady(10000);
                    } else {
                        return CompletableFuture.failedFuture(new RuntimeException("Failed to connect"));
                    }
                }).thenCompose(v -> {
                    // 连接就绪后，开始会话
                    Map<String, Object> sessionConfig = config.getSessionConfig();
                    return handler.startSession(sessionConfig);
                }).whenComplete((sessionId, throwable) -> {
                    if (throwable != null) {
                        future.completeExceptionally(throwable);
                        return;
                    }
                    
                    // 创建新的上下文并设置handler
                    StreamingContext newContext = new StreamingContext(config);
                    newContext.setSessionId(sessionId);
                    newContext.handler = handler;
                    
                    // 存储上下文
                    streamingContexts.put(sessionId, newContext);
                    
                    log.info("Streaming session started: {}", sessionId);
                    future.complete(newContext);
                });

            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /**
     * 流式发送文本
     */
    public CompletableFuture<Void> streamText(StreamingContext context, String text) {
        if (context.getHandler() == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Session handler not available"));
        }

        if (!context.isStreaming()) {
            return CompletableFuture.failedFuture(new IllegalStateException("Session not active"));
        }

        return context.getHandler().streamText(
            context.getSessionId(),
            text,
            context.getConfig().getTaskConfig(text),
            50,  // 块大小
            50   // 延迟ms
        );
    }

    /**
     * 结束流式会话并获取音频
     */
    /**
     * 结束流式会话并获取音频
     */
    public CompletableFuture<byte[]> finishStreamingSession(StreamingContext context) {
        if (context.getHandler() == null) {
            return CompletableFuture.failedFuture(new IllegalStateException("Session handler not available"));
        }

        context.setStreaming(false);

        // 记录会话ID以便清理
        String sessionId = context.getSessionId();

        return context.getHandler().finishSession(context.getSessionId())
                .thenCompose(audio -> {
                    // 从上下文中移除并关闭连接
                    streamingContexts.remove(context.getSessionId());

                    // 新增：清理MediaProcessor注册
                    if (sessionId != null) {
                        unregisterMediaProcessor(sessionId);
                    }

                    return context.getHandler().disconnect().thenApply(v -> audio);
                })
                .exceptionally(throwable -> {
                    // 即使出错也要清理资源
                    if (sessionId != null) {
                        unregisterMediaProcessor(sessionId);
                    }
                    streamingContexts.remove(sessionId);
                    throw new CompletionException(throwable);
                });
    }

    /**
     * 同步合成（一次性）
     */
    public CompletableFuture<byte[]> synthesize(String text, OkHttpTtsConfig config) {
        return startStreamingSession(config)
            .thenCompose(context -> {
                // 发送完整文本
                CompletableFuture<Void> sendFuture = context.getHandler().sendText(
                    context.getSessionId(),
                    text,
                    config.getTaskConfig(text)
                );

                // 结束会话并获取音频
                return sendFuture.thenCompose(v -> finishStreamingSession(context));
            });
    }

    /**
     * 与大模型集成的流式合成
     */
    public StreamingSession createStreamingSession(OkHttpTtsConfig config) {
        return new StreamingSession(config);
    }

    /**
     * 流式会话类（用于大模型集成）
     */
    public class StreamingSession {
        private final OkHttpTtsConfig config;
        private StreamingContext context;
        private final List<CompletableFuture<Void>> pendingOperations;
        private volatile boolean active;

        public StreamingSession(OkHttpTtsConfig config) {
            this.config = config;
            this.pendingOperations = Collections.synchronizedList(new ArrayList<>());
            this.active = false;
        }

        /**
         * 开始会话
         */
        public CompletableFuture<Void> start() {
            log.info("Starting streaming session");
            return startStreamingSession(config)
                .thenAccept(ctx -> {
                    this.context = ctx;
                    this.active = true;
                });
        }

        /**
         * 发送文本
         */
        public CompletableFuture<Void> sendText(String text) {
            if (!active || context == null || context.getHandler() == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("Session not active"));
            }

            CompletableFuture<Void> future = streamText(context, text);
            pendingOperations.add(future);

            // 清理完成的future
            future.whenComplete((result, error) -> pendingOperations.remove(future));

            return future;
        }

        /**
         * 结束会话
         */
        public CompletableFuture<byte[]> finish() {
            if (!active || context == null) {
                return CompletableFuture.failedFuture(new IllegalStateException("Session not active"));
            }

            active = false;

            // 等待所有挂起的操作完成
            return CompletableFuture.allOf(
                pendingOperations.toArray(new CompletableFuture[0])
            ).thenCompose(v -> finishStreamingSession(context));
        }

        /**
         * 获取实时音频
         */
        public List<byte[]> getAudioChunks() {
            return context != null ? context.getAudioChunks() : Collections.emptyList();
        }

        public boolean isActive() {
            return active;
        }

        public String getSessionId() {
            return context != null ? context.getSessionId() : null;
        }
    }

    /**
     * 关闭客户端
     */
    public CompletableFuture<Void> shutdown() {
        return CompletableFuture.runAsync(() -> {
            executor.shutdownNow();
            streamingContexts.clear();
            log.info("TTS Client shutdown completed");
        });
    }

    /**
     * 获取连接状态
     */
    public boolean isConnected() {
        // 在按需连接模式下，没有全局连接状态，返回是否有活跃会话
        return !streamingContexts.isEmpty();
    }

    /**
     * 获取活跃会话数
     */
    public int getActiveSessionCount() {
        return streamingContexts.size();
    }
}