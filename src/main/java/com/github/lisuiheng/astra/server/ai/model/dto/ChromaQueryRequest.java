package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class ChromaQueryRequest {
    private String queryText;
    private int topK;
    private Map<String, Object> where = new HashMap<>();

}