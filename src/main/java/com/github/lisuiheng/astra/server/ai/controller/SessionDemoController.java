package com.github.lisuiheng.astra.server.ai.controller;

import com.github.lisuiheng.astra.server.ai.model.entity.AiSession;
import com.github.lisuiheng.astra.server.ai.service.SessionManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/demo/session")
@RequiredArgsConstructor
@Slf4j
public class SessionDemoController {
    
    private final SessionManagementService sessionManagementService;
    
    /**
     * 创建新的会话
     */
    @PostMapping("/create")
    public AiSession createSession(@RequestParam String userId, 
                                  @RequestParam String model, 
                                  @RequestParam String language) {
        log.info("收到创建会话请求 | UserId: {} | Model: {} | Language: {}", userId, model, language);
        return sessionManagementService.createSession(userId, model, language);
    }
    
    /**
     * 验证会话是否有效
     */
    @GetMapping("/validate/{sessionId}")
    public boolean validateSession(@PathVariable String sessionId) {
        log.info("收到验证会话请求 | SessionId: {}", sessionId);
        return sessionManagementService.isValidSession(sessionId);
    }
    
    /**
     * 更新会话活动时间
     */
    @PostMapping("/update-active-time/{sessionId}")
    public void updateActiveTime(@PathVariable String sessionId) {
        log.info("收到更新会话活动时间请求 | SessionId: {}", sessionId);
        sessionManagementService.updateLastActiveTime(sessionId);
    }
    
    /**
     * 增加对话轮次和token数
     */
    @PostMapping("/increment-turn/{sessionId}")
    public void incrementTurn(@PathVariable String sessionId, @RequestParam int tokens) {
        log.info("收到增加对话轮次请求 | SessionId: {} | Tokens: {}", sessionId, tokens);
        sessionManagementService.incrementTurnAndTokens(sessionId, tokens);
    }
    
    /**
     * 结束会话
     */
    @PostMapping("/end/{sessionId}")
    public void endSession(@PathVariable String sessionId) {
        log.info("收到结束会话请求 | SessionId: {}", sessionId);
        sessionManagementService.endSession(sessionId);
    }
}