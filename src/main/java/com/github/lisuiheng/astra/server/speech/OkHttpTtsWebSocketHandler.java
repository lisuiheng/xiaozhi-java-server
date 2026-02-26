package com.github.lisuiheng.astra.server.speech;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.speech.model.dto.TtsEvent;
import com.github.lisuiheng.astra.server.speech.protocol.EventType;
import com.github.lisuiheng.astra.server.speech.protocol.Message;
import com.github.lisuiheng.astra.server.speech.protocol.MsgType;
import com.github.lisuiheng.astra.server.speech.protocol.MsgTypeFlagBits;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;
import okio.ByteString;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.StandardCharsets;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * 基于 OkHttp3 的 TTS WebSocket 处理器
 */
@Slf4j
public class OkHttpTtsWebSocketHandler extends WebSocketListener {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // 协议常量
    private static final byte PROTOCOL_VERSION_1 = 0b0001;
    private static final byte HEADER_SIZE_4_BYTES = 0b0001;
    private static final byte SERIALIZATION_JSON = 0b0001;
    private static final byte COMPRESSION_NONE = 0b0000;

    // 事件类型
    private static final int EVENT_START_CONNECTION = 1;
    private static final int EVENT_FINISH_CONNECTION = 2;
    private static final int EVENT_CONNECTION_STARTED = 50;
    private static final int EVENT_CONNECTION_FINISHED = 52;
    private static final int EVENT_START_SESSION = 100;
    private static final int EVENT_FINISH_SESSION = 102;
    private static final int EVENT_SESSION_STARTED = 150;
    private static final int EVENT_SESSION_FINISHED = 152;
    private static final int EVENT_TASK_REQUEST = 200;
    private static final int EVENT_TTS_RESPONSE = 352;

    // 消息类型
    private static final byte MESSAGE_TYPE_FULL_CLIENT_REQUEST = 0b1;
    private static final byte MESSAGE_TYPE_FULL_SERVER_RESPONSE = 0b1001;
    private static final byte MESSAGE_TYPE_AUDIO_ONLY_SERVER = 0b1011;
    private static final byte MESSAGE_TYPE_ERROR = 0b1111;

    // 消息标志
    private static final byte MESSAGE_FLAG_WITH_EVENT = 0b0100;
    private static final byte MESSAGE_FLAG_NO_SEQ = 0b0000;

    private final OkHttpClient okHttpClient;
    private final String endpoint;
    private final Map<String, String> headers;

    private WebSocket webSocket;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private final AtomicBoolean connecting = new AtomicBoolean(false);

    // 回调处理器
    private Consumer<byte[]> onAudioData;
    private Consumer<TtsEvent> onEvent;
    private Consumer<Exception> onError;
    private Consumer<String> onClose;

    // 会话管理
    private final Map<String, SessionContext> sessions = new ConcurrentHashMap<>();

    // 消息队列和锁
    private final BlockingQueue<Message> messageQueue = new LinkedBlockingQueue<>();
    private final Object connectionLock = new Object();

    // 连接状态Future
    private CompletableFuture<Boolean> connectionStartedFuture = new CompletableFuture<>();

    /**
     * WebSocket消息包装类
     */
    @lombok.Data
    public static class WebSocketMessage {
        private byte messageType;
        private byte messageFlags;
        private int event;
        private String sessionId;
        private String connectId;
        private byte[] payload;
        private int errorCode;

        // 添加无参构造函数
        public WebSocketMessage() {
        }

        // 添加全参构造函数
        public WebSocketMessage(byte messageType, byte messageFlags, int event, String sessionId, String connectId, byte[] payload, int errorCode) {
            this.messageType = messageType;
            this.messageFlags = messageFlags;
            this.event = event;
            this.sessionId = sessionId;
            this.connectId = connectId;
            this.payload = payload;
            this.errorCode = errorCode;
        }

        public boolean isAudioMessage() {
            return messageType == MESSAGE_TYPE_AUDIO_ONLY_SERVER;
        }

        public boolean isEventMessage() {
            return messageType == MESSAGE_TYPE_FULL_SERVER_RESPONSE;
        }

        public boolean isErrorMessage() {
            return messageType == MESSAGE_TYPE_ERROR;
        }
    }

    /**
     * 会话上下文（增强版）
     */
    @lombok.Data
    public static class SessionContext {
        private final String sessionId;
        private final long startTime;
        private volatile boolean active = true;
        private final List<byte[]> audioChunks = Collections.synchronizedList(new ArrayList<>());
        private final CompletableFuture<Void> completionFuture = new CompletableFuture<>();
        // 新增：文件输出流和文件名
        private volatile FileOutputStream fileOutputStream;
        private String pcmFilePath;

        // 新增：会话特定的事件等待器
        private final Map<Integer, CompletableFuture<Message>> eventWaiters = new ConcurrentHashMap<>();

        // 新增：会话配置
        private Map<String, Object> sessionConfig;

        public SessionContext(String sessionId) {
            this.sessionId = sessionId;
            this.startTime = System.currentTimeMillis();
            // 初始化文件输出
//            initAudioFile(sessionId);
        }

        /**
         * 初始化音频文件
         */
        private void initAudioFile(String sessionId) {
            try {
                // 创建保存目录
                File outputDir = new File("audio_sessions");
                if (!outputDir.exists()) {
                    outputDir.mkdirs();
                }

                // 生成文件名
                String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date(startTime));
                this.pcmFilePath = String.format("%s/%s_%s.pcm",
                        outputDir.getAbsolutePath(),
                        sessionId, timestamp);

                this.fileOutputStream = new FileOutputStream(pcmFilePath, true);
                log.info("Initialized audio file for session {}: {}", sessionId, pcmFilePath);

            } catch (Exception e) {
                log.error("Failed to initialize audio file for session {}", sessionId, e);
            }
        }

        public byte[] getCompleteAudio() {
            synchronized (audioChunks) {
                int totalSize = audioChunks.stream().mapToInt(chunk -> chunk.length).sum();
                ByteBuffer buffer = ByteBuffer.allocate(totalSize);
                audioChunks.forEach(buffer::put);
                return buffer.array();
            }
        }

        // 新增：处理会话消息
        public void handleMessage(Message message, Consumer<byte[]> audioCallback, Consumer<TtsEvent> eventCallback) {
            if (!active) {
                log.warn("Session {} is inactive, ignoring message", sessionId);
                return;
            }

            try {
                // 根据消息类型分发处理
                switch (message.getType()) {
                    case AUDIO_ONLY_SERVER:
                        handleAudioMessage(message, audioCallback);
                        break;
                    case FULL_SERVER_RESPONSE:
                        handleEventMessage(message, eventCallback);
                        break;
                    case ERROR:
                        handleSessionErrorMessage(message);
                        break;
                    default:
                        log.warn("Unhandled message type {} for session {}", message.getType(), sessionId);
                }
            } catch (Exception e) {
                log.error("Error handling message for session {}", sessionId, e);
            }
        }


        private void handleAudioMessage(Message message, Consumer<byte[]> audioCallback) {
            if (message.getPayload() != null && message.getPayload().length > 0) {
                audioChunks.add(message.getPayload());

                // 保存到文件
//                saveChunkToFile(message.getPayload());

                // 触发音频回调
                if (audioCallback != null) {
                    audioCallback.accept(message.getPayload());
                }

                log.debug("Audio chunk added to session {}: {} bytes", sessionId, message.getPayload().length);
            }
        }

        /**
         * 保存音频块到文件
         */
        private synchronized void saveChunkToFile(byte[] audioData) {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.write(audioData);
                    fileOutputStream.flush();
                } catch (Exception e) {
                    log.warn("Failed to write audio chunk to file for session {}", sessionId, e);
                }
            }
        }

        /**
         * 关闭文件输出流
         */
        public void closeAudioFile() {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                    log.info("Audio file closed for session {}: {} ({} chunks, ~{} bytes)",
                            sessionId, pcmFilePath, audioChunks.size(),
                            audioChunks.stream().mapToInt(b -> b.length).sum());
                } catch (Exception e) {
                    log.error("Error closing audio file for session {}", sessionId, e);
                }
                fileOutputStream = null;
            }
        }

        private void handleEventMessage(Message message, Consumer<TtsEvent> eventCallback) {

            // 完成对应事件的等待器
            int eventType = message.getEvent().getValue();
            CompletableFuture<Message> waiter = eventWaiters.remove(eventType);
            if (waiter != null && !waiter.isDone()) {
                waiter.complete(message);
            }

            // 特殊事件处理
            if (message.getEvent().getValue() == EVENT_SESSION_FINISHED) {
                active = false;
                completionFuture.complete(null);
            }

            // 触发事件回调
            if (eventCallback != null) {
                TtsEvent event = createTtsEvent(message, false);
                eventCallback.accept(event);
            }
        }

        private void handleSessionErrorMessage(Message message) {
            log.error("Session {} received error: code={}", sessionId, message.getErrorCode());
            active = false;

            // 完成所有等待器并标记错误
            eventWaiters.values().forEach(future ->
                future.completeExceptionally(new RuntimeException(
                    "Session error: " + message.getErrorCode())));
            eventWaiters.clear();

            if (!completionFuture.isDone()) {
                completionFuture.completeExceptionally(new RuntimeException(
                    "Session error: " + message.getErrorCode()));
            }
        }

        // 新增：等待特定会话事件
        public CompletableFuture<Message> waitForEvent(int eventType, long timeoutMs) {
            CompletableFuture<Message> future = new CompletableFuture<>();
            eventWaiters.put(eventType, future);

            // 设置超时
            ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
            executor.schedule(() -> {
                if (!future.isDone()) {
                    eventWaiters.remove(eventType);
                    future.completeExceptionally(new TimeoutException(
                        String.format("Timeout waiting for event %d in session %s", eventType, sessionId)));
                }
                executor.shutdown();
            }, timeoutMs, TimeUnit.MILLISECONDS);

            return future;
        }

        private TtsEvent createTtsEvent(Message message, boolean isSystem) {
            Object data = null;

            if (message.getPayload() != null && message.getPayload().length > 0) {
                try {
                    data = objectMapper.readValue(message.getPayload(), Map.class);
                } catch (Exception e) {
                    data = new String(message.getPayload(), StandardCharsets.UTF_8);
                }
            }

            return TtsEvent.builder()
                    .eventType(message.getEvent())
                    .sessionId(sessionId)
                    .connectId(message.getConnectId())
                    .system(isSystem)
                    .data(data)
                    .timestamp(System.currentTimeMillis())
                    .build();
        }
    }

    /**
     * 构造器
     */
    public OkHttpTtsWebSocketHandler(String endpoint, Map<String, String> headers) {
        this.endpoint = endpoint;
        this.headers = headers;

        // 创建信任所有证书的TrustManager（仅用于测试，生产环境请使用正式证书）
        TrustManager[] trustAllCerts = new TrustManager[] {
            new X509TrustManager() {
                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) {}
                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) {}
                @Override
                public X509Certificate[] getAcceptedIssuers() { return new X509Certificate[0]; }
            }
        };

        try {
            SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());

            this.okHttpClient = new OkHttpClient.Builder()
                .sslSocketFactory(sslContext.getSocketFactory(), (X509TrustManager) trustAllCerts[0])
                .hostnameVerifier((hostname, session) -> true)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();

        } catch (Exception e) {
            throw new RuntimeException("Failed to create OkHttpClient", e);
        }
    }

    /**
     * 连接到TTS服务
     */
    public CompletableFuture<Boolean> connect() {
        if (connected.get() || connecting.get()) {
            return CompletableFuture.completedFuture(false);
        }

        connecting.set(true);
        connectFuture = new CompletableFuture<>();

        try {
            // 构建请求头
            Request.Builder requestBuilder = new Request.Builder()
                .url(endpoint)
                .addHeader("Sec-WebSocket-Protocol", "binary");

            // 添加自定义头
            headers.forEach(requestBuilder::addHeader);

            // 创建请求
            Request request = requestBuilder.build();

            // 使用当前实例作为WebSocket监听器
            webSocket = okHttpClient.newWebSocket(request, this);

        } catch (Exception e) {
            connecting.set(false);
            connectFuture.completeExceptionally(e);
        }

        return connectFuture;
    }

    /**
     * 断开连接
     */
    public CompletableFuture<Void> disconnect() {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (webSocket != null && connected.get()) {
            try {
                sendFinishConnection();

                // 等待连接完成事件
                waitForEvent(EVENT_CONNECTION_FINISHED, 5000)
                    .thenAccept(msg -> {
                        webSocket.close(1000, "Normal closure");
                        cleanup();
                        future.complete(null);
                    })
                    .exceptionally(e -> {
                        webSocket.close(1000, "Normal closure");
                        cleanup();
                        future.complete(null);
                        return null;
                    });

            } catch (Exception e) {
                webSocket.close(1000, "Error closure");
                cleanup();
                future.completeExceptionally(e);
            }
        } else {
            cleanup();
            future.complete(null);
        }

        return future;
    }

    /**
     * 开始TTS会话（优化版）
     */
    public CompletableFuture<String> startSession(Map<String, Object> sessionConfig) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String sessionId = UUID.randomUUID().toString();

                // 等待连接就绪
                waitForConnectionReady(10000).join();

                // 创建会话上下文
                SessionContext sessionContext = new SessionContext(sessionId);
                sessionContext.setSessionConfig(sessionConfig);
                sessions.put(sessionId, sessionContext);

                // 构建开始会话消息
                Map<String, Object> startRequest = new HashMap<>();
                startRequest.put("user", Map.of("uid", UUID.randomUUID().toString()));
                startRequest.put("namespace", "BidirectionalTTS");
                startRequest.put("event", EVENT_START_SESSION);
                startRequest.put("req_params", sessionConfig != null ? sessionConfig : Map.of());

                // 发送请求并等待响应
                sendMessage(MESSAGE_TYPE_FULL_CLIENT_REQUEST, MESSAGE_FLAG_WITH_EVENT,
                           EVENT_START_SESSION, sessionId, startRequest)
                    .join();

                // 使用会话的waitForEvent等待SESSION_STARTED
                Message response = sessionContext.waitForEvent(EVENT_SESSION_STARTED, 10000).join();

                log.info("Session started successfully: {}", sessionId);
                return sessionId;

            } catch (Exception e) {
                throw new CompletionException("Failed to start session", e);
            }
        });
    }

    /**
     * 发送TTS文本请求
     */
    public CompletableFuture<Void> sendText(String sessionId, String text, Map<String, Object> ttsConfig) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (!sessions.containsKey(sessionId)) {
            future.completeExceptionally(new IllegalArgumentException("Session not found: " + sessionId));
            return future;
        }

        try {
            // 构建文本请求
            Map<String, Object> request = new HashMap<>();
            request.put("user", Map.of("uid", UUID.randomUUID().toString()));
            request.put("namespace", "BidirectionalTTS");
            request.put("event", EVENT_TASK_REQUEST);

            Map<String, Object> reqParams = new HashMap<>();
            if (ttsConfig != null) {
                reqParams.putAll(ttsConfig);
            }
            reqParams.put("text", text);

            request.put("req_params", reqParams);

            // 发送文本请求（使用新的 sendMessage 方法）
            sendMessage(MESSAGE_TYPE_FULL_CLIENT_REQUEST, MESSAGE_FLAG_WITH_EVENT,
                       EVENT_TASK_REQUEST, sessionId, request)
                .thenAccept(msg -> {
                    log.debug("Text sent to session {}: {} chars", sessionId, text.length());
                    future.complete(null);
                })
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        } catch (Exception e) {
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 结束TTS会话（优化版）
     */
    public CompletableFuture<byte[]> finishSession(String sessionId) {
        return CompletableFuture.supplyAsync(() -> {
            SessionContext sessionContext = sessions.get(sessionId);
            if (sessionContext == null) {
                throw new IllegalArgumentException("Session not found: " + sessionId);
            }

            try {
                // 构建结束会话请求
                Map<String, Object> finishRequest = new HashMap<>();
                finishRequest.put("user", Map.of("uid", UUID.randomUUID().toString()));
                finishRequest.put("namespace", "BidirectionalTTS");
                finishRequest.put("event", EVENT_FINISH_SESSION);
                finishRequest.put("req_params", Map.of());

                // 发送结束请求
                sendMessage(MESSAGE_TYPE_FULL_CLIENT_REQUEST, MESSAGE_FLAG_WITH_EVENT,
                           EVENT_FINISH_SESSION, sessionId, finishRequest)
                    .join();

                // 等待会话完成事件
                sessionContext.waitForEvent(EVENT_SESSION_FINISHED, 15000).join();

                // 等待完成future
                sessionContext.getCompletionFuture().join();

                // 获取完整音频并清理
                byte[] completeAudio = sessionContext.getCompleteAudio();
                sessions.remove(sessionId);

                log.info("Session {} finished. Audio size: {} bytes", sessionId, completeAudio.length);
                return completeAudio;

            } catch (Exception e) {
                sessions.remove(sessionId);
                throw new CompletionException("Failed to finish session: " + sessionId, e);
            }
        });
    }

    /**
     * 流式发送文本（分批发送）
     */
    public CompletableFuture<Void> streamText(String sessionId, String text,
                                            Map<String, Object> ttsConfig,
                                            int chunkSize, int delayMs) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (!sessions.containsKey(sessionId)) {
            future.completeExceptionally(new IllegalArgumentException("Session not found: " + sessionId));
            return future;
        }

        // 在后台线程中流式发送
        CompletableFuture.runAsync(() -> {
            try {
                // 分割文本
                List<String> chunks = splitTextIntoChunks(text, chunkSize);
                AtomicInteger sentCount = new AtomicInteger(0);

                // 顺序发送每个块
                for (String chunk : chunks) {
                    if (!sessions.containsKey(sessionId)) {
                        break; // 会话已结束
                    }

                    sendText(sessionId, chunk, ttsConfig).join();
                    sentCount.incrementAndGet();

                    // 添加延迟以模拟流式
                    if (delayMs > 0) {
                        Thread.sleep(delayMs);
                    }
                }

                log.debug("Streamed {} chunks to session {}", sentCount.get(), sessionId);
                future.complete(null);

            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    /**
     * 发送二进制消息（修复版）- 返回简单的 CompletableFuture<Void>
     */
    private CompletableFuture<Void> sendMessageSimple(byte messageType, byte messageFlags,
                                                      int event, String sessionId, byte[] payload) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (!connected.get() || webSocket == null) {
            future.completeExceptionally(new IllegalStateException("WebSocket not connected"));
            return future;
        }

        try {
            // 使用修复的序列化方法
            byte[] messageBytes = marshalMessage(messageType, messageFlags, event,
                    sessionId, payload);


            // 发送消息
            boolean sent = webSocket.send(ByteString.of(messageBytes));

            if (sent) {
                log.info("Message sent successfully: type=0x{}, flags=0x{}, event={}, session={}, size={}",
                        Integer.toHexString(messageType & 0xFF),
                        Integer.toHexString(messageFlags & 0xFF),
                        event, sessionId, messageBytes.length);
                future.complete(null);
            } else {
                log.error("Failed to send message");
                future.completeExceptionally(new RuntimeException("Failed to send message"));
            }

        } catch (Exception e) {
            log.error("Failed to send message", e);
            future.completeExceptionally(e);
        }

        return future;
    }

    /**
     * 发送二进制消息（保持原有接口兼容性）
     */
    private CompletableFuture<Message> sendMessage(byte messageType, byte messageFlags,
                                                   int event, String sessionId,
                                                   byte[] payload) {
        CompletableFuture<Message> future = new CompletableFuture<>();

        sendMessageSimple(messageType, messageFlags, event, sessionId, payload)
                .thenAccept(v -> future.complete(null))
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });

        return future;
    }

    /**
     * 新增的 sendMessage 方法，接收 req 对象并打印 req
     */
    private CompletableFuture<Message> sendMessage(byte messageType, byte messageFlags,
                                                   int event, String sessionId,
                                                   Map<String, Object> req) {
        CompletableFuture<Message> future = new CompletableFuture<>();
        
        try {
            // 打印 req 对象（所有事件）
            if (req != null) {
                log.info("req object for event {}: {}", event, objectMapper.writeValueAsString(req));
            }
            
            // 将 req 对象转换为字节数组
            byte[] payload = objectMapper.writeValueAsBytes(req);
            
            // 调用原有的 sendMessage 方法
            sendMessage(messageType, messageFlags, event, sessionId, payload)
                .thenAccept(msg -> future.complete(msg))
                .exceptionally(e -> {
                    future.completeExceptionally(e);
                    return null;
                });
        } catch (Exception e) {
            log.error("Failed to serialize req object", e);
            future.completeExceptionally(e);
        }
        
        return future;
    }

    /**
     * 发送连接开始事件
     */
    private void sendStartConnection() {
        try {
            Map<String, Object> startConnRequest = new HashMap<>();
            startConnRequest.put("user", Map.of("uid", UUID.randomUUID().toString()));
            startConnRequest.put("namespace", "BidirectionalTTS");
            startConnRequest.put("event", EVENT_START_CONNECTION);

            // 发送连接开始请求（使用新的 sendMessage 方法）
            sendMessage(MESSAGE_TYPE_FULL_CLIENT_REQUEST, MESSAGE_FLAG_WITH_EVENT,
                       EVENT_START_CONNECTION, null, startConnRequest);

        } catch (Exception e) {
            log.error("Failed to send start connection", e);
        }
    }

    /**
     * 发送连接结束事件
     */
    private void sendFinishConnection() {
        try {
            Map<String, Object> finishConnRequest = new HashMap<>();
            finishConnRequest.put("user", Map.of("uid", UUID.randomUUID().toString()));
            finishConnRequest.put("namespace", "BidirectionalTTS");
            finishConnRequest.put("event", EVENT_FINISH_CONNECTION);
            finishConnRequest.put("req_params", Map.of());

            // 发送连接结束请求（使用新的 sendMessage 方法）
            sendMessage(MESSAGE_TYPE_FULL_CLIENT_REQUEST, MESSAGE_FLAG_WITH_EVENT,
                       EVENT_FINISH_CONNECTION, null, finishConnRequest);
        } catch (Exception e) {
            log.error("Failed to send finish connection", e);
        }
    }



    /**
     * 等待会话特定事件
     */


    /**
     * 添加等待连接准备就绪的方法
     */
    public CompletableFuture<Void> waitForConnectionReady(long timeoutMs) {
        CompletableFuture<Void> future = new CompletableFuture<>();

        if (connectionStartedFuture.isDone()) {
            future.complete(null);
            return future;
        }

        // 设置超时
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(() -> {
            if (!future.isDone()) {
                future.completeExceptionally(new TimeoutException("Timeout waiting for connection ready"));
            }
        }, timeoutMs, TimeUnit.MILLISECONDS);

        connectionStartedFuture.thenAccept(result -> {
            future.complete(null);
            executor.shutdown();
        }).exceptionally(e -> {
            future.completeExceptionally(e);
            executor.shutdown();
            return null;
        });

        return future;
    }

    /**
     * 启动消息分发线程
     */
    private void startMessageDispatcher() {
        Thread dispatcher = new Thread(() -> {
            while (connected.get() && !Thread.currentThread().isInterrupted()) {
                try {
                    Message message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (message != null) {
                        dispatchMessage(message);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error in message dispatcher", e);
                }
            }
        }, "Message-Dispatcher");
        dispatcher.setDaemon(true);
        dispatcher.start();
    }



/**
 * 分发消息到相应的处理器
 */
private void dispatchMessage(Message message) {
    // 打印接收到的消息信息
    log.debug("Dispatching message: type={}, event={}, session={}",
             message.getType(), message.getEvent(), message.getSessionId());

    String sessionId = message.getSessionId();

    // 1. 系统级消息（无sessionId或特殊事件）
    if (sessionId == null ||
        message.getEvent().getValue() == EVENT_CONNECTION_STARTED ||
        message.getEvent().getValue() == EVENT_CONNECTION_FINISHED) {
        handleSystemMessage(message);
        return;
    }

    // 2. 会话消息
    SessionContext session = sessions.get(sessionId);
    if (session != null) {
        // 转发到会话处理器
        session.handleMessage(message, onAudioData, onEvent);
    } else {
        // 会话不存在，可能是过期或错误的会话ID
        log.warn("Received message for unknown session: {}", sessionId);
        handleOrphanedMessage(message);
    }
}

/**
 * 处理系统级消息
 */
private void handleSystemMessage(Message message) {
    log.info("System message: type={}, event={}",
             message.getType(), message.getEvent().getValue());

    // 连接事件
    if (message.getEvent().getValue() == EVENT_CONNECTION_STARTED) {
        log.info("✅ CONNECTION_STARTED received, connection is ready");
        connectionStartedFuture.complete(true);

        // 通知所有等待连接就绪的调用者
        synchronized (connectionLock) {
            connectionLock.notifyAll();
        }
    }

    // 连接结束事件
    else if (message.getEvent().getValue() == EVENT_CONNECTION_FINISHED) {
        log.info("Connection finished");
    }

    // 触发事件回调
    if (onEvent != null) {
        TtsEvent event = createSystemTtsEvent(message);
        onEvent.accept(event);
    }
}

/**
 * 处理无主消息（会话不存在）
 */
private void handleOrphanedMessage(Message message) {
    log.warn("Handling orphaned message: type={}, event={}, session={}",
             message.getType(), message.getEvent(), message.getSessionId());

    if (message.getType() == MsgType.ERROR) {
        // 如果是错误消息，可以全局处理
        handleErrorMessage(message);
    } else {
        // 对于非错误的孤儿消息，也创建TtsEvent并触发回调
        if (onEvent != null) {
            TtsEvent event = createSystemTtsEvent(message);
            onEvent.accept(event);
        }
    }
}

private void handleErrorMessage(Message message) {
    try {
        String errorDetail = "";

        // 尝试从多个可能的字段中提取错误信息
        if (message.getPayload() != null && message.getPayload().length > 0) {
            // payload 中有错误信息
            errorDetail = new String(message.getPayload(), StandardCharsets.UTF_8);
        } else if (message.getSessionId() != null && message.getSessionId().startsWith("{")) {
            // sessionId 字段中包含了 JSON 格式的错误信息（这是火山引擎的错误格式）
            errorDetail = message.getSessionId();
        } else if (message.getConnectId() != null && message.getConnectId().startsWith("{")) {
            // connectId 字段中包含了错误信息
            errorDetail = message.getConnectId();
        }

        // 解析错误详情
        String errorMessage = parseErrorMessage(errorDetail, message.getErrorCode());

        // 特别处理协议解码错误
        if (message.getErrorCode() == 45000000) {
            log.error("❌ PROTOCOL DECODE ERROR (45000000): {}", errorMessage);
            log.error("This indicates our binary message format is incorrect!");

            // 记录关键调试信息
            log.error("Error details: {}", errorDetail);
            log.error("Message type: 0x{}, flags: 0x{}, event: {}",
                        Integer.toHexString(message.getType().getValue() & 0xFF),
                        Integer.toHexString(message.getFlag().getValue() & 0xFF),
                        message.getEvent().getValue());

            // 触发连接失败
            connectionStartedFuture.completeExceptionally(
                        new RuntimeException("Protocol decode error: " + errorMessage));
        } else {
            log.error("Received TTS error message: code={}, message={}",
                        message.getErrorCode(), errorMessage);
        }

        // 创建错误事件
        TtsEvent errorEvent = TtsEvent.builder()
                .eventType(EventType.SESSION_FAILED)
                .sessionId(message.getSessionId())
                .connectId(message.getConnectId())
                .errorCode(message.getErrorCode())
                .errorMessage(errorMessage)
                .system(true)
                .timestamp(System.currentTimeMillis())
                .build();

        // 触发错误事件回调
        if (onEvent != null) {
            onEvent.accept(errorEvent);
        }

        // 通知错误回调
        if (onError != null) {
            onError.accept(new RuntimeException(
                        "TTS Error " + message.getErrorCode() + ": " + errorMessage));
        }

    } catch (Exception e) {
        log.error("Error while handling error message", e);
    }
}

    /**
     * 创建系统级 TtsEvent
     */
    private TtsEvent createSystemTtsEvent(Message message) {
        Object data = null;

        if (message.getPayload() != null && message.getPayload().length > 0) {
            try {
                data = objectMapper.readValue(message.getPayload(), Map.class);
            } catch (Exception e) {
                data = new String(message.getPayload(), StandardCharsets.UTF_8);
            }
        }

        return TtsEvent.builder()
                .eventType(message.getEvent())
                .sessionId(message.getSessionId())
                .connectId(message.getConnectId())
                .system(true)
                .data(data)
                .timestamp(System.currentTimeMillis())
                .build();
    }

    /**
     * 解析错误信息
     */
    private String parseErrorMessage(String errorDetail, int errorCode) {
        if (errorDetail == null || errorDetail.isEmpty()) {
            return "Unknown error (code: " + errorCode + ")";
        }

        try {
            // 尝试解析为JSON
            if (errorDetail.startsWith("{") && errorDetail.endsWith("}")) {
                Map<String, Object> errorMap = objectMapper.readValue(errorDetail, Map.class);

                // 尝试获取常见的错误字段
                if (errorMap.containsKey("error")) {
                    return String.valueOf(errorMap.get("error"));
                } else if (errorMap.containsKey("message")) {
                    return String.valueOf(errorMap.get("message"));
                } else if (errorMap.containsKey("detail")) {
                    return String.valueOf(errorMap.get("detail"));
                }

                // 如果没有特定字段，返回整个JSON
                return errorDetail;
            }

            // 如果不是JSON，直接返回
            return errorDetail;

        } catch (Exception e) {
            // 如果解析失败，返回原始字符串
            log.debug("Failed to parse error detail as JSON: {}", errorDetail);
            return errorDetail;
        }
    }



    /**
     * 清理资源
     */
    private void cleanup() {
        connected.set(false);
        connecting.set(false);


        // 清理所有会话
        sessions.values().forEach(session ->
            session.getCompletionFuture().completeExceptionally(
                new RuntimeException("Connection closed")));
        sessions.clear();



        // 清空消息队列
        messageQueue.clear();

        log.info("TTS WebSocket handler cleaned up");
    }

    /**
     * 等待特定事件（系统级）
     */
    private CompletableFuture<Message> waitForEvent(int eventType, long timeoutMs) {
        CompletableFuture<Message> future = new CompletableFuture<>();

        CompletableFuture.runAsync(() -> {
            long startTime = System.currentTimeMillis();

            while (System.currentTimeMillis() - startTime < timeoutMs) {
                try {
                    Message message = messageQueue.poll(100, TimeUnit.MILLISECONDS);

                    if (message != null && message.getEvent().getValue() == eventType) {
                        future.complete(message);
                        return;
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    future.completeExceptionally(e);
                    return;
                }
            }

            future.completeExceptionally(new TimeoutException("Timeout waiting for event: " + eventType));
        });

        return future;
    }

    // ========== 序列化/反序列化方法 ==========

    /**
     * 反序列化消息（使用V1协议）
     */
    private Message unmarshalMessage(byte[] data) {
        try {
            return Message.unmarshal(data);
        } catch (Exception e) {
            log.error("Failed to unmarshal V1 protocol message", e);
            throw new RuntimeException("Unmarshal V1 protocol message failed", e);
        }
    }

    /**
     * 序列化消息（使用V1协议）
     */
    private byte[] marshalMessage(byte messageType, byte messageFlags, int event,
                                  String sessionId, byte[] payload) {
        try {
            // 1. 计算各部分大小（简化版，只包含必要字段）
            int sessionIdSize = (sessionId != null && !sessionId.isEmpty()) ?
                    sessionId.getBytes(StandardCharsets.UTF_8).length : 0;
            int payloadSize = (payload != null) ? payload.length : 0;

            // 2. 计算消息总大小（简化结构）
            int totalSize = 4; // 固定头部
            totalSize += 4;    // 事件号
            if (sessionId != null) {
                totalSize += 4 + sessionIdSize; // sessionId长度(4) + 内容
            }
            totalSize += 4 + payloadSize;   // payload长度 + 内容

            // 注意：根据buildFullClientRequest的格式，不包含connectId和错误码字段
            // 除非是特定类型的消息（如错误消息或连接事件）

            // 3. 创建缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(totalSize);

            // 4. 写入4字节固定头部（基于buildFullClientRequest的硬编码格式）
            // Byte 0: 0x11 = 版本1(0001) + 头大小4字节(0001)
            buffer.put((byte) 0x11);

            // Byte 1: 消息类型(4位) + 消息标志(4位)
            buffer.put((byte) 0x14);

            // Byte 2: 0x10 = JSON序列化(0001) + 无压缩(0000)
            buffer.put((byte) 0x10);

            // Byte 3: 保留位
            buffer.put((byte) 0x00);

            log.debug("Header written: 0x{} 0x{} 0x10 0x00",
                    Integer.toHexString(0x11 & 0xFF),
                    Integer.toHexString(((messageType << 4) | messageFlags) & 0xFF));

            // 5. 写入事件号
            buffer.putInt(event);
            log.debug("Event written: {} (0x{})", event, Integer.toHexString(event));

            // 6. 写入sessionId（长度 + 内容）
            if (sessionId != null) {
                buffer.putInt(sessionIdSize);
                buffer.put(sessionId.getBytes(StandardCharsets.UTF_8));
            }

            // 7. 写入payload（长度 + 内容）
            buffer.putInt(payloadSize);
            if (payloadSize > 0) {
                buffer.put(payload);
            } else {
                log.debug("Payload length written: 0");
            }

            // 8. 验证缓冲区位置
            if (buffer.position() != totalSize) {
                log.error("❌ Buffer position mismatch! Expected: {}, Actual: {}", totalSize, buffer.position());
                throw new RuntimeException("Buffer position mismatch");
            }

            // 9. 返回完整的消息
            byte[] result = new byte[totalSize];
            buffer.flip();
            buffer.get(result);

            log.info("✅ Created protocol message: type=0x{}, flags=0x{}, event={}, session={}, payloadSize={}, totalSize={}",
                    Integer.toHexString(messageType & 0xFF),
                    Integer.toHexString(messageFlags & 0xFF),
                    event, sessionId, payloadSize, totalSize);

            // 调试：显示完整消息结构
            debugBinaryMessage(result, "Created Message");

            return result;

        } catch (Exception e) {
            log.error("❌ Failed to marshal V1 protocol message", e);
            throw new RuntimeException("Marshal V1 protocol message failed", e);
        }
    }

    // ========== 工具方法 ==========

    /**
     * 分割文本
     */
    private List<String> splitTextIntoChunks(String text, int chunkSize) {
        List<String> chunks = new ArrayList<>();
        if (text == null || text.isEmpty()) {
            return chunks;
        }

        int length = text.length();
        for (int i = 0; i < length; i += chunkSize) {
            int end = Math.min(i + chunkSize, length);
            chunks.add(text.substring(i, end));
        }

        return chunks;
    }

    /**
     * 获取当前连接状态
     */
    public boolean isConnected() {
        return connected.get();
    }

    /**
     * 获取活跃会话数量
     */
    public int getActiveSessionCount() {
        return (int) sessions.values().stream()
            .filter(SessionContext::isActive)
            .count();
    }

    // ========== 回调设置器 ==========

    public void setOnAudioData(Consumer<byte[]> onAudioData) {
        this.onAudioData = onAudioData;
    }

    public void setOnEvent(Consumer<TtsEvent> onEvent) {
        this.onEvent = onEvent;
    }

    public void setOnError(Consumer<Exception> onError) {
        this.onError = onError;
    }

    public void setOnClose(Consumer<String> onClose) {
        this.onClose = onClose;
    }

    // ========== OkHttp WebSocketListener 接口实现 ==========

    private CompletableFuture<Boolean> connectFuture = new CompletableFuture<>();

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        OkHttpTtsWebSocketHandler.this.webSocket = webSocket;
        connected.set(true);
        connecting.set(false);

        log.info("WebSocket connection established. Response: {}", response);
        log.info("Headers: {}", response.headers());

        // 启动消息分发器（代替原来的消息处理器）
        startMessageDispatcher();

        // 完成连接future
        connectFuture.complete(true);

        // 重要：立即发送start connection，但不要立即发送start session
        sendStartConnectionImmediately();
    }

    /**
     * 立即发送开始连接消息
     */
    private void sendStartConnectionImmediately() {
        try {
            Map<String, Object> startConnRequest = new HashMap<>();
            startConnRequest.put("user", Map.of("uid", UUID.randomUUID().toString()));
            startConnRequest.put("namespace", "BidirectionalTTS");
            startConnRequest.put("event", EVENT_START_CONNECTION);

            byte[] payload = objectMapper.writeValueAsBytes(startConnRequest);

            // 直接使用 sendMessageSimple 方法
            CompletableFuture<Void> sendFuture = sendMessageSimple(
                    MESSAGE_TYPE_FULL_CLIENT_REQUEST,
                    MESSAGE_FLAG_WITH_EVENT,
                    EVENT_START_CONNECTION,
                    null,
                    payload
            );

            sendFuture.thenAccept(v -> {
                log.info("Start connection request sent successfully, waiting for CONNECTION_STARTED event...");
            }).exceptionally(e -> {
                log.error("Failed to send start connection request", e);
                return null;
            });

        } catch (Exception e) {
            log.error("Failed to prepare start connection message", e);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        log.warn("Received unexpected text message: {}", text);
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteString bytes) {
        try {
            Message message = Message.unmarshal(bytes.toByteArray());
            log.info("Received message: {}", message);
            messageQueue.put(message);
        } catch (Exception e) {
            log.error("Failed to parse binary message", e);
        }
    }

    @Override
    public void onClosing(WebSocket webSocket, int code, String reason) {
        log.info("WebSocket closing: code={}, reason={}", code, reason);
    }

    @Override
    public void onClosed(WebSocket webSocket, int code, String reason) {
        log.info("WebSocket closed: code={}, reason={}", code, reason);
        connected.set(false);
        cleanup();

        // 完成连接future
        if (!connectFuture.isDone()) {
            connectFuture.completeExceptionally(new RuntimeException("Connection closed: " + reason));
        }

        if (onClose != null) {
            onClose.accept(reason);
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        log.error("WebSocket failure", t);
        connecting.set(false);
        connected.set(false);
        cleanup();

        // 完成连接future
        if (!connectFuture.isDone()) {
            connectFuture.completeExceptionally(new Exception("WebSocket failure", t));
        }

        if (onError != null) {
            onError.accept(new Exception("WebSocket failure", t));
        }
    }

    /**
     * 调试：打印二进制消息的十六进制格式
     */
    private void debugBinaryMessage(byte[] data, String description) {
        if (data == null || data.length == 0) {
            log.warn("{}: Empty data", description);
            return;
        }

        StringBuilder hex = new StringBuilder();
        for (int i = 0; i < Math.min(data.length, 32); i++) { // 只显示前32字节
            hex.append(String.format("%02X ", data[i]));
        }

        log.debug("{}: size={}, hex=[{}]", description, data.length, hex.toString().trim());

        // 尝试解析头部
        if (data.length >= 4) {
            ByteBuffer buffer = ByteBuffer.wrap(data);
            buffer.order(ByteOrder.BIG_ENDIAN);

            byte firstByte = buffer.get();
            byte version = (byte) ((firstByte >> 4) & 0x0F);
            byte headerUnits = (byte) (firstByte & 0x0F);

            byte secondByte = buffer.get();
            byte messageType = (byte) ((secondByte >> 4) & 0x0F);
            byte messageFlags = (byte) (secondByte & 0x0F);

            byte thirdByte = buffer.get();
            byte serialization = (byte) ((thirdByte >> 4) & 0x0F);
            byte compression = (byte) (thirdByte & 0x0F);

            log.debug("  Header: version={}, headerUnits={}, type=0x{}, flags=0x{}, serialization={}, compression={}",
                     version, headerUnits,
                     Integer.toHexString(messageType & 0xFF),
                     Integer.toHexString(messageFlags & 0xFF),
                     serialization, compression);
        }
    }

}
