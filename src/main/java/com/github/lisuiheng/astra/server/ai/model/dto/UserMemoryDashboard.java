package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户记忆仪表板
 */
@Data
public class UserMemoryDashboard {
    private String userId;
    private UserMemoryStats stats;
    private List<MemoryItem> recentMemories;
    private Map<String, Object> memoryGraph;
}