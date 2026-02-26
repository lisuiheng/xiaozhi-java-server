package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

@Data
public class QueryRequest {
    private String queryText;
    private int topK;
}
