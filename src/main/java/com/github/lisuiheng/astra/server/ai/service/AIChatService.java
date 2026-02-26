package com.github.lisuiheng.astra.server.ai.service;

import com.github.lisuiheng.astra.server.server.model.dto.ChatRequest;
import com.github.lisuiheng.astra.server.server.model.dto.ChatResponse;
import com.github.lisuiheng.astra.server.ai.model.dto.MemoryQuery;
import com.github.lisuiheng.astra.server.ai.model.dto.MemoryRecord;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AIChatService {

    @Autowired
    @Lazy
    private ChatClient chatClient;
    @Autowired
    @Lazy
    private VectorMemoryService memoryService;


    private static final String SYSTEM_PROMPT = """
        你是一个名为"小智"的AI助手，具有记忆功能。你可以记住用户的重要信息、偏好和对话历史。
        
        # 记忆上下文：
        {memoryContext}
        
        # 指导原则：
        1. 根据记忆上下文提供个性化回复
        2. 如果用户提到重要信息，主动询问是否需要记住
        3. 保持友好、专业的对话风格
        4. 对于不确定的信息要诚实说明
        
        当前时间：{currentTime}
        """;

    public ChatResponse processChat(ChatRequest request) {
        // 1. 检索相关记忆
        List<MemoryRecord> relevantMemories = retrieveRelevantMemories(request);

        // 2. 构建增强提示词
        String memoryContext = buildMemoryContext(relevantMemories);
        String systemMessage = new SystemPromptTemplate(SYSTEM_PROMPT)
                .createMessage(Map.of(
                    "memoryContext", memoryContext,
                    "currentTime", LocalDateTime.now().toString()
                )).getText();

        // 3. 调用AI模型
        org.springframework.ai.chat.model.ChatResponse aiResponse = chatClient.prompt()
                .system(systemMessage)
                .user(request.getMessage())
                .call()
                .chatResponse();

        // 4. 构建响应
        return buildChatResponse(request, aiResponse, relevantMemories);
    }

    private List<MemoryRecord> retrieveRelevantMemories(ChatRequest request) {
        if (!request.isUseMemory()) {
            return List.of();
        }

        MemoryQuery query = new MemoryQuery();
        query.setUserId(request.getUserId());
        query.setSessionId(request.getSessionId());
        query.setQueryText(request.getMessage());
        query.setMaxResults(5);

        return memoryService.searchMemories(query);
    }

    private String buildMemoryContext(List<MemoryRecord> memories) {
        if (memories.isEmpty()) {
            return "暂无相关记忆。";
        }

        return memories.stream()
                .map(memory -> String.format("- %s (类型: %s, 重要性: %.1f)",
                    memory.getContent(),
                    memory.getType(),
                    memory.getImportance()))
                .collect(Collectors.joining("\n"));
    }

    private ChatResponse buildChatResponse(
            ChatRequest request,
            org.springframework.ai.chat.model.ChatResponse aiResponse,
            List<MemoryRecord> memories) {

        ChatResponse response = new ChatResponse();
        response.setMessage(aiResponse.getResult().getOutput().getText());
        response.setSessionId(request.getSessionId());
        response.setTimestamp(LocalDateTime.now());

        // 构建记忆引用
        List<ChatResponse.MemoryReference> memoryRefs = memories.stream()
                .map(memory -> {
                    ChatResponse.MemoryReference ref = new ChatResponse.MemoryReference();
                    ref.setMemoryId(memory.getId());
                    ref.setContent(memory.getContent());
                    ref.setSimilarity(0.8); // 实际应从搜索中获取
                    return ref;
                })
                .collect(Collectors.toList());
        response.setMemoryReferences(memoryRefs);

        // 使用信息
        ChatResponse.UsageInfo usage = new ChatResponse.UsageInfo();
        usage.setPromptTokens(aiResponse.getMetadata().getUsage().getPromptTokens());
        usage.setCompletionTokens(aiResponse.getMetadata().getUsage().getCompletionTokens());
        usage.setTotalTokens(aiResponse.getMetadata().getUsage().getTotalTokens());
        response.setUsage(usage);

        return response;
    }

    public void storeImportantMemory(MemoryRecord memory) {
        memoryService.storeMemory(memory);
    }
}