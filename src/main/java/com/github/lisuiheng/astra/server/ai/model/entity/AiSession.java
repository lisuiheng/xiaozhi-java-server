package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@TableName("ai_sessions")
public class AiSession {
    @TableId(type = IdType.NONE)
    private String sessionId;      // sess_xxx
    
    private String userId;         // usr_xxx
    
    @TableField("status")
    private SessionStatus status;  // ACTIVE, ENDED, TIMEOUT
    
    private String model;          // gpt-4, claude-3, etc
    private String language;       // zh, en, etc
    
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> metadata;  // 客户端信息、设备等
    
    private Instant createdAt;
    private Instant lastActiveAt;
    private Instant endedAt;
    
    private Integer totalTurns;    // 对话轮次
    private Long totalTokens;      // 累计token
}