package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;
import java.util.Map;

@Data
@Accessors(chain = true)
public class SearchResultDto {
    private boolean success;
    private String errorMessage;
    private String kbId;
    private String query;
    private int total;
    private List<DocumentResultDto> documents;
}