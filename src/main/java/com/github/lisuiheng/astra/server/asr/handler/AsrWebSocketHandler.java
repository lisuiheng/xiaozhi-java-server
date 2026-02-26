package com.github.lisuiheng.astra.server.asr.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.ai.model.dto.StreamChunk;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import com.github.lisuiheng.astra.server.ai.service.AgentService;
import com.github.lisuiheng.astra.server.ai.service.EnhancedAgentService;
import com.github.lisuiheng.astra.server.asr.model.dto.AsrChatMessage;
import com.github.lisuiheng.astra.server.asr.model.dto.GenericConnection;
import com.github.lisuiheng.astra.server.asr.model.dto.outbound.SttOutbound;
import com.github.lisuiheng.astra.server.asr.service.AsrWebSocketSessionManager;
import com.github.lisuiheng.astra.server.asr.service.UnifiedSessionManager;
import com.github.lisuiheng.astra.server.asr.service.VoiceChatService;
import com.github.lisuiheng.astra.common.util.CallContext;
import com.github.lisuiheng.astra.common.util.RequestContext;
import com.github.lisuiheng.astra.server.server.service.ChatDetailService;
import com.github.lisuiheng.astra.server.speech.model.dto.MediaProcessor;
import com.github.lisuiheng.astra.server.asr.model.dto.outbound.TtsOutbound;
import com.github.lisuiheng.astra.server.speech.pool.OpusResourcePool;
import com.github.lisuiheng.astra.server.speech.service.TtsService;
import com.github.lisuiheng.astra.server.speech.OkHttpTtsClient;
import com.github.lisuiheng.astra.server.speech.service.MediaProcessorManager;
import com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo;
import com.github.lisuiheng.astra.server.speech.model.dto.TtsEvent;
import com.github.lisuiheng.astra.server.server.model.dto.ChatDetailDTO;
import com.github.lisuiheng.astra.server.speech.protocol.EventType;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.ArrayList;

@Slf4j
@Service
public class AsrWebSocketHandler extends TextWebSocketHandler {
    @Autowired
    private OpusResourcePool opusPool;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private AsrWebSocketSessionManager asrWebSocketSessionManager;
    @Autowired
    private VoiceChatService voiceChatService;
    @Autowired
    private UnifiedSessionManager unifiedSessionManager;
    @Autowired
    private EnhancedAgentService enhancedAgentService;
    @Autowired
    private AgentService agentService;

    @Autowired
    private TtsService ttsService;

    @Autowired
    private MediaProcessorManager mediaProcessorManager;

    @Autowired
    private ChatDetailService chatDetailService;


    // 内部类：会话处理器，确保同一会话的消息顺序处理
    private class SessionProcessor {
        private final String callId;
        private final GenericConnection connection;
        private final OkHttpTtsClient.StreamingSession ttsSession;
        private final ObjectMapper objectMapper;
        
        // 句子队列：存储待处理的原始句子
        private final BlockingQueue<String> sentenceQueue = new LinkedBlockingQueue<>();
        
        // 当前正在处理的句子状态
        private volatile CurrentSentenceState currentSentenceState = null;
        
        // 处理线程
        private final ExecutorService executorService;
        private volatile boolean processing = false;
        private volatile boolean ttsCompleted = false;
        
        // TTS事件监听器
        private final Consumer<TtsEvent> ttsEventListener;
        
        // 用于等待句子完成的锁
        private final Object sentenceCompletionLock = new Object();
        
        public SessionProcessor(String callId, GenericConnection connection,
                                OkHttpTtsClient.StreamingSession ttsSession, ObjectMapper objectMapper) {
            this.callId = callId;
            this.connection = connection;
            this.ttsSession = ttsSession;
            this.objectMapper = objectMapper;
            this.executorService = Executors.newSingleThreadExecutor(r -> {
                Thread t = new Thread(r, "SentenceProcessor-" + callId);
                t.setDaemon(true);
                return t;
            });
            
            this.ttsEventListener = this::handleTtsEventTyped;
        }
        
        /**
         * 当前句子状态
         */
        private class CurrentSentenceState {
            private final String sentenceId;
            private final String originalText;
            private final long startTime;
            
            // 跟踪子句子
            private final List<SubSentence> subSentences = new ArrayList<>();
            private final AtomicInteger completedSubSentences = new AtomicInteger(0);
            private volatile boolean allSubSentencesComplete = false;
            
            // 用于文本匹配
            private final StringBuilder receivedText = new StringBuilder();
            
            public CurrentSentenceState(String sentenceId, String originalText) {
                this.sentenceId = sentenceId;
                this.originalText = originalText;
                this.startTime = System.currentTimeMillis();
            }
            
            /**
             * 处理TTS子句子事件
             */
            public boolean processTtsEvent(int eventType, String eventText) {
                synchronized (this) {
                    if (allSubSentencesComplete) {
                        return false;
                    }
                    
                    if (eventType == EventType.TTS_SENTENCE_START.getValue()) { // TTS_SENTENCE_START
                        // 开始一个新的子句子
                        SubSentence sub = new SubSentence(eventText);
                        subSentences.add(sub);
                        log.debug("Sub-sentence started for {}: {}", sentenceId, eventText);

                    } else if (eventType == EventType.TTS_SENTENCE_END.getValue()) { // TTS_SENTENCE_END
                        // 结束一个子句子
                        if (!subSentences.isEmpty()) {
                            SubSentence lastSub = subSentences.get(subSentences.size() - 1);
                            if (!lastSub.isCompleted()) {
                                lastSub.markCompleted(eventText);
                                completedSubSentences.incrementAndGet();
                                receivedText.append(eventText);
                                
                                log.debug("Sub-sentence completed for {}: {} (total: {}/{})", 
                                         sentenceId, eventText, 
                                         completedSubSentences.get(), subSentences.size());

                                
                                // 检查是否所有子句子都完成了
                                checkAllSubSentencesComplete();
                            }
                        }
                    }
                    
                    return true;
                }
            }
            
            /**
             * 检查是否所有子句子都完成了
             */
            private void checkAllSubSentencesComplete() {
                synchronized (this) {
                    if (!subSentences.isEmpty() && 
                        completedSubSentences.get() >= subSentences.size()) {
                        
                        allSubSentencesComplete = true;
                        
                        // 计算覆盖比例
                        double coverage = calculateTextCoverage();
                        log.info("All sub-sentences completed for {}: {}/{} subsentences, coverage: {}%, text: {}",
                                sentenceId, completedSubSentences.get(), subSentences.size(),
                                coverage * 100, receivedText.toString());
                        
                        // 通知等待线程
                        synchronized (sentenceCompletionLock) {
                            sentenceCompletionLock.notifyAll();
                        }
                    }
                }
            }
            
            /**
             * 计算接收文本对原始文本的覆盖比例
             */
            private double calculateTextCoverage() {
                if (originalText == null || originalText.isEmpty()) {
                    return 1.0;
                }
                
                String received = receivedText.toString();
                if (received.isEmpty()) {
                    return 0.0;
                }
                
                // 简单覆盖计算：计算接收文本在原始文本中的覆盖率
                // 可以更复杂：使用编辑距离或最长公共子序列
                int matchedChars = 0;
                int originalIdx = 0;
                int receivedIdx = 0;
                
                while (originalIdx < originalText.length() && receivedIdx < received.length()) {
                    char origChar = originalText.charAt(originalIdx);
                    char recvChar = received.charAt(receivedIdx);
                    
                    // 跳过标点差异
                    if (isSimilarCharacter(origChar, recvChar)) {
                        matchedChars++;
                        originalIdx++;
                        receivedIdx++;
                    } else if (isPunctuation(origChar)) {
                        originalIdx++; // 跳过原始文本中的标点
                    } else if (isPunctuation(recvChar)) {
                        receivedIdx++; // 跳过接收文本中的标点
                    } else {
                        // 字符不匹配，尝试继续
                        originalIdx++;
                    }
                }
                
                return (double) matchedChars / originalText.length();
            }
            
            private boolean isSimilarCharacter(char c1, char c2) {
                // 简单的字符相似性判断
                return c1 == c2 || 
                       Character.toLowerCase(c1) == Character.toLowerCase(c2) ||
                       (isChinesePunctuation(c1) && isChinesePunctuation(c2));
            }
            
            private boolean isPunctuation(char c) {
                return "，。！？；,.!?;".indexOf(c) != -1;
            }
            
            private boolean isChinesePunctuation(char c) {
                return "，。！？；".indexOf(c) != -1;
            }
            
            /**
             * 等待所有子句子完成
             */
            public boolean waitForCompletion(long timeoutMs) {
                long startTime = System.currentTimeMillis();
                long endTime = startTime + timeoutMs;
                
                synchronized (sentenceCompletionLock) {
                    while (!allSubSentencesComplete && System.currentTimeMillis() < endTime) {
                        try {
                            long remaining = endTime - System.currentTimeMillis();
                            if (remaining <= 0) {
                                break;
                            }
                            sentenceCompletionLock.wait(Math.min(remaining, 100));
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                    }
                }
                
                return allSubSentencesComplete;
            }
            
            public boolean isAllSubSentencesComplete() {
                return allSubSentencesComplete;
            }
            
            public String getSentenceId() {
                return sentenceId;
            }
            
            public String getOriginalText() {
                return originalText;
            }
            
            public int getCompletedSubSentenceCount() {
                return completedSubSentences.get();
            }
            
            public int getTotalSubSentenceCount() {
                return subSentences.size();
            }
            
            @Override
            public String toString() {
                return String.format("CurrentSentenceState{id=%s, text='%s', subs=%d/%d, complete=%s}",
                        sentenceId, 
                        originalText.length() > 30 ? originalText.substring(0, 30) + "..." : originalText,
                        completedSubSentences.get(), subSentences.size(),
                        allSubSentencesComplete);
            }
        }
        
        /**
         * 子句子状态
         */
        private static class SubSentence {
            private final String text;
            private volatile boolean completed = false;
            private volatile String completedText;
            private final long startTime;
            
            public SubSentence(String text) {
                this.text = text;
                this.startTime = System.currentTimeMillis();
            }
            
            public void markCompleted(String completedText) {
                this.completed = true;
                this.completedText = completedText;
            }
            
            public boolean isCompleted() {
                return completed;
            }
            
            public String getText() {
                return text;
            }
            
            public String getCompletedText() {
                return completedText;
            }
        }
        
        /**
         * 添加句子到处理队列
         */
        public void addSentence(String sentence) {
            if (sentence == null || sentence.trim().isEmpty()) {
                return;
            }
            
            String trimmedSentence = sentence.trim();
            sentenceQueue.offer(trimmedSentence);
            log.debug("Sentence added to queue: {}", trimmedSentence);
            
            if (!processing) {
                startProcessing();
            }
        }
        
        /**
         * 标记TTS完成
         */
        public void markTtsCompleted() {
            this.ttsCompleted = true;
            log.info("TTS completion marked for session: {}", callId);
        }

        /**
         * 获取TTS事件监听器
         */
        public Consumer<TtsEvent> getTtsEventListener() {
            return ttsEventListener;
        }
        
        /**
         * 处理TTS事件
         */
        private void handleTtsEventTyped(TtsEvent ttsEvent) {
            try {
                EventType eventType = ttsEvent.getEventType();
                String sessionId = ttsEvent.getSessionId();
                
                if (eventType == null || sessionId == null || !sessionId.equals(callId)) {
                    return;
                }
                
                // 提取事件数据
                String eventText = ttsEvent.getData() != null ? ttsEvent.getData().toString() : null;
                
                if (eventText == null) {
                    log.warn("TTS event has no text: {}", eventType);
                    return;
                }
                
                // 转发事件到当前句子状态
                boolean processed = false;
                CurrentSentenceState currentState = currentSentenceState;
                
                if (currentState != null) {
                    processed = currentState.processTtsEvent(eventType.getValue(), eventText);
                    
                    if (!processed) {
                        log.warn("Received TTS event for already completed sentence: {}", eventText);
                    }
                } else {
                    log.warn("Received TTS event but no current sentence state: {}", eventText);
                }
                
                // 记录事件
                String eventTypeStr = eventType == EventType.TTS_SENTENCE_START ? "SENTENCE_START" : "SENTENCE_END";
                log.debug("TTS event processed: {} - {} (processed: {})", eventTypeStr, eventText, processed);
                
            } catch (Exception e) {
                log.error("Error handling TTS event", e);
            }
        }
        
        /**
         * 开始处理
         */
        private void startProcessing() {
            if (processing) return;
            
            processing = true;
            executorService.submit(() -> {
                log.info("Starting sentence processing for session: {}", callId);
                
                try {
                    processSentences();
                } catch (Exception e) {
                    log.error("Error in sentence processing for session: {}", callId, e);
                } finally {
                    processing = false;
                    log.info("Sentence processing completed for session: {}", callId);
                }
            });
        }
        
        /**
         * 处理句子队列
         */
        private void processSentences() {
            int sentenceIndex = 0;
            
            while (true) {
                try {
                    // 检查是否应该结束
                    if (ttsCompleted && sentenceQueue.isEmpty()) {
                        log.info("All sentences processed, finishing session: {}", callId);
                        break;
                    }
                    
                    // 从队列中获取句子
                    String sentence = sentenceQueue.poll(100, TimeUnit.MILLISECONDS);
                    if (sentence == null || sentence.isEmpty()) {
                        continue;
                    }
                    
                    // 处理单个句子
                    processSingleSentence(sentence, ++sentenceIndex);
                    
                } catch (InterruptedException e) {
                    log.info("Sentence processing interrupted for session: {}", callId);
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    log.error("Error in sentence processing loop", e);
                }
            }

            // 在句子处理完成后，结束TTS会话
            try {
                if (ttsSession != null && ttsSession.isActive()) {
                    log.info("Finishing TTS session for callId: {}", callId);
                    ttsSession.finish().join(); // 等待TTS会话结束完成
                    log.info("TTS session finished successfully for callId: {}", callId);
                }
            } catch (Exception e) {
                log.error("Error finishing TTS session for callId: {}", callId, e);
            }
        }
        
        /**
         * 处理单个句子
         */
        private void processSingleSentence(String sentence, int sentenceIndex) {
            String sentenceId = String.format("sent_%d_%d", sentenceIndex, System.currentTimeMillis());
            
            log.info("Processing sentence {} [{}]: {}", sentenceIndex, sentenceId, sentence);
            
            // 创建当前句子状态
            CurrentSentenceState state = new CurrentSentenceState(sentenceId, sentence);
            currentSentenceState = state;
            
            try {
                // 发送句子到TTS
                CompletableFuture<Void> sendFuture = ttsSession.sendText(sentence);
                
                // 等待发送完成（发送超时）
//                sendFuture.get(5, TimeUnit.SECONDS);
//                log.info("Sentence sent to TTS: {} [{}], waiting for sub-sentences to complete", sentenceId, sentence);
//
//                // 等待所有子句子完成（TTS处理超时）
//                boolean completed = state.waitForCompletion(calculateWaitTimeout(sentence));
//
//                if (completed) {
//                    log.info("✅ Sentence completed successfully: {} [{}] - {} sub-sentences",
//                            sentenceId, sentence, state.getTotalSubSentenceCount());
//
//                } else {
//                    log.warn("⚠️ Sentence timeout: {} [{}] - completed {}/{} sub-sentences",
//                            sentenceId, sentence,
//                            state.getCompletedSubSentenceCount(), state.getTotalSubSentenceCount());
//                }
                
//            } catch (TimeoutException e) {
//                log.error("Timeout sending sentence to TTS: {} [{}]", sentenceId, sentence, e);
            } catch (Exception e) {
                log.error("Error processing sentence: {} [{}]", sentenceId, sentence, e);
            } finally {
                // 清理当前句子状态
                currentSentenceState = null;
            }
        }

        /**
         * 计算等待超时时间
         */
//        private long calculateWaitTimeout(String text) {
//            // 基于文本长度计算超时时间
//            // 每个字符大约50ms + 基础时间
//            int baseTimeout = Math.max(text.length() * 50, 5000); // 至少5秒
//            return Math.min(baseTimeout, 30000); // 最多30秒
//        }


        /**
         * 发送TTS停止消息
         */
//        private void sendTtsStop() {
//            try {
////                // 发送TTS停止消息
//                TtsOutbound stopMsg = new TtsOutbound();
//                stopMsg.setSessionId(callId);
//                stopMsg.setType("tts");
//                stopMsg.setState("stop");
//
//                connection.sendText(stopMsg);
//                log.info("TTS stop message sent for session: {}", callId);
//
//                // 结束TTS会话
//                if (ttsSession.isActive()) {
//                    ttsSession.finish().get(10, TimeUnit.SECONDS);
//                    log.info("TTS session finished for session: {}", callId);
//                }
//
//            } catch (Exception e) {
//                log.error("Error sending TTS stop message", e);
//            }
//        }
        
        /**
         * 关闭处理器
         */
        public void shutdown() {
            log.info("Shutting down SessionProcessor for session: {}", callId);
            
            // 结束TTS会话
            try {
                if (ttsSession != null && ttsSession.isActive()) {
                    log.info("Finishing TTS session during shutdown for callId: {}", callId);
                    ttsSession.finish().join(); // 等待TTS会话结束完成
                    log.info("TTS session finished successfully during shutdown for callId: {}", callId);
                }
            } catch (Exception e) {
                log.error("Error finishing TTS session during shutdown for callId: {}", callId, e);
            }
            
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                    log.warn("Forced shutdown of SessionProcessor for session: {}", callId);
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    private static class SentenceTask {
        final String sentence;

        SentenceTask(String sentence) {
            this.sentence = sentence;
        }
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String sessionId = session.getId();
        asrWebSocketSessionManager.addSession(sessionId, session);

        // 创建调用上下文并设置到MDC中
        CallContext callContext = CallContext.create();
        callContext.putIntoMDC();
        callContext.setSessionId(sessionId);

        // 尝试从session属性中获取userId
        String userId = (String) session.getAttributes().get("userId");
        if (userId != null) {
            callContext.setUserId(userId);
        }

        log.info("ASR连接已建立 | ID: {} | 协议: {} | 远程地址: {} | 扩展: {}",
                sessionId,
                session.getAcceptedProtocol(),
                session.getRemoteAddress(),
                session.getExtensions());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String sessionId = session.getId();
        asrWebSocketSessionManager.removeSession(sessionId);

        // 创建调用上下文用于日志记录
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ?
                RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("ASR_CLOSE");

        RequestContext.runWithRequestId(requestId, () -> {
            // 替代方案1：使用系统当前时间计算（如果无法获取建立时间）
            boolean connectionStartTime = session.getAttributes().containsKey("connectionStartTime");
            if (connectionStartTime) {
                long durationSeconds = (System.currentTimeMillis() - (long) session.getAttributes().get("connectionStartTime")) / 1000;
                log.info("ASR连接已关闭 | ID: {} | 关闭码: {} | 关闭原因: {} | 会话时长: {}秒",
                        sessionId,
                        status.getCode(),
                        status.getReason(),
                        durationSeconds);
            } else {
                log.info("ASR连接已关闭 | ID: {} | 关闭码: {} | 关闭原因: {}",
                        sessionId,
                        status.getCode(),
                        status.getReason());
            }
        });

        // 清理MDC
        CallContext.clearMDC();
        
        // 从session中提取callId
        String callId = extractCallId(session);
        if (callId != null) {
            // 获取并关闭MediaProcessor
            MediaProcessor mediaProcessor = mediaProcessorManager.get(callId);
            if (mediaProcessor != null) {
                log.info("Starting graceful shutdown for MediaProcessor: {}", callId);

                // 标记连接已关闭并关闭MediaProcessor
                mediaProcessor.markConnectionClosed();
                mediaProcessor.shutdown();
                
                // 从管理器中移除
                mediaProcessorManager.remove(callId);
            }
        }
    }
    
    private String extractCallId(WebSocketSession session) {
        // 尝试从session属性中获取callId
        String callId = (String) session.getAttributes().get("callId");
        if (callId != null) {
            return callId;
        }
        
        // 如果没有在属性中找到，尝试从session ID中提取（如果适用）
        String sessionId = session.getId();
        return sessionId; // 根据实际实现可能需要更复杂的逻辑
    }

    @Override
    @Transactional(readOnly = true)
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {

//        try {
//            String payload = message.getPayload().toString();
//            AsrChatMessage chatServerMessage = objectMapper.readValue(payload, AsrChatMessage.class);
//            String callId = chatServerMessage.getSessionId();
//            GenericConnection connection = unifiedSessionManager.getConnectionBySessionId(callId);
//            TtsOutbound ttsStart = new TtsOutbound();
//            ttsStart.setSessionId(callId);
//            ttsStart.setType("tts");
//            ttsStart.setState("start");
//            connection.sendText(ttsStart);
//            Thread.sleep(2000L);
//
//            List<String> sentences = Lists.newArrayList(
//                    "如是我闻，一时，佛在舍卫国祗树给孤独园，与大比丘众千二百五十人俱。",
//                    "尔时，世尊食时，著衣持钵，入舍卫大城乞食。",
//                    "于其城中，次第乞已，还至本处。",
//                    "饭食讫，收衣钵，洗足已，敷座而坐。"
//            );
//
//
//            MediaProcessor processor = new MediaProcessor(
//                    callId, objectMapper, connection, opusPool
//            );
//
//            String path = "/home/lee/Downloads/volcengine_bidirection_demo";
//            for (int i = 0; i < sentences.size(); i++) {
//                String sentence = sentences.get(i);
//                TtsOutbound sentenceStart = new TtsOutbound();
//                sentenceStart.setSessionId(callId);
//                sentenceStart.setType("tts");
//                sentenceStart.setState("sentence_start");
//                sentenceStart.setText(sentence);
//                connection.sendText(sentenceStart);
//
//                File file = new File(path, String.format("%d.pcm", i));
//                byte[] audioBuffer = Files.readAllBytes(file.toPath());
//                processor.sendEncodedAudio(audioBuffer);
//
//
//                TtsOutbound sentenceEnd = new TtsOutbound();
//                sentenceEnd.setSessionId(callId);
//                sentenceEnd.setType("tts");
//                sentenceEnd.setState("sentence_end");
//                sentenceEnd.setText(sentence);
//                connection.sendText(sentenceEnd);
//            }
//
//            TtsOutbound ttsEnd = new TtsOutbound();
//            ttsEnd.setSessionId(callId);
//            ttsEnd.setType("tts");
//            ttsEnd.setState("stop");
//            connection.sendText(ttsEnd);
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }

        String sessionId = session.getId();
        String payload = message.getPayload().toString();

        // 创建调用上下文
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ?
                RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("HANDLE_ASR");

        RequestContext.runWithRequestId(requestId, () -> {
            log.debug("收到ASR消息 | ID: {} | 消息长度: {} | 消息摘要: {}...",
                    sessionId,
                    payload.length(),
                    payload.substring(0, Math.min(50, payload.length())));
        });

        try {
            AsrChatMessage chatServerMessage = objectMapper.readValue(payload, AsrChatMessage.class);
            String callId = chatServerMessage.getSessionId();

            // 更新MDC中的callId
            if (callId != null) {
                MDC.put("callId", callId);
            }

            // 更新MDC中的sessionId
            MDC.put("sessionId", sessionId);

            // 尝试从连接中获取userId
            GenericConnection connection = unifiedSessionManager.getConnectionBySessionId(callId);
            String userId = null;

            if (connection != null && connection.getDeviceInfo() != null && connection.getDeviceInfo().getCreateBy() != null) {
                Long createBy = connection.getDeviceInfo().getCreateBy();
                userId = createBy.toString();
                MDC.put("userId", userId);
            }

            RequestContext.runWithRequestId(requestId, () -> {
                log.info("开始处理ASR请求 | 会话ID: {} | 消息类型: {}  |  | 消息: {}",
                        callId,
                        chatServerMessage.getType(),
                        chatServerMessage.getContent());
            });

            if (connection == null) {
                String errorRequestId = callContext.generateRequestId("CONN_NOT_FOUND");
                RequestContext.runWithRequestId(errorRequestId, () -> {
                    log.warn("关联会话不存在 | 当前会话ID: {} | 目标会话ID: {}", sessionId, callId);
                });
                return;
            }

            List<List<Float>> embeddings = chatServerMessage.getEmbedding();
            String embedRequestId = callContext.generateRequestId("EMBED_INFO");
            RequestContext.runWithRequestId(embedRequestId, () -> {
                log.debug("语音特征维度 | 会话ID: {} | 向量数量: {} | 向量维度: {}",
                        callId,
                        embeddings.size(),
                        !embeddings.isEmpty() ? embeddings.get(0).size() : 0);
            });

            long start = System.currentTimeMillis();

            if (connection.getDeviceInfo() != null) {
                DeviceInfo deviceInfo = connection.getDeviceInfo();

                // 获取设备绑定的agentId
                String agentId = deviceInfo.getAgentId();
                if (agentId != null) {
                    Agent agent = agentService.getById(agentId);
                    if (agent != null) {
                        // 获取用户消息
                        String userMessage = chatServerMessage.getContent();

                        // 保存用户对话记录
                        List<Float> voiceEmbedding = chatServerMessage.getEmbedding() != null &&
                                            !chatServerMessage.getEmbedding().isEmpty() ?
                                            chatServerMessage.getEmbedding().get(0) : null;

                        saveChatDetail(callId, userId, deviceInfo.getSerialNumber(), agentId,
                                       userMessage, voiceEmbedding,
                                       com.github.lisuiheng.astra.server.server.constant.SpeakerType.USER, "用户");

                        // 发送STT消息 - 语音转文本结果
                        SttOutbound sttMessage = new SttOutbound();
                        sttMessage.setSessionId(callId);
                        sttMessage.setText(userMessage);

                        // 使用MediaProcessor的GenericConnection发送消息，而不是直接通过WebSocket session
                        try {
                            connection.sendText(sttMessage);
                        } catch (Exception e) {
                            log.error("Failed to send STT message via GenericConnection", e);
                            // 如果通过GenericConnection发送失败，回退到原来的WebSocket方式
                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(sttMessage)));
                        }

                        // 获取对应的MediaProcessor
//                        MediaProcessor mediaProcessor = mediaProcessorManager.get(callId);
//                        if (mediaProcessor == null) {
//                            log.error("找不到对应的MediaProcessor | callId: {}", callId);
//                            return;
//                        }

                        // 媒体处理器设置
                        MediaProcessor mediaProcessor = mediaProcessorManager.getOrCreate(connection);
                        // 发送TTS开始消息
                        TtsOutbound ttsStartMsg = new TtsOutbound();
                        ttsStartMsg.setSessionId(callId);
                        ttsStartMsg.setType("tts");
                        ttsStartMsg.setState("start");

                        mediaProcessor.onTTSReceived(ttsStartMsg);

                        // 创建TTS流式会话并连接到MediaProcessor
                        OkHttpTtsClient.StreamingSession ttsSession = ttsService.createStreamingSessionWithMediaProcessor(ttsService.createDefaultConfig(), mediaProcessor);


//                        // 使用MediaProcessor的GenericConnection发送消息，而不是直接通过WebSocket session
//                        try {
//                            connection.sendText(ttsStartMsg);
//                        } catch (Exception e) {
//                            log.error("Failed to send TTS start message via GenericConnection", e);
//                            // 如果通过GenericConnection发送失败，回退到原来的WebSocket方式
//                            session.sendMessage(new TextMessage(objectMapper.writeValueAsString(ttsStartMsg)));
//                        }

                        // 创建会话处理器
                        // 检查是否已存在处理器

                        SessionProcessor sessionProcessor = new SessionProcessor(callId, connection, ttsSession, objectMapper);

                        // 注册TTS事件监听器到MediaProcessor
                        mediaProcessor.setTtsEventListener(sessionProcessor.getTtsEventListener());

                        // 创建累积响应字符串
                        AtomicReference<String> accumulatedResponse = new AtomicReference<>("");

                        // 定义句子分隔符的正则表达式
                        Pattern sentencePattern = Pattern.compile("[。！？.!?]+");

                        Flux<StreamChunk> aiResponse = enhancedAgentService.chatStream(callId, agentId, userId, userMessage);

                        // 订阅大模型流式响应
                        String finalUserId = userId;
                        aiResponse.subscribe(
                                chunk -> {
                                    if ("chunk".equals(chunk.getType()) && chunk.getContent() != null) {
                                        String content = chunk.getContent();
                                        String currentAccumulated = accumulatedResponse.get();
                                        String newAccumulated = currentAccumulated + content;
                                        accumulatedResponse.set(newAccumulated);

                                        log.debug("Received chunk: {}", content);

                                        // 提取完整句子
                                        extractAndProcessSentences(newAccumulated, accumulatedResponse, sessionProcessor);
                                    }
                                },
                                error -> {
                                    log.error("Error in AI stream processing", error);

                                    // 发送错误消息
                                    TtsOutbound errorMsg = new TtsOutbound();
                                    errorMsg.setSessionId(callId);
                                    errorMsg.setType("tts");
                                    errorMsg.setState("error");
                                    errorMsg.setText("AI处理出错: " + error.getMessage());
                                    try {
                                        connection.sendText(errorMsg);
                                    } catch (Exception e) {
                                        log.error("Failed to send error message", e);
                                    }


                                    sessionProcessor.markTtsCompleted();
                                },
                                () -> {
                                    log.info("AI stream completed");

                                    // 处理剩余的文本
                                    String remainingText = accumulatedResponse.get().trim();
                                    if (!remainingText.isEmpty()) {
                                        log.info("Processing remaining text: {}", remainingText);

                                        sessionProcessor.addSentence(remainingText);

                                        // 保存AI的完整响应
                                        saveChatDetail(callId, finalUserId, deviceInfo.getSerialNumber(), agentId,
                                                      remainingText, null,
                                                      com.github.lisuiheng.astra.server.server.constant.SpeakerType.AGENT,
                                                      agent != null ? agent.getAgentName() : "智能体");
                                    }


                                    sessionProcessor.markTtsCompleted();


                                }
                        );

                    } else {
                        log.warn("未找到绑定的智能体 | AgentId: {}", agentId);
                    }
                } else {
                    log.warn("设备未绑定智能体 | DeviceId: {}", deviceInfo.getSerialNumber());
                }
            }

            long cost = System.currentTimeMillis() - start;

            String completeRequestId = callContext.generateRequestId("ASR_COMPLETE");
            RequestContext.runWithRequestId(completeRequestId, () -> {
                log.info("ASR处理完成 | 会话ID: {} | 耗时: {}ms | 结果: {}",
                        callId,
                        cost,
                        chatServerMessage.getContent());
            });
        } catch (Exception e) {
            String errorRequestId = callContext.generateRequestId("MSG_ERROR");
            RequestContext.runWithRequestId(errorRequestId, () -> {
                log.error("ASR消息处理失败 | ID: {} | 错误类型: {} | 错误详情: {}",
                        sessionId,
                        e.getClass().getSimpleName(),
                        e.getMessage(),
                        e);
            });
            throw e;
        }
    }

    // 提取句子并添加到处理器（考虑TTS拆分特性）
    private void extractAndAddSentences(String text, AtomicReference<String> accumulatedResponse,
                                       SessionProcessor sessionProcessor) {
        if (text == null || text.trim().isEmpty()) {
            return;
        }
        
        // 智能句子分割：识别自然句子边界
        List<SentenceMatch> sentenceMatches = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            current.append(c);
            
            // 检查句子结束条件
            if (isNaturalSentenceEnd(text, i)) {
                String sentence = current.toString().trim();
                if (!sentence.isEmpty()) {
                    sentenceMatches.add(new SentenceMatch(sentence, i - current.length() + 1, i + 1));
                    current.setLength(0);
                }
            }
        }
        
        // 处理剩余文本
        String remaining = current.toString().trim();
        
        if (!sentenceMatches.isEmpty()) {
            // 按顺序添加完整句子
            for (SentenceMatch match : sentenceMatches) {
                sessionProcessor.addSentence(match.sentence);
                log.debug("Extracted sentence: {}", match.sentence);
            }
            
            // 更新累积响应
            accumulatedResponse.set(remaining);
        } else {
            // 没有完整句子，保留所有文本
            accumulatedResponse.set(text);
        }
    }
    
    /**
     * 判断是否为自然句子结束
     */
    private boolean isNaturalSentenceEnd(String text, int index) {
        char c = text.charAt(index);
        
        // 基本结束标点
        if ("。！？.!?".indexOf(c) != -1) {
            // 检查是否是连续的结束标点（如"！？"）
            if (index + 1 < text.length() && "。！？.!?".indexOf(text.charAt(index + 1)) != -1) {
                return false; // 不是真正的结束，还有连续标点
            }
            return true;
        }
        
        // 分号可能不是句子结束，除非后面有换行或空格
        if (c == ';' || c == '；') {
            if (index + 1 < text.length()) {
                char next = text.charAt(index + 1);
                return Character.isWhitespace(next) || next == '\n';
            }
            return true;
        }
        
        // 逗号通常不是句子结束，除非是长句中的自然停顿
        if (c == ',' || c == '，') {
            // 简单的启发式：如果逗号后面有较长的空格或换行，可能是一个句子边界
            if (index + 1 < text.length()) {
                char next = text.charAt(index + 1);
                return Character.isWhitespace(next) && index > 20; // 较长的句子
            }
        }
        
        return false;
    }
    
    /**
     * 句子匹配结果
     */
    private static class SentenceMatch {
        final String sentence;
        final int start;
        final int end;
        
        SentenceMatch(String sentence, int start, int end) {
            this.sentence = sentence;
            this.start = start;
            this.end = end;
        }
    }
    
    // 提取并处理完整句子
    private void extractAndProcessSentences(String text, AtomicReference<String> accumulatedResponse,
                                            SessionProcessor sessionProcessor) {
        extractAndAddSentences(text, accumulatedResponse, sessionProcessor);
    }
    
    // 保存聊天记录的辅助方法
    private void saveChatDetail(String callId, String userId, String deviceId, 
                           String agentId, String content, 
                           List<Float> voiceEmbedding, 
                           com.github.lisuiheng.astra.server.server.constant.SpeakerType speakerType, String speakerName) {
        try {
            ChatDetailDTO chatDetailDTO = new ChatDetailDTO();
            chatDetailDTO.setCallId(callId);
            chatDetailDTO.setUserId(userId);
            chatDetailDTO.setDeviceId(deviceId);
            chatDetailDTO.setAgentId(agentId);
            chatDetailDTO.setContent(content);
            chatDetailDTO.setVoiceRemark(voiceEmbedding);
            chatDetailDTO.setQuestionKind(speakerType);
            chatDetailDTO.setQuestionName(speakerName);
            chatDetailDTO.setChatKind("voice");
            chatDetailDTO.setChatTime(java.time.LocalDateTime.now());
            
            // 异步保存，避免阻塞主流程

            chatDetailService.saveChatDetailAsync(chatDetailDTO);
            
        } catch (Exception e) {
            log.error("Error preparing chat detail for saving", e);
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        String sessionId = session.getId();

        // 创建调用上下文用于错误处理
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ?
                RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("TRANSPORT_ERROR");

        RequestContext.runWithRequestId(requestId, () -> {
            log.error("ASR连接传输错误 | ID: {} | 错误类型: {} | 错误信息: {}",
                    sessionId,
                    exception.getClass().getSimpleName(),
                    exception.getMessage(),
                    exception);
        });
    }



}