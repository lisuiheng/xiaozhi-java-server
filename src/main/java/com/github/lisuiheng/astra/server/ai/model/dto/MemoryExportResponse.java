package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 导出响应
 */
@Data
public class MemoryExportResponse {
    private String userId;
    private String exportTime;
    private Integer totalMemories;
    private String format;
    private Object content; // 可能是字符串或对象
    private String filename;
}