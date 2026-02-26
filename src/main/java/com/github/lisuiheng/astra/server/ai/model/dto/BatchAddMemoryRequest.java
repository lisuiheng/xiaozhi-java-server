package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 批量添加记忆请求
 */
@Data
public class BatchAddMemoryRequest {
    private String userId;
    private List<List<Map<String, String>>> memories;
}