package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

@Data
public class SearchAndUpdateRequest {
    private String userId;
    private String oldContent;
    private String newContent;
}