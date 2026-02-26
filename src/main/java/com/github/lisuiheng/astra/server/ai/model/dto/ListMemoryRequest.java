package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 分页列表请求
 */
@Data
public class ListMemoryRequest {
    private String userId;
    private Integer page = 1;
    private Integer pageSize = 20;
    private String sortBy = "timestamp";
    private String sortOrder = "desc";
}