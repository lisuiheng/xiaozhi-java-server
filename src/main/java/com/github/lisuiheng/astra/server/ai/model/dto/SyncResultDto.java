package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class SyncResultDto {
    private String kbId;
    private boolean success;
    private String errorMessage;
    private Date startTime;
    private Date endTime;
    private Integer totalDocuments;
    private Integer successCount;
    private Integer failCount;
}