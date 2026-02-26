package com.github.lisuiheng.astra.server.ai.service;

import com.github.lisuiheng.astra.server.ai.model.entity.AiSession;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class SessionManagementServiceTest {
    
    @Autowired
    private SessionManagementService sessionManagementService;
    
    @Test
    public void testCreateAndValidateSession() {
        // 创建会话
        AiSession session = sessionManagementService.createSession("test-user-123", "gpt-4", "zh");
        assertNotNull(session);
        assertNotNull(session.getSessionId());
        assertEquals("test-user-123", session.getUserId());
        assertEquals("gpt-4", session.getModel());
        assertEquals("zh", session.getLanguage());
        
        // 验证会话
        boolean isValid = sessionManagementService.isValidSession(session.getSessionId());
        assertTrue(isValid);
        
        // 更新活动时间
        sessionManagementService.updateLastActiveTime(session.getSessionId());
        
        // 增加轮次和token
        sessionManagementService.incrementTurnAndTokens(session.getSessionId(), 100);
        
        // 结束会话
        sessionManagementService.endSession(session.getSessionId());
        
        // 验证会话已结束
        boolean isStillValid = sessionManagementService.isValidSession(session.getSessionId());
        assertFalse(isStillValid);
    }
}