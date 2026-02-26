package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import java.util.Map;

@Data
public class UpdateMemoryRequest {
    private String memoryId;
    private String userId;
    private String newContent;
    private Map<String, Object> metadata;
}