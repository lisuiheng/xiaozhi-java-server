package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("server_agent_knowledge_base")
public class AgentKnowledgeBase extends BaseEntity {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    @TableField("agent_id")
    private String agentId; // 智能体ID

    @TableField("kb_id")
    private String kbId; // 知识库ID

    @TableField("enabled")
    private Boolean enabled = true; // 是否启用

    @TableField("priority")
    private Integer priority = 0; // 优先级

    @TableField("similarity_threshold")
    private BigDecimal similarityThreshold = BigDecimal.valueOf(0.5); // 相似度阈值

    @TableField("top_k")
    private Integer topK = 5; // 检索数量

    @TableField("max_tokens")
    private Integer maxTokens = 2000; // 上下文最大token数

    @TableField("prompt_template")
    private String promptTemplate; // 自定义提示词模板

    @TableField("config_json")
    private String configJson; // 扩展配置
}