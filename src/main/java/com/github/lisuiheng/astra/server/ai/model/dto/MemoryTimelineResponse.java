package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 时间线响应
 */
@Data
public class MemoryTimelineResponse {
    private String userId;
    private Integer totalPeriods;
    private Integer totalMemories;
    private String groupBy;
    private List<Map<String, Object>> timeline;
}