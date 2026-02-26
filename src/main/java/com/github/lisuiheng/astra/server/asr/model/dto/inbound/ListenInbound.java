package com.github.lisuiheng.astra.server.asr.model.dto.inbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ListenInbound extends InboundMessage {
    private final String type = "listen";
    @JsonProperty("session_id")
    private String sessionId;
    private String state;
    private String mode;
}