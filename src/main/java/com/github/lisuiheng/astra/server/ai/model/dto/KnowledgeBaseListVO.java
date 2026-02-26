package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;

@Data
@Accessors(chain = true)
public class KnowledgeBaseListVO {
    private String id;
    private String name; // 对应kbName
    private String description;
    private Integer status;
    private Long totalDocuments;
    private Long totalChunks;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}