package com.github.lisuiheng.astra.server.ai.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

/**
 * 流式响应（带TTS）
 */
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class StreamWithTTSResponse {
    
    public enum ResponseType {
        TEXT,      // 文本响应
        AUDIO,     // 音频响应
        META,      // 元数据
        COMPLETE,  // 完成标志
        ERROR      // 错误信息
    }
    
    private ResponseType type;
    private String text;
    private byte[] audio;
    private String audioFormat;
    private Long timestamp;
    private Integer sequence;
    private Boolean isFinal;
    private String error;
    
    // 静态工厂方法
    public static StreamWithTTSResponse textChunk(String text) {
        return StreamWithTTSResponse.builder()
                .type(ResponseType.TEXT)
                .text(text)
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static StreamWithTTSResponse audioChunk(byte[] audio) {
        return StreamWithTTSResponse.builder()
                .type(ResponseType.AUDIO)
                .audio(audio)
                .audioFormat("mp3")
                .timestamp(System.currentTimeMillis())
                .build();
    }
    
    public static StreamWithTTSResponse complete() {
        return StreamWithTTSResponse.builder()
                .type(ResponseType.COMPLETE)
                .timestamp(System.currentTimeMillis())
                .isFinal(true)
                .build();
    }
    
    public static StreamWithTTSResponse error(String error) {
        return StreamWithTTSResponse.builder()
                .type(ResponseType.ERROR)
                .error(error)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}