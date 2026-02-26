package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 搜索记忆请求
 */
@Data
public class SearchMemoryRequest {
    private String userId;
    private String query;
    private Integer topK = 10;
}