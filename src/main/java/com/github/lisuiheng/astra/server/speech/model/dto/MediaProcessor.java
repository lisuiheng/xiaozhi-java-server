package com.github.lisuiheng.astra.server.speech.model.dto;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.asr.model.dto.GenericConnection;
import com.github.lisuiheng.astra.server.asr.model.dto.outbound.TtsOutbound;
import com.github.lisuiheng.astra.server.speech.pool.OpusResourcePool;
import com.github.lisuiheng.astra.server.speech.protocol.EventType;
import com.sun.jna.ptr.PointerByReference;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import tomp2p.opuswrapper.Opus;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Consumer;

@Slf4j
@Getter
public class MediaProcessor {
    private final String callId;
    private final int encodeFrameSize;
    private final ObjectMapper objectMapper;
    private final OpusResourcePool opusPool;

    // 服务依赖
    private final GenericConnection connection;

    // 编解码器
    private PointerByReference encoder;
    private PointerByReference decoder;

    // 音频参数
    private static final int SAMPLE_RATE = 24000;
    private static final int BYTES_PER_SAMPLE = 2; // 16-bit PCM
    private static final int CHANNELS = 1; // 单声道
    private static final int FRAME_DURATION_MS = 60; // 60ms

    // 性能参数
    private static final int MAX_QUEUE_SIZE = 500;
    private static final int MAX_FRAME_SIZE = 6 * 960; // Opus最大帧大小

    // 状态管理
    private final BlockingQueue<MediaMessage> messageQueue = new LinkedBlockingQueue<>(MAX_QUEUE_SIZE);
    private final AtomicBoolean running = new AtomicBoolean(true);
    private final AtomicBoolean interruptFlag = new AtomicBoolean(false);
    private final AtomicBoolean connectionClosed = new AtomicBoolean(false);
    private final ReentrantLock processLock = new ReentrantLock();

    // 发送控制 - 基于时间轴的精确控制
    private long startTimestamp = 0; // 会话开始时间戳（毫秒）
    private long audioPosition = 0;  // 当前音频位置（样本数）
    private final int sampleRate = SAMPLE_RATE;
    private final int frameDurationMs = FRAME_DURATION_MS;
    private final int encodeFrameSizeSamples; // 编码帧的样本数

    // 统计信息
    private final AtomicInteger processedMessages = new AtomicInteger(0);
    private final AtomicInteger droppedMessages = new AtomicInteger(0);
    private volatile long lastLogTime = System.currentTimeMillis();
    private static final long LOG_INTERVAL_MS = 30_000; // 30秒记录一次统计

    // TTS事件监听器
    private Consumer<TtsEvent> ttsEventListener;

    // 调度器
    private final ScheduledExecutorService scheduler;

    public MediaProcessor(String callId,
                          ObjectMapper objectMapper,
                          GenericConnection connection,
                          OpusResourcePool opusPool) {
        this.callId = callId;
        this.encodeFrameSize = this.sampleRate / 1000 * this.frameDurationMs;
        this.objectMapper = objectMapper;
        this.connection = connection;
        this.opusPool = opusPool;
        this.encodeFrameSizeSamples = encodeFrameSize;

        // 初始化编解码器
        initCodec();

        // 初始化调度器
        this.scheduler = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "MediaProcessor-Scheduler-" + callId);
            t.setPriority(Thread.MAX_PRIORITY);
            t.setDaemon(true);
            return t;
        });

        // 启动处理线程
        startProcessingThread();

        // 启动统计线程
        startStatisticsThread();

        // 启动连接状态检查线程
        startConnectionCheckThread();

        log.info("[Session: {}] MediaProcessor initialized", callId);
    }

    private void startConnectionCheckThread() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                checkConnectionStatus();
            } catch (Exception e) {
                log.warn("[Session: {}] Connection check failed", callId, e);
            }
        }, 5, 5, TimeUnit.SECONDS);
    }

    /**
     * 检查连接状态
     */
    private void checkConnectionStatus() {
        try {
            if (!connection.isOpen()) {
                log.warn("[Session: {}] Connection is not open, stopping MediaProcessor", callId);
                connectionClosed.set(true);
                shutdown();
            }
        } catch (Exception e) {
            log.debug("[Session: {}] Error checking connection status", callId, e);
        }
    }

    private void initCodec() {
        try {
            this.encoder = opusPool.borrowEncoder();
            this.decoder = opusPool.borrowDecoder();
            log.debug("[Session: {}] Opus codec initialized", callId);
        } catch (Exception e) {
            log.error("[Session: {}] Failed to initialize Opus codec", callId, e);
            throw new IllegalStateException("Failed to initialize Opus codec", e);
        }
    }

    private void startProcessingThread() {
        Thread processorThread = new Thread(this::processMessages,
                "MediaProcessor-Thread-" + callId);
        processorThread.setDaemon(true);
        processorThread.start();
    }

    private void startStatisticsThread() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                logStatistics();
            } catch (Exception e) {
                log.warn("[Session: {}] Statistics logging failed", callId, e);
            }
        }, LOG_INTERVAL_MS, LOG_INTERVAL_MS, TimeUnit.MILLISECONDS);
    }

    /**
     * 接收音频数据
     */
    public boolean onAudioReceived(byte[] audio) {
        if (!running.get() || interruptFlag.get() || connectionClosed.get()) {
            log.debug("[Session: {}] Processor not running or connection closed, ignoring audio", callId);
            return false;
        }

        if (messageQueue.remainingCapacity() < 10) {
            droppedMessages.incrementAndGet();
            log.warn("[Session: {}] Queue near capacity, dropping audio. Queue size: {}",
                    callId, messageQueue.size());
            return false;
        }

        boolean offered = messageQueue.offer(new MediaMessage(audio));
        if (offered) {
            processedMessages.incrementAndGet();
        } else {
            droppedMessages.incrementAndGet();
        }
        return offered;
    }

    /**
     * 接收TTS消息
     */
    public boolean onTTSReceived(TtsOutbound ttsMessage) {
        if (!running.get() || connectionClosed.get()) {
            log.debug("[Session: {}] Processor not running or connection closed, ignoring TTS", callId);
            return false;
        }

        // TTS控制消息处理
        if ("sentence_end".equals(ttsMessage.getState())) {
            // 重置发送状态
            resetSendingState();
        }

        if ("interrupt".equals(ttsMessage.getState())) {
            interruptFlag.set(true);
            resetSendingState(); // 中断时重置发送状态
            return true;
        }

//        if ("stop".equals(ttsMessage.getState())) {
//            log.info("[Session: {}] Received TTS stop message, shutting down", callId);
//            shutdown();
//            return true;
//        }

        // 根据TTS消息状态触发相应的TTS事件
        triggerTtsEventFromTtsMessage(ttsMessage);

        boolean offered = messageQueue.offer(new MediaMessage(ttsMessage));
        if (offered) {
            processedMessages.incrementAndGet();
        }
        return offered;
    }

    /**
     * 重置发送状态
     */
    private void resetSendingState() {
        startTimestamp = 0;
        audioPosition = 0;
    }

    /**
     * 从TTS消息触发TTS事件
     */
    private void triggerTtsEventFromTtsMessage(TtsOutbound ttsMessage) {
        // 根据消息状态创建TtsEvent
        if ("sentence_start".equals(ttsMessage.getState())) {
            TtsEvent event = TtsEvent.builder()
                    .eventType(EventType.TTS_SENTENCE_START)
                    .sessionId(callId)
                    .data(ttsMessage.getText())
                    .timestamp(System.currentTimeMillis())
                    .build();
            triggerTtsEvent(event);
        } else if ("sentence_end".equals(ttsMessage.getState())) {
            TtsEvent event = TtsEvent.builder()
                    .eventType(EventType.TTS_SENTENCE_END)
                    .sessionId(callId)
                    .data(ttsMessage.getText())
                    .timestamp(System.currentTimeMillis())
                    .build();
            triggerTtsEvent(event);
        } else if ("sentence_complete".equals(ttsMessage.getState())) {
            // 句子完成事件
            TtsEvent event = TtsEvent.builder()
                    .eventType(EventType.TTS_RESPONSE)
                    .sessionId(callId)
                    .data(ttsMessage.getText())
                    .timestamp(System.currentTimeMillis())
                    .build();
            triggerTtsEvent(event);
        } else {
            // 其他状态不触发事件
            log.debug("[Session: {}] No event triggered for TTS message state: {}", callId, ttsMessage.getState());
        }
    }

    /**
     * 主处理循环
     */
    private void processMessages() {
        log.info("[Session: {}] Processing thread started", callId);

        while (running.get() && !Thread.currentThread().isInterrupted()) {
            try {
                // 检查连接是否关闭
                if (connectionClosed.get()) {
                    log.info("[Session: {}] Connection closed, stopping processing", callId);
                    break;
                }

                MediaMessage message = messageQueue.poll(100, TimeUnit.MILLISECONDS);
                if (message == null) continue;

                processSingleMessage(message);

            } catch (InterruptedException e) {
                log.info("[Session: {}] Processing thread interrupted", callId);
                Thread.currentThread().interrupt();
                break;
            } catch (Exception e) {
                log.error("[Session: {}] Error processing message", callId, e);
                // 继续处理，不要因为单个消息失败而停止
            }
        }

        log.info("[Session: {}] Processing thread stopped", callId);
        releaseResources();
    }

    /**
     * 处理单个消息
     */
    private void processSingleMessage(MediaMessage message) {
        processLock.lock();
        try {
            if (interruptFlag.get() || connectionClosed.get()) {
                log.debug("[Session: {}] Interrupt flag set or connection closed, skipping message", callId);
                return;
            }

            switch (message.getType()) {
                case AUDIO:
                    handleAudioMessage(message.getAudioData());
                    break;
                case TTS:
                    TtsOutbound ttsMessage = message.getTtsMessage();
                    log.info("[Session: {}] Received TTS message {} {} ", callId, ttsMessage.getText(), ttsMessage.getState());
                    handleTTSMessage(ttsMessage);
                    break;
                default:
                    log.warn("[Session: {}] Unknown message type: {}",
                            callId, message.getType());
            }
        } catch (Exception e) {
            log.error("[Session: {}] Error processing {} message",
                    callId, message.getType(), e);
        } finally {
            processLock.unlock();
        }
    }

    /**
     * 处理音频消息
     */
    private void handleAudioMessage(byte[] audioData) throws IOException {
        if (!connection.isOpen()) {
            log.warn("[Session: {}] Connection closed, cannot send audio", callId);
            connectionClosed.set(true);
            return;
        }

        // 初始化开始时间戳
        if (startTimestamp == 0) {
            startTimestamp = System.currentTimeMillis();
            audioPosition = 0;
            log.debug("[Session: {}] Audio session started at {}", callId, startTimestamp);
        }

        // 默认使用 Opus 编码格式处理音频
        String audioFormat = null; // 可以根据需要设置默认值

        if (audioFormat != null && audioFormat.endsWith("pcm")) {
            sendPCMAudio(audioData);
        } else {
            sendEncodedAudio(audioData);
        }
    }

    /**
     * 发送PCM音频（直通模式）
     */
    private void sendPCMAudio(byte[] pcmData) throws IOException {
        int samples = pcmData.length / BYTES_PER_SAMPLE;

        if (startTimestamp == 0) {
            startTimestamp = System.currentTimeMillis();
            audioPosition = 0;
            log.debug("[Session: {}] Audio session started at {}", callId, startTimestamp);
        }

        // 控制发送时序
        long waitTime = controlSendTiming(samples);
        if (waitTime > 0) {
            log.debug("[Session: {}] Rate control, waiting {} ms", callId, waitTime);
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("[Session: {}] Send timing control interrupted", callId);
                return;
            }
        }

        // 检查连接状态
        if (!connection.isOpen()) {
            log.warn("[Session: {}] Connection closed during PCM audio sending", callId);
            connectionClosed.set(true);
            return;
        }

        // 更新音频位置（按样本数）
        audioPosition += samples;

        // 发送音频数据
        connection.sendBinary(pcmData);
        log.trace("[Session: {}] Sent {} bytes of PCM audio", callId, pcmData.length);
    }

    /**
     * 发送编码后的音频（Opus模式）
     */
    public void sendEncodedAudio(byte[] audioBuffer) throws IOException {
        log.info("[Session: {}] Sending audio buffer: {}", callId, audioBuffer.length);
        int audioDataPosition = 0; // 音频缓冲区中的位置（字节）
        int samplesPerFrame = encodeFrameSizeSamples; // 每帧的样本数

        while (audioDataPosition <= audioBuffer.length - samplesPerFrame * BYTES_PER_SAMPLE) {
            // 控制发送时序
            long waitTime = controlSendTiming(samplesPerFrame);
            if (waitTime > 0) {
                log.info("[sendEncodedAudio] [Session: {}] Rate control, waiting {} ms", callId, waitTime);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    log.warn("[Session: {}] Send timing control interrupted", callId);
                    return;
                }
            }

            // 检查连接状态
            if (!connection.isOpen()) {
                log.warn("[Session: {}] Connection closed during encoded audio sending", callId);
                connectionClosed.set(true);
                return;
            }

            // 提取一帧PCM数据
            byte[] frame = Arrays.copyOfRange(
                    audioBuffer, audioDataPosition,
                    audioDataPosition + samplesPerFrame * BYTES_PER_SAMPLE
            );
            audioDataPosition += samplesPerFrame * BYTES_PER_SAMPLE;

            // 编码为Opus
            byte[] encodedData = encodeAudioFrame(frame);
            if (encodedData == null || encodedData.length == 0) {
                log.warn("[Session: {}] Opus encoding failed, skipping frame", callId);
                continue; // 编码失败，跳过此帧
            }

            // 更新音频位置（按样本数）
            audioPosition += samplesPerFrame;

            // 发送编码后的数据
            connection.sendBinary(encodedData);
            log.info("[sendEncodedAudio]  [Session: {}] Sent {} bytes of Opus audio", callId, encodedData.length);
        }
    }

    /**
     * 编码单帧音频
     */
    private byte[] encodeAudioFrame(byte[] pcmFrame) {
        ByteBuffer pcmBuffer = ByteBuffer.allocateDirect(pcmFrame.length)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(pcmFrame)
                .flip();

        ShortBuffer shortBuffer = pcmBuffer.asShortBuffer();
        ByteBuffer encodedBuffer = ByteBuffer.allocateDirect(MAX_FRAME_SIZE);

        int encodedSize = Opus.INSTANCE.opus_encode(
                encoder, shortBuffer, encodeFrameSizeSamples,
                encodedBuffer, MAX_FRAME_SIZE
        );

        if (encodedSize < 0) {
            log.error("[Session: {}] Opus encode failed: {}", callId, encodedSize);
            return null;
        }

        byte[] encodedData = new byte[encodedSize];
        encodedBuffer.get(encodedData);
        return encodedData;
    }

    /**
     * 控制发送时序 - 基于时间轴的精确控制
     */
    private long controlSendTiming(int samples) {
        if (startTimestamp == 0) {
            startTimestamp = System.currentTimeMillis();
            return 0; // 尚未开始，立即发送
        }

        // 计算已过去的时间（毫秒）
        long elapsedMs = System.currentTimeMillis() - startTimestamp;

        // 计算当前音频位置应该播放的时间点（毫秒）
        // audioPosition是已经发送的样本数，加上本次要发送的样本数
        long nextAudioPositionSamples = audioPosition + samples;
        long nextOutputMs = (nextAudioPositionSamples * 1000L) / sampleRate;

        // 如果还没到应该发送的时间，计算需要等待的时间
        if (elapsedMs < nextOutputMs) {
            long waitMs = nextOutputMs - elapsedMs;
            log.info("controlSendTiming [Session: {}] elapsedMs {} nextOutputMs {} Waiting for {} ms", callId, elapsedMs, nextOutputMs, waitMs);
            return Math.max(waitMs, 0);
        }

        return 0; // 已经到时间或超时，立即发送
    }

    /**
     * 处理TTS消息
     */
    private void handleTTSMessage(TtsOutbound ttsMessage) throws IOException {
        if (!connection.isOpen()) {
            log.warn("[Session: {}] Connection closed, cannot send TTS message", callId);
            connectionClosed.set(true);
            return;
        }

        try {
            connection.sendText(ttsMessage);
            log.debug("[Session: {}] TTS message sent: {}", callId, ttsMessage.getState());
        } catch (IOException e) {
            // 检查是否是连接关闭错误
            if (e.getMessage() != null &&
                    (e.getMessage().contains("ClosedChannelException") ||
                            e.getMessage().contains("Connection reset"))) {
                log.warn("[Session: {}] Connection closed while sending TTS message", callId);
                connectionClosed.set(true);
            } else {
                log.error("[Session: {}] Failed to send TTS message", callId, e);
                throw e;
            }
        }
    }

    /**
     * 中断媒体处理
     */
    public void interrupt() {
        if (!interruptFlag.compareAndSet(false, true)) {
            return; // 已经中断
        }

        log.info("[Session: {}] Interrupting media processing", callId);

        processLock.lock();
        try {
            // 清空队列
            messageQueue.clear();

            // 重置编解码器状态
            resetCodecState();

            // 重置发送状态
            resetSendingState();

            // 发送中断信号
            sendInterruptSignal();

            log.info("[Session: {}] Media processing interrupted", callId);
        } finally {
            processLock.unlock();
        }
    }

    /**
     * 重置编解码器状态
     */
    private void resetCodecState() {
        try {
            if (encoder != null) {
                Opus.INSTANCE.opus_encoder_ctl(encoder, Opus.OPUS_RESET_STATE);
            }
            if (decoder != null) {
                Opus.INSTANCE.opus_decoder_ctl(decoder, Opus.OPUS_RESET_STATE);
            }
            log.debug("[Session: {}] Codec state reset", callId);
        } catch (Exception e) {
            log.warn("[Session: {}] Failed to reset codec state", callId, e);
        }
    }

    /**
     * 发送中断信号
     */
    private void sendInterruptSignal() {
        try {
            TtsOutbound interruptMsg = new TtsOutbound();
            interruptMsg.setState("interrupt");
            interruptMsg.setSessionId(callId);
            interruptMsg.setType("system");
            interruptMsg.setText("系统中断");

            connection.sendText(interruptMsg);
            log.debug("[Session: {}] Interrupt signal sent", callId);
        } catch (IOException e) {
            log.warn("[Session: {}] Failed to send interrupt signal", callId, e);
        }
    }

    /**
     * 恢复媒体处理
     */
    public void resume() {
        if (interruptFlag.compareAndSet(true, false)) {
            log.info("[Session: {}] Media processing resumed", callId);
            resetSendingState(); // 恢复时重置发送状态
        }
    }

    /**
     * 优雅关闭
     */
    public void shutdown() {
        if (!running.compareAndSet(true, false)) {
            return; // 已经关闭
        }

        log.info("[Session: {}] Shutting down MediaProcessor", callId);

        // 标记连接已关闭
        connectionClosed.set(true);

        // 中断处理线程
        Thread.currentThread().interrupt();

        // 关闭调度器
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
            Thread.currentThread().interrupt();
        }

        // 释放资源
        releaseResources();

        // 记录最终统计
        logStatistics();

        log.info("[Session: {}] MediaProcessor shutdown completed", callId);
    }

    /**
     * 释放资源
     */
    private void releaseResources() {
        try {
            if (encoder != null) {
                opusPool.returnEncoder(encoder);
                encoder = null;
            }
            if (decoder != null) {
                opusPool.returnDecoder(decoder);
                decoder = null;
            }
            log.debug("[Session: {}] Codec resources released", callId);
        } catch (Exception e) {
            log.warn("[Session: {}] Failed to release codec resources", callId, e);
        }

        // 清空队列
        messageQueue.clear();
    }

    /**
     * 重置处理器
     */
    public void reset() {
        log.info("[Session: {}] Resetting MediaProcessor", callId);

        processLock.lock();
        try {
            // 清空状态
            messageQueue.clear();
            resetSendingState();
            interruptFlag.set(false);
            connectionClosed.set(false);
            running.set(true);

            // 重置编解码器
            releaseResources();
            initCodec();

            log.info("[Session: {}] MediaProcessor reset completed", callId);
        } finally {
            processLock.unlock();
        }
    }

    /**
     * 记录统计信息
     */
    private void logStatistics() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastLogTime < LOG_INTERVAL_MS) {
            return;
        }

        int queueSize = messageQueue.size();
        int processed = processedMessages.getAndSet(0);
        int dropped = droppedMessages.getAndSet(0);

        log.info("[Session: {}] Statistics - Queue: {}, Processed: {}, Dropped: {}, Active: {}, Audio Position: {} samples",
                callId, queueSize, processed, dropped, running.get(), audioPosition);

        lastLogTime = currentTime;
    }

    /**
     * 获取处理器状态
     */
    public String getStatus() {
        return String.format(
                "MediaProcessor[%s]: queue=%d, running=%s, interrupted=%s, connectionClosed=%s, audioPosition=%d samples, startTimestamp=%d",
                callId, messageQueue.size(), running.get(), interruptFlag.get(), connectionClosed.get(),
                audioPosition, startTimestamp
        );
    }

    /**
     * 健康检查
     */
    public boolean isHealthy() {
        return running.get() && !interruptFlag.get() && !connectionClosed.get() && encoder != null && decoder != null;
    }

    /**
     * 标记连接已关闭
     */
    public void markConnectionClosed() {
        log.info("[Session: {}] Marking connection as closed", callId);
        connectionClosed.set(true);
        running.set(false);
    }

    /**
     * 设置TTS事件监听器
     */
    public void setTtsEventListener(Consumer<TtsEvent> ttsEventListener) {
        this.ttsEventListener = ttsEventListener;
        log.debug("[Session: {}] TTS event listener set", callId);
    }

    /**
     * 获取TTS事件监听器
     */
    public Consumer<TtsEvent> getTtsEventListener() {
        return this.ttsEventListener;
    }

    /**
     * 触发TTS事件
     */
    public void triggerTtsEvent(TtsEvent event) {
        if (ttsEventListener != null) {
            try {
                ttsEventListener.accept(event);
                log.debug("[Session: {}] TTS event triggered", callId);
            } catch (Exception e) {
                log.error("[Session: {}] Error in TTS event listener", callId, e);
            }
        } else {
            log.debug("[Session: {}] No TTS event listener set", callId);
        }
    }

    /**
     * 设置音频参数（可选，用于动态配置）
     */
    public void setAudioParameters(int sampleRate, int frameDurationMs) {
        // 如果需要支持动态参数，可以在这里实现
        // 注意：修改参数后需要重置状态
        log.debug("[Session: {}] Audio parameters set - sampleRate: {}, frameDuration: {}ms",
                callId, sampleRate, frameDurationMs);
    }
}