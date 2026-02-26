package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

@Data
public class MemoryRecord {
    private String id;
    private String userId;
    private String sessionId;
    private String content;
    private MemoryType type;
    private Double importance;
    private Map<String, Object> metadata;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessed;
    
    public enum MemoryType {
        CONVERSATION, FACT, PREFERENCE, RELATIONSHIP, EVENT
    }
}