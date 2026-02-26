package com.github.lisuiheng.astra.server.ai.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.server.server.handler.JsonbTypeHandler;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@TableName(value = "server_agent", autoResultMap = true)
public class Agent extends BaseEntity {

    @JsonSerialize(using = ToStringSerializer.class)
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private String id; // 主键，唯一标识符

    @TableField("agent_name")
    private String agentName; // 智能体名称

    @TableField("nickname")
    private String nickname; // 助手昵称

    @TableField("voice_id")
    private String voiceId; // 角色音色ID

    @TableField("memory_id")
    private String memoryId; // 记忆体ID

    @TableField("description")
    private String description; // 角色介绍

    @TableField("agent_template_id")
    private String agentTemplateId; // 智能体模板ID

    @TableField("llm_type")
    private String llmType; // 大模型类型：openai、dify、coze、zhipu、deepseek等

    @TableField("model_name")
    private String modelName; // 模型名称：如gpt-4、claude-3、qwen-max等

    @TableField("api_key")
    @JsonIgnore
    private String apiKey; // API密钥（加密存储）

    @TableField("api_base_url")
    private String apiBaseUrl; // API基础URL

    @TableField("temperature")
    private BigDecimal temperature = BigDecimal.valueOf(0.7); // 温度参数

    @TableField("max_tokens")
    private Integer maxTokens = 2000; // 最大token数

    @TableField("top_p")
    private BigDecimal topP = BigDecimal.valueOf(0.9); // Top-p采样参数

    @TableField("presence_penalty")
    private BigDecimal presencePenalty = BigDecimal.ZERO; // 存在惩罚

    @TableField("frequency_penalty")
    private BigDecimal frequencyPenalty = BigDecimal.ZERO; // 频率惩罚

    @TableField("enable_rag")
    private Boolean enableRag = false; // 是否启用RAG检索增强

    @TableField("rag_threshold")
    private BigDecimal ragThreshold = BigDecimal.valueOf(0.5); // RAG相似度阈值

    @TableField("rag_top_k")
    private Integer ragTopK = 5; // RAG检索top_k数量

    @TableField("max_memories_to_retrieve")
    private Integer maxMemoriesToRetrieve = 5; // 默认检索5条记忆

    @TableField("system_prompt")
    private String systemPrompt; // 系统提示词

    @TableField("welcome_message")
    private String welcomeMessage; // 欢迎语

    @TableField("avatar_url")
    private String avatarUrl; // 头像URL

    @TableField("category")
    private String category; // 分类：客服、助理、翻译等

    @TableField("status")
    private Integer status = 1; // 状态：0-禁用，1-启用

    @TableField("priority")
    private Integer priority = 0; // 优先级

    @TableField("rate_limit")
    private Integer rateLimit = 100; // 每分钟请求限制

    @TableField("total_tokens")
    private Long totalTokens = 0L; // 总消耗token数

    @TableField("total_calls")
    private Long totalCalls = 0L; // 总调用次数

    @TableField("config_json")
    private String configJson; // 扩展配置JSON

    // Mem0ai配置
    @TableField("enable_memory")
    private Boolean enableMemory = false; // 是否启用长期记忆

    @TableField("memory_type")
    private String memoryType; // 记忆类型：mem0、redis、database等

    @TableField("memory_config")
    private String memoryConfig; // 记忆配置（JSON）

    @TableField("memory_context_window")
    private Integer memoryContextWindow = 10; // 记忆上下文窗口大小

    @TableField("memory_summary_threshold")
    private Integer memorySummaryThreshold = 5; // 记忆摘要阈值（对话轮次）

    @TableField("memory_threshold")
    private BigDecimal memoryThreshold = BigDecimal.valueOf(0.6); // RAG相似度阈值

    @TableField(value = "kb_configs", typeHandler = JsonbTypeHandler.class)
    private List<Map<String, Object>> kbConfigs; // 知识库配置列表

    @TableField(value = "deleted", fill = FieldFill.INSERT)
    @TableLogic
    private Integer deleted = 0; // 逻辑删除：0-未删除，1-已删除


    // 添加用户关联字段
    @TableField("user_id")
    private String userId; // 所属用户ID

    @TableField("is_public")
    private Boolean isPublic = false; // 是否公开：true-公开，false-私有

    // 非数据库字段
    @TableField(exist = false)
    private String chatMode; // 聊天模式：block（阻塞模式）或 stream（流式模式）

    @TableField(exist = false)
    private String llm; // 大语言模型平台：dify 或 coze

    @TableField(exist = false)
    private String llmMode; // 大语言模型方式：default 或 aimp_v1

    @TableField(exist = false)
    private String difyToken; // Dify 平台 Token

    @TableField(exist = false)
    private String cozeToken; // Coze 平台 Token

    @TableField(exist = false)
    private String cozeBotId; // Coze Bot ID

    @TableField(exist = false)
    private Object memory; // 记忆体

    @TableField(exist = false)
    private Object voice; // 音色信息

    @TableField(exist = false)
    private String mailHost; // 邮件服务器主机地址

    @TableField(exist = false)
    private String mailPort; // 邮件服务器端口号

    @TableField(exist = false)
    private String mailUsername; // 邮件账号用户名

    @TableField(exist = false)
    @JsonIgnore
    private String mailPassword; // 邮件账号密码（加密存储）

    @TableField(exist = false)
    private List<AgentTool> tools; // 智能体可用的工具列表

    @TableField(exist = false)
    private List<KnowledgeBase> knowledgeBases; // 关联的知识库列表

    // 在非数据库字段部分添加
    @TableField(exist = false)
    private com.github.lisuiheng.astra.server.user.model.entity.User creator; // 创建者信息

    @TableField(exist = false)
    private List<com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo> boundDevices; // 绑定的设备列表

    /**
     * 检查 Agent 是否有邮件配置
     */
    public boolean hasMailConfig() {
        return StringUtils.isNotBlank(this.getMailHost())
                && StringUtils.isNotBlank(this.getMailPort())
                && StringUtils.isNotBlank(this.getMailUsername())
                && StringUtils.isNotBlank(this.getMailPassword());
    }

    /**
     * 检查 Agent 是否有大模型配置
     */
    public boolean hasLlmConfig() {
        return StringUtils.isNotBlank(this.getApiKey())
                && StringUtils.isNotBlank(this.getLlmType())
                && StringUtils.isNotBlank(this.getModelName());
    }

    /**
     * 检查是否启用RAG
     */
    public boolean isRagEnabled() {
        return Boolean.TRUE.equals(this.getEnableRag());
    }

    /**
     * 检查是否启用记忆
     */
    public boolean isMemoryEnabled() {
        return Boolean.TRUE.equals(this.getEnableMemory());
    }

    /**
     * 获取完整的系统提示词
     */
    public String getFullSystemPrompt() {
        StringBuilder prompt = new StringBuilder();

        if (StringUtils.isNotBlank(this.getSystemPrompt())) {
            prompt.append(this.getSystemPrompt());
        }

        if (StringUtils.isNotBlank(this.getDescription())) {
            prompt.append("\n\n角色设定：").append(this.getDescription());
        }

        if (StringUtils.isNotBlank(this.getNickname())) {
            prompt.append("\n你的名字：").append(this.getNickname());
        }

        return prompt.toString();
    }

    /**
     * 构建请求参数DTO
     */
    public com.github.lisuiheng.astra.server.server.model.dto.LlmRequestDTO buildLlmRequest(String userMessage) {
        com.github.lisuiheng.astra.server.server.model.dto.LlmRequestDTO request = new com.github.lisuiheng.astra.server.server.model.dto.LlmRequestDTO();
        request.setAgentId(this.getId());
        request.setModel(this.getModelName());
        request.setMessages(java.util.List.of(
                new com.github.lisuiheng.astra.server.server.model.dto.MessageDTO("system", this.getFullSystemPrompt()),
                new com.github.lisuiheng.astra.server.server.model.dto.MessageDTO("user", userMessage)
        ));
        request.setTemperature(this.getTemperature());
        request.setMaxTokens(this.getMaxTokens());
        request.setTopP(this.getTopP());
        request.setPresencePenalty(this.getPresencePenalty());
        request.setFrequencyPenalty(this.getFrequencyPenalty());
        request.setStream("STREAM".equals(this.getChatMode()));

        return request;
    }

    /**
     * 检查是否是用户的智能体
     */
    public boolean isUserAgent(String userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    /**
     * 检查用户是否有权限访问
     */
    public boolean canAccess(String userId) {
        return this.isPublic != null && (this.isPublic || isUserAgent(userId));
    }
}