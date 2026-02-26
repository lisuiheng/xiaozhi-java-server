package com.github.lisuiheng;

import com.github.lisuiheng.astra.server.ai.mapper.RedisChatMemoryRepository;
import com.github.lisuiheng.astra.server.ai.model.dto.MemoryItem;
import com.github.lisuiheng.astra.server.ai.model.dto.Message;
import com.github.lisuiheng.astra.server.ai.model.dto.Role;
import com.github.lisuiheng.astra.server.ai.service.Mem0aiService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@SpringBootTest(classes = AstraServerApplication.class)
public class AiApplicationTest {
    @Autowired
    private VectorStore vectorStore;
    @Autowired
    private ChatClient chatClient;
    @Autowired
    private RedisChatMemoryRepository chatMemoryRepository;
    @Autowired
    private ChatMemory chatMemory;
    @Autowired
    private Mem0aiService mem0aiService;

    @Test
    public void testRag() {
//        Document doc1 = Document.builder().text("退票30块").build();
//
//
//        vectorStore.add(List.of(doc1));
        String content = chatClient
                .prompt()
                .user("什么是计算机科学的一个分支")
                .advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                        SearchRequest.builder().topK(5).similarityThreshold(0.5d).build()
                                )
                                .build())
                .call()
                .content();
        log.info("content {}", content);
    }

    @Test
    public void testRagStream() {
        Flux<String> responseStream = chatClient
                .prompt()
                .user("什么是计算机科学的一个分支")
                .advisors(
                        QuestionAnswerAdvisor.builder(vectorStore)
                                .searchRequest(
                                        SearchRequest.builder()
                                                .topK(5)
                                                .similarityThreshold(0.5d)
                                                .build()
                                )
                                .build()
                )
                .stream().content(); // 提取文本内容

        // 订阅并逐块打印
        responseStream
                .doOnNext(chunk -> log.info("Received chunk: {}", chunk))
                .collectList() // 可选：如果想最后拿到完整内容
                .block();      // 仅用于测试！生产环境不要在响应链中 block()
    }

//    @Test
//    public void testRagWithMemory() {
//        // 使用固定的会话ID
//        String conversationId = "test-rag-conversation-001";
//
//        String content = chatClient
//                .prompt()
//                .user("什么是计算机科学的一个分支")
//                .advisors(
//                        // 添加消息记忆顾问 - Spring AI会自动使用已配置的ChatMemoryRepository
//                        MessageChatMemoryAdvisor.builder(chatMemory)
//                                .conversationId(conversationId)
//                                .build(),
//                        // 添加RAG顾问
//                        QuestionAnswerAdvisor.builder(vectorStore)
//                                .searchRequest(
//                                        SearchRequest.builder()
//                                                .topK(5)
//                                                .similarityThreshold(0.5d)
//                                                .build()
//                                )
//                                .build()
//                )
//                .call()
//                .content();
//
//        log.info("content: {}", content);
//
//        // 验证记忆是否保存
//        List<Message> messages = chatMemoryRepository.findByConversationId(conversationId);
//        log.info("对话历史消息数量: {}", messages.size());
//        messages.forEach(msg ->
//                log.debug("消息类型: {}, 内容: {}", msg.getMessageType(), msg.getText()));
//    }
//
//
//    @Test
//    public void testRagWithMemoryMem0() {
//        String conversationId = "test-rag-conversation-001";
//        String userId = "user-alice"; // 假设这是当前用户ID
//
//        // === 1. 从 Mem0 检索长期记忆 ===
//        List<MemoryItem> longTermMemories = mem0aiService.searchMemory(userId, "什么是计算机科学的一个分支", 3, null);
//        String longTermContext = "";
//        if (!longTermMemories.isEmpty()) {
//            longTermContext = "相关长期记忆:\n" +
//                    longTermMemories.stream()
//                            .map(m -> "- " + m.getContent())
//                            .collect(Collectors.joining("\n"));
//        }
//
//        // === 2. 构造增强后的用户问题 ===
//        String originalQuestion = "什么是计算机科学的一个分支";
//        String enhancedQuestion = originalQuestion;
//        if (!longTermContext.isEmpty()) {
//            enhancedQuestion = longTermContext + "\n\n基于以上信息，请回答：" + originalQuestion;
//        }
//
//        // === 3. 调用 LLM（含短期记忆 + RAG）===
//        String content = chatClient
//                .prompt()
//                .user(enhancedQuestion) // ← 关键：这里包含了长期记忆
//                .advisors(
//                        // 短期记忆（对话历史）
//                        MessageChatMemoryAdvisor.builder(chatMemory)
//                                .conversationId(conversationId)
//                                .build(),
//                        // RAG（知识库）
//                        QuestionAnswerAdvisor.builder(vectorStore)
//                                .searchRequest(SearchRequest.builder()
//                                        .topK(5)
//                                        .similarityThreshold(0.5d)
//                                        .build())
//                                .build()
//                )
//                .call()
//                .content();
//
//        log.info("最终回答: {}", content);
//
//        // === 4. （可选）将本次对话存入 Mem0 长期记忆 ===
//        List<com.github.lisuiheng.astra.server.ai.model.dto.Message> messagesToSave = Arrays.asList(
//                createMessage(Role.USER, originalQuestion),
//                createMessage(Role.ASSISTANT, content)
//        );
//        boolean saved = mem0aiService.addMemory(userId, messagesToSave);
//        if (saved) {
//            log.info("已保存对话到 Mem0 长期记忆");
//        } else {
//            log.warn("保存 Mem0 记忆失败");
//        }
//
//        // === 5. 验证短期记忆是否保存 ===
//        List<Message> shortTermMessages = chatMemoryRepository.findByConversationId(conversationId);
//        log.info("短期对话历史消息数量: {}", shortTermMessages.size());
//    }
}