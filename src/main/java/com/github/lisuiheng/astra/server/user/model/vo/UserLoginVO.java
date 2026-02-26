package com.github.lisuiheng.astra.server.user.model.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "用户登录VO")
public class UserLoginVO {

    @Schema(description = "访问令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    @Schema(description = "令牌类型", example = "Bearer")
    private String tokenType = "Bearer";

    @Schema(description = "过期时间（秒）", example = "7200")
    private Long expiresIn = 7200L;

    @Schema(description = "用户信息")
    private UserVO userInfo;

    @Schema(description = "权限列表", example = "[\"user:read\", \"user:write\"]")
    private String[] permissions;
}