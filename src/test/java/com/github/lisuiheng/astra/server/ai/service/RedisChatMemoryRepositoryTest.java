package com.github.lisuiheng.astra.server.ai.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.AstraServerApplication;
import com.github.lisuiheng.astra.server.ai.mapper.RedisChatMemoryRepository;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest(classes = AstraServerApplication.class)
public class RedisChatMemoryRepositoryTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisChatMemoryRepository redisChatMemoryRepository;

    @Test
    void testFindByConversationId_Success() throws JsonProcessingException {
        // Arrange
        String conversationId = "conv1";

        Message userMessage = UserMessage.builder()
                .text("Hello")
                .metadata(Map.of("timestamp", Instant.now().toString()))
                .build();

        Message assistantMessage = new AssistantMessage(
                "Hi there",
                Map.of("timestamp", Instant.now().toString())
        );


        redisChatMemoryRepository.saveAll(conversationId, Lists.newArrayList(userMessage, assistantMessage));

        // Act
        List<Message> messages = redisChatMemoryRepository.findByConversationId(conversationId);

        // Assert
        assertThat(messages).hasSize(2);
        assertThat(messages.get(0)).isInstanceOf(UserMessage.class);
        assertThat(messages.get(1)).isInstanceOf(AssistantMessage.class);
        assertThat(messages.get(0).getText()).isEqualTo("Hello");
        assertThat(messages.get(1).getText()).isEqualTo("Hi there");
    }
}
