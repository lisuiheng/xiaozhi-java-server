package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 分页列表响应
 */
@Data
public class MemoryListResponse {
    private List<MemoryItem> items;
    private Integer total;
    private Integer page;
}