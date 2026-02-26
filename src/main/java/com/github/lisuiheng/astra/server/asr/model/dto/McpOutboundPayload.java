package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class McpOutboundPayload {
    @JsonProperty("jsonrpc")
    private String jsonRpc;

    private String method;

    private String id;

    private McpOutboundParams mcpOutboundParams;
}
