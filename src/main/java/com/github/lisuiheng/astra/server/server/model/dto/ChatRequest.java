package com.github.lisuiheng.astra.server.server.model.dto;

import lombok.Data;

import java.util.Map;

@Data
public class ChatRequest {
    private String message;
    private String sessionId;
    private String userId;
    private boolean useMemory = true;
    private Map<String, Object> parameters;
    private AudioConfig audioConfig;
    
    @Data
    public static class AudioConfig {
        private String format;
        private Integer sampleRate;
        private Boolean needTTS;
    }
}