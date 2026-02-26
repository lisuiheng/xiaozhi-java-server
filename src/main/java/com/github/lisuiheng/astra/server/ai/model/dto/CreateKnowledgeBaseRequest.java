package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class CreateKnowledgeBaseRequest {
    private String kbName;
    private String description;
    private String embeddingModel = "text-embedding-v4";
    private Boolean isPublic = false;
}