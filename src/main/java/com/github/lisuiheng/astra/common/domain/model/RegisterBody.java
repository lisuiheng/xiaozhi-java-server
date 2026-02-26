package com.github.lisuiheng.astra.common.domain.model;

import lombok.Data;

/**
 * 注册对象
 */
@Data
public class RegisterBody {

    /**
     * 用户名
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 租户ID
     */
    private String tenantId;
}