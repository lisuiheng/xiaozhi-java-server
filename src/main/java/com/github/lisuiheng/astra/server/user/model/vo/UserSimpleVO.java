package com.github.lisuiheng.astra.server.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户简单VO")
public class UserSimpleVO {

    @Schema(description = "用户ID", example = "1234567890")
    private String id;

    @Schema(description = "用户名", example = "john_doe")
    private String username;

    @Schema(description = "昵称", example = "John")
    private String nickname;

    @Schema(description = "头像URL", example = "https://example.com/avatar.jpg")
    private String avatarUrl;

    @Schema(description = "用户类型: 0-普通用户, 1-管理员", example = "0")
    private Integer userType;
}