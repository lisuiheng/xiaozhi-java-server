package com.github.lisuiheng.astra.server.ai.service;

import com.github.lisuiheng.astra.server.ai.model.dto.*;
import com.github.lisuiheng.astra.server.ai.model.entity.Agent;
import com.github.lisuiheng.astra.server.server.constant.SpeakerType;
import com.github.lisuiheng.astra.server.server.model.dto.ChatDetailDTO;
import com.github.lisuiheng.astra.server.server.service.ChatDetailService;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.stream.Collectors;

@Service
@Slf4j
public class EnhancedAgentService {

    private static final String CALLID_FIRST_MESSAGE_KEY = "astra:server:callid:first:";
    private static final long FIRST_MESSAGE_EXPIRE_SECONDS = 24 * 60 * 60; // 24小时过期

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private AgentService agentService;

    @Autowired
    private VectorStore vectorStore;

    @Autowired
    private Mem0aiService mem0aiService;

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private ChatMemory chatMemory; // 已配置为 RedisChatMemory

    @Autowired
    private ChatDetailService chatDetailService;

    /**
     * 智能体聊天：集成 RAG + 短期记忆 + Mem0 长期记忆（支持流式和阻塞两种模式）
     */
    public Object chat(String agentId, String userId, String userMessage) {
        Agent agent = agentService.getById(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("智能体不存在: " + agentId);
        }

        // 根据 chatMode 决定使用流式还是阻塞模式
        boolean isStreamMode = "stream".equalsIgnoreCase(agent.getChatMode());

        if (isStreamMode) {
            return chatStream(null, agent, userId, userMessage);
        } else {
            return chatBlocking(agent, userId, userMessage);
        }
    }

    /**
     * 保证同一个callId的所有消息都生效系统提示词
     */
    private Flux<StreamChunk> chatStream(String callId, Agent agent, String userId, String userMessage) {
        log.info("流式聊天模式，agentId: {}, userId: {}, callId: {}",
                agent.getId(), userId, callId);

        // 1. 构造会话ID，优先使用callId
        String conversationId = StringUtils.hasText(callId)
                ? "call:" + callId
                : "agent:" + agent.getId() + ":user:" + userId;

        // 2. 获取或创建系统提示词
        String systemPrompt = getOrCreateSystemPrompt(conversationId, agent);

        // 2.1 保存系统提示词到聊天详情（新增）
        saveSystemPromptToChatDetail(conversationId, userId, agent.getId(), systemPrompt, agent.getAgentName());


        // 3. 获取长期记忆（如果启用）
        List<MemoryItem> longTermMemories = null;
        if (Boolean.TRUE.equals(agent.getEnableMemory())) {
            longTermMemories = mem0aiService.searchMemory(userId, userMessage,
                    agent.getMaxMemoriesToRetrieve() != null ? agent.getMaxMemoriesToRetrieve() : 3,
                    agent.getMemoryThreshold().floatValue());
        }

        // 4. 构建完整的用户消息（包含上下文）
        String completeUserMessage = buildCompleteUserMessage(
                agent, userMessage, longTermMemories, conversationId);

        // 5. 保存用户消息到聊天详情

        // 6. 构建 ChatClient 请求 - 每次都包含系统提示词
        ChatClient.ChatClientRequestSpec requestSpec = chatClient.prompt()
                .system(systemPrompt)  // 关键：每次都包含系统提示词
                .user(completeUserMessage);

        // 7. 添加短期记忆Advisor（配置为不存储系统消息）
        requestSpec = requestSpec.advisors(
                MessageChatMemoryAdvisor.builder(chatMemory)
                        .conversationId(conversationId)
                        .build()
        );

        // 8. 如果启用了 RAG，添加知识库顾问
        if (agent.isRagEnabled() && vectorStore != null) {
            requestSpec = requestSpec.advisors(
                    QuestionAnswerAdvisor.builder(vectorStore)
                            .searchRequest(SearchRequest.builder()
                                    .topK(agent.getRagTopK() != null ? agent.getRagTopK() : 5)
                                    .similarityThreshold(
                                            agent.getRagThreshold() != null ? agent.getRagThreshold().doubleValue() : 0.5d)
                                    .build())
                            .build()
            );
        }

        // 9. 后续流式处理逻辑...
        ConcurrentLinkedDeque<String> responseChunks = new ConcurrentLinkedDeque<>();

        return requestSpec
                .stream()
                .chatResponse()
                .flatMap(chatResponse -> Flux.fromIterable(chatResponse.getResults())
                        .map(generation -> {
                            String chunk = generation.getOutput().getText();
                            String cleanedChunk = cleanTextForTts(chunk);
                            responseChunks.add(cleanedChunk);

                            return StreamChunk.builder()
                                    .type("chunk")
                                    .content(cleanedChunk)
                                    .build();
                        }))
                .doOnComplete(() -> {
                    // 处理完成逻辑
                    String finalResponse = String.join("", responseChunks);
                    saveAiResponseToChatDetail(conversationId, userId, agent.getId(), finalResponse, agent.getAgentName());
                });
    }


    /**
     * 获取或创建会话的系统提示词
     */
    private String getOrCreateSystemPrompt(String callId, Agent agent) {
        String key = CALLID_FIRST_MESSAGE_KEY + callId;

        try {
            // 尝试从Redis获取
            String cachedPrompt = redisTemplate.opsForValue().get(key);

            if (cachedPrompt != null) {
                return cachedPrompt;
            }

            // 生成新的系统提示词
            String systemPrompt = buildCompleteSystemPrompt(agent);

            // 存储到Redis，24小时过期
            redisTemplate.opsForValue().set(
                    key,
                    systemPrompt,
                    Duration.ofHours(24)
            );

            return systemPrompt;

        } catch (Exception e) {
            log.error("获取系统提示词失败，使用实时生成", e);
            return buildCompleteSystemPrompt(agent);
        }
    }




    /**
     * 处理流式聊天完成后的逻辑
     */
    private void handleStreamComplete(Agent agent, String conversationId, String userId,
                                      String systemPrompt, String userMessage,
                                      ConcurrentLinkedDeque<String> responseChunks,
                                      boolean isFirstMessage) {
        log.debug("流式调用完成，开始处理后续逻辑，agentId: {}, userId: {}", agent.getId(), userId);

        // 拼接完整响应
        StringBuilder completeResponse = new StringBuilder();
        for (String chunk : responseChunks) {
            completeResponse.append(chunk);
        }
        String finalResponse = completeResponse.toString();

        log.info("完整响应内容: {}", finalResponse);

        // 保存AI响应到聊天详情
        saveAiResponseToChatDetail(conversationId, userId, agent.getId(), finalResponse, agent.getAgentName());

        // 保存长期记忆（如果启用）
        if (Boolean.TRUE.equals(agent.getEnableMemory())) {
            try {
                saveMemoryToMem0(agent, userId, userMessage, finalResponse, systemPrompt, isFirstMessage);
                log.debug("长期记忆保存成功");
            } catch (Exception e) {
                log.error("保存长期记忆失败，userId: {}", userId, e);
            }
        }

        // 记录统计信息（如果需要）
        recordUsageStatistics(agent, finalResponse.length());
    }

//    /**
//     * 保存用户消息到聊天详情
//     */
//    private void saveUserMessageToChatDetail(String callId, String userId, String agentId, String content, String agentName) {
//        try {
//            ChatDetailDTO chatDetailDTO = new ChatDetailDTO();
//            chatDetailDTO.setCallId(callId);
//            chatDetailDTO.setUserId(userId);
//            chatDetailDTO.setAgentId(agentId);
//            chatDetailDTO.setQuestionKind(SpeakerType.USER);
//            chatDetailDTO.setQuestionName("用户");
//            chatDetailDTO.setContent(content);
//            chatDetailDTO.setChatTime(LocalDateTime.now());
//            chatDetailDTO.setChatKind("chat");
//
//            chatDetailService.saveChatDetailAsync(chatDetailDTO);
//
//            log.debug("用户消息已保存到聊天详情，callId: {}, userId: {}, agentId: {}", callId, userId, agentId);
//        } catch (Exception e) {
//            log.error("保存用户消息到聊天详情失败，callId: {}, userId: {}, agentId: {}", callId, userId, agentId, e);
//        }
//    }

    /**
     * 保存AI响应到聊天详情
     */
    private void saveAiResponseToChatDetail(String callId, String userId, String agentId, String content, String agentName) {
        try {
            ChatDetailDTO chatDetailDTO = new ChatDetailDTO();
            chatDetailDTO.setCallId(callId);
            chatDetailDTO.setUserId(userId);
            chatDetailDTO.setAgentId(agentId);
            chatDetailDTO.setQuestionKind(SpeakerType.AGENT);
            chatDetailDTO.setQuestionName(agentName != null ? agentName : "智能体");
            chatDetailDTO.setContent(content);
            chatDetailDTO.setChatTime(LocalDateTime.now());
            chatDetailDTO.setChatKind("chat");
            
            chatDetailService.saveChatDetailAsync(chatDetailDTO);
            
            log.debug("AI响应已保存到聊天详情，callId: {}, userId: {}, agentId: {}", callId, userId, agentId);
        } catch (Exception e) {
            log.error("保存AI响应到聊天详情失败，callId: {}, userId: {}, agentId: {}", callId, userId, agentId, e);
        }
    }

    /**
     * 保存系统提示词到聊天详情
     */
    private void saveSystemPromptToChatDetail(String callId, String userId, String agentId,
                                              String systemPrompt, String agentName) {
        try {
            ChatDetailDTO chatDetailDTO = new ChatDetailDTO();
            chatDetailDTO.setCallId(callId);
            chatDetailDTO.setUserId(userId);
            chatDetailDTO.setAgentId(agentId);
            chatDetailDTO.setQuestionKind(SpeakerType.SYSTEM); // 需要添加SYSTEM类型到SpeakerType枚举
            chatDetailDTO.setQuestionName("系统");
            chatDetailDTO.setContent(systemPrompt);
            chatDetailDTO.setChatTime(LocalDateTime.now());
            chatDetailDTO.setChatKind("system_prompt");

            chatDetailService.saveChatDetailAsync(chatDetailDTO);

            log.debug("系统提示词已保存到聊天详情，callId: {}, userId: {}, agentId: {}",
                    callId, userId, agentId);
        } catch (Exception e) {
            log.error("保存系统提示词到聊天详情失败，callId: {}, userId: {}, agentId: {}",
                    callId, userId, agentId, e);
        }
    }

    /**
     * 清洗文本，使其更适合TTS
     */
    private String cleanTextForTts(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 1. 移除Markdown格式标记
        String cleaned = text
                // 移除粗体标记
                .replace("**", "")
                // 移除斜体标记
                .replace("*", "")
                // 移除代码块标记
                .replace("`", "")
                // 移除标题标记
                .replace("#", "")
                // 移除列表标记
                .replace("- ", "")
                .replace("• ", "")
                // 移除链接标记
                .replaceAll("\\[([^\\]]+)\\]\\([^\\)]+\\)", "$1");

        // 2. 标准化空格和换行
        cleaned = cleaned
                // 将多个连续空格替换为单个空格
                .replaceAll("\\s+", " ")
                // 将多个连续换行替换为单个换行
                .replaceAll("\\n{3,}", "\n\n")
                // 将制表符替换为空格
                .replaceAll("\\t+", " ")
                // 移除行首尾的空格
                .replaceAll("(?m)^\\s+|\\s+$", "")
                // 将中文空格替换为英文空格
                .replaceAll("　", " ")
                // 移除零宽空格
                .replaceAll("\\u200B", "");

        // 3. 处理特殊标点符号
        cleaned = cleaned
                // 将英文标点替换为中文标点（可选）
                .replace(",", "，")
                .replace(".", "。")
                .replace("!", "！")
                .replace("?", "？")
                .replace(":", "：")
                .replace(";", "；")
                // 移除多余的省略号
                .replaceAll("\\.{3,}", "……")
                // 标准化破折号
                .replaceAll("-{2,}", "——");

        // 4. 处理特定字符（根据日志中的特殊字符）
        cleaned = cleaned
                .replace("　", "")  // 全角空格
                .replace(" ", "")   // 窄空格
                .replace(" ", "")   // 细空格
                .replace(" ", "")   // 头发空格
                .replace("​", "");  // 零宽空格

        // 5. 移除不可见字符
        cleaned = cleaned.replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "");

        // 6. 最后trim一下
        cleaned = cleaned.trim();

        return cleaned;
    }

    /**
     * 批量清洗文本
     */
    private List<String> cleanChunksForTts(List<String> chunks) {
        return chunks.stream()
                .map(this::cleanTextForTts)
                .filter(chunk -> !chunk.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * 阻塞式聊天
     */
    private AgentResponse chatBlocking(Agent agent, String userId, String userMessage) {
        log.info("阻塞式聊天模式，agentId: {}, userId: {}", agent.getId(), userId);

        // 1. 构造会话 ID（隔离不同用户和智能体）
        String conversationId = "agent:" + agent.getId() + ":user:" + userId;

        // 2. 从 Mem0 检索长期记忆（仅当启用记忆）
        String longTermContext = "";
        if (Boolean.TRUE.equals(agent.getEnableMemory())) {
            List<MemoryItem> memories = mem0aiService.searchMemory(userId, userMessage,
                    agent.getMaxMemoriesToRetrieve() != null ? agent.getMaxMemoriesToRetrieve() : 3, agent.getMemoryThreshold().floatValue());
            if (!memories.isEmpty()) {
                longTermContext = "相关长期记忆:\n" +
                        memories.stream()
                                .map(m -> "- " + m.getContent())
                                .collect(Collectors.joining("\n"));
            }
        }

        // 3. 构造增强后的用户输入（将长期记忆作为上下文前置）
        String enhancedUserMessage = userMessage;
        if (StringUtils.hasText(longTermContext)) {
            enhancedUserMessage = longTermContext + "\n\n基于以上信息，请回答：" + userMessage;
        }



        // 4. 构建 Prompt 并添加 Advisors
        ChatClient.ChatClientRequestSpec advisors = chatClient.prompt()
                .user(enhancedUserMessage)
                .advisors(
                        // 短期记忆：自动加载/保存对话历史
                        MessageChatMemoryAdvisor.builder(chatMemory)
                                .conversationId(conversationId)
                                .build()
                );

        // 保存用户消息到聊天详情

        // 5. 如果启用了 RAG，添加知识库顾问
        if (agent.isRagEnabled()) {
            if (vectorStore != null) {
                advisors.advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(SearchRequest.builder()
                                        .topK(agent.getRagTopK() != null ? agent.getRagTopK() : 5)
                                        .similarityThreshold(
                                                agent.getRagThreshold() != null ? agent.getRagThreshold().doubleValue() : 0.5d)
                                        .build())
                                .build()
                );
            }
        }

        // 6. 调用模型
        String responseContent = advisors.call().content();

        // 保存AI响应到聊天详情
        saveAiResponseToChatDetail(conversationId, userId, agent.getId(), responseContent, agent.getAgentName());

        // 7. 保存本次对话到 Mem0（仅当启用记忆）
        if (Boolean.TRUE.equals(agent.getEnableMemory())) {
            String systemPrompt = buildCompleteSystemPrompt(agent);
//            saveMemoryToMem0(agent, userId, userMessage, responseContent, systemPrompt);
        }

        // 8. 记录统计信息
        recordUsageStatistics(agent, responseContent.length());

        // 9. 返回响应
        return AgentResponse.builder()
                .agentId(agent.getId())
                .agentName(agent.getAgentName())
                .content(responseContent)
                .timestamp(System.currentTimeMillis())
                .memoryEnabled(agent.getEnableMemory())
                .build();
    }

    /**
     * 保存记忆到 Mem0
     */
    private void saveMemoryToMem0(Agent agent, String userId, String userMessage,
                                  String responseContent, String systemPrompt,
                                  boolean isFirstMessage) {
        List<Message> messagesToSave = new ArrayList<>();

        // 只有在第一次消息时，才保存系统提示词到记忆
        if (isFirstMessage && StringUtils.hasText(systemPrompt)) {
            messagesToSave.add(createMessage(Role.SYSTEM, systemPrompt));
        }

        // 保存用户和助手的消息
        messagesToSave.add(createMessage(Role.USER, userMessage));
        messagesToSave.add(createMessage(Role.ASSISTANT, responseContent));

        try {
            boolean saved = mem0aiService.addMemory(userId, messagesToSave);
            if (!saved) {
                log.warn("Mem0 记忆保存失败，userId: {}", userId);
            } else {
                log.debug("Mem0 记忆保存成功，userId: {}", userId);
            }
        } catch (Exception e) {
            log.error("保存 Mem0 记忆异常，userId: {}", userId, e);
        }
    }

    private Message createMessage(Role role, String content) {
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);
        return message;
    }

    /**
     * 记录使用统计
     */
    private void recordUsageStatistics(Agent agent, int responseLength) {
        // 这里可以记录token使用量、调用次数等统计信息
        // 例如：更新agent的totalTokens和totalCalls字段
        log.debug("智能体 {} 响应长度: {} 字符", agent.getAgentName(), responseLength);

        // 实际实现中，可以调用agentService更新统计信息
        // agent.setTotalTokens(agent.getTotalTokens() + estimatedTokens);
        // agent.setTotalCalls(agent.getTotalCalls() + 1);
        // agentService.updateById(agent);
    }

    /**
     * 专门的流式聊天方法（返回 Flux<StreamChunk>）
     */
    public Flux<StreamChunk> chatStream(String callId, String agentId, String userId, String userMessage) {
        Agent agent = agentService.getById(agentId);
        if (agent == null) {
            return Flux.error(new IllegalArgumentException("智能体不存在: " + agentId));
        }

        return chatStream(callId, agent, userId, userMessage);
    }

    /**
     * 专门的阻塞聊天方法（保持向后兼容）
     */
    public AgentResponse chatBlocking(String agentId, String userId, String userMessage) {
        Agent agent = agentService.getById(agentId);
        if (agent == null) {
            throw new IllegalArgumentException("智能体不存在: " + agentId);
        }

        return chatBlocking(agent, userId, userMessage);
    }

    /**
     * 构建完整的系统提示词
     */
    private String buildCompleteSystemPrompt(Agent agent) {
        StringBuilder prompt = new StringBuilder();
        
        // 基础系统提示词
        if (StringUtils.hasText(agent.getSystemPrompt())) {
            prompt.append(agent.getSystemPrompt());
        } else {
            // 默认系统提示词
            prompt.append("你是一个专业的人工智能助手。");
        }
        
        // 添加智能体信息
        if (StringUtils.hasText(agent.getAgentName())) {
            prompt.append("\n\n你的名称：").append(agent.getAgentName());
        }
        
        if (StringUtils.hasText(agent.getNickname())) {
            prompt.append("\n你的昵称：").append(agent.getNickname());
        }
        
        if (StringUtils.hasText(agent.getDescription())) {
            prompt.append("\n角色描述：").append(agent.getDescription());
        }
        
        if (StringUtils.hasText(agent.getCategory())) {
            prompt.append("\n服务类别：").append(agent.getCategory());
        }
        
        // 添加行为规范
        prompt.append("\n\n行为规范：");
        prompt.append("\n1. 保持专业和友好的态度");
        prompt.append("\n2. 提供准确、有用的信息");
        prompt.append("\n3. 如果不知道，坦诚承认");
        prompt.append("\n4. 遵守法律法规");

        // 添加回复长度限制（核心部分）
        prompt.append("\n\n回复要求：");
        prompt.append("\n1. 保持回复简洁明了");
        prompt.append("\n2. 回复尽量控制在 ").append(agent.getMaxTokens() != null ?
                agent.getMaxTokens() : 500).append(" 字以内");
        prompt.append("\n3. 如果内容较多，请分点说明，每个点尽量精简");
        prompt.append("\n4. 避免冗长重复的表达");
        
        // 根据智能体类型添加特定规范
        if ("客服".equals(agent.getCategory())) {
            prompt.append("\n5. 重点解决用户问题，提供明确步骤");
            prompt.append("\n6. 结束时询问用户是否还有其他问题");
        } else if ("助理".equals(agent.getCategory())) {
            prompt.append("\n5. 主动提供建议，考虑用户需求");
            prompt.append("\n6. 注意提醒重要事项");
        }
        
        return prompt.toString();
    }

    /**
     * 构建完整的用户消息（包含上下文）
     */
    private String buildCompleteUserMessage(Agent agent, String userMessage, 
                                        List<MemoryItem> memories, String conversationId) {
        StringBuilder message = new StringBuilder();
        
        // 1. 添加上下文说明
        message.append("请根据以下信息回答用户的问题：\n\n");
        
        // 2. 添加长期记忆（如果有）
        if (memories != null && !memories.isEmpty()) {
            message.append("【长期记忆】\n");
            memories.forEach(memory -> {
                message.append("- ").append(memory.getContent());
                if (memory.getTimestamp() != null) {
                    message.append(" (")
                           .append(memory.getTimestamp())
                           .append(")");
                }
                message.append("\n");
            });
            message.append("\n");
        }
        
        // 3. 添加当前对话上下文（通过MessageChatMemoryAdvisor自动管理）
        message.append("【当前对话】\n");
        message.append("用户的问题：").append(userMessage).append("\n\n");
        
        // 4. 添加特定指令
        if (agent.isRagEnabled()) {
            message.append("注意：如果有相关知识库内容，请优先参考使用。\n\n");
        }
        
        // 5. 添加回答要求
        message.append("请基于以上信息，提供详细、准确的回答。");
        
        return message.toString();
    }


}