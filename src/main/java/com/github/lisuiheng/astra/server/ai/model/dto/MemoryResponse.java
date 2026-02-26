package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 基础的记忆响应
 */
@Data
public class MemoryResponse {
    private List<Map<String, Object>> results;
}