package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class SearchRequestDto {
    private String kbId;
    private String query;
    private Integer topK = 5;
    private Double similarityThreshold = 0.5;
}