package com.github.lisuiheng.astra.sys.domain.vo;

import lombok.Data;
import java.util.Set;

/**
 * 用户信息
 */
@Data
public class UserInfoVo {

    /**
     * 用户基本信息
     */
    private SysUserVo user;

    /**
     * 菜单权限
     */
    private Set<String> permissions;

    /**
     * 角色权限
     */
    private Set<String> roles;
}