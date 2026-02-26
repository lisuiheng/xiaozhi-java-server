package com.github.lisuiheng.astra.server.server.model.dto;

import lombok.Data;

/**
 * 消息DTO
 */
@Data
public class MessageDTO {
    private String role; // system/user/assistant
    private String content;
    
    public MessageDTO() {
    }
    
    public MessageDTO(String role, String content) {
        this.role = role;
        this.content = content;
    }
}