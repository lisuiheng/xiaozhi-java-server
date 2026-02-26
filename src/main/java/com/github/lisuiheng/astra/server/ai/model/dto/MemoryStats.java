package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 记忆统计
 */
@Data
public class MemoryStats {
    private Integer memoryCount;
    private String lastUpdated;
}