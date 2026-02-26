package com.github.lisuiheng.astra.server.asr.model.dto.outbound;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LlmOutbound implements OutboundMessage {
    private final String type = "llm";
    private String sessionId;
    
    @JsonProperty("session_id")
    public String getSessionId() {
        return sessionId;
    }
    
    private String emotion;
    private String text;
}