package com.github.lisuiheng.astra.common.util;

import org.slf4j.MDC;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 调用上下文管理器，负责管理callId和requestId
 * callId: 用户级别的调用标识，代表一次完整的用户交互
 * requestId: 系统内部的请求标识，代表单次服务调用
 */
public class CallContext {
    private final String callId;
    private final AtomicInteger requestSeq = new AtomicInteger(1);
    
    private CallContext(String callId) {
        this.callId = callId;
    }
    
    /**
     * 创建新的调用上下文
     */
    public static CallContext create() {
        return new CallContext(generateCallId());
    }
    
    /**
     * 从现有callId创建调用上下文
     */
    public static CallContext fromCallId(String callId) {
        return new CallContext(callId);
    }
    
    /**
     * 生成callId: 时间戳转base36
     */
    private static String generateCallId() {
        long timestamp = System.currentTimeMillis() / 1000;
        return Long.toString(timestamp, 36);
    }
    
    /**
     * 为每个内部服务调用生成唯一的requestId
     */
    public String generateRequestId(String serviceName) {
        int seq = requestSeq.getAndIncrement();
        // 格式: callId-序号-服务标识(可选)
        return String.format("%s-%03d-%s", callId, seq, serviceName);
    }
    
    /**
     * 获取当前callId
     */
    public String getCallId() {
        return callId;
    }
    
    /**
     * 将当前上下文放入MDC
     */
    public void putIntoMDC() {
        MDC.put("callId", callId);
    }
    
    /**
     * 从MDC中清除上下文
     */
    public static void clearMDC() {
        MDC.remove("callId");
        MDC.remove("requestId");
        MDC.remove("sessionId");
        MDC.remove("userId");
    }
    
    /**
     * 设置userId到MDC
     */
    public void setUserId(String userId) {
        if (userId != null) {
            MDC.put("userId", userId);
        }
    }
    
    /**
     * 设置sessionId到MDC
     */
    public void setSessionId(String sessionId) {
        if (sessionId != null) {
            MDC.put("sessionId", sessionId);
        }
    }
}