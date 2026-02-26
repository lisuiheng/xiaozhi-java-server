package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class MemoryBatchUpdateResponse {
    private int total;
    private int successCount;
    private int failureCount;
    private List<Map<String, Object>> results;
}