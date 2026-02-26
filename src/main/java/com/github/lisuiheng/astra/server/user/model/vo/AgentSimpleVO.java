package com.github.lisuiheng.astra.server.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "智能体简单VO")
public class AgentSimpleVO {

    @Schema(description = "智能体ID", example = "1234567890")
    private String id;

    @Schema(description = "智能体名称", example = "客服助手")
    private String name;

    @Schema(description = "智能体描述", example = "用于处理客户咨询的智能助手")
    private String description;

    @Schema(description = "创建时间")
    private java.time.LocalDateTime createdTime;

    @Schema(description = "状态: 0-禁用, 1-启用", example = "1")
    private Integer status;
}