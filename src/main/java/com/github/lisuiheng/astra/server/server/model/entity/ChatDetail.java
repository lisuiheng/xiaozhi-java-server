package com.github.lisuiheng.astra.server.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.common.typehandler.ListFloatTypeHandler;
import com.github.lisuiheng.astra.server.server.constant.SpeakerType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("server_chat_detail")
public class ChatDetail extends BaseEntity {
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId("id")
    private String id; // id

    @TableField("call_id")
    private String callId; // 会话ID

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField("chat_time")
    private LocalDateTime chatTime; // 对话时间

    @TableField("question_kind")
    private SpeakerType questionKind; // 说话者类型(使用者/智能体)

    @TableField("question_name")
    private String questionName; // 说话者名称(姓名(昵称)/智能体名称(角色))

    @TableField("content")
    private String content; // 对话内容

    @TableField(value = "voice_remark", typeHandler = ListFloatTypeHandler.class)
    private List<Float> voiceRemark; // 声纹数据

    @TableField("user_id")
    private String userId; // 用户ID

    @TableField("agent_id")
    private String agentId; // 智能体ID

    @TableField("device_id")
    private String deviceId; // 设备ID

    @TableField("chat_kind")
    private String chatKind; // 对话类型 chat/pic/file/video

    @TableField("chat_id")
    private String chatId; // 对话文件ID

    @TableField("is_interrupted")
    private Boolean isInterrupted; // 是否被打断

    @TableField("conversation_content")
    private String conversationContent; // 实际对话内容

    @TableField("longitude")
    private BigDecimal longitude; // 经度，精度为小数点后6位

    @TableField("latitude")
    private BigDecimal latitude; // 纬度，精度为小数点后6位
}