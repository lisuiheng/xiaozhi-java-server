package com.github.lisuiheng.astra.server.server.model.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import com.github.lisuiheng.astra.common.core.domain.BaseEntity;
import com.github.lisuiheng.astra.server.server.constant.Tts;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("server_voice")
public class Voice extends BaseEntity {

    /**
     * 主键，唯一标识符
     */
    @JsonSerialize(using = ToStringSerializer.class)
    @TableId("id")
    private String id;

    /**
     * 声音名称
     */
    @TableField("name")
    private String name;

    /**
     * 音色
     */
    @TableField("speaker")
    private String speaker;

    /**
     * tts
     */
    @TableField("tts")
    private Tts tts;

    /**
     * 样例录音文件ID（OSS存储ID）
     */
    @TableField("oss_id")
    private Long ossId;

    /**
     * 样例录音文本
     */
    @TableField("prompt_text")
    private String promptText;

    /**
     * 声纹数据
     */
    @TableField("voice_remark")
    private String voiceRemark;

    /**
     * 声音描述
     */
    @TableField("description")
    private String description;
}
