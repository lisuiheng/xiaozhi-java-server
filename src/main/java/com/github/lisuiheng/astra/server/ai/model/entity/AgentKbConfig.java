package com.github.lisuiheng.astra.server.ai.model.entity;


import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 智能体知识库配置
 */
@Data
public class AgentKbConfig {
    private String kbId; // 知识库ID
    private String kbName; // 知识库名称
    private Boolean enabled = true; // 是否启用
    private BigDecimal similarityThreshold = BigDecimal.valueOf(0.5);
    private Integer topK = 5;
    private Integer maxTokens = 2000;
    private String promptTemplate; // 自定义提示词模板
    private Map<String, Object> metadata; // 扩展元数据
    private Integer priority = 0; // 优先级
}