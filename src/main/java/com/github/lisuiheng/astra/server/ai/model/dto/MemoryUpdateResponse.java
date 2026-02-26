package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

@Data
public class MemoryUpdateResponse {
    private boolean success;
    private String message;
    private String memoryId;
    private String userId;
    private String oldContent;
    private String newContent;
    private String updatedAt;
}