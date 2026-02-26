package com.github.lisuiheng.astra.common.util;

import org.slf4j.MDC;
import java.util.function.Supplier;

/**
 * 请求上下文工具类，用于在系统内部传递requestId
 */
public class RequestContext {
    
    /**
     * 在指定的requestId上下文中执行操作
     */
    public static <T> T executeWithRequestId(String requestId, Supplier<T> supplier) {
        String oldRequestId = MDC.get("requestId");
        try {
            MDC.put("requestId", requestId);
            return supplier.get();
        } finally {
            if (oldRequestId != null) {
                MDC.put("requestId", oldRequestId);
            } else {
                MDC.remove("requestId");
            }
        }
    }
    
    /**
     * 在指定的requestId上下文中执行操作（无返回值）
     */
    public static void runWithRequestId(String requestId, Runnable runnable) {
        String oldRequestId = MDC.get("requestId");
        try {
            MDC.put("requestId", requestId);
            runnable.run();
        } finally {
            if (oldRequestId != null) {
                MDC.put("requestId", oldRequestId);
            } else {
                MDC.remove("requestId");
            }
        }
    }
    
    /**
     * 获取当前的requestId
     */
    public static String getCurrentRequestId() {
        return MDC.get("requestId");
    }
    
    /**
     * 获取当前的callId
     */
    public static String getCurrentCallId() {
        return MDC.get("callId");
    }
    
    /**
     * 获取当前的sessionId
     */
    public static String getCurrentSessionId() {
        return MDC.get("sessionId");
    }
    
    /**
     * 获取当前的userId
     */
    public static String getCurrentUserId() {
        return MDC.get("userId");
    }
}