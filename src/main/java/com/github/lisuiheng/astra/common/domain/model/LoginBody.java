package com.github.lisuiheng.astra.common.domain.model;

import lombok.Data;

/**
 * 登录对象
 */
@Data
public class LoginBody {

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 验证码
     */
    private String code;

    /**
     * 唯一标识
     */
    private String uuid;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 授权类型
     */
    private String grantType;

    /**
     * 客户端ID
     */
    private String clientId;
}