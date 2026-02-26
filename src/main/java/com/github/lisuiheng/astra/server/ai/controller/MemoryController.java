package com.github.lisuiheng.astra.server.ai.controller;

import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.server.ai.model.dto.*;
import com.github.lisuiheng.astra.server.ai.service.Mem0aiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Mem0 记忆管理 Controller
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/memory")
@RequiredArgsConstructor
public class MemoryController {

    private final Mem0aiService mem0aiService;

    // ==================== 基础CRUD接口 ====================

    /**
     * 添加用户记忆
     */
    @PostMapping("/add")
    public R<Boolean> addMemory(@RequestBody AddMemoryRequest request) {
        try {
            boolean result = mem0aiService.addMemory(request.getUserId(), request.getMessages());
            return result ? R.ok("添加记忆成功", true) : R.fail("添加记忆失败", false);
        } catch (Exception e) {
            log.error("添加用户记忆失败: ", e);
            return R.fail("添加记忆失败: " + e.getMessage(), false);
        }
    }

    /**
     * 搜索用户记忆
     */
    @PostMapping("/search")
    public R<List<MemoryItem>> searchMemory(@RequestBody SearchMemoryRequest request) {
        try {
            List<MemoryItem> items = mem0aiService.searchMemory(
                request.getUserId(),
                request.getQuery(),
                request.getTopK(),
            null
            );
            
            // 为每个 MemoryItem 填充 userId 字段
            if (items != null) {
                for (MemoryItem item : items) {
                    if (item.getUserId() == null) {
                        item.setUserId(request.getUserId());
                    }
                }
            }
            
            return R.ok("搜索成功", items);
        } catch (Exception e) {
            log.error("搜索用户记忆失败: ", e);
            return R.fail("搜索记忆失败: " + e.getMessage());
        }
    }

    /**
     * 分页获取用户记忆列表
     */
    @PostMapping("/list")
    public TableDataInfo<MemoryItem> listMemories(@RequestBody ListMemoryRequest request) {
        try {
            MemoryListResponse response = mem0aiService.listMemories(
                request.getUserId(),
                request.getPage(),
                request.getPageSize(),
                request.getSortBy(),
                request.getSortOrder()
            );

            // 为每个 MemoryItem 填充 userId 字段
            if (response.getItems() != null) {
                for (MemoryItem item : response.getItems()) {
                    if (item.getUserId() == null) {
                        item.setUserId(request.getUserId());
                    }
                }
            }
            
            // 转换响应格式
            TableDataInfo<MemoryItem> tableDataInfo = new TableDataInfo<>();
            tableDataInfo.setRows(response.getItems());
            tableDataInfo.setTotal(response.getTotal());

            return tableDataInfo;
        } catch (Exception e) {
            log.error("获取记忆列表失败: ", e);
            TableDataInfo<MemoryItem> errorTable = new TableDataInfo<>();
            errorTable.setRows(List.of());
            errorTable.setTotal(0);
            return errorTable;
        }
    }

    /**
     * 清除用户记忆
     */
    @DeleteMapping("/clear/{userId}")
    public R<MemoryClearResponse> clearMemory(@PathVariable String userId) {
        try {
            MemoryClearResponse response = mem0aiService.clearMemory(userId);
            return R.ok("清除记忆请求已处理", response);
        } catch (Exception e) {
            log.error("清除用户记忆失败: ", e);
            return R.fail("清除记忆失败: " + e.getMessage());
        }
    }

    /**
     * 批量清除记忆
     */
    @DeleteMapping("/clear/batch")
    public R<Map<String, Object>> batchClearMemory(@RequestBody List<String> userIds) {
        try {
            Map<String, Object> result = new HashMap<>();
            int successCount = 0;
            int failCount = 0;

            for (String userId : userIds) {
                MemoryClearResponse response = mem0aiService.clearMemory(userId);
                if (response.getSuccess() != null && response.getSuccess()) {
                    successCount++;
                } else {
                    failCount++;
                }
            }

            result.put("successCount", successCount);
            result.put("failCount", failCount);
            result.put("total", userIds.size());

            return R.ok("批量清除记忆完成", result);
        } catch (Exception e) {
            log.error("批量清除记忆失败: ", e);
            return R.fail("批量清除记忆失败: " + e.getMessage());
        }
    }

    // ==================== 新增：记忆编辑接口 ====================

    /**
     * 根据ID获取单个记忆
     */
    @GetMapping("/{memoryId}")
    public R<MemoryItem> getMemoryById(
            @PathVariable String memoryId,
            @RequestParam String userId) {
        try {
            MemoryItem memory = mem0aiService.getMemoryById(memoryId, userId);

            if (memory == null) {
                return R.fail("记忆不存在或无权访问");
            }

            return R.ok("获取记忆成功", memory);
        } catch (Exception e) {
            log.error("获取记忆详情失败, memory_id: {}, user_id: {}", memoryId, userId, e);
            return R.fail("获取记忆失败: " + e.getMessage());
        }
    }

    /**
     * 根据ID更新记忆
     */
    @PutMapping("/{memoryId}")
    public R<MemoryUpdateResponse> updateMemory(
            @PathVariable String memoryId,
            @RequestBody UpdateMemoryRequest request) {
        try {
            // 验证请求参数
            if (!memoryId.equals(request.getMemoryId())) {
                return R.fail("URL中的memoryId与请求体中的memoryId不一致");
            }

            MemoryUpdateResponse response = mem0aiService.updateMemory(
                memoryId,
                request.getUserId(),
                request.getNewContent(),
                request.getMetadata()
            );

            if (response == null) {
                return R.fail("更新记忆失败");
            }

            if (response.isSuccess()) {
                return R.ok("更新记忆成功", response);
            } else {
                return R.fail(response.getMessage(), response);
            }
        } catch (Exception e) {
            log.error("更新记忆失败, memory_id: {}, user_id: {}", memoryId, request.getUserId(), e);
            return R.fail("更新记忆失败: " + e.getMessage());
        }
    }

    /**
     * 批量更新记忆
     */
    @PutMapping("/batch/update")
    public R<MemoryBatchUpdateResponse> batchUpdateMemories(
            @RequestBody MemoryBatchUpdateRequest request) {
        try {
            MemoryBatchUpdateResponse response = mem0aiService.batchUpdateMemories(
                request.getUserId(),
                request.getUpdates()
            );

            if (response == null) {
                return R.fail("批量更新失败");
            }

            return R.ok("批量更新完成", response);
        } catch (Exception e) {
            log.error("批量更新记忆失败, user_id: {}", request.getUserId(), e);
            return R.fail("批量更新失败: " + e.getMessage());
        }
    }

    /**
     * 根据内容搜索并更新记忆（便捷接口）
     */
    @PutMapping("/search-and-update")
    public R<Map<String, Object>> searchAndUpdateMemory(
            @RequestBody SearchAndUpdateRequest request) {
        try {
            boolean success = mem0aiService.searchAndUpdateMemory(
                request.getUserId(),
                request.getOldContent(),
                request.getNewContent()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("userId", request.getUserId());
            result.put("message", success ? "搜索并更新成功" : "搜索并更新失败，未找到匹配的记忆");

            return R.ok(success ? "操作成功" : "操作失败", result);
        } catch (Exception e) {
            log.error("搜索并更新记忆失败, user_id: {}, old_content: {}",
                     request.getUserId(), request.getOldContent(), e);
            return R.fail("操作失败: " + e.getMessage());
        }
    }

    // ==================== 用户记忆展示接口 ====================

    /**
     * 获取用户记忆仪表板
     */
    @GetMapping("/user/{userId}/dashboard")
    public R<UserMemoryDashboard> getUserMemoryDashboard(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "10") Integer recentLimit,
            @RequestParam(required = false, defaultValue = "10") Integer keywordLimit,
            @RequestParam(required = false, defaultValue = "30") Integer daysBack) {

        try {
            UserMemoryDashboard dashboard = mem0aiService.getUserMemoryDashboard(
                userId, recentLimit, keywordLimit, daysBack
            );

            if (dashboard == null) {
                return R.fail("获取记忆仪表板失败，用户可能不存在或无记忆数据");
            }

            return R.ok("获取记忆仪表板成功", dashboard);
        } catch (Exception e) {
            log.error("获取用户记忆仪表板失败: ", e);
            return R.fail("获取记忆仪表板失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户记忆时间线
     */
    @GetMapping("/user/{userId}/timeline")
    public R<MemoryTimelineResponse> getUserMemoryTimeline(
            @PathVariable String userId,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "day") String groupBy) {

        try {
            MemoryTimelineResponse timeline = mem0aiService.getUserMemoryTimeline(
                userId, startDate, endDate, groupBy
            );

            if (timeline == null) {
                return R.fail("获取记忆时间线失败，用户可能不存在或无记忆数据");
            }

            return R.ok("获取记忆时间线成功", timeline);
        } catch (Exception e) {
            log.error("获取用户记忆时间线失败: ", e);
            return R.fail("获取记忆时间线失败: " + e.getMessage());
        }
    }

    /**
     * 分析用户记忆
     */
    @PostMapping("/analyze")
    public R<AnalysisResult> analyzeMemories(@RequestBody MemoryAnalysisRequest request) {
        try {
            AnalysisResult result = mem0aiService.analyzeMemories(
                request.getUserId(),
                request.getAnalysisType()
            );

            if (result == null) {
                return R.fail("分析记忆失败，用户可能不存在或无记忆数据");
            }

            return R.ok("分析记忆成功", result);
        } catch (Exception e) {
            log.error("分析用户记忆失败: ", e);
            return R.fail("分析记忆失败: " + e.getMessage());
        }
    }

    /**
     * 获取记忆关系图
     */
    @GetMapping("/user/{userId}/graph")
    public R<MemoryGraphResponse> getMemoryRelationshipGraph(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "30") Integer limit,
            @RequestParam(required = false, defaultValue = "0.5") Double similarityThreshold,
            @RequestParam(required = false, defaultValue = "2") Integer minClusterSize) {

        try {
            MemoryGraphResponse graph = mem0aiService.getMemoryRelationshipGraph(
                userId, limit, similarityThreshold, minClusterSize
            );

            if (graph == null) {
                return R.fail("获取记忆关系图失败，用户可能不存在或无记忆数据");
            }

            return R.ok("获取记忆关系图成功", graph);
        } catch (Exception e) {
            log.error("获取记忆关系图失败: ", e);
            return R.fail("获取记忆关系图失败: " + e.getMessage());
        }
    }

    // ==================== 工具功能接口 ====================

    /**
     * 健康检查
     */
    @GetMapping("/health")
    public R<HealthCheckResponse> healthCheck() {
        try {
            HealthCheckResponse health = mem0aiService.healthCheck();
            return R.ok("健康检查成功", health);
        } catch (Exception e) {
            log.error("健康检查失败: ", e);
            return R.fail("健康检查失败: " + e.getMessage());
        }
    }

    /**
     * 获取用户记忆统计
     */
    @GetMapping("/user/{userId}/statistics")
    public R<Map<String, Object>> getMemoryStatistics(@PathVariable String userId) {
        try {
            Map<String, Object> statistics = mem0aiService.getMemoryStatistics(userId);
            return R.ok("获取记忆统计成功", statistics);
        } catch (Exception e) {
            log.error("获取用户记忆统计失败: ", e);
            return R.fail("获取记忆统计失败: " + e.getMessage());
        }
    }

    /**
     * 检查用户是否存在记忆
     */
    @GetMapping("/user/{userId}/exists")
    public R<Boolean> hasMemories(@PathVariable String userId) {
        try {
            boolean hasMemories = mem0aiService.hasMemories(userId);
            return R.ok("检查成功", hasMemories);
        } catch (Exception e) {
            log.error("检查用户记忆存在性失败: ", e);
            return R.fail("检查失败: " + e.getMessage(), false);
        }
    }

    /**
     * 获取用户关键词
     */
    @GetMapping("/user/{userId}/keywords")
    public R<List<Map<String, Object>>> getUserKeywords(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "20") Integer limit) {

        try {
            List<Map<String, Object>> keywords = mem0aiService.getUserKeywords(userId, limit);
            return R.ok("获取关键词成功", keywords);
        } catch (Exception e) {
            log.error("获取用户关键词失败: ", e);
            return R.fail("获取关键词失败: " + e.getMessage());
        }
    }

    /**
     * 快速查询接口（简化搜索）
     */
    @GetMapping("/quick-search")
    public R<List<MemoryItem>> quickSearch(
            @RequestParam String userId,
            @RequestParam String query,
            @RequestParam(required = false, defaultValue = "5") Integer topK) {

        try {
            List<MemoryItem> items = mem0aiService.searchMemory(userId, query, topK, null);
            return R.ok("快速搜索成功", items);
        } catch (Exception e) {
            log.error("快速搜索失败: ", e);
            return R.fail("快速搜索失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近记忆
     */
    @GetMapping("/user/{userId}/recent")
    public R<List<MemoryItem>> getRecentMemories(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "10") Integer limit) {

        try {
            // 通过分页接口获取最近的记忆
            MemoryListResponse response = mem0aiService.listMemories(
                userId, 1, limit, "timestamp", "desc"
            );

            return R.ok("获取最近记忆成功", response.getItems());
        } catch (Exception e) {
            log.error("获取最近记忆失败: ", e);
            return R.fail("获取最近记忆失败: " + e.getMessage());
        }
    }

    /**
     * 测试记忆服务连接
     */
    @GetMapping("/test-connection")
    public R<Map<String, Object>> testConnection() {
        try {
            HealthCheckResponse health = mem0aiService.healthCheck();

            Map<String, Object> result = new HashMap<>();
            result.put("health", health);
            result.put("serviceUrl", mem0aiService.getServiceUrl());
            result.put("timestamp", System.currentTimeMillis());
            result.put("connected", "healthy".equals(health.getStatus()));

            return R.ok("连接测试完成", result);
        } catch (Exception e) {
            log.error("测试连接失败: ", e);
            Map<String, Object> errorResult = new HashMap<>();
            errorResult.put("connected", false);
            errorResult.put("error", e.getMessage());
            errorResult.put("timestamp", System.currentTimeMillis());
            return R.fail("连接测试失败", errorResult);
        }
    }

    /**
     * 便捷编辑记忆接口
     */
    @PutMapping("/edit/{memoryId}")
    public R<Map<String, Object>> editMemory(
            @PathVariable String memoryId,
            @RequestParam String userId,
            @RequestParam String newContent) {
        try {
            boolean success = mem0aiService.editMemory(memoryId, userId, newContent);

            Map<String, Object> result = new HashMap<>();
            result.put("success", success);
            result.put("memoryId", memoryId);
            result.put("userId", userId);
            result.put("message", success ? "编辑成功" : "编辑失败");

            return R.ok(success ? "编辑成功" : "编辑失败", result);
        } catch (Exception e) {
            log.error("编辑记忆失败, memory_id: {}, user_id: {}", memoryId, userId, e);
            return R.fail("编辑失败: " + e.getMessage());
        }
    }

    /**
     * 删除单个记忆（需要实现对应服务方法）
     */
    @DeleteMapping("/{memoryId}")
    public R<Map<String, Object>> deleteMemory(
            @PathVariable String memoryId,
            @RequestParam String userId) {
        try {
            // 这里可以实现删除单个记忆的逻辑
            // 注意：当前服务层没有直接删除单个记忆的方法
            // 可以先获取记忆，确认权限，然后实现删除逻辑

            Map<String, Object> result = new HashMap<>();
            result.put("memoryId", memoryId);
            result.put("userId", userId);
            result.put("message", "删除单个记忆功能待实现");
            result.put("note", "可以通过清除用户全部记忆或使用批量删除来实现");

            return R.ok("删除功能待实现", result);
        } catch (Exception e) {
            log.error("删除记忆失败, memory_id: {}, user_id: {}", memoryId, userId, e);
            return R.fail("删除失败: " + e.getMessage());
        }
    }
}