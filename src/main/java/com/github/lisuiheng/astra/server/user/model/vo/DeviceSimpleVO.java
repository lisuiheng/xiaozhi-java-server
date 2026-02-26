package com.github.lisuiheng.astra.server.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "设备简单VO")
public class DeviceSimpleVO {

    @Schema(description = "设备ID", example = "1234567890")
    private String id;

    @Schema(description = "设备名称", example = "智能音箱")
    private String name;

    @Schema(description = "设备类型", example = "speaker")
    private String type;

    @Schema(description = "设备状态: 0-离线, 1-在线", example = "1")
    private Integer status;

    @Schema(description = "绑定时间")
    private java.time.LocalDateTime bindTime;
}