package com.github.lisuiheng.astra.common.satoken.core.service;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import com.github.lisuiheng.astra.common.domain.model.LoginUser;
import com.github.lisuiheng.astra.common.enums.UserType;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.common.utils.SpringUtils;
import com.github.lisuiheng.astra.sys.service.ISysPermissionService;

import java.util.ArrayList;
import java.util.List;

/**
 * sa-token 权限管理实现类
 */
public class SaPermissionImpl implements StpInterface {

    /**
     * 获取菜单权限列表
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (ObjectUtil.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            ISysPermissionService permissionService = getPermissionService();
            if (ObjectUtil.isNotNull(permissionService)) {
                List<String> list = new ArrayList<>();
                list.add(loginId.toString());
                return new ArrayList<>(permissionService.getMenuPermission(Long.parseLong(list.get(0))));
            } else {
                throw new RuntimeException("PermissionService 实现类不存在");
            }
        }
        UserType userType = UserType.getUserType(loginUser.getUserType());
        if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        if (CollUtil.isNotEmpty(loginUser.getMenuPermission())) {
            // SYS_USER 默认返回权限
            return new ArrayList<>(loginUser.getMenuPermission());
        } else {
            return new ArrayList<>();
        }
    }

    /**
     * 获取角色权限列表
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        LoginUser loginUser = LoginHelper.getLoginUser();
        if (ObjectUtil.isNull(loginUser) || !loginUser.getLoginId().equals(loginId)) {
            ISysPermissionService permissionService = getPermissionService();
            if (ObjectUtil.isNotNull(permissionService)) {
                List<String> list = new ArrayList<>();
                list.add(loginId.toString());
                return new ArrayList<>(permissionService.getRolePermission(Long.parseLong(list.get(0))));
            } else {
                throw new RuntimeException("PermissionService 实现类不存在");
            }
        }
        UserType userType = UserType.getUserType(loginUser.getUserType());
        if (userType == UserType.APP_USER) {
            // 其他端 自行根据业务编写
        }
        if (CollUtil.isNotEmpty(loginUser.getRolePermission())) {
            // SYS_USER 默认返回权限
            return new ArrayList<>(loginUser.getRolePermission());
        } else {
            return new ArrayList<>();
        }
    }

    private ISysPermissionService getPermissionService() {
        try {
            return SpringUtils.getBean(ISysPermissionService.class);
        } catch (Exception e) {
            return null;
        }
    }

}