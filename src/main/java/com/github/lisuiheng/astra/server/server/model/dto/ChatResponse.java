package com.github.lisuiheng.astra.server.server.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatResponse {
    private String message;
    private String sessionId;
    private String responseId;
    private LocalDateTime timestamp;
    private List<MemoryReference> memoryReferences;
    private AudioResponse audio;
    private UsageInfo usage;
    
    @Data
    public static class AudioResponse {
        private String audioUrl;
        private String format;
        private Integer duration;
    }
    
    @Data
    public static class MemoryReference {
        private String memoryId;
        private String content;
        private Double similarity;
    }
    
    @Data
    public static class UsageInfo {
        private Integer promptTokens;
        private Integer completionTokens;
        private Integer totalTokens;
    }
}