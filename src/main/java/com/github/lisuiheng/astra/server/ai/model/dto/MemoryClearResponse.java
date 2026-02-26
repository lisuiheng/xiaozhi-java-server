package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 清除记忆响应
 */
@Data
public class MemoryClearResponse {
    private Integer deletedCount;
    private String userId;
    private Boolean success;
    private String message;
}