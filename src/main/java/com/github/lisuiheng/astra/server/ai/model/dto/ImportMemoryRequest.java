package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 导入记忆请求
 */
@Data
public class ImportMemoryRequest {
    private String userId;
    private String importType; // single, batch
    private List<List<Map<String, String>>> memories;
}