package com.github.lisuiheng.astra.server.ai.model.dto;

import lombok.Data;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.filter.FilterExpressionTextParser;

@Data
public class MemoryQuery {
    private String userId;
    private String sessionId;
    private String queryText;
    private Integer maxResults = 5;
    private Double similarityThreshold = 0.7;
    private String filterExpression; // ✅ 字符串表达式，如 "userId == '123'"

    public SearchRequest toSearchRequest() {
        var builder = SearchRequest.builder()
                .query(queryText != null ? queryText : "")
                .topK(maxResults != null ? maxResults : 5)
                .similarityThreshold(similarityThreshold != null ? similarityThreshold : 0.7);

        // 如果提供了 filterExpression 字符串，解析并设置
        if (filterExpression != null && !filterExpression.trim().isEmpty()) {
            var parser = new FilterExpressionTextParser();
            var expression = parser.parse(filterExpression);
            builder.filterExpression(expression);
        }

        // 或者：根据 userId/sessionId 自动生成 filter（更安全）
        // 见下方“方案 B”

        return builder.build();
    }
}