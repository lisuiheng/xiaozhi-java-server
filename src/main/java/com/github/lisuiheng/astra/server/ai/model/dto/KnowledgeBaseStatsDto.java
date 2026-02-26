package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Accessors(chain = true)
public class KnowledgeBaseStatsDto {
    private String kbId;
    private String kbName;
    private Integer totalDocuments;
    private Long totalChunks;
    private Long normalDocuments;
    private Long embeddedDocuments;
    private Long failedDocuments;
    private Integer status;
    private Boolean isPublic;
    private LocalDateTime createdTime;
}