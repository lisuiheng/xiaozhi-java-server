package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class McpInboundPayload {
    @JsonProperty("jsonrpc")
    private String jsonRpc;

    private String id;

    private McpInboundResult result;
}
