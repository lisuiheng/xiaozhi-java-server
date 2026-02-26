package com.github.lisuiheng.astra.sys.service.impl;

import com.github.lisuiheng.astra.common.constant.SystemConstants;
import com.github.lisuiheng.astra.common.constant.TenantConstants;
import com.github.lisuiheng.astra.sys.service.ISysPermissionService;
import com.github.lisuiheng.astra.sys.service.ISysRoleService;
import com.github.lisuiheng.astra.sys.service.ISysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

/**
 * 用户权限处理
 */
@RequiredArgsConstructor
@Service
public class SysPermissionServiceImpl implements ISysPermissionService {

    private final ISysRoleService roleService;
    private final ISysMenuService menuService;

    /**
     * 获取角色数据权限
     *
     * @param userId  用户id
     * @return 角色权限信息
     */
    @Override
    public Set<String> getRolePermission(Long userId) {
        Set<String> roles = new HashSet<>();
        // 管理员拥有所有权限
        if (isSuperAdmin(userId)) {
            roles.add(TenantConstants.SUPER_ADMIN_ROLE_KEY);
        } else {
            roles.addAll(roleService.selectRolePermissionByUserId(userId));
        }
        return roles;
    }

    /**
     * 获取菜单数据权限
     *
     * @param userId  用户id
     * @return 菜单权限信息
     */
    @Override
    public Set<String> getMenuPermission(Long userId) {
        Set<String> perms = new HashSet<>();
        // 管理员拥有所有权限
        if (isSuperAdmin(userId)) {
            perms.add("*:*:*");
        } else {
            perms.addAll(menuService.selectMenuPermsByUserId(userId));
        }
        return perms;
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    private boolean isSuperAdmin(Long userId) {
        return SystemConstants.SUPER_ADMIN_ID.equals(userId);
    }
}