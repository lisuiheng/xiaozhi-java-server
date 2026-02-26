package com.github.lisuiheng.astra.server.ai.service;

import com.github.lisuiheng.astra.common.util.CallContext;
import com.github.lisuiheng.astra.common.util.RequestContext;
import com.github.lisuiheng.astra.server.ai.model.entity.AiSession;
import com.github.lisuiheng.astra.server.ai.model.entity.SessionStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class SessionManagementService {
    
    @Autowired
    private AiSessionService aiSessionService;
    
    @Autowired
    private ConversationTurnService conversationTurnService;
    
    /**
     * 创建新的AI会话
     */
    public AiSession createSession(String userId, String model, String language) {
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ? 
            RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("CREATE_SESS");
        
        return RequestContext.executeWithRequestId(requestId, () -> {
            AiSession session = new AiSession();
            session.setSessionId("sess_" + UUID.randomUUID().toString().replace("-", ""));
            session.setUserId(userId);
            session.setStatus(SessionStatus.ACTIVE);
            session.setModel(model);
            session.setLanguage(language);
            session.setCreatedAt(Instant.now());
            session.setLastActiveAt(Instant.now());
            session.setTotalTurns(0);
            session.setTotalTokens(0L);
            
            aiSessionService.save(session);
            log.info("创建新的AI会话 | SessionId: {} | UserId: {} | Model: {} | Language: {}", 
                    session.getSessionId(), userId, model, language);
            
            return session;
        });
    }
    
    /**
     * 更新会话活动时间
     */
    public void updateLastActiveTime(String sessionId) {
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ? 
            RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("UPDATE_TIME");
        
        RequestContext.runWithRequestId(requestId, () -> {
            aiSessionService.updateLastActiveTime(sessionId);
        });
    }
    
    /**
     * 增加对话轮次和token数
     */
    public void incrementTurnAndTokens(String sessionId, int tokens) {
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ? 
            RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("INC_TURN");
        
        RequestContext.runWithRequestId(requestId, () -> {
            aiSessionService.incrementTurnAndTokens(sessionId, tokens);
        });
    }
    
    /**
     * 结束会话
     */
    public void endSession(String sessionId) {
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ? 
            RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("END_SESS");
        
        RequestContext.runWithRequestId(requestId, () -> {
            aiSessionService.endSession(sessionId);
        });
    }
    
    /**
     * 记录对话轮次
     */
    public void recordTurn(String callId, String requestId, String sessionId, 
                          String userMessage, String aiResponse,
                          String modelUsed, int tokensUsed, int latencyMs) {
        CallContext callContext = CallContext.fromCallId(callId != null ? callId : 
            (RequestContext.getCurrentCallId() != null ? RequestContext.getCurrentCallId() : "unknown"));
        String traceRequestId = callContext.generateRequestId("RECORD_TURN");
        
        RequestContext.runWithRequestId(traceRequestId, () -> {
            conversationTurnService.recordTurn(callId, requestId, sessionId, userMessage, aiResponse, 
                modelUsed, tokensUsed, latencyMs);
        });
    }
    
    /**
     * 验证会话是否有效
     */
    public boolean isValidSession(String sessionId) {
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }
        
        CallContext callContext = CallContext.fromCallId(RequestContext.getCurrentCallId() != null ? 
            RequestContext.getCurrentCallId() : "unknown");
        String requestId = callContext.generateRequestId("VALIDATE");
        
        return RequestContext.executeWithRequestId(requestId, () -> {
            AiSession session = aiSessionService.getById(sessionId);
            if (session == null) {
                log.warn("会话不存在 | SessionId: {}", sessionId);
                return false;
            }
            
            if (session.getStatus() != SessionStatus.ACTIVE) {
                log.warn("会话状态无效 | SessionId: {} | Status: {}", sessionId, session.getStatus());
                return false;
            }
            
            // 检查会话是否超时（假设超时时间为30分钟）
            Instant now = Instant.now();
            Instant lastActive = session.getLastActiveAt();
            long minutesSinceLastActive = java.time.Duration.between(lastActive, now).toMinutes();
            
            if (minutesSinceLastActive > 30) {
                log.warn("会话已超时 | SessionId: {} | LastActive: {} minutes ago", 
                        sessionId, minutesSinceLastActive);
                // 自动结束超时会话
                endSession(sessionId);
                return false;
            }
            
            log.info("会话验证通过 | SessionId: {}", sessionId);
            return true;
        });
    }
}