package com.github.lisuiheng.astra.server.asr;

import com.github.lisuiheng.astra.common.util.CallContext;
import com.github.lisuiheng.astra.common.util.RequestContext;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;

import static org.junit.jupiter.api.Assertions.*;

public class SessionContextTest {
    
    @Test
    public void testCallContextCreation() {
        // 创建调用上下文
        CallContext callContext = CallContext.create();
        assertNotNull(callContext.getCallId());
        assertTrue(callContext.getCallId().length() > 0);
    }
    
    @Test
    public void testRequestIdGeneration() {
        CallContext callContext = CallContext.create();
        String requestId1 = callContext.generateRequestId("TEST");
        String requestId2 = callContext.generateRequestId("TEST");
        
        assertNotNull(requestId1);
        assertNotNull(requestId2);
        assertNotEquals(requestId1, requestId2);
        assertTrue(requestId1.contains("TEST"));
        assertTrue(requestId2.contains("TEST"));
    }
    
    @Test
    public void testMDCIntegration() {
        // 确保MDC初始为空
        assertNull(MDC.get("userId"));
        assertNull(MDC.get("sessionId"));
        assertNull(MDC.get("callId"));
        assertNull(MDC.get("requestId"));
        
        // 创建调用上下文并设置到MDC
        CallContext callContext = CallContext.create();
        callContext.putIntoMDC();
        callContext.setUserId("test-user-123");
        callContext.setSessionId("test-session-456");
        
        // 验证MDC中的值
        assertNull(MDC.get("userId")); // userId需要手动设置
        assertNull(MDC.get("sessionId")); // sessionId需要手动设置
        assertNotNull(MDC.get("callId"));
        assertEquals(callContext.getCallId(), MDC.get("callId"));
        
        // 手动设置userId和sessionId到MDC
        MDC.put("userId", "test-user-123");
        MDC.put("sessionId", "test-session-456");
        
        assertEquals("test-user-123", MDC.get("userId"));
        assertEquals("test-session-456", MDC.get("sessionId"));
        
        // 清理MDC
        CallContext.clearMDC();
        assertNull(MDC.get("userId"));
        assertNull(MDC.get("sessionId"));
        assertNull(MDC.get("callId"));
        assertNull(MDC.get("requestId"));
    }
    
    @Test
    public void testRequestContextExecution() {
        CallContext callContext = CallContext.create();
        callContext.putIntoMDC();
        
        String testRequestId = callContext.generateRequestId("UNIT_TEST");
        
        String result = RequestContext.executeWithRequestId(testRequestId, () -> {
            assertEquals(testRequestId, MDC.get("requestId"));
            return "success";
        });
        
        assertEquals("success", result);
        // 执行完毕后requestId应该被清理
        assertNull(MDC.get("requestId"));
    }
}