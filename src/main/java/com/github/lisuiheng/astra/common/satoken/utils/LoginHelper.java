package com.github.lisuiheng.astra.common.satoken.utils;

import cn.dev33.satoken.session.SaSession;
import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.github.lisuiheng.astra.common.constant.SystemConstants;
import com.github.lisuiheng.astra.common.constant.TenantConstants;
import com.github.lisuiheng.astra.common.domain.model.LoginUser;
import com.github.lisuiheng.astra.common.enums.UserType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class LoginHelper {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String TENANT_KEY = "tenantId";
    public static final String USER_KEY = "userId";
    public static final String USER_NAME_KEY = "userName";
    public static final String DEPT_KEY = "deptId";
    public static final String DEPT_NAME_KEY = "deptName";
    public static final String DEPT_CATEGORY_KEY = "deptCategory";
    public static final String CLIENT_KEY = "clientid";

    /**
     * 登录系统 基于 设备类型
     * 针对相同用户体系不同设备
     *
     * @param loginUser 登录用户信息
     * @param model     配置参数
     */
    public static void login(LoginUser loginUser, SaLoginParameter model) {
        model = ObjectUtil.defaultIfNull(model, new SaLoginParameter());
        StpUtil.login(loginUser.getLoginId(),
                model.setExtra(TENANT_KEY, loginUser.getTenantId())
                        .setExtra(USER_KEY, loginUser.getUserId())
                        .setExtra(USER_NAME_KEY, loginUser.getUsername())
                        .setExtra(DEPT_KEY, loginUser.getDeptId())
                        .setExtra(DEPT_NAME_KEY, loginUser.getDeptName())
                        .setExtra(DEPT_CATEGORY_KEY, loginUser.getDeptCategory())
        );
        StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends LoginUser> T getLoginUser() {
        SaSession session = StpUtil.getTokenSession();
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (T) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户基于token
     */
    @SuppressWarnings("unchecked cast")
    public static <T extends LoginUser> T getLoginUser(String token) {
        SaSession session = StpUtil.getTokenSessionByToken(token);
        if (ObjectUtil.isNull(session)) {
            return null;
        }
        return (T) session.get(LOGIN_USER_KEY);
    }

    /**
     * 获取用户id
     */
    public static Long getUserId() {
        return Convert.toLong(getExtra(USER_KEY));
    }

    /**
     * 获取用户id
     */
    public static String getUserIdStr() {
        return Convert.toStr(getExtra(USER_KEY));
    }

    /**
     * 获取用户账户
     */
    public static String getUsername() {
        return Convert.toStr(getExtra(USER_NAME_KEY));
    }

    /**
     * 获取租户ID
     */
    public static String getTenantId() {
        return Convert.toStr(getExtra(TENANT_KEY));
    }

    /**
     * 获取部门ID
     */
    public static Long getDeptId() {
        return Convert.toLong(getExtra(DEPT_KEY));
    }

    /**
     * 获取部门名
     */
    public static String getDeptName() {
        return Convert.toStr(getExtra(DEPT_NAME_KEY));
    }

    /**
     * 获取部门类别编码
     */
    public static String getDeptCategory() {
        return Convert.toStr(getExtra(DEPT_CATEGORY_KEY));
    }

    /**
     * 获取当前 Token 的扩展信息
     *
     * @param key 键值
     * @return 对应的扩展数据
     */
    private static Object getExtra(String key) {
        try {
            return StpUtil.getExtra(key);
        } catch (Exception e) {
            return null;
        }
    }
    
    /**
     * 获取访问令牌
     */
    public static String getAccessToken() {
        return StpUtil.getTokenValue();
    }
    
    /**
     * 获取过期时间
     */
    public static long getExpireIn() {
        return StpUtil.getTokenTimeout();
    }
    
    /**
     * 获取用户类型
     */
    public static UserType getUserType() {
        String loginType = StpUtil.getLoginIdAsString();
        return UserType.getUserType(loginType);
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isSuperAdmin(Long userId) {
        return SystemConstants.SUPER_ADMIN_ID.equals(userId);
    }

    /**
     * 是否为超级管理员
     *
     * @return 结果
     */
    public static boolean isSuperAdmin() {
        return isSuperAdmin(getUserId());
    }

    /**
     * 是否为租户管理员
     *
     * @param rolePermission 角色权限标识组
     * @return 结果
     */
    public static boolean isTenantAdmin(Set<String> rolePermission) {
        if (CollUtil.isEmpty(rolePermission)) {
            return false;
        }
        return rolePermission.contains(TenantConstants.TENANT_ADMIN_ROLE_KEY);
    }

    /**
     * 是否为租户管理员
     *
     * @return 结果
     */
    public static boolean isTenantAdmin() {
        LoginUser loginUser = getLoginUser();
        if (loginUser == null) {
            return false;
        }
        return Convert.toBool(isTenantAdmin(loginUser.getRolePermission()));
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 结果
     */
    public static boolean isLogin() {
        try {
            StpUtil.checkLogin();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
