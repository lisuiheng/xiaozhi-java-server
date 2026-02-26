package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 分析结果
 */
@Data
public class AnalysisResult {
    private String type;
    private List<Map<String, Object>> data;
    private String summary;
    private List<String> insights;
}