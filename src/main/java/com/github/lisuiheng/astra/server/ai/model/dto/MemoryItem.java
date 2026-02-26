package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * 记忆项
 */
@Data
public class MemoryItem {
    private String id;          // 新增：记忆ID
    private String userId;      // 新增：用户ID
    private String content;
    private Double score;
    private Map<String, Object> metadata;
    private String timestamp;   // 新增：时间戳
    private String createdAt;
}