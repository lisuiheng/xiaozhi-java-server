package com.github.lisuiheng.astra.server.ai.service.tts.impl;

//import com.github.lisuiheng.astra.server.user.service.AudioListener;
//import com.github.lisuiheng.astra.server.tts.service.TtsService;
//import com.github.lisuiheng.astra.server.tts.config.VoiceConfig;
//import com.github.lisuiheng.astra.server.tts.model.entity.SessionContext;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.function.Consumer;
//import java.util.concurrent.ThreadLocalRandom;
//
///**
// * 通用TTS服务实现
// */
//@Service
//@Slf4j
//public class GenericTtsService implements TtsService {
//
//    // 会话管理
//    private final Map<String, SessionContext> sessionContexts = new ConcurrentHashMap<>();
//    private final Map<String, List<Consumer<byte[]>>> audioConsumers = new ConcurrentHashMap<>();
//
//    @Override
//    public void createSession(String sessionId, VoiceConfig voiceConfig) {
//        try {
//            // 初始化会话上下文
//            SessionContext context = new SessionContext(sessionId, voiceConfig);
//            sessionContexts.put(sessionId, context);
//
//            log.info("TTS session created: {}", sessionId);
//        } catch (Exception e) {
//            log.error("Failed to create TTS session: {}", sessionId, e);
//            throw new RuntimeException("TTS session creation failed", e);
//        }
//    }
//
//    @Override
//    public void startSentence(String sessionId, String text) {
//        SessionContext context = sessionContexts.get(sessionId);
//        if (context != null && context.getAudioListener() != null) {
//            context.getAudioListener().onSentenceStart(text);
//        }
//    }
//
//    @Override
//    public void synthesizeText(String sessionId, String text) {
//        SessionContext context = sessionContexts.get(sessionId);
//        if (context != null) {
//            // 模拟TTS合成过程，生成随机音频数据
//            byte[] audioData = generateMockAudioData(text.length());
//
//            // 通知所有消费者
//            notifyAudioConsumers(sessionId, audioData);
//
//            // 通知注册的监听器
//            if (context.getAudioListener() != null) {
//                context.getAudioListener().onAudioReceived(audioData);
//            }
//        }
//    }
//
//    @Override
//    public void endMessage(String sessionId) {
//        SessionContext context = sessionContexts.get(sessionId);
//        if (context != null && context.getAudioListener() != null) {
//            context.getAudioListener().onSessionFinished();
//        }
//    }
//
//    @Override
//    public void interruptTtsStream(String sessionId) {
//        SessionContext context = sessionContexts.get(sessionId);
//        if (context != null && context.getAudioListener() != null) {
//            context.getAudioListener().onSessionError("Interrupted");
//        }
//        cleanupSession(sessionId);
//    }
//
//    @Override
//    public Flux<byte[]> getAudioStream(String sessionId) {
//        return Flux.create(sink -> {
//            registerAudioConsumer(sessionId, audio -> {
//                if (!sink.isCancelled()) {
//                    sink.next(audio);
//                }
//            });
//
//            sink.onDispose(() -> {
//                removeAudioConsumer(sessionId);
//            });
//        });
//    }
//
//    @Override
//    public void registerAudioListener(String sessionId, AudioListener listener) {
//        SessionContext context = sessionContexts.get(sessionId);
//        if (context != null) {
//            context.setAudioListener(listener);
//        }
//    }
//
//    @Override
//    public void cleanupSession(String sessionId) {
//        SessionContext context = sessionContexts.remove(sessionId);
//        audioConsumers.remove(sessionId);
//        if (context != null) {
//            log.info("Cleaned up TTS session: {}", sessionId);
//        }
//    }
//
//    private void registerAudioConsumer(String sessionId, Consumer<byte[]> consumer) {
//        audioConsumers.computeIfAbsent(sessionId, k -> new CopyOnWriteArrayList<>())
//                .add(consumer);
//    }
//
//    private void notifyAudioConsumers(String sessionId, byte[] audio) {
//        List<Consumer<byte[]>> consumers = audioConsumers.get(sessionId);
//        if (consumers != null) {
//            for (Consumer<byte[]> consumer : consumers) {
//                try {
//                    consumer.accept(audio);
//                } catch (Exception e) {
//                    log.warn("Error notifying audio consumer", e);
//                }
//            }
//        }
//    }
//
//    private void removeAudioConsumer(String sessionId) {
//        audioConsumers.remove(sessionId);
//    }
//
//    /**
//     * 生成模拟音频数据
//     * @param length 文本长度
//     * @return 模拟的音频数据
//     */
//    private byte[] generateMockAudioData(int length) {
//        // 根据文本长度生成相应大小的模拟音频数据
//        int size = Math.max(100, length * 10); // 至少100字节
//        byte[] audioData = new byte[size];
//        ThreadLocalRandom.current().nextBytes(audioData);
//        return audioData;
//    }
//
//}