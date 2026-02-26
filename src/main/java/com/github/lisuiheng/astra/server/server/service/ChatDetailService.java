package com.github.lisuiheng.astra.server.server.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.lisuiheng.astra.server.server.constant.SpeakerType;
import com.github.lisuiheng.astra.server.server.model.dto.ChatDetailDTO;
import com.github.lisuiheng.astra.server.server.mapper.ChatDetailMapper;
import com.github.lisuiheng.astra.server.server.model.entity.ChatDetail;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ChatDetailService extends ServiceImpl<ChatDetailMapper, ChatDetail>  {
    
    @Autowired
    private DeviceInfoService deviceInfoService;





    /**
     * 异步保存聊天记录
     * 使用 @Async 注解实现异步调用
     */
    @Async
    public CompletableFuture<ChatDetail> saveChatDetailAsync(ChatDetailDTO chatDetailDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                ChatDetail saved = saveChatDetail(chatDetailDTO);
                log.debug("Chat detail saved asynchronously: id={}, callId={}, speaker={}",
                        saved.getId(), saved.getCallId(), saved.getQuestionName());
                return saved;
            } catch (Exception e) {
                log.error("Failed to save chat detail asynchronously: {}", chatDetailDTO, e);
                throw new RuntimeException("Async save failed", e);
            }
        });
    }

    /**
     * 异步保存聊天记录（无返回值版本）
     */
    @Async
    public void saveChatDetailAsyncWithoutResult(ChatDetailDTO chatDetailDTO) {
        try {
            ChatDetail saved = saveChatDetail(chatDetailDTO);
            log.debug("Chat detail saved asynchronously (no result): id={}, callId={}",
                    saved.getId(), saved.getCallId());
        } catch (Exception e) {
            log.error("Failed to save chat detail asynchronously: {}", chatDetailDTO, e);
        }
    }

    /**
     * 批量异步保存聊天记录
     */
    @Async
    public CompletableFuture<List<ChatDetail>> batchSaveChatDetailsAsync(List<ChatDetailDTO> chatDetailDTOs) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                List<ChatDetail> saved = batchSaveChatDetails(chatDetailDTOs);
                log.debug("Batch saved {} chat details asynchronously", saved.size());
                return saved;
            } catch (Exception e) {
                log.error("Failed to batch save chat details asynchronously", e);
                throw new RuntimeException("Async batch save failed", e);
            }
        });
    }
    
    @Transactional
    public ChatDetail saveChatDetail(ChatDetailDTO chatDetailDTO) {
        try {
            ChatDetail chatDetail = new ChatDetail();

            // 复制基础字段
            BeanUtils.copyProperties(chatDetailDTO, chatDetail);

            // 设置必填字段
            if (chatDetailDTO.getId() == null) {
                chatDetail.setId(UUID.randomUUID().toString());
            }

            if (chatDetailDTO.getChatTime() == null) {
                chatDetail.setChatTime(LocalDateTime.now());
            }

            // 保存到数据库
            boolean success = this.save(chatDetail);

            if (success) {
                log.debug("Chat detail saved successfully: id={}, callId={}, speaker={}",
                        chatDetail.getId(), chatDetail.getCallId(), chatDetail.getQuestionName());
                return chatDetail;
            }

            log.error("Failed to save chat detail: {}", chatDetailDTO);
            throw new RuntimeException("Failed to save chat detail");

        } catch (Exception e) {
            log.error("Error saving chat detail: {}", chatDetailDTO, e);
            throw new RuntimeException("Failed to save chat detail", e);
        }
    }

    @Transactional
    public List<ChatDetail> batchSaveChatDetails(List<ChatDetailDTO> chatDetailDTOs) {
        List<ChatDetail> chatDetails = chatDetailDTOs.stream()
                .map(dto -> {
                    ChatDetail chatDetail = new ChatDetail();
                    BeanUtils.copyProperties(dto, chatDetail);

                    if (dto.getId() == null) {
                        chatDetail.setId(UUID.randomUUID().toString());
                    }

                    if (dto.getChatTime() == null) {
                        chatDetail.setChatTime(LocalDateTime.now());
                    }

                    return chatDetail;
                })
                .collect(Collectors.toList());

        boolean success = this.saveBatch(chatDetails);

        if (success) {
            log.info("Batch saved {} chat details", chatDetails.size());
            return chatDetails;
        }

        throw new RuntimeException("Failed to batch save chat details");
    }

    
    public List<ChatDetail> getChatDetailsByCallId(String callId) {
        LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetail::getCallId, callId)
                .orderByAsc(ChatDetail::getChatTime);

        return this.list(queryWrapper);
    }

    
    public List<ChatDetail> getChatDetailsByUserId(String userId, int page, int size) {
        LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetail::getUserId, userId)
                .orderByDesc(ChatDetail::getChatTime);

        IPage<ChatDetail> pageResult = new Page<>(page, size);
        IPage<ChatDetail> result = this.page(pageResult, queryWrapper);

        return result.getRecords();
    }

    
    public List<ChatDetail> getChatDetailsByDeviceAndTimeRange(String deviceId,
                                                               LocalDateTime startTime,
                                                               LocalDateTime endTime) {
        LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetail::getDeviceId, deviceId)
                .ge(ChatDetail::getChatTime, startTime)
                .le(ChatDetail::getChatTime, endTime)
                .orderByDesc(ChatDetail::getChatTime);

        return this.list(queryWrapper);
    }

    
    public Long countChatDetailsByUserId(String userId) {
        LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetail::getUserId, userId);

        return this.count(queryWrapper);
    }

    
    @Transactional
    public void deleteByCallId(String callId) {
        LambdaQueryWrapper<ChatDetail> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatDetail::getCallId, callId);

        boolean success = this.remove(queryWrapper);

        if (success) {
            log.info("Deleted chat details for callId: {}", callId);
        } else {
            log.warn("No chat details found for callId: {}", callId);
        }
    }
}
