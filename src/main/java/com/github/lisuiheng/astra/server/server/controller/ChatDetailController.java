package com.github.lisuiheng.astra.server.server.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.server.server.constant.SpeakerType;
import com.github.lisuiheng.astra.server.server.model.entity.ChatDetail;
import com.github.lisuiheng.astra.server.server.service.ChatDetailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/chat-details")
@RequiredArgsConstructor
public class ChatDetailController {

    private final ChatDetailService chatDetailService;

    /**
     * 创建聊天详情
     */
    @PostMapping
    public R<ChatDetail> createChatDetail(@RequestBody Map<String, Object> chatDetailData) {
        try {
            ChatDetail chatDetail = new ChatDetail();
            // 从chatDetailData中提取字段并设置到ChatDetail对象
            if (chatDetailData.containsKey("callId")) {
                chatDetail.setCallId((String) chatDetailData.get("callId"));
            }
            if (chatDetailData.containsKey("content")) {
                chatDetail.setContent((String) chatDetailData.get("content"));
            }
            if (chatDetailData.containsKey("questionKind") && chatDetailData.get("questionKind") != null) {
                String questionKindValue = (String) chatDetailData.get("questionKind");
                if (questionKindValue != null && !questionKindValue.isEmpty()) {
                    chatDetail.setQuestionKind(SpeakerType.valueOf(questionKindValue));
                }
            }
            if (chatDetailData.containsKey("questionName")) {
                chatDetail.setQuestionName((String) chatDetailData.get("questionName"));
            }
            if (chatDetailData.containsKey("userId")) {
                chatDetail.setUserId((String) chatDetailData.get("userId"));
            }
            if (chatDetailData.containsKey("agentId")) {
                chatDetail.setAgentId((String) chatDetailData.get("agentId"));
            }
            if (chatDetailData.containsKey("deviceId")) {
                chatDetail.setDeviceId((String) chatDetailData.get("deviceId"));
            }
            if (chatDetailData.containsKey("chatKind")) {
                chatDetail.setChatKind((String) chatDetailData.get("chatKind"));
            }
            if (chatDetailData.containsKey("chatId")) {
                chatDetail.setChatId((String) chatDetailData.get("chatId"));
            }
            if (chatDetailData.containsKey("isInterrupted")) {
                chatDetail.setIsInterrupted((Boolean) chatDetailData.get("isInterrupted"));
            }
            if (chatDetailData.containsKey("conversationContent")) {
                chatDetail.setConversationContent((String) chatDetailData.get("conversationContent"));
            }
            if (chatDetailData.containsKey("longitude")) {
                chatDetail.setLongitude(new java.math.BigDecimal(chatDetailData.get("longitude").toString()));
            }
            if (chatDetailData.containsKey("latitude")) {
                chatDetail.setLatitude(new java.math.BigDecimal(chatDetailData.get("latitude").toString()));
            }

            boolean result = chatDetailService.save(chatDetail);
            if (result) {
                return R.ok("创建成功", chatDetail);
            } else {
                return R.fail("创建失败");
            }
        } catch (Exception e) {
            log.error("创建聊天详情失败: ", e);
            return R.fail("创建聊天详情失败: " + e.getMessage());
        }
    }

    /**
     * 更新聊天详情
     */
    @PutMapping("/{chatDetailId}")
    public R<ChatDetail> updateChatDetail(@PathVariable String chatDetailId, @RequestBody Map<String, Object> chatDetailData) {
        try {
            ChatDetail chatDetail = chatDetailService.getById(chatDetailId);
            if (chatDetail == null) {
                return R.fail("聊天详情不存在");
            }

            // 从chatDetailData中提取字段并更新ChatDetail对象
            if (chatDetailData.containsKey("callId")) {
                chatDetail.setCallId((String) chatDetailData.get("callId"));
            }
            if (chatDetailData.containsKey("content")) {
                chatDetail.setContent((String) chatDetailData.get("content"));
            }
            if (chatDetailData.containsKey("questionKind") && chatDetailData.get("questionKind") != null) {
                String questionKindValue = (String) chatDetailData.get("questionKind");
                if (questionKindValue != null && !questionKindValue.isEmpty()) {
                    chatDetail.setQuestionKind(SpeakerType.valueOf(questionKindValue));
                }
            }
            if (chatDetailData.containsKey("questionName")) {
                chatDetail.setQuestionName((String) chatDetailData.get("questionName"));
            }
            if (chatDetailData.containsKey("userId")) {
                chatDetail.setUserId((String) chatDetailData.get("userId"));
            }
            if (chatDetailData.containsKey("agentId")) {
                chatDetail.setAgentId((String) chatDetailData.get("agentId"));
            }
            if (chatDetailData.containsKey("deviceId")) {
                chatDetail.setDeviceId((String) chatDetailData.get("deviceId"));
            }
            if (chatDetailData.containsKey("chatKind")) {
                chatDetail.setChatKind((String) chatDetailData.get("chatKind"));
            }
            if (chatDetailData.containsKey("chatId")) {
                chatDetail.setChatId((String) chatDetailData.get("chatId"));
            }
            if (chatDetailData.containsKey("isInterrupted")) {
                chatDetail.setIsInterrupted((Boolean) chatDetailData.get("isInterrupted"));
            }
            if (chatDetailData.containsKey("conversationContent")) {
                chatDetail.setConversationContent((String) chatDetailData.get("conversationContent"));
            }
            if (chatDetailData.containsKey("longitude")) {
                chatDetail.setLongitude(new java.math.BigDecimal(chatDetailData.get("longitude").toString()));
            }
            if (chatDetailData.containsKey("latitude")) {
                chatDetail.setLatitude(new java.math.BigDecimal(chatDetailData.get("latitude").toString()));
            }

            boolean result = chatDetailService.updateById(chatDetail);
            if (result) {
                return R.ok("更新成功", chatDetail);
            } else {
                return R.fail("更新失败");
            }
        } catch (Exception e) {
            log.error("更新聊天详情失败: ", e);
            return R.fail("更新聊天详情失败: " + e.getMessage());
        }
    }

    /**
     * 删除聊天详情
     */
    @DeleteMapping("/{chatDetailId}")
    public R<Boolean> deleteChatDetail(@PathVariable String chatDetailId) {
        try {
            boolean result = chatDetailService.removeById(chatDetailId);
            return result ? R.ok("删除成功", true) : R.fail("删除失败", false);
        } catch (Exception e) {
            log.error("删除聊天详情失败: ", e);
            return R.fail("删除聊天详情失败: " + e.getMessage(), false);
        }
    }

    /**
     * 批量删除聊天详情
     */
    @DeleteMapping("/batch")
    public R<Boolean> batchDeleteChatDetails(@RequestBody List<String> chatDetailIds) {
        try {
            boolean result = chatDetailService.removeBatchByIds(chatDetailIds);
            return result ? R.ok("批量删除成功", true) : R.fail("批量删除失败", false);
        } catch (Exception e) {
            log.error("批量删除聊天详情失败: ", e);
            return R.fail("批量删除聊天详情失败: " + e.getMessage(), false);
        }
    }

    /**
     * 获取聊天详情详情
     */
    @GetMapping("/{chatDetailId}")
    public R<ChatDetail> getChatDetailDetail(@PathVariable String chatDetailId) {
        try {
            ChatDetail chatDetail = chatDetailService.getById(chatDetailId);
            if (chatDetail != null) {
                return R.ok("查询成功", chatDetail);
            } else {
                return R.fail("聊天详情不存在");
            }
        } catch (Exception e) {
            log.error("获取聊天详情详情失败: ", e);
            return R.fail("获取聊天详情详情失败: " + e.getMessage());
        }
    }

    /**
     * 分页查询聊天详情
     */
    @GetMapping
    public TableDataInfo<ChatDetail> getChatDetailPage(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String callId,
            @RequestParam(required = false) SpeakerType questionKind,
            @RequestParam(required = false) String userId,
            @RequestParam(required = false) String agentId,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String chatKind) {

        LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(callId != null, ChatDetail::getCallId, callId)
                .eq(questionKind != null , ChatDetail::getQuestionKind, questionKind)
                .eq(userId != null, ChatDetail::getUserId, userId)
                .eq(agentId != null, ChatDetail::getAgentId, agentId)
                .eq(deviceId != null, ChatDetail::getDeviceId, deviceId)
                .eq(chatKind != null, ChatDetail::getChatKind, chatKind)
                .orderByDesc(ChatDetail::getChatTime);

        IPage<ChatDetail> page = chatDetailService.page(new Page<>(pageNum, pageSize), queryWrapper);
        return TableDataInfo.build(page);
    }

    /**
     * 搜索聊天详情
     */
    @GetMapping("/search")
    public R<List<ChatDetail>> searchChatDetails(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String chatKind) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            if (keyword != null) {
                queryWrapper.and(wrapper -> wrapper
                        .like(ChatDetail::getContent, keyword)
                        .or()
                        .like(ChatDetail::getCallId, keyword)
                        .or()
                        .like(ChatDetail::getQuestionName, keyword));
            }
            if (chatKind != null) {
                queryWrapper.eq(ChatDetail::getChatKind, chatKind);
            }

            List<ChatDetail> chatDetails = chatDetailService.list(queryWrapper);
            return R.ok("搜索成功", chatDetails);
        } catch (Exception e) {
            log.error("搜索聊天详情失败: ", e);
            return R.fail("搜索聊天详情失败: " + e.getMessage());
        }
    }

    /**
     * 根据会话ID获取聊天记录
     */
    @GetMapping("/session/{callId}")
    public R<List<ChatDetail>> getChatDetailsBySession(@PathVariable String callId) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatDetail::getCallId, callId)
                    .orderByAsc(ChatDetail::getChatTime);

            List<ChatDetail> chatDetails = chatDetailService.list(queryWrapper);
            return R.ok("查询成功", chatDetails);
        } catch (Exception e) {
            log.error("根据会话ID获取聊天记录失败: ", e);
            return R.fail("根据会话ID获取聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据智能体ID获取聊天记录
     */
    @GetMapping("/agent/{agentId}")
    public R<List<ChatDetail>> getChatDetailsByAgent(@PathVariable String agentId) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatDetail::getAgentId, agentId)
                    .orderByAsc(ChatDetail::getChatTime);

            List<ChatDetail> chatDetails = chatDetailService.list(queryWrapper);
            return R.ok("查询成功", chatDetails);
        } catch (Exception e) {
            log.error("根据智能体ID获取聊天记录失败: ", e);
            return R.fail("根据智能体ID获取聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据用户ID获取聊天记录
     */
    @GetMapping("/user/{userId}")
    public R<List<ChatDetail>> getChatDetailsByUser(@PathVariable String userId) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatDetail::getUserId, userId)
                    .orderByAsc(ChatDetail::getChatTime);

            List<ChatDetail> chatDetails = chatDetailService.list(queryWrapper);
            return R.ok("查询成功", chatDetails);
        } catch (Exception e) {
            log.error("根据用户ID获取聊天记录失败: ", e);
            return R.fail("根据用户ID获取聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 根据设备ID获取聊天记录
     */
    @GetMapping("/device/{deviceId}")
    public R<List<ChatDetail>> getChatDetailsByDevice(@PathVariable String deviceId) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatDetail::getDeviceId, deviceId)
                    .orderByAsc(ChatDetail::getChatTime);

            List<ChatDetail> chatDetails = chatDetailService.list(queryWrapper);
            return R.ok("查询成功", chatDetails);
        } catch (Exception e) {
            log.error("根据设备ID获取聊天记录失败: ", e);
            return R.fail("根据设备ID获取聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 获取总体聊天统计
     */
    @GetMapping("/stats/total")
    public R<Object> getTotalChatStats() {
        try {
            // 这里应该实现总体聊天统计逻辑
            // 为了示例，我们返回一个统计对象
            Map<String, Object> stats = java.util.Map.of(
                "totalChats", chatDetailService.count(),
                "totalUsers", chatDetailService.count(new LambdaQueryWrapper<ChatDetail>().isNotNull(ChatDetail::getUserId).groupBy(ChatDetail::getUserId)),
                "totalAgents", chatDetailService.count(new LambdaQueryWrapper<ChatDetail>().isNotNull(ChatDetail::getAgentId).groupBy(ChatDetail::getAgentId))
            );
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取总体聊天统计失败: ", e);
            return R.fail("获取总体聊天统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取各类型聊天统计
     */
    @GetMapping("/stats/type")
    public R<List<Object>> getChatTypeStats() {
        try {
            // 这里应该实现各类型聊天统计逻辑
            // 为了示例，我们返回一个统计列表
            List<Object> stats = java.util.List.of();
            return R.ok("查询成功", stats);
        } catch (Exception e) {
            log.error("获取各类型聊天统计失败: ", e);
            return R.fail("获取各类型聊天统计失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近聊天记录
     */
    @GetMapping("/recent")
    public R<List<ChatDetail>> getRecentChats(@RequestParam(defaultValue = "10") Integer limit) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.orderByDesc(ChatDetail::getChatTime)
                    .last("LIMIT " + limit);

            List<ChatDetail> chatDetails = chatDetailService.list(queryWrapper);
            return R.ok("查询成功", chatDetails);
        } catch (Exception e) {
            log.error("获取最近聊天记录失败: ", e);
            return R.fail("获取最近聊天记录失败: " + e.getMessage());
        }
    }

    /**
     * 清空指定会话的聊天记录
     */
    @DeleteMapping("/session/{callId}/clear")
    public R<Boolean> clearSessionChats(@PathVariable String callId) {
        try {
            LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(ChatDetail::getCallId, callId);

            boolean result = chatDetailService.remove(queryWrapper);
            return result ? R.ok("清空成功", true) : R.fail("清空失败", false);
        } catch (Exception e) {
            log.error("清空指定会话的聊天记录失败: ", e);
            return R.fail("清空指定会话的聊天记录失败: " + e.getMessage(), false);
        }
    }
}