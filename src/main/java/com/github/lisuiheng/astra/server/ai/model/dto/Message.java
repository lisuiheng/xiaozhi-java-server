package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;

/**
 * 消息实体
 */
@Data
public class Message {
    private Role role;      // 角色: user, assistant, system 等
    private String content;   // 消息内容
}