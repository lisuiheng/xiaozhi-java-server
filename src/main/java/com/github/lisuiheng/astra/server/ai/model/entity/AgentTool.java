package com.github.lisuiheng.astra.server.ai.model.entity;

import lombok.Data;

/**
 * 工具DTO
 */
@Data
public class AgentTool {
    private String toolId;
    private String toolName;
    private String toolType;
    private String description;
    private String config;
    private Boolean enabled;
}