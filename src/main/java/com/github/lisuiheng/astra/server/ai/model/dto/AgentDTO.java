package com.github.lisuiheng.astra.server.ai.model.dto;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
public class AgentDTO {

    @JsonSerialize(using = ToStringSerializer.class)
    private String id; // 主键，唯一标识符

    private String agentName; // 智能体名称

    private String nickname; // 助手昵称

    private String voiceId; // 角色音色ID

    private String memoryId; // 记忆体ID

    private String description; // 角色介绍

    private String agentTemplateId; // 智能体模板ID

    private String llmType; // 大模型类型：openai、dify、coze、zhipu、deepseek等

    private String modelName; // 模型名称：如gpt-4、claude-3、qwen-max等

    // 注意：不包含apiKey字段，以实现脱敏

    private String apiBaseUrl; // API基础URL

    private BigDecimal temperature; // 温度参数

    private Integer maxTokens; // 最大token数

    private BigDecimal topP; // Top-p采样参数

    private BigDecimal presencePenalty; // 存在惩罚

    private BigDecimal frequencyPenalty; // 频率惩罚

    private Boolean enableRag; // 是否启用RAG检索增强

    private BigDecimal ragThreshold; // RAG相似度阈值

    private Integer ragTopK; // RAG检索top_k数量

    private Integer maxMemoriesToRetrieve; // 默认检索5条记忆

    private String systemPrompt; // 系统提示词

    private String welcomeMessage; // 欢迎语

    private String avatarUrl; // 头像URL

    private String category; // 分类：客服、助理、翻译等

    private Integer status; // 状态：0-禁用，1-启用

    private Integer priority; // 优先级

    private Integer rateLimit; // 每分钟请求限制

    private Long totalTokens; // 总消耗token数

    private Long totalCalls; // 总调用次数

    private String configJson; // 扩展配置JSON

    // Mem0ai配置
    private Boolean enableMemory; // 是否启用长期记忆

    private String memoryType; // 记忆类型：mem0、redis、database等

    private String memoryConfig; // 记忆配置（JSON）

    private Integer memoryContextWindow; // 记忆上下文窗口大小

    private Integer memorySummaryThreshold; // 记忆摘要阈值（对话轮次）

    private List<Map<String, Object>> kbConfigs; // 知识库配置列表

    // 添加用户关联字段
    private String userId; // 所属用户ID

    private Boolean isPublic; // 是否公开：true-公开，false-私有

    // 非数据库字段
    private String chatMode; // 聊天模式：block（阻塞模式）或 stream（流式模式）

    private String llm; // 大语言模型平台：dify 或 coze

    private String llmMode; // 大语言模型方式：default 或 aimp_v1

    private String difyToken; // Dify 平台 Token

    // 注意：不包含cozeToken字段，以实现脱敏

    private String cozeBotId; // Coze Bot ID

    private Object memory; // 记忆体

    private Object voice; // 音色信息

    private String mailHost; // 邮件服务器主机地址

    private String mailPort; // 邮件服务器端口号

    private String mailUsername; // 邮件账号用户名

    // 注意：不包含mailPassword字段，以实现脱敏

    private List<AgentToolDTO> tools; // 智能体可用的工具列表

    private List<KnowledgeBaseDTO> knowledgeBases; // 关联的知识库列表

    private com.github.lisuiheng.astra.server.user.model.entity.User creator; // 创建者信息

    private List<com.github.lisuiheng.astra.server.server.model.entity.DeviceInfo> boundDevices; // 绑定的设备列表

    // 创建一个从Agent实体转换为DTO的静态方法
    public static AgentDTO fromEntity(com.github.lisuiheng.astra.server.ai.model.entity.Agent agent) {
        if (agent == null) {
            return null;
        }

        AgentDTO dto = new AgentDTO();
        dto.setId(agent.getId());
        dto.setAgentName(agent.getAgentName());
        dto.setNickname(agent.getNickname());
        dto.setVoiceId(agent.getVoiceId());
        dto.setMemoryId(agent.getMemoryId());
        dto.setDescription(agent.getDescription());
        dto.setAgentTemplateId(agent.getAgentTemplateId());
        dto.setLlmType(agent.getLlmType());
        dto.setModelName(agent.getModelName());
        // 不设置apiKey以实现脱敏
        dto.setApiBaseUrl(agent.getApiBaseUrl());
        dto.setTemperature(agent.getTemperature());
        dto.setMaxTokens(agent.getMaxTokens());
        dto.setTopP(agent.getTopP());
        dto.setPresencePenalty(agent.getPresencePenalty());
        dto.setFrequencyPenalty(agent.getFrequencyPenalty());
        dto.setEnableRag(agent.getEnableRag());
        dto.setRagThreshold(agent.getRagThreshold());
        dto.setRagTopK(agent.getRagTopK());
        dto.setMaxMemoriesToRetrieve(agent.getMaxMemoriesToRetrieve());
        dto.setSystemPrompt(agent.getSystemPrompt());
        dto.setWelcomeMessage(agent.getWelcomeMessage());
        dto.setAvatarUrl(agent.getAvatarUrl());
        dto.setCategory(agent.getCategory());
        dto.setStatus(agent.getStatus());
        dto.setPriority(agent.getPriority());
        dto.setRateLimit(agent.getRateLimit());
        dto.setTotalTokens(agent.getTotalTokens());
        dto.setTotalCalls(agent.getTotalCalls());
        dto.setConfigJson(agent.getConfigJson());
        dto.setEnableMemory(agent.getEnableMemory());
        dto.setMemoryType(agent.getMemoryType());
        dto.setMemoryConfig(agent.getMemoryConfig());
        dto.setMemoryContextWindow(agent.getMemoryContextWindow());
        dto.setMemorySummaryThreshold(agent.getMemorySummaryThreshold());
        dto.setKbConfigs(agent.getKbConfigs());
        dto.setUserId(agent.getUserId());
        dto.setIsPublic(agent.getIsPublic());
        dto.setChatMode(agent.getChatMode());
        dto.setLlm(agent.getLlm());
        dto.setLlmMode(agent.getLlmMode());
        // 不设置difyToken和cozeToken以实现脱敏
        dto.setCozeBotId(agent.getCozeBotId());
        dto.setMemory(agent.getMemory());
        dto.setVoice(agent.getVoice());
        dto.setMailHost(agent.getMailHost());
        dto.setMailPort(agent.getMailPort());
        // 不设置mailPassword以实现脱敏
        // 转换AgentTool列表为AgentToolDTO列表
        if (agent.getTools() != null) {
            dto.setTools(agent.getTools().stream()
                .map(tool -> {
                    AgentToolDTO toolDTO = new AgentToolDTO();
                    toolDTO.setToolId(tool.getToolId());
                    toolDTO.setToolName(tool.getToolName());
                    toolDTO.setToolType(tool.getToolType());
                    toolDTO.setDescription(tool.getDescription());
                    toolDTO.setConfig(tool.getConfig());
                    toolDTO.setEnabled(tool.getEnabled());
                    return toolDTO;
                })
                .collect(java.util.stream.Collectors.toList()));
        }
        
        // 转换KnowledgeBase列表为KnowledgeBaseDTO列表
        if (agent.getKnowledgeBases() != null) {
            dto.setKnowledgeBases(agent.getKnowledgeBases().stream()
                .map(kb -> {
                    KnowledgeBaseDTO kbDTO = new KnowledgeBaseDTO();
                    kbDTO.setId(kb.getId());
                    kbDTO.setKbName(kb.getKbName());
                    kbDTO.setDescription(kb.getDescription());
                    kbDTO.setVectorStoreType(kb.getVectorStoreType());
                    kbDTO.setEmbeddingModel(kb.getEmbeddingModel());
                    kbDTO.setDocCount(kb.getDocCount());
                    kbDTO.setStatus(kb.getStatus());
                    kbDTO.setIsPublic(kb.getIsPublic());
                    kbDTO.setCreateTime(kb.getCreateTime());
                    kbDTO.setUpdateTime(kb.getUpdateTime());
                    kbDTO.setCreateBy(kb.getCreateBy());
                    kbDTO.setUpdateBy(kb.getUpdateBy());
                    return kbDTO;
                })
                .collect(java.util.stream.Collectors.toList()));
        }
        dto.setCreator(agent.getCreator());
        dto.setBoundDevices(agent.getBoundDevices());

        return dto;
    }
}