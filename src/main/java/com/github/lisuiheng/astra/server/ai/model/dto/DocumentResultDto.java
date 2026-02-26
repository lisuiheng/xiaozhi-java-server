package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Map;

@Data
@Accessors(chain = true)
public class DocumentResultDto {
    private String id;
    private String content;
    private Double score;
    private Map<String, Object> metadata;
}