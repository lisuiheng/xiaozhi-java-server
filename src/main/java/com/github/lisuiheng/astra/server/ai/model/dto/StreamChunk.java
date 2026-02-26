package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StreamChunk {
    private String type; // chunk, complete, error
    private String content;
    private String originalContent;
    private String error;

    // 添加清洗方法
    public String getCleanedContent() {
        if (content == null) {
            return null;
        }

        return content
                .replaceAll("\\n+", " ")
                .replaceAll("\\s+", " ")
                .replaceAll("[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}]", "")
                .trim();
    }

    // 检查是否有需要清洗的内容
    public boolean needsCleaning() {
        if (content == null) return false;
        return content.contains("\n") ||
                content.matches(".*\\s{2,}.*") ||
                content.matches(".*[\\p{Cc}\\p{Cf}\\p{Co}\\p{Cn}].*");
    }
}