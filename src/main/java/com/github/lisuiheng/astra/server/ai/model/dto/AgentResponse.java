package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AgentResponse {
    private String agentId;
    private String agentName;
    private String content;
    private long timestamp;
    private Boolean memoryEnabled;
}