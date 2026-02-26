package com.github.lisuiheng.astra.server.ai.advisor;

import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.MessageType;
import org.springframework.ai.chat.messages.SystemMessage;

import java.util.List;
import java.util.stream.Collectors;

public class SystemSimpleLoggerAdvisor {

    public static String requestWithSystemToString(ChatClientRequest request) {
        // ✅ 关键：使用 getInstructions() 而不是 getMessages()
        List<Message> messages = request.prompt().getInstructions();

        StringBuilder sb = new StringBuilder();

        // 提取 system 消息
        String systemContent = messages.stream()
                .filter(msg -> MessageType.SYSTEM.equals(msg.getMessageType()))
                .map(Message::getText)
                .collect(Collectors.joining("\n")).trim();

        if (!systemContent.isEmpty()) {
            sb.append("【System Prompt】: ").append(systemContent).append("\n\n");
        }

        // 提取 user 消息
        String userContent = messages.stream()
                .filter(msg -> MessageType.USER.equals(msg.getMessageType()))
                .map(Message::getText)
                .collect(Collectors.joining("\n")).trim();

        sb.append("【User Prompt】: ").append(userContent);

        return sb.toString();
    }
}