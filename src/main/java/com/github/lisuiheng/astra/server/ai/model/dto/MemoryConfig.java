package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.Map;

/**
 * Mem0ai记忆配置
 */
@Data
public class MemoryConfig {
    private String apiKey; // Mem0ai API密钥
    private String baseUrl = "https://api.mem0.ai"; // API地址
    private String memoryId; // 记忆ID（可为空，自动创建）
    private String userId; // 用户ID（用于区分不同用户的记忆）

    // 记忆管理参数
    private Double memoryRelevanceThreshold = 0.7; // 记忆相关性阈值
    private Integer maxMemoriesToRetrieve = 5; // 最大检索记忆数
    private Boolean enableMemorySummarization = true; // 启用记忆摘要
    private Boolean enableMemoryForgetting = false; // 启用遗忘机制
    private Integer memoryTtlHours = 720; // 记忆TTL（小时，0表示永久）

    // 高级配置
    private String embeddingModel = "text-embedding-3-small"; // 嵌入模型
    private String llmModel = "gpt-4"; // 摘要生成模型
    private Map<String, Object> customConfig; // 自定义配置
}