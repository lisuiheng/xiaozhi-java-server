package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

import java.util.List;


/**
 * 添加记忆请求
 */
@Data
public class AddMemoryRequest {
    private String userId;
    private List<Message> messages;
}