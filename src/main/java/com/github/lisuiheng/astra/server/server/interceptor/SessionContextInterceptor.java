package com.github.lisuiheng.astra.server.server.interceptor;

import com.github.lisuiheng.astra.common.util.CallContext;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class SessionContextInterceptor implements HandlerInterceptor {
    
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        // 获取或生成 userId
        String userId = request.getHeader("X-User-Id");
        
        // 获取或生成 sessionId
        String sessionId = request.getHeader("X-Session-Id");
        
        // 获取或生成 callId
        String callId = request.getHeader("X-Call-Id");
        if (callId == null || callId.isEmpty()) {
            callId = generateCallId();
        }
        
        // 创建调用上下文并放入MDC
        CallContext callContext = CallContext.fromCallId(callId);
        callContext.putIntoMDC();
        callContext.setUserId(userId);
        callContext.setSessionId(sessionId);
        
        // 设置响应头
        response.setHeader("X-Call-Id", callId);
        
        log.info("[{}][{}] 开始处理请求 | UserId: {} | SessionId: {}", 
                callId, "REQUEST", userId, sessionId);
        
        return true;
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // 清理MDC
        CallContext.clearMDC();
    }
    
    private String generateCallId() {
        // 时间戳转base36
        long timestamp = System.currentTimeMillis() / 1000;
        return Long.toString(timestamp, 36);
    }
}