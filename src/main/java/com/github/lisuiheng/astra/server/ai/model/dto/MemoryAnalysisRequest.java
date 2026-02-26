package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 记忆分析请求
 */
@Data
public class MemoryAnalysisRequest {
    private String userId;
    private String analysisType; // keywords, topics, clusters, summary
}