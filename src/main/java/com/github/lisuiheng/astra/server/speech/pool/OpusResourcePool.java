package com.github.lisuiheng.astra.server.speech.pool;


import com.sun.jna.ptr.PointerByReference;
import lombok.extern.slf4j.Slf4j;
import tomp2p.opuswrapper.Opus;

import jakarta.annotation.PreDestroy;
import java.nio.IntBuffer;
import java.util.Map;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

@Slf4j
public class OpusResourcePool {

    // 配置类
    @Slf4j
    public static class OpusConfig {
        private final int sampleRate;
        private final int channels;
        private final int application;
        private final int complexity;
        private final int bitrate;
        private final boolean variableBitrate;

        public static class Builder {
            private int sampleRate = 24000;
            private int channels = 1;
            private int application = Opus.OPUS_APPLICATION_VOIP;
            private int complexity = 5; // 中等复杂度
            private int bitrate = 24000; // 24kbps
            private boolean variableBitrate = false;

            public Builder sampleRate(int sampleRate) {
                this.sampleRate = sampleRate;
                return this;
            }

            public Builder channels(int channels) {
                this.channels = channels;
                return this;
            }

            public Builder application(int application) {
                this.application = application;
                return this;
            }

            public Builder complexity(int complexity) {
                this.complexity = Math.max(0, Math.min(10, complexity));
                return this;
            }

            public Builder bitrate(int bitrate) {
                this.bitrate = bitrate;
                return this;
            }

            public Builder variableBitrate(boolean variableBitrate) {
                this.variableBitrate = variableBitrate;
                return this;
            }

            public OpusConfig build() {
                return new OpusConfig(this);
            }
        }

        private OpusConfig(Builder builder) {
            this.sampleRate = builder.sampleRate;
            this.channels = builder.channels;
            this.application = builder.application;
            this.complexity = builder.complexity;
            this.bitrate = builder.bitrate;
            this.variableBitrate = builder.variableBitrate;
        }

        // Getters
        public int getSampleRate() { return sampleRate; }
        public int getChannels() { return channels; }
        public int getApplication() { return application; }
        public int getComplexity() { return complexity; }
        public int getBitrate() { return bitrate; }
        public boolean isVariableBitrate() { return variableBitrate; }

        @Override
        public String toString() {
            return String.format("OpusConfig[rate=%d, ch=%d, app=%d, comp=%d, bitrate=%d, vbr=%s]",
                    sampleRate, channels, application, complexity, bitrate, variableBitrate);
        }
    }

    // 资源包装类
    private static class OpusResource {
        final PointerByReference pointer;
        final OpusConfig config;
        final long createTime;
        volatile long lastBorrowTime;
        volatile long lastReturnTime;
        volatile int usageCount;
        volatile boolean valid = true;

        OpusResource(PointerByReference pointer, OpusConfig config) {
            this.pointer = pointer;
            this.config = config;
            this.createTime = System.currentTimeMillis();
            this.lastBorrowTime = 0;
            this.lastReturnTime = 0;
            this.usageCount = 0;
        }

        void markBorrowed() {
            this.lastBorrowTime = System.currentTimeMillis();
            this.usageCount++;
        }

        void markReturned() {
            this.lastReturnTime = System.currentTimeMillis();
        }

        boolean isExpired(long maxAgeMs) {
            return (System.currentTimeMillis() - createTime) > maxAgeMs;
        }

        boolean isIdleTooLong(long maxIdleMs) {
            return lastReturnTime > 0 &&
                    (System.currentTimeMillis() - lastReturnTime) > maxIdleMs;
        }

        String getStatus() {
            return String.format("Resource[create=%d, lastBorrow=%d, lastReturn=%d, usage=%d, valid=%s]",
                    createTime, lastBorrowTime, lastReturnTime, usageCount, valid);
        }
    }

    // 池配置
    private final OpusConfig encoderConfig;
    private final OpusConfig decoderConfig;
    private final int minIdle;
    private final int maxIdle;
    private final int maxTotal;
    private final long maxResourceAgeMs;
    private final long maxIdleTimeMs;
    private final long borrowTimeoutMs;

    // 资源池
    private final ConcurrentHashMap<PointerByReference, OpusResource> encoderResources;
    private final ConcurrentHashMap<PointerByReference, OpusResource> decoderResources;
    private final BlockingQueue<PointerByReference> availableEncoders;
    private final BlockingQueue<PointerByReference> availableDecoders;

    // 统计和状态
    private final AtomicInteger activeEncoders = new AtomicInteger(0);
    private final AtomicInteger activeDecoders = new AtomicInteger(0);
    private final AtomicInteger createdEncoders = new AtomicInteger(0);
    private final AtomicInteger createdDecoders = new AtomicInteger(0);
    private final AtomicInteger destroyedEncoders = new AtomicInteger(0);
    private final AtomicInteger destroyedDecoders = new AtomicInteger(0);

    // 锁和调度器
    private final ReentrantLock encoderLock = new ReentrantLock();
    private final ReentrantLock decoderLock = new ReentrantLock();
    private final ScheduledExecutorService maintenanceExecutor;
    private final ScheduledExecutorService healthCheckExecutor;

    // 状态标志
    private volatile boolean running = true;

    public OpusResourcePool() {
        this(new Builder());
    }

    public OpusResourcePool(Builder builder) {
        this.encoderConfig = builder.encoderConfig;
        this.decoderConfig = builder.decoderConfig;
        this.minIdle = builder.minIdle;
        this.maxIdle = builder.maxIdle;
        this.maxTotal = builder.maxTotal;
        this.maxResourceAgeMs = builder.maxResourceAgeMs;
        this.maxIdleTimeMs = builder.maxIdleTimeMs;
        this.borrowTimeoutMs = builder.borrowTimeoutMs;

        // 初始化数据结构
        this.encoderResources = new ConcurrentHashMap<>();
        this.decoderResources = new ConcurrentHashMap<>();
        this.availableEncoders = new LinkedBlockingQueue<>(maxTotal);
        this.availableDecoders = new LinkedBlockingQueue<>(maxTotal);

        // 初始化调度器
        this.maintenanceExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OpusPool-Maintenance");
            t.setDaemon(true);
            return t;
        });

        this.healthCheckExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "OpusPool-HealthCheck");
            t.setDaemon(true);
            return t;
        });

        // 初始化池
        initializePool();

        // 启动维护任务
        startMaintenanceTasks();

        log.info("OpusResourcePool initialized: {}", this.getConfigInfo());
    }

    private void initializePool() {
        log.info("Initializing Opus resource pool with {} initial resources", minIdle);

        for (int i = 0; i < minIdle; i++) {
            try {
                createAndAddEncoder();
                createAndAddDecoder();
            } catch (Exception e) {
                log.error("Failed to initialize Opus resource {}", i, e);
                if (i == 0) {
                    throw new IllegalStateException("Failed to initialize any Opus resources", e);
                }
                break;
            }
        }

        log.info("Pool initialized: {} encoders, {} decoders created",
                createdEncoders.get(), createdDecoders.get());
    }

    private void startMaintenanceTasks() {
        // 资源回收任务（每30秒运行一次）
        maintenanceExecutor.scheduleAtFixedRate(() -> {
            try {
                evictIdleResources();
                evictExpiredResources();
                replenishPool();
            } catch (Exception e) {
                log.error("Maintenance task failed", e);
            }
        }, 30, 30, TimeUnit.SECONDS);

        // 健康检查任务（每分钟运行一次）
        healthCheckExecutor.scheduleAtFixedRate(() -> {
            try {
                checkPoolHealth();
            } catch (Exception e) {
                log.error("Health check task failed", e);
            }
        }, 60, 60, TimeUnit.SECONDS);

        log.info("Maintenance tasks started");
    }

    // 创建并添加编码器
    private void createAndAddEncoder() {
        encoderLock.lock();
        try {
            if (createdEncoders.get() >= maxTotal) {
                log.warn("Maximum encoder limit reached: {}", maxTotal);
                return;
            }

            PointerByReference encoder = createEncoder(encoderConfig);
            OpusResource resource = new OpusResource(encoder, encoderConfig);
            encoderResources.put(encoder, resource);
            availableEncoders.offer(encoder);
            createdEncoders.incrementAndGet();

            log.debug("Created new encoder: total={}", createdEncoders.get());
        } finally {
            encoderLock.unlock();
        }
    }

    // 创建并添加解码器
    private void createAndAddDecoder() {
        decoderLock.lock();
        try {
            if (createdDecoders.get() >= maxTotal) {
                log.warn("Maximum decoder limit reached: {}", maxTotal);
                return;
            }

            PointerByReference decoder = createDecoder(decoderConfig);
            OpusResource resource = new OpusResource(decoder, decoderConfig);
            decoderResources.put(decoder, resource);
            availableDecoders.offer(decoder);
            createdDecoders.incrementAndGet();

            log.debug("Created new decoder: total={}", createdDecoders.get());
        } finally {
            decoderLock.unlock();
        }
    }

    // 创建编码器
    private PointerByReference createEncoder(OpusConfig config) {
        IntBuffer error = IntBuffer.allocate(1);
        PointerByReference encoder = Opus.INSTANCE.opus_encoder_create(
                config.getSampleRate(),
                config.getChannels(),
                config.getApplication(),
                error
        );

        if (error.get() != Opus.OPUS_OK) {
            throw new IllegalStateException("Failed to create encoder: " + error.get());
        }

        // 配置编码器参数
        configureEncoder(encoder, config);

        log.debug("Encoder created with config: {}", config);
        return encoder;
    }

    // 配置编码器
    private void configureEncoder(PointerByReference encoder, OpusConfig config) {
        IntBuffer error = IntBuffer.allocate(1);

        // 设置比特率
        Opus.INSTANCE.opus_encoder_ctl(encoder, Opus.OPUS_SET_BITRATE_REQUEST, config.getBitrate());

        // 设置复杂度
        Opus.INSTANCE.opus_encoder_ctl(encoder, Opus.OPUS_SET_COMPLEXITY_REQUEST, config.getComplexity());

        // 设置VBR
        Opus.INSTANCE.opus_encoder_ctl(encoder, Opus.OPUS_SET_VBR_REQUEST, config.isVariableBitrate() ? 1 : 0);

        // 设置预期丢包率
        Opus.INSTANCE.opus_encoder_ctl(encoder, Opus.OPUS_SET_PACKET_LOSS_PERC_REQUEST, 10);

        log.debug("Encoder configured: {}", config);
    }

    // 创建解码器
    private PointerByReference createDecoder(OpusConfig config) {
        IntBuffer error = IntBuffer.allocate(1);
        PointerByReference decoder = Opus.INSTANCE.opus_decoder_create(
                config.getSampleRate(),
                config.getChannels(),
                error
        );

        if (error.get() != Opus.OPUS_OK) {
            throw new IllegalStateException("Failed to create decoder: " + error.get());
        }

        log.debug("Decoder created with config: {}", config);
        return decoder;
    }

    // 借出编码器
    public PointerByReference borrowEncoder() throws InterruptedException, ResourceException {
        if (!running) {
            throw new ResourceException("Pool is shutting down");
        }

        long startTime = System.currentTimeMillis();
        PointerByReference encoder = null;

        while (encoder == null && running) {
            // 尝试从队列获取
            encoder = availableEncoders.poll(100, TimeUnit.MILLISECONDS);

            if (encoder != null) {
                OpusResource resource = encoderResources.get(encoder);
                if (resource != null && resource.valid) {
                    resource.markBorrowed();
                    activeEncoders.incrementAndGet();
                    log.debug("Encoder borrowed: active={}", activeEncoders.get());
                    return encoder;
                } else {
                    // 无效资源，销毁并继续尝试
                    destroyEncoder(encoder);
                    encoder = null;
                }
            }

            // 检查超时
            if (System.currentTimeMillis() - startTime > borrowTimeoutMs) {
                throw new ResourceException("Timeout waiting for encoder");
            }

            // 如果池中资源不足，创建新的
            if (availableEncoders.isEmpty() && createdEncoders.get() < maxTotal) {
                createAndAddEncoder();
            }
        }

        throw new ResourceException("Failed to borrow encoder");
    }

    // 归还编码器
    public void returnEncoder(PointerByReference encoder) {
        if (encoder == null) {
            log.warn("Attempted to return null encoder");
            return;
        }

        encoderLock.lock();
        try {
            OpusResource resource = encoderResources.get(encoder);
            if (resource == null) {
                log.warn("Returned unknown encoder");
                destroyEncoder(encoder);
                return;
            }

            if (!resource.valid) {
                log.debug("Discarding invalid encoder");
                destroyEncoder(encoder);
                return;
            }

            // 重置编码器状态
            IntBuffer error = IntBuffer.allocate(1);
            Opus.INSTANCE.opus_encoder_ctl(encoder, Opus.OPUS_RESET_STATE);

            resource.markReturned();
            activeEncoders.decrementAndGet();

            // 如果池中空闲资源过多，销毁而不是归还
            if (availableEncoders.size() >= maxIdle) {
                log.debug("Too many idle encoders, destroying: total={}, idle={}",
                        createdEncoders.get(), availableEncoders.size());
                destroyEncoder(encoder);
            } else {
                availableEncoders.offer(encoder);
                log.debug("Encoder returned: active={}, idle={}",
                        activeEncoders.get(), availableEncoders.size());
            }
        } finally {
            encoderLock.unlock();
        }
    }

    // 借出解码器
    public PointerByReference borrowDecoder() throws InterruptedException, ResourceException {
        if (!running) {
            throw new ResourceException("Pool is shutting down");
        }

        long startTime = System.currentTimeMillis();
        PointerByReference decoder = null;

        while (decoder == null && running) {
            decoder = availableDecoders.poll(100, TimeUnit.MILLISECONDS);

            if (decoder != null) {
                OpusResource resource = decoderResources.get(decoder);
                if (resource != null && resource.valid) {
                    resource.markBorrowed();
                    activeDecoders.incrementAndGet();
                    log.debug("Decoder borrowed: active={}", activeDecoders.get());
                    return decoder;
                } else {
                    destroyDecoder(decoder);
                    decoder = null;
                }
            }

            if (System.currentTimeMillis() - startTime > borrowTimeoutMs) {
                throw new ResourceException("Timeout waiting for decoder");
            }

            if (availableDecoders.isEmpty() && createdDecoders.get() < maxTotal) {
                createAndAddDecoder();
            }
        }

        throw new ResourceException("Failed to borrow decoder");
    }

    // 归还解码器
    public void returnDecoder(PointerByReference decoder) {
        if (decoder == null) {
            log.warn("Attempted to return null decoder");
            return;
        }

        decoderLock.lock();
        try {
            OpusResource resource = decoderResources.get(decoder);
            if (resource == null) {
                log.warn("Returned unknown decoder");
                destroyDecoder(decoder);
                return;
            }

            if (!resource.valid) {
                log.debug("Discarding invalid decoder");
                destroyDecoder(decoder);
                return;
            }

            // 重置解码器状态
            IntBuffer error = IntBuffer.allocate(1);
            Opus.INSTANCE.opus_decoder_ctl(decoder, Opus.OPUS_RESET_STATE);

            resource.markReturned();
            activeDecoders.decrementAndGet();

            if (availableDecoders.size() >= maxIdle) {
                log.debug("Too many idle decoders, destroying: total={}, idle={}",
                        createdDecoders.get(), availableDecoders.size());
                destroyDecoder(decoder);
            } else {
                availableDecoders.offer(decoder);
                log.debug("Decoder returned: active={}, idle={}",
                        activeDecoders.get(), availableDecoders.size());
            }
        } finally {
            decoderLock.unlock();
        }
    }

    // 销毁编码器
    private void destroyEncoder(PointerByReference encoder) {
        encoderLock.lock();
        try {
            if (encoder != null) {
                try {
                    Opus.INSTANCE.opus_encoder_destroy(encoder);
                    encoderResources.remove(encoder);
                    availableEncoders.remove(encoder);
                    destroyedEncoders.incrementAndGet();
                    log.debug("Encoder destroyed: totalDestroyed={}", destroyedEncoders.get());
                } catch (Exception e) {
                    log.error("Failed to destroy encoder", e);
                }
            }
        } finally {
            encoderLock.lock();
        }
    }

    // 销毁解码器
    private void destroyDecoder(PointerByReference decoder) {
        decoderLock.lock();
        try {
            if (decoder != null) {
                try {
                    Opus.INSTANCE.opus_decoder_destroy(decoder);
                    decoderResources.remove(decoder);
                    availableDecoders.remove(decoder);
                    destroyedDecoders.incrementAndGet();
                    log.debug("Decoder destroyed: totalDestroyed={}", destroyedDecoders.get());
                } catch (Exception e) {
                    log.error("Failed to destroy decoder", e);
                }
            }
        } finally {
            decoderLock.unlock();
        }
    }

    // 回收空闲资源
    private void evictIdleResources() {
        evictIdleEncoders();
        evictIdleDecoders();
    }

    private void evictIdleEncoders() {
        encoderLock.lock();
        try {
            int idleToKeep = Math.max(minIdle, availableEncoders.size() - maxIdle);
            int evicted = 0;

            for (PointerByReference encoder : availableEncoders) {
                OpusResource resource = encoderResources.get(encoder);
                if (resource != null && resource.isIdleTooLong(maxIdleTimeMs)) {
                    if (availableEncoders.size() - evicted > idleToKeep) {
                        destroyEncoder(encoder);
                        evicted++;
                    }
                }
            }

            if (evicted > 0) {
                log.info("Evicted {} idle encoders", evicted);
            }
        } finally {
            encoderLock.unlock();
        }
    }

    private void evictIdleDecoders() {
        decoderLock.lock();
        try {
            int idleToKeep = Math.max(minIdle, availableDecoders.size() - maxIdle);
            int evicted = 0;

            for (PointerByReference decoder : availableDecoders) {
                OpusResource resource = decoderResources.get(decoder);
                if (resource != null && resource.isIdleTooLong(maxIdleTimeMs)) {
                    if (availableDecoders.size() - evicted > idleToKeep) {
                        destroyDecoder(decoder);
                        evicted++;
                    }
                }
            }

            if (evicted > 0) {
                log.info("Evicted {} idle decoders", evicted);
            }
        } finally {
            decoderLock.unlock();
        }
    }

    // 回收过期资源
    private void evictExpiredResources() {
        evictExpiredEncoders();
        evictExpiredDecoders();
    }

    private void evictExpiredEncoders() {
        encoderLock.lock();
        try {
            int expired = 0;
            for (Map.Entry<PointerByReference, OpusResource> entry : encoderResources.entrySet()) {
                if (entry.getValue().isExpired(maxResourceAgeMs)) {
                    destroyEncoder(entry.getKey());
                    expired++;
                }
            }

            if (expired > 0) {
                log.info("Evicted {} expired encoders", expired);
            }
        } finally {
            encoderLock.unlock();
        }
    }

    private void evictExpiredDecoders() {
        decoderLock.lock();
        try {
            int expired = 0;
            for (Map.Entry<PointerByReference, OpusResource> entry : decoderResources.entrySet()) {
                if (entry.getValue().isExpired(maxResourceAgeMs)) {
                    destroyDecoder(entry.getKey());
                    expired++;
                }
            }

            if (expired > 0) {
                log.info("Evicted {} expired decoders", expired);
            }
        } finally {
            decoderLock.unlock();
        }
    }

    // 补充池资源
    private void replenishPool() {
        replenishEncoders();
        replenishDecoders();
    }

    private void replenishEncoders() {
        encoderLock.lock();
        try {
            int needed = minIdle - availableEncoders.size();
            for (int i = 0; i < needed && createdEncoders.get() < maxTotal; i++) {
                createAndAddEncoder();
            }

            if (needed > 0) {
                log.debug("Replenished {} encoders", Math.min(needed, maxTotal - createdEncoders.get()));
            }
        } finally {
            encoderLock.unlock();
        }
    }

    private void replenishDecoders() {
        decoderLock.lock();
        try {
            int needed = minIdle - availableDecoders.size();
            for (int i = 0; i < needed && createdDecoders.get() < maxTotal; i++) {
                createAndAddDecoder();
            }

            if (needed > 0) {
                log.debug("Replenished {} decoders", Math.min(needed, maxTotal - createdDecoders.get()));
            }
        } finally {
            decoderLock.unlock();
        }
    }

    // 健康检查
    private void checkPoolHealth() {
        encoderLock.lock();
        decoderLock.lock();
        try {
            int totalEncoders = createdEncoders.get();
            int idleEncoders = availableEncoders.size();
            int activeEnc = activeEncoders.get();
            int totalDecoders = createdDecoders.get();
            int idleDecoders = availableDecoders.size();
            int activeDec = activeDecoders.get();

            log.info("Pool Health Check:");
            log.info("  Encoders - Total: {}, Active: {}, Idle: {}, Destroyed: {}",
                    totalEncoders, activeEnc, idleEncoders, destroyedEncoders.get());
            log.info("  Decoders - Total: {}, Active: {}, Idle: {}, Destroyed: {}",
                    totalDecoders, activeDec, idleDecoders, destroyedDecoders.get());
            log.info("  Queue Sizes - Encoders: {}, Decoders: {}",
                    availableEncoders.size(), availableDecoders.size());

            // 检查资源泄漏
            if (activeEnc + idleEncoders != totalEncoders - destroyedEncoders.get()) {
                log.warn("Encoder resource leak detected!");
            }

            if (activeDec + idleDecoders != totalDecoders - destroyedDecoders.get()) {
                log.warn("Decoder resource leak detected!");
            }

        } finally {
            decoderLock.unlock();
            encoderLock.unlock();
        }
    }

    // 获取池信息
    public String getPoolInfo() {
        return String.format(
                "OpusResourcePool[Encoders: total=%d, active=%d, idle=%d; Decoders: total=%d, active=%d, idle=%d]",
                createdEncoders.get(), activeEncoders.get(), availableEncoders.size(),
                createdDecoders.get(), activeDecoders.get(), availableDecoders.size()
        );
    }

    private String getConfigInfo() {
        return String.format(
                "Config[encoder=%s, decoder=%s, minIdle=%d, maxIdle=%d, maxTotal=%d, maxAge=%dms, maxIdle=%dms, timeout=%dms]",
                encoderConfig, decoderConfig, minIdle, maxIdle, maxTotal, maxResourceAgeMs, maxIdleTimeMs, borrowTimeoutMs
        );
    }

    // 优雅关闭
    @PreDestroy
    public void shutdown() {
        if (!running) return;

        log.info("Shutting down OpusResourcePool...");
        running = false;

        // 关闭调度器
        maintenanceExecutor.shutdown();
        healthCheckExecutor.shutdown();

        try {
            if (!maintenanceExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                maintenanceExecutor.shutdownNow();
            }
            if (!healthCheckExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                healthCheckExecutor.shutdownNow();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            maintenanceExecutor.shutdownNow();
            healthCheckExecutor.shutdownNow();
        }

        // 销毁所有资源
        encoderLock.lock();
        decoderLock.lock();
        try {
            // 销毁编码器
            for (PointerByReference encoder : encoderResources.keySet()) {
                try {
                    Opus.INSTANCE.opus_encoder_destroy(encoder);
                } catch (Exception e) {
                    log.error("Error destroying encoder during shutdown", e);
                }
            }
            encoderResources.clear();
            availableEncoders.clear();

            // 销毁解码器
            for (PointerByReference decoder : decoderResources.keySet()) {
                try {
                    Opus.INSTANCE.opus_decoder_destroy(decoder);
                } catch (Exception e) {
                    log.error("Error destroying decoder during shutdown", e);
                }
            }
            decoderResources.clear();
            availableDecoders.clear();

            log.info("OpusResourcePool shutdown completed. Final stats: {}", getPoolInfo());

        } finally {
            decoderLock.unlock();
            encoderLock.unlock();
        }
    }

    // 异常类
    public static class ResourceException extends Exception {
        public ResourceException(String message) {
            super(message);
        }

        public ResourceException(String message, Throwable cause) {
            super(message, cause);
        }
    }

    // Builder类
    public static class Builder {
        private OpusConfig encoderConfig = new OpusConfig.Builder().build();
        private OpusConfig decoderConfig = new OpusConfig.Builder().build();
        private int minIdle = 5;
        private int maxIdle = 20;
        private int maxTotal = 100;
        private long maxResourceAgeMs = 30 * 60 * 1000; // 30分钟
        private long maxIdleTimeMs = 10 * 60 * 1000;    // 10分钟
        private long borrowTimeoutMs = 5000;            // 5秒

        public Builder encoderConfig(OpusConfig encoderConfig) {
            this.encoderConfig = encoderConfig;
            return this;
        }

        public Builder decoderConfig(OpusConfig decoderConfig) {
            this.decoderConfig = decoderConfig;
            return this;
        }

        public Builder minIdle(int minIdle) {
            this.minIdle = minIdle;
            return this;
        }

        public Builder maxIdle(int maxIdle) {
            this.maxIdle = maxIdle;
            return this;
        }

        public Builder maxTotal(int maxTotal) {
            this.maxTotal = maxTotal;
            return this;
        }

        public Builder maxResourceAgeMs(long maxResourceAgeMs) {
            this.maxResourceAgeMs = maxResourceAgeMs;
            return this;
        }

        public Builder maxIdleTimeMs(long maxIdleTimeMs) {
            this.maxIdleTimeMs = maxIdleTimeMs;
            return this;
        }

        public Builder borrowTimeoutMs(long borrowTimeoutMs) {
            this.borrowTimeoutMs = borrowTimeoutMs;
            return this;
        }

        public OpusResourcePool build() {
            return new OpusResourcePool(this);
        }
    }
}