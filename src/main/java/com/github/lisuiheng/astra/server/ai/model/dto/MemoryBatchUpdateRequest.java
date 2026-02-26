package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class MemoryBatchUpdateRequest {
    private String userId;
    private List<MemoryUpdateRequest> updates;
}