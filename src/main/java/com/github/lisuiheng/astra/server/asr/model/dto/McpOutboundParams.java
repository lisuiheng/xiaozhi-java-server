package com.github.lisuiheng.astra.server.asr.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class McpOutboundParams {
    @JsonProperty("protocolVersion")
    private String protocolVersion;

    private Capabilities capabilities;

    @JsonProperty("clientInfo")
    private ClientInfo clientInfo;
}
