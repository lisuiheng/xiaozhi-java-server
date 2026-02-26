package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 用户记忆统计
 */
@Data
public class UserMemoryStats {
    private Integer totalMemories;
    private String firstMemoryTime;
    private String lastMemoryTime;
    private List<String> topics;
    private List<Map<String, Object>> topKeywords;
    private List<Map<String, Object>> memoryTimeline;
    private Double dailyAverage;
    private Map<String, Integer> recentActivity;
}