package com.github.lisuiheng.astra.server.ai.service;

//import com.github.lisuiheng.astra.server.ai.model.dto.StreamChunk;
//import com.github.lisuiheng.astra.server.ai.model.dto.StreamWithTTSResponse;
//import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
//import com.github.lisuiheng.astra.server.tts.service.TtsService;
//import com.github.lisuiheng.astra.server.user.service.AudioListener;
//import com.github.lisuiheng.astra.server.tts.config.VoiceConfig;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Sinks;
//import reactor.core.scheduler.Schedulers;
//
//import java.util.List;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.CopyOnWriteArrayList;
//import java.util.regex.Pattern;
//
///**
// * 流式聊天与TTS同步输出服务
// */
//@Service
//@Slf4j
//@RequiredArgsConstructor
//public class StreamingChatWithTTSService {
//
//    private final EnhancedAgentService enhancedAgentService;
//    private final AgentService agentService;
//    private final TtsService ttsService;
//
//    // 会话管理
//    private final ConcurrentHashMap<String, ChatTTSSession> sessions = new ConcurrentHashMap<>();
//
//    // 句子边界检测正则
//    private static final Pattern SENTENCE_BOUNDARY = Pattern.compile(
//        "([。！？.!?]|\\n\\s*\\n|~{2,}|-{2,})"
//    );
//
//    /**
//     * 流式聊天带TTS合成
//     *
//     * @param agentId 智能体ID
//     * @param userId 用户ID
//     * @param userMessage 用户消息
//     * @param stream 是否流式
//     * @return 包含文本和音频的响应流
//     */
//    public Flux<StreamWithTTSResponse> chatWithTTS(
//            String agentId,
//            String userId,
//            String userMessage,
//            boolean stream) {
//
//        // 获取智能体配置
//        Agent agent = agentService.getById(agentId);
//        if (agent == null) {
//            return Flux.error(new IllegalArgumentException("智能体不存在: " + agentId));
//        }
//
//        // 生成会话ID
//        String sessionId = generateSessionId(agentId, userId);
//
//        // 创建语音配置
//        VoiceConfig voiceConfig = createVoiceConfig(agent);
//
//        // 创建会话
//        ChatTTSSession session = new ChatTTSSession(sessionId, agent, voiceConfig);
//        sessions.put(sessionId, session);
//
//        try {
//            // 初始化TTS会话
//            ttsService.createSession(sessionId, voiceConfig);
//
//        } catch (Exception e) {
//            log.error("Failed to initialize TTS session", e);
//            sessions.remove(sessionId);
//            return Flux.error(e);
//        }
//
//        // 获取聊天流
//        Flux<StreamChunk> chatStream = enhancedAgentService.chatStream(agentId, userId, userMessage);
//
//        // 处理聊天流并转换为TTS响应
//        return chatStream
//                .publishOn(Schedulers.boundedElastic())
//                .concatMap(chunk -> processChunk(session, chunk))
//                .doOnComplete(() -> completeSession(session))
//                .doOnError(error -> handleError(session, error))
//                .doOnCancel(() -> cancelSession(session))
//                .onErrorResume(error -> {
//                    handleError(session, error);
//                    return Flux.empty();
//                });
//    }
//
//    /**
//     * 中断流式会话
//     *
//     * @param agentId 智能体ID
//     * @param userId 用户ID
//     */
//    public void interruptStream(String agentId, String userId) {
//        String sessionId = generateSessionId(agentId, userId);
//        ChatTTSSession session = sessions.remove(sessionId);
//
//        if (session != null) {
//            session.setInterrupted(true);
//            ttsService.interruptTtsStream(sessionId);
//            ttsService.cleanupSession(sessionId);
//            log.info("Stream interrupted for session: {}", sessionId);
//        }
//    }
//
//    /**
//     * 处理聊天chunk
//     */
//    private Flux<StreamWithTTSResponse> processChunk(ChatTTSSession session, StreamChunk chunk) {
//        if (session.isInterrupted()) {
//            return Flux.empty();
//        }
//
//        String text = chunk.getContent();
//
//        // 添加到文本缓冲区
//        session.appendToBuffer(text);
//
//        // 检查句子边界
//        String remainingText = session.getBuffer();
//        StringBuilder toSynthesize = new StringBuilder();
//
//        int lastBoundary = 0;
//        java.util.regex.Matcher matcher = SENTENCE_BOUNDARY.matcher(remainingText);
//
//        while (matcher.find()) {
//            int boundaryPos = matcher.end();
//            String sentence = remainingText.substring(lastBoundary, boundaryPos).trim();
//
//            if (!sentence.isEmpty()) {
//                toSynthesize.append(sentence);
//
//                // 发送到TTS
//                if (session.isTtsReady()) {
//                    synthesizeText(session, sentence);
//                }
//            }
//
//            lastBoundary = boundaryPos;
//        }
//
//        // 处理剩余文本
//        if (lastBoundary < remainingText.length()) {
//            String remaining = remainingText.substring(lastBoundary).trim();
//            if (!remaining.isEmpty()) {
//                toSynthesize.append(remaining);
//            }
//        }
//
//        // 更新缓冲区
//        if (lastBoundary > 0) {
//            session.setBuffer(remainingText.substring(lastBoundary));
//        }
//
//        // 返回文本响应
//        if (toSynthesize.length() > 0) {
//            return Flux.just(StreamWithTTSResponse.textChunk(toSynthesize.toString()));
//        }
//
//        return Flux.just(StreamWithTTSResponse.textChunk(text));
//    }
//
//    /**
//     * 合成文本到TTS
//     */
//    private void synthesizeText(ChatTTSSession session, String text) {
//        if (session.isInterrupted() || text.trim().isEmpty()) {
//            return;
//        }
//
//        try {
//            // 开始句子
//            ttsService.startSentence(session.getSessionId(), text);
//
//            // 合成文本
//            ttsService.synthesizeText(session.getSessionId(), text);
//
//            log.debug("TTS synthesis started for text: {}", text.substring(0, Math.min(text.length(), 50)));
//
//        } catch (Exception e) {
//            log.error("TTS synthesis failed", e);
//        }
//    }
//
//    /**
//     * 完成会话
//     */
//    private void completeSession(ChatTTSSession session) {
//        try {
//            // 处理缓冲区中剩余的文本
//            String remainingText = session.getBuffer();
//            if (!remainingText.isEmpty() && session.isTtsReady()) {
//                synthesizeText(session, remainingText);
//            }
//
//            // 结束TTS消息
//            ttsService.endMessage(session.getSessionId());
//
//            // 等待TTS完成
//            Thread.sleep(100); // 短暂等待确保TTS处理完成
//
//        } catch (Exception e) {
//            log.error("Error completing session", e);
//        } finally {
//            cleanupSession(session);
//        }
//    }
//
//    /**
//     * 处理错误
//     */
//    private void handleError(ChatTTSSession session, Throwable error) {
//        log.error("Chat with TTS error", error);
//        cleanupSession(session);
//    }
//
//    /**
//     * 取消会话
//     */
//    private void cancelSession(ChatTTSSession session) {
//        log.info("Session cancelled: {}", session.getSessionId());
//        cleanupSession(session);
//    }
//
//    /**
//     * 清理会话
//     */
//    private void cleanupSession(ChatTTSSession session) {
//        if (session != null) {
//            sessions.remove(session.getSessionId());
//            ttsService.cleanupSession(session.getSessionId());
//            session.setCompleted(true);
//            log.debug("Session cleaned up: {}", session.getSessionId());
//        }
//    }
//
//    /**
//     * 生成会话ID
//     */
//    private String generateSessionId(String agentId, String userId) {
//        return String.format("agent:%s:user:%s", agentId, userId);
//    }
//
//    /**
//     * 创建语音配置
//     */
//    private VoiceConfig createVoiceConfig(Agent agent) {
//        return VoiceConfig.builder()
//                .voiceId(agent.getVoiceId())
//                .speed(1)
//                .pitch(1)
//                .volume(100)
//                .encoding("mp3")
//                .sampleRate(24000)
//                .build();
//    }
//
//    /**
//     * TTS会话监听器
//     */
//    private class TTSSessionListener implements AudioListener {
//        private final ChatTTSSession session;
//
//        public TTSSessionListener(ChatTTSSession session) {
//            this.session = session;
//        }
//
//        @Override
//        public void onAudioReceived(byte[] audio) {
//            // 将音频数据添加到会话的音频队列
//            session.addAudioChunk(audio);
//
//            // 创建音频响应
//            StreamWithTTSResponse audioResponse = StreamWithTTSResponse.audioChunk(audio);
//
//            // 通过session的响应sink发送
//            if (session.getResponseSink() != null) {
//                session.getResponseSink().emitNext(audioResponse, Sinks.EmitFailureHandler.FAIL_FAST);
//            }
//        }
//
//        @Override
//        public void onSentenceStart(String text) {
//            log.debug("TTS sentence started: {}", text);
//        }
//
//        @Override
//        public void onSentenceEnd(String text) {
//            log.debug("TTS sentence ended: {}", text);
//        }
//
//        @Override
//        public void onSessionStarted() {
//            log.debug("TTS session started");
//            session.setTtsReady(true);
//        }
//
//        @Override
//        public void onSessionFinished() {
//            log.debug("TTS session finished");
//            session.setCompleted(true);
//            if (session.getResponseSink() != null) {
//                session.getResponseSink().emitComplete(Sinks.EmitFailureHandler.FAIL_FAST);
//            }
//        }
//
//        @Override
//        public void onSessionError(String error) {
//            log.error("TTS session error: {}", error);
//            session.setTtsReady(false);
//            if (session.getResponseSink() != null) {
//                session.getResponseSink().emitError(new RuntimeException(error), Sinks.EmitFailureHandler.FAIL_FAST);
//            }
//        }
//    }
//
//    /**
//     * 聊天TTS会话
//     */
//    private static class ChatTTSSession {
//        private final String sessionId;
//        private final Agent agent;
//        private final VoiceConfig voiceConfig;
//
//        private final StringBuilder textBuffer = new StringBuilder();
//        private final List<byte[]> audioChunks = new CopyOnWriteArrayList<>();
//        private final Sinks.Many<StreamWithTTSResponse> responseSink =
//                Sinks.many().unicast().onBackpressureBuffer();
//
//        private volatile boolean ttsReady = false;
//        private volatile boolean interrupted = false;
//        private volatile boolean completed = false;
//
//        public ChatTTSSession(String sessionId, Agent agent, VoiceConfig voiceConfig) {
//            this.sessionId = sessionId;
//            this.agent = agent;
//            this.voiceConfig = voiceConfig;
//        }
//
//        public void appendToBuffer(String text) {
//            synchronized (textBuffer) {
//                textBuffer.append(text);
//            }
//        }
//
//        public String getBuffer() {
//            synchronized (textBuffer) {
//                return textBuffer.toString();
//            }
//        }
//
//        public void setBuffer(String text) {
//            synchronized (textBuffer) {
//                textBuffer.setLength(0);
//                textBuffer.append(text);
//            }
//        }
//
//        public void addAudioChunk(byte[] audio) {
//            audioChunks.add(audio);
//        }
//
//        public String getSessionId() {
//            return sessionId;
//        }
//
//        public boolean isTtsReady() {
//            return ttsReady;
//        }
//
//        public void setTtsReady(boolean ttsReady) {
//            this.ttsReady = ttsReady;
//        }
//
//        public boolean isInterrupted() {
//            return interrupted;
//        }
//
//        public void setInterrupted(boolean interrupted) {
//            this.interrupted = interrupted;
//        }
//
//        public boolean isCompleted() {
//            return completed;
//        }
//
//        public void setCompleted(boolean completed) {
//            this.completed = completed;
//        }
//
//        public Sinks.Many<StreamWithTTSResponse> getResponseSink() {
//            return responseSink;
//        }
//    }
//}