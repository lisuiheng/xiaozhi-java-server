package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 记忆关系图响应
 */
@Data
public class MemoryGraphResponse {
    private List<Map<String, Object>> nodes;
    private List<Map<String, Object>> edges;
    private List<Map<String, Object>> clusters;
    private Map<String, Object> metrics;
}