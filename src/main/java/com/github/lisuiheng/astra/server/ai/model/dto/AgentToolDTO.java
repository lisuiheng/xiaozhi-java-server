package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 工具DTO
 */
@Data
public class AgentToolDTO {
    private String toolId;
    private String toolName;
    private String toolType;
    private String description;
    private String config;
    private Boolean enabled;
}