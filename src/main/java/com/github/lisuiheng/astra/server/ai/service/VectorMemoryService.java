package com.github.lisuiheng.astra.server.ai.service;

import com.github.lisuiheng.astra.server.ai.model.dto.MemoryRecord;
import com.github.lisuiheng.astra.server.ai.model.dto.MemoryQuery;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
public class VectorMemoryService {

    @Autowired
    @Lazy
    private VectorStore vectorStore;
    
    public void storeMemory(MemoryRecord memory) {
        Map<String, Object> metadata = Map.of(
            "userId", memory.getUserId(),
            "sessionId", memory.getSessionId(),
            "type", memory.getType().name(),
            "importance", memory.getImportance(),
            "createdAt", memory.getCreatedAt().toString(),
            "lastAccessed", LocalDateTime.now().toString()
        );

        Document document = Document.builder()
                .id(memory.getId())
                .text(memory.getContent())
                .metadata(metadata)
                .build();
        
        vectorStore.add(List.of(document));
        log.info("Stored memory for user: {}, type: {}", memory.getUserId(), memory.getType());
    }
    
    public List<MemoryRecord> searchMemories(MemoryQuery query) {
        SearchRequest searchRequest = query.toSearchRequest();
        
        List<Document> results = vectorStore.similaritySearch(searchRequest);
        
        return results.stream()
                .map(this::toMemoryRecord)
                .collect(Collectors.toList());
    }

    public void deleteUserMemories(String userId) {
        // 构建一个“查询所有”的 SearchRequest
        // 注意：不是所有 VectorStore 都支持空查询或全量返回！
        SearchRequest searchRequest = SearchRequest.builder()
                .query("") // 空查询（部分实现可能要求非空，可用一个占位符如 " "）
                .topK(1000) // 限制最大返回数量（根据你的数据规模调整）
                .similarityThreshold(0.0) // 接受所有相似度
                .build();

        List<String> memoryIds = vectorStore
                .similaritySearch(searchRequest)
                .stream()
                .filter(doc -> userId.equals(doc.getMetadata().get("userId")))
                .map(Document::getId)
                .filter(Objects::nonNull) // 防止 null ID
                .toList();

        if (!memoryIds.isEmpty()) {
            vectorStore.delete(memoryIds);
            log.info("Deleted {} memories for user: {}", memoryIds.size(), userId);
        } else {
            log.info("No memories found for user: {}", userId);
        }
    }
    
    public void deleteMemory(String memoryId) {
        vectorStore.delete(List.of(memoryId));
        log.info("Deleted memory: {}", memoryId);
    }
    
    private MemoryRecord toMemoryRecord(Document document) {
        MemoryRecord memory = new MemoryRecord();
        memory.setId(document.getId());
        memory.setContent(document.getText());
        
        Map<String, Object> metadata = document.getMetadata();
        memory.setUserId((String) metadata.get("userId"));
        memory.setSessionId((String) metadata.get("sessionId"));
        memory.setType(MemoryRecord.MemoryType.valueOf((String) metadata.get("type")));
        memory.setImportance((Double) metadata.get("importance"));
        
        return memory;
    }
}