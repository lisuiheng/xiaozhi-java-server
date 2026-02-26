package com.github.lisuiheng.astra.server.asr.model.dto.inbound;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/**
 * MCP入站消息
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class McpInbound extends InboundMessage {
    private String type = "mcp";
    private Object payload;

    public McpInbound() {
        super();
    }
    
    @Override
    public String getType() {
        return type;
    }
}