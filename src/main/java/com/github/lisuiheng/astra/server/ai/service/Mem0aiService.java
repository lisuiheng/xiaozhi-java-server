package com.github.lisuiheng.astra.server.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.lisuiheng.astra.server.ai.model.dto.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 自建 Mem0 记忆服务（调用本地 memory_service.py v2.0）
 */
@Service
@Slf4j
public class Mem0aiService {

    @Value("${mem0.local.base-url:http://localhost:8000}")
    private String defaultBaseUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    // ==================== 基础CRUD接口 ====================

    /**
     * 添加用户对话记忆
     *
     * @param userId   用户唯一ID
     * @param messages 对话列表，格式：Message 对象列表，包含 role 和 content 属性
     * @return 是否成功
     */
    public boolean addMemory(String userId, List<Message> messages) {
        String url = defaultBaseUrl + "/memory/add";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        
        // 将 Message 对象列表转换为 Map 列表
        List<Map<String, String>> messageMaps = messages.stream()
            .map(message -> {
                Map<String, String> messageMap = new HashMap<>();
                messageMap.put("role", message.getRole().getValue());
                messageMap.put("content", message.getContent());
                return messageMap;
            })
            .collect(Collectors.toList());
        
        request.put("messages", messageMaps);

        // 打印请求内容
        try {
            log.info("准备发送添加记忆请求，user_id: {}, request: {}", userId, objectMapper.writeValueAsString(request));
        } catch (Exception e) {
            log.error("序列化请求对象失败", e);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MemoryResponse> response = restTemplate.postForEntity(
                url, entity, MemoryResponse.class);
            return response.getStatusCode().is2xxSuccessful();
        } catch (Exception e) {
            log.error("添加本地 Mem0 记忆失败, user_id: {}", userId, e);
            return false;
        }
    }

    /**
     * 检索用户相关记忆
     *
     * @param userId 用户ID
     * @param query  查询语句
     * @param topK   返回条数
     * @param threshold 相似度阈值（可选，0.0-1.0）
     * @return 记忆列表
     */
    public List<MemoryItem> searchMemory(String userId, String query, Integer topK, Float threshold) {
        String url = defaultBaseUrl + "/memory/search";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("query", query);

        // 修复：threshold 应该是浮点数，不是字符串
        if (threshold != null) {
            request.put("threshold", threshold);
        }

        if (topK != null && topK > 0) {
            request.put("limit", topK);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MemoryResponse> response = restTemplate.postForEntity(
                    url, entity, MemoryResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MemoryResponse memoryResponse = response.getBody();
                List<Map<String, Object>> results = memoryResponse.getResults();
                if (results == null) return Collections.emptyList();

                return results.stream()
                        .map(this::mapToMemoryItem)
                        .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("检索本地 Mem0 记忆失败, user_id: {}, query: {}", userId, query, e);
        }

        return Collections.emptyList();
    }

    /**
     * 分页获取用户记忆列表
     *
     * @param userId   用户ID
     * @param page     页码（从1开始）
     * @param pageSize 每页大小
     * @param sortBy   排序字段（timestamp, score）
     * @param sortOrder 排序顺序（asc, desc）
     * @return 分页记忆列表
     */
    public MemoryListResponse listMemories(String userId, Integer page, Integer pageSize,
                                          String sortBy, String sortOrder) {
        String url = defaultBaseUrl + "/memory/list";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("page", page != null ? page : 1);
        request.put("page_size", pageSize != null ? pageSize : 20);
        request.put("sort_by", StringUtils.hasText(sortBy) ? sortBy : "timestamp");
        request.put("sort_order", StringUtils.hasText(sortOrder) ? sortOrder : "desc");

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MemoryListResponse> response = restTemplate.postForEntity(
                url, entity, MemoryListResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("获取用户记忆列表失败, user_id: {}, page: {}, pageSize: {}",
                     userId, page, pageSize, e);
        }

        // 返回空响应
        MemoryListResponse emptyResponse = new MemoryListResponse();
        emptyResponse.setItems(Collections.emptyList());
        emptyResponse.setTotal(0);
        emptyResponse.setPage(page != null ? page : 1);
        return emptyResponse;
    }

    /**
     * 清除指定用户的全部长期记忆
     *
     * @param userId 用户唯一ID
     * @return 清除结果，包含删除数量
     */
    public MemoryClearResponse clearMemory(String userId) {
        if (!StringUtils.hasText(userId)) {
            log.warn("clearMemory 被调用时 userId 为空");
            MemoryClearResponse response = new MemoryClearResponse();
            response.setDeletedCount(0);
            response.setUserId(userId);
            response.setSuccess(false);
            response.setMessage("用户ID不能为空");
            return response;
        }

        String url = defaultBaseUrl + "/memory/clear/" + userId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<MemoryClearResponse> response = restTemplate.exchange(
                url,
                HttpMethod.DELETE,
                entity,
                MemoryClearResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MemoryClearResponse clearResponse = response.getBody();
                clearResponse.setSuccess(true);
                log.info("成功清除用户 {} 的 {} 条记忆", userId, clearResponse.getDeletedCount());
                return clearResponse;
            } else {
                log.warn("清除记忆失败，HTTP 状态码: {}", response.getStatusCode());
                MemoryClearResponse errorResponse = new MemoryClearResponse();
                errorResponse.setDeletedCount(0);
                errorResponse.setUserId(userId);
                errorResponse.setSuccess(false);
                errorResponse.setMessage("清除记忆失败，HTTP状态码: " + response.getStatusCode());
                return errorResponse;
            }
        } catch (Exception e) {
            log.error("清除本地 Mem0 记忆失败, user_id: {}", userId, e);
            MemoryClearResponse errorResponse = new MemoryClearResponse();
            errorResponse.setDeletedCount(0);
            errorResponse.setUserId(userId);
            errorResponse.setSuccess(false);
            errorResponse.setMessage("清除记忆失败: " + e.getMessage());
            return errorResponse;
        }
    }

    // ==================== 新增：记忆编辑接口 ====================

    /**
     * 根据ID获取单个记忆
     *
     * @param memoryId 记忆ID
     * @param userId   用户ID（用于权限验证）
     * @return 记忆详情
     */
    public MemoryItem getMemoryById(String memoryId, String userId) {
        String url = UriComponentsBuilder.fromHttpUrl(defaultBaseUrl + "/memory/{memoryId}")
            .queryParam("user_id", userId)
            .buildAndExpand(memoryId)
            .toUriString();

        try {
            ResponseEntity<MemoryItem> response = restTemplate.getForEntity(
                url, MemoryItem.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("根据ID获取记忆失败, memory_id: {}, user_id: {}", memoryId, userId, e);
        }

        return null;
    }

    /**
     * 根据ID更新记忆内容
     *
     * @param memoryId    记忆ID
     * @param userId      用户ID
     * @param newContent  新的记忆内容
     * @param metadata    可选的元数据
     * @return 更新结果
     */
    public MemoryUpdateResponse updateMemory(String memoryId, String userId, 
                                           String newContent, Map<String, Object> metadata) {
        String url = defaultBaseUrl + "/memory/" + memoryId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("new_content", newContent);
        if (metadata != null && !metadata.isEmpty()) {
            request.put("metadata", metadata);
        }

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MemoryUpdateResponse> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                MemoryUpdateResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MemoryUpdateResponse updateResponse = response.getBody();
                updateResponse.setSuccess(true);
                log.info("成功更新记忆, memory_id: {}, user_id: {}", memoryId, userId);
                return updateResponse;
            } else {
                log.warn("更新记忆失败，HTTP 状态码: {}", response.getStatusCode());
                MemoryUpdateResponse errorResponse = new MemoryUpdateResponse();
                errorResponse.setSuccess(false);
                errorResponse.setMemoryId(memoryId);
                errorResponse.setUserId(userId);
                errorResponse.setMessage("更新记忆失败，HTTP状态码: " + response.getStatusCode());
                return errorResponse;
            }
        } catch (Exception e) {
            log.error("更新本地 Mem0 记忆失败, memory_id: {}, user_id: {}", memoryId, userId, e);
            MemoryUpdateResponse errorResponse = new MemoryUpdateResponse();
            errorResponse.setSuccess(false);
            errorResponse.setMemoryId(memoryId);
            errorResponse.setUserId(userId);
            errorResponse.setMessage("更新记忆失败: " + e.getMessage());
            return errorResponse;
        }
    }

    /**
     * 批量更新多个记忆
     *
     * @param userId  用户ID
     * @param updates 更新列表，每个更新包含 memoryId, newContent 和可选的 metadata
     * @return 批量更新结果
     */
    public MemoryBatchUpdateResponse batchUpdateMemories(String userId, 
                                                        List<MemoryUpdateRequest> updates) {
        String url = defaultBaseUrl + "/memory/batch/update";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        
        // 转换更新请求
        List<Map<String, Object>> updatesList = updates.stream()
            .map(update -> {
                Map<String, Object> updateMap = new HashMap<>();
                updateMap.put("memory_id", update.getMemoryId());
                updateMap.put("new_content", update.getNewContent());
                if (update.getMetadata() != null && !update.getMetadata().isEmpty()) {
                    updateMap.put("metadata", update.getMetadata());
                }
                return updateMap;
            })
            .collect(Collectors.toList());
        
        request.put("updates", updatesList);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<MemoryBatchUpdateResponse> response = restTemplate.exchange(
                url,
                HttpMethod.PUT,
                entity,
                MemoryBatchUpdateResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                MemoryBatchUpdateResponse batchResponse = response.getBody();
                log.info("批量更新成功, user_id: {}, 总数: {}, 成功: {}, 失败: {}", 
                        userId, batchResponse.getTotal(), 
                        batchResponse.getSuccessCount(), batchResponse.getFailureCount());
                return batchResponse;
            } else {
                log.warn("批量更新失败，HTTP 状态码: {}", response.getStatusCode());
                MemoryBatchUpdateResponse errorResponse = new MemoryBatchUpdateResponse();
                errorResponse.setTotal(updates.size());
                errorResponse.setSuccessCount(0);
                errorResponse.setFailureCount(updates.size());
                errorResponse.setResults(Collections.emptyList());
                return errorResponse;
            }
        } catch (Exception e) {
            log.error("批量更新本地 Mem0 记忆失败, user_id: {}", userId, e);
            MemoryBatchUpdateResponse errorResponse = new MemoryBatchUpdateResponse();
            errorResponse.setTotal(updates.size());
            errorResponse.setSuccessCount(0);
            errorResponse.setFailureCount(updates.size());
            errorResponse.setResults(Collections.emptyList());
            return errorResponse;
        }
    }

    // ==================== 用户记忆展示接口 ====================

    /**
     * 获取用户记忆仪表板数据
     *
     * @param userId       用户ID
     * @param recentLimit  最近记忆数量限制
     * @param keywordLimit 关键词数量限制
     * @param daysBack     回溯天数
     * @return 用户记忆仪表板数据
     */
    public UserMemoryDashboard getUserMemoryDashboard(String userId, Integer recentLimit,
                                                     Integer keywordLimit, Integer daysBack) {
        String url = UriComponentsBuilder.fromHttpUrl(defaultBaseUrl + "/memory/user/{userId}/dashboard")
            .queryParamIfPresent("recent_limit", Optional.ofNullable(recentLimit))
            .queryParamIfPresent("keyword_limit", Optional.ofNullable(keywordLimit))
            .queryParamIfPresent("days_back", Optional.ofNullable(daysBack))
            .buildAndExpand(userId)
            .toUriString();

        try {
            ResponseEntity<UserMemoryDashboard> response = restTemplate.getForEntity(
                url, UserMemoryDashboard.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("获取用户记忆仪表板失败, user_id: {}", userId, e);
        }

        return null;
    }

    /**
     * 获取用户记忆时间线
     *
     * @param userId     用户ID
     * @param startDate  开始日期（可选）
     * @param endDate    结束日期（可选）
     * @param groupBy    分组方式（day, week, month）
     * @return 时间线数据
     */
    public MemoryTimelineResponse getUserMemoryTimeline(String userId, String startDate,
                                                       String endDate, String groupBy) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(defaultBaseUrl + "/memory/user/{userId}/timeline")
            .queryParamIfPresent("start_date", Optional.ofNullable(startDate))
            .queryParamIfPresent("end_date", Optional.ofNullable(endDate))
            .queryParamIfPresent("group_by", Optional.ofNullable(groupBy));

        String url = builder.buildAndExpand(userId).toUriString();

        try {
            ResponseEntity<MemoryTimelineResponse> response = restTemplate.getForEntity(
                url, MemoryTimelineResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("获取用户记忆时间线失败, user_id: {}", userId, e);
        }

        return null;
    }

    /**
     * 分析用户记忆
     *
     * @param userId        用户ID
     * @param analysisType  分析类型（keywords, topics, clusters, summary）
     * @return 分析结果
     */
    public AnalysisResult analyzeMemories(String userId, String analysisType) {
        String url = defaultBaseUrl + "/memory/analyze";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        Map<String, Object> request = new HashMap<>();
        request.put("user_id", userId);
        request.put("analysis_type", analysisType);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        try {
            ResponseEntity<AnalysisResult> response = restTemplate.postForEntity(
                url, entity, AnalysisResult.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("分析用户记忆失败, user_id: {}, analysis_type: {}", userId, analysisType, e);
        }

        return null;
    }

    // ==================== 可视化接口 ====================

    /**
     * 获取记忆关系图
     *
     * @param userId                用户ID
     * @param limit                 限制数量
     * @param similarityThreshold   相似度阈值
     * @param minClusterSize        最小簇大小
     * @return 记忆关系图数据
     */
    public MemoryGraphResponse getMemoryRelationshipGraph(String userId, Integer limit,
                                                         Double similarityThreshold,
                                                         Integer minClusterSize) {
        UriComponentsBuilder builder = UriComponentsBuilder
            .fromHttpUrl(defaultBaseUrl + "/memory/user/{userId}/graph")
            .queryParamIfPresent("limit", Optional.ofNullable(limit))
            .queryParamIfPresent("similarity_threshold", Optional.ofNullable(similarityThreshold))
            .queryParamIfPresent("min_cluster_size", Optional.ofNullable(minClusterSize));

        String url = builder.buildAndExpand(userId).toUriString();

        try {
            ResponseEntity<MemoryGraphResponse> response = restTemplate.getForEntity(
                url, MemoryGraphResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("获取记忆关系图失败, user_id: {}", userId, e);
        }

        return null;
    }

    // ==================== 工具功能接口 ====================

    /**
     * 健康检查
     *
     * @return 健康检查结果
     */
    public HealthCheckResponse healthCheck() {
        String url = defaultBaseUrl + "/health";

        try {
            ResponseEntity<HealthCheckResponse> response = restTemplate.getForEntity(
                url, HealthCheckResponse.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            } else {
                HealthCheckResponse errorResponse = new HealthCheckResponse();
                errorResponse.setStatus("unhealthy");
                errorResponse.setService("mem0-memory-service");
                errorResponse.setError("HTTP状态码: " + response.getStatusCode());
                return errorResponse;
            }
        } catch (Exception e) {
            log.warn("Mem0 本地服务不可用", e);
            HealthCheckResponse errorResponse = new HealthCheckResponse();
            errorResponse.setStatus("unhealthy");
            errorResponse.setService("mem0-memory-service");
            errorResponse.setError("连接失败: " + e.getMessage());
            return errorResponse;
        }
    }

    // ==================== 辅助方法 ====================

    /**
     * 将API返回的Map转换为MemoryItem
     */
    private MemoryItem mapToMemoryItem(Map<String, Object> data) {
        MemoryItem item = new MemoryItem();
        
        // 优先从"data"字段获取内容，如果不存在则从"memory"字段获取
        String content = data.containsKey("data") ? 
            (String) data.get("data") : 
            (String) data.get("memory");
        item.setContent(content);
        
        if (data.get("score") != null) {
            if (data.get("score") instanceof Number) {
                item.setScore(((Number) data.get("score")).doubleValue());
            }
        }

        // 设置其他字段
        item.setId((String) data.get("id"));
        
        // 优先从"created_at"字段获取时间戳，如果不存在则从"timestamp"字段获取
        String timestamp = data.containsKey("created_at") ?
            (String) data.get("created_at") :
            (String) data.get("timestamp");
        item.setTimestamp(timestamp);

        // 设置metadata
        if (data.containsKey("metadata") && data.get("metadata") instanceof Map) {
            item.setMetadata((Map<String, Object>) data.get("metadata"));
        }

        return item;
    }

    /**
     * 批量添加记忆
     *
     * @param userId    用户ID
     * @param memories  记忆列表（每条记忆为Message对象列表）
     * @return 成功添加的数量
     */
    public int batchAddMemory(String userId, List<List<Message>> memories) {
        int successCount = 0;

        for (List<Message> messagePair : memories) {
            if (addMemory(userId, messagePair)) {
                successCount++;
            }
        }

        return successCount;
    }

    /**
     * 获取用户记忆统计信息
     *
     * @param userId 用户ID
     * @return 统计信息
     */
    public Map<String, Object> getMemoryStatistics(String userId) {
        UserMemoryDashboard dashboard = getUserMemoryDashboard(userId, 0, 0, 30);

        if (dashboard == null || dashboard.getStats() == null) {
            return Collections.emptyMap();
        }

        UserMemoryStats stats = dashboard.getStats();
        Map<String, Object> statistics = new HashMap<>();
        statistics.put("totalMemories", stats.getTotalMemories());
        statistics.put("firstMemoryTime", stats.getFirstMemoryTime());
        statistics.put("lastMemoryTime", stats.getLastMemoryTime());
        statistics.put("dailyAverage", stats.getDailyAverage());
        statistics.put("recentActivity", stats.getRecentActivity());

        return statistics;
    }

    /**
     * 检查用户是否存在记忆
     *
     * @param userId 用户ID
     * @return 是否存在记忆
     */
    public boolean hasMemories(String userId) {
        try {
            MemoryListResponse response = listMemories(userId, 1, 1, null, null);
            return response.getTotal() > 0;
        } catch (Exception e) {
            log.error("检查用户记忆失败, user_id: {}", userId, e);
            return false;
        }
    }

    /**
     * 获取用户的关键词云数据
     *
     * @param userId 用户ID
     * @param limit  关键词数量限制
     * @return 关键词列表
     */
    public List<Map<String, Object>> getUserKeywords(String userId, Integer limit) {
        AnalysisResult analysis = analyzeMemories(userId, "keywords");

        if (analysis == null || analysis.getData() == null) {
            return Collections.emptyList();
        }

        List<Map<String, Object>> keywords = new ArrayList<>();
        for (Object item : analysis.getData()) {
            if (item instanceof Map) {
                Map<String, Object> keywordMap = (Map<String, Object>) item;
                keywords.add(keywordMap);

                if (limit != null && keywords.size() >= limit) {
                    break;
                }
            }
        }

        return keywords;
    }

    /**
     * 编辑用户的某条记忆（便捷方法）
     *
     * @param memoryId    记忆ID
     * @param userId      用户ID
     * @param newContent  新的记忆内容
     * @return 是否成功
     */
    public boolean editMemory(String memoryId, String userId, String newContent) {
        MemoryUpdateResponse response = updateMemory(memoryId, userId, newContent, null);
        return response != null && response.isSuccess();
    }

    /**
     * 根据内容搜索并更新记忆
     *
     * @param userId     用户ID
     * @param oldContent 原记忆内容（用于搜索）
     * @param newContent 新的记忆内容
     * @return 是否成功
     */
    public boolean searchAndUpdateMemory(String userId, String oldContent, String newContent) {
        try {
            // 先搜索相关的记忆
            List<MemoryItem> memories = searchMemory(userId, oldContent, 5, null);
            if (memories.isEmpty()) {
                log.warn("未找到相关记忆，无法更新, user_id: {}, old_content: {}", userId, oldContent);
                return false;
            }

            // 更新第一条匹配的记忆
            MemoryItem targetMemory = memories.get(0);
            return editMemory(targetMemory.getId(), userId, newContent);
        } catch (Exception e) {
            log.error("搜索并更新记忆失败, user_id: {}, old_content: {}", userId, oldContent, e);
            return false;
        }
    }

    /**
     * 获取服务URL
     *
     * @return 服务URL
     */
    public String getServiceUrl() {
        return defaultBaseUrl;
    }
}