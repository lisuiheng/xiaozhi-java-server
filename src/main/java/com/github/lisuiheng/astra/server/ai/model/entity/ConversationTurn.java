package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.Map;

@Data
@NoArgsConstructor
@TableName("conversation_turns")
public class ConversationTurn {
    @TableId(type = IdType.AUTO)
    private Long id;
    
    private String callId;        // 1kzv8s
    private String requestId;     // 1kzv8s-001
    private String sessionId;     // sess_xxx
    
    private String userMessage;
    private String aiResponse;
    
    // 上下文引用
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> memoryContext;     // 使用的记忆片段
    
    @TableField(typeHandler = com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler.class)
    private Map<String, Object> kbContext;         // 使用的知识片段
    
    private String promptVersion; // 提示词版本
    
    // 性能指标
    private Integer tokensUsed;
    private Integer latencyMs;
    private String modelUsed;
    
    // 审计字段
    private Instant createdAt;
    private String errorCode;
}