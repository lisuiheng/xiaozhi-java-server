package com.github.lisuiheng.astra.server.asr.model.dto.outbound;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.lisuiheng.astra.server.asr.model.dto.McpOutboundPayload;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class McpOutbound implements OutboundMessage {
    private String sessionId;
    private String type = "mcp";

    private McpOutboundPayload payload;
}