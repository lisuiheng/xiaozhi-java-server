package com.github.lisuiheng.astra.common.sse.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * SSE消息DTO
 */
@Data
public class SseMessageDto implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 消息内容
     */
    private String message;

    /**
     * 用户ID列表
     */
    private List<Long> userIds;
}