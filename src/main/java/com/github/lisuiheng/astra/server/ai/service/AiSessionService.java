package com.github.lisuiheng.astra.server.ai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.ai.mapper.AiSessionMapper;
import com.github.lisuiheng.astra.server.ai.model.entity.AiSession;
import com.github.lisuiheng.astra.server.ai.model.entity.SessionStatus;
import com.github.lisuiheng.astra.common.util.CallContext;
import com.github.lisuiheng.astra.common.util.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Slf4j
@Service
public class AiSessionService extends ServiceImpl<AiSessionMapper, AiSession> {
    
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
            
            this.save(session);
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
            this.lambdaUpdate()
                    .eq(AiSession::getSessionId, sessionId)
                    .set(AiSession::getLastActiveAt, Instant.now())
                    .update();
            
            log.info("更新会话活动时间 | SessionId: {}", sessionId);
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
            this.lambdaUpdate()
                    .eq(AiSession::getSessionId, sessionId)
                    .setSql("total_turns = total_turns + 1")
                    .setSql("total_tokens = total_tokens + " + tokens)
                    .update();
            
            log.info("增加对话轮次和token数 | SessionId: {} | Tokens: {}", sessionId, tokens);
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
            this.lambdaUpdate()
                    .eq(AiSession::getSessionId, sessionId)
                    .set(AiSession::getStatus, SessionStatus.ENDED)
                    .set(AiSession::getEndedAt, Instant.now())
                    .update();
            
            log.info("会话已结束 | SessionId: {}", sessionId);
        });
    }
}