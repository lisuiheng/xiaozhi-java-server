package com.github.lisuiheng.astra.server.server.model.dto;

import com.github.lisuiheng.astra.server.server.constant.SpeakerType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ChatDetailDTO {
    private String id;
    private String callId; // 会话ID
    private LocalDateTime chatTime; // 对话时间
    private SpeakerType questionKind; // 说话者类型(使用者/智能体)
    private String questionName; // 说话者名称(姓名(昵称)/智能体名称(角色))
    private String content; // 对话内容
    private List<Float> voiceRemark; // 声纹数据
    private String userId; // 用户ID
    private String agentId; // 智能体ID
    private String deviceId; // 设备ID
    private String chatKind; // 对话类型 chat/pic/file/video
    private String chatId; // 对话文件ID
    private Boolean isInterrupted; // 是否被打断
    private String conversationContent; // 实际对话内容
    private BigDecimal longitude; // 经度
    private BigDecimal latitude; // 纬度
}