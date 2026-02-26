package com.github.lisuiheng.astra.server.ai.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.ai.mapper.ConversationTurnMapper;
import com.github.lisuiheng.astra.server.ai.model.entity.ConversationTurn;
import com.github.lisuiheng.astra.common.util.CallContext;
import com.github.lisuiheng.astra.common.util.RequestContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Slf4j
@Service
public class ConversationTurnService extends ServiceImpl<ConversationTurnMapper, ConversationTurn> {
    
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
            ConversationTurn turn = new ConversationTurn();
            turn.setCallId(callId);
            turn.setRequestId(requestId);
            turn.setSessionId(sessionId);
            turn.setUserMessage(userMessage);
            turn.setAiResponse(aiResponse);
            turn.setModelUsed(modelUsed);
            turn.setTokensUsed(tokensUsed);
            turn.setLatencyMs(latencyMs);
            turn.setCreatedAt(Instant.now());
            
            this.save(turn);
            log.info("记录对话轮次 | SessionId: {} | Tokens: {} | Latency: {}ms", 
                    sessionId, tokensUsed, latencyMs);
        });
    }
}