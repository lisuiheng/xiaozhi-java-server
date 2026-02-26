package com.github.lisuiheng.astra.sys.utils;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.constant.Constants;
import com.github.lisuiheng.astra.sys.domain.entity.SysRole;
import com.github.lisuiheng.astra.common.domain.model.LoginUser;
import com.github.lisuiheng.astra.common.enums.UserType;
import com.github.lisuiheng.astra.common.tenant.helper.TenantHelper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

/**
 * 安全服务工具类
 * 基于 SaToken 框架实现
 *
 * @author Qoder
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class SecurityUtils {

    public static final String LOGIN_USER_KEY = "loginUser";
    public static final String TENANT_KEY = "tenantId";
    public static final String USER_KEY = "userId";
    public static final String USER_NAME_KEY = "userName";
    public static final String DEPT_KEY = "deptId";
    public static final String DEPT_NAME_KEY = "deptName";
    public static final String DEPT_CATEGORY_KEY = "deptCategory";
    public static final String CLIENT_KEY = "clientid";

    // TODO: 在实际实现中，需要集成 SaToken 或其他安全框架来获取真实用户信息
    // 目前只是模拟实现，实际部署时需要替换为真实的实现
    
    /**
     * 登录系统
     *
     * @param loginUser 登录用户信息
     * @param model     配置参数
     */
    public static void login(LoginUser loginUser, Object model) {
        // 模拟实现，实际应使用 SaToken 实现
        // StpUtil.login(loginUser.getLoginId(),
        //         model.setExtra(TENANT_KEY, loginUser.getTenantId())
        //                 .setExtra(USER_KEY, loginUser.getUserId())
        //                 .setExtra(USER_NAME_KEY, loginUser.getUsername())
        //                 .setExtra(DEPT_KEY, loginUser.getDeptId())
        //                 .setExtra(DEPT_NAME_KEY, loginUser.getDeptName())
        //                 .setExtra(DEPT_CATEGORY_KEY, loginUser.getDeptCategory())
        // );
        // StpUtil.getTokenSession().set(LOGIN_USER_KEY, loginUser);
    }

    /**
     * 获取用户(多级缓存)
     */
    @SuppressWarnings("unchecked")
    public static <T extends LoginUser> T getLoginUser() {
        // 模拟实现，实际应从 SaToken 获取
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        loginUser.setUsername("admin");
        loginUser.setTenantId(getTenantId());
        loginUser.setDeptId(getDeptId());
        loginUser.setDeptName(getDeptName());
        return (T) loginUser;
    }

    /**
     * 获取用户基于token
     */
    @SuppressWarnings("unchecked")
    public static <T extends LoginUser> T getLoginUser(String token) {
        // 模拟实现，实际应从 SaToken 获取
        LoginUser loginUser = new LoginUser();
        loginUser.setUserId(1L);
        loginUser.setUsername("admin");
        loginUser.setTenantId(getTenantId());
        loginUser.setDeptId(getDeptId());
        loginUser.setDeptName(getDeptName());
        return (T) loginUser;
    }

    /**
     * 获取用户ID
     *
     * @return 用户ID
     */
    public static Long getUserId() {
        // 模拟实现，实际应从 SaToken 获取
        return 1L;
    }

    /**
     * 获取用户ID字符串
     *
     * @return 用户ID字符串
     */
    public static String getUserIdStr() {
        // 模拟实现，实际应从 SaToken 获取
        return "1";
    }

    /**
     * 获取用户账户
     *
     * @return 用户账户
     */
    public static String getUsername() {
        // 模拟实现，实际应从 SaToken 获取
        return "admin";
    }

    /**
     * 获取租户ID
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        // 模拟实现，实际应从 SaToken 获取
        return "000000";
    }

    /**
     * 获取部门ID
     *
     * @return 部门ID
     */
    public static Long getDeptId() {
        // 模拟实现，实际应从 SaToken 获取
        return 100L;
    }

    /**
     * 获取部门名
     *
     * @return 部门名
     */
    public static String getDeptName() {
        // 模拟实现，实际应从 SaToken 获取
        return "研发部";
    }

    /**
     * 获取部门类别编码
     *
     * @return 部门类别编码
     */
    public static String getDeptCategory() {
        // 模拟实现，实际应从 SaToken 获取
        return "RD";
    }

    /**
     * 获取当前 Token 的扩展信息
     *
     * @param key 键值
     * @return 对应的扩展数据
     */
    private static Object getExtra(String key) {
        // 模拟实现，实际应从 SaToken 获取
        switch (key) {
            case USER_KEY:
                return 1L;
            case USER_NAME_KEY:
                return "admin";
            case TENANT_KEY:
                return "000000";
            case DEPT_KEY:
                return 100L;
            case DEPT_NAME_KEY:
                return "研发部";
            case DEPT_CATEGORY_KEY:
                return "RD";
            default:
                return null;
        }
    }

    /**
     * 获取用户类型
     *
     * @return 用户类型
     */
    public static UserType getUserType() {
        // 模拟实现，实际应从 SaToken 获取
        return UserType.SYS_USER;
    }

    /**
     * 是否为超级管理员
     *
     * @param userId 用户ID
     * @return 结果
     */
    public static boolean isSuperAdmin(Long userId) {
        return Constants.SUPER_ADMIN_ID.equals(userId);
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
        if (rolePermission == null || rolePermission.isEmpty()) {
            return false;
        }
        return rolePermission.contains(Constants.TENANT_ADMIN_ROLE_KEY);
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
        return isTenantAdmin(loginUser.getRolePermission());
    }

    /**
     * 检查当前用户是否已登录
     *
     * @return 结果
     */
    public static boolean isLogin() {
        // 模拟实现，始终返回true表示已登录
        return true;
    }

    /**
     * 获取当前用户的token值
     *
     * @return token值
     */
    public static String getTokenValue() {
        // 模拟实现
        return "mock_token_value";
    }

    /**
     * 登出当前用户
     */
    public static void logout() {
        // 模拟实现，实际应使用 SaToken 实现
    }

    /**
     * 登出指定用户
     *
     * @param loginId 用户登录ID
     */
    public static void logout(Object loginId) {
        // 模拟实现，实际应使用 SaToken 实现
    }

    /**
     * 检查用户是否具有指定权限
     *
     * @param permission 权限标识
     * @return 是否具有权限
     */
    public static boolean hasPermission(String permission) {
        // 模拟实现，实际应使用 SaToken 实现
        return true; // 模拟返回true
    }

    /**
     * 检查用户是否具有指定角色
     *
     * @param role 角色标识
     * @return 是否具有角色
     */
    public static boolean hasRole(String role) {
        // 模拟实现，实际应使用 SaToken 实现
        return true; // 模拟返回true
    }

    /**
     * 构建租户条件
     */
    public static Optional<Consumer<QueryWrapper<?>>> buildTenantCondition() {
        // 检查是否启用多租户
        if (TenantHelper.isEnable()) {
            String tenantId = TenantHelper.getTenantId();
            if (StrUtil.isNotBlank(tenantId)) {
                return Optional.of(wrapper -> wrapper.eq("tenant_id", tenantId));
            }
        }
        return Optional.empty();
    }

    /**
     * 构建租户条件 (针对 SysRole 实体的 LambdaQueryWrapper)
     */
    public static Optional<Consumer<LambdaQueryWrapper<SysRole>>> buildTenantConditionForSysRole() {
        // 检查是否启用多租户
        if (TenantHelper.isEnable()) {
            String tenantId = TenantHelper.getTenantId();
            if (StrUtil.isNotBlank(tenantId)) {
                return Optional.of(wrapper -> wrapper.eq(SysRole::getTenantId, tenantId));
            }
        }
        return Optional.empty();
    }

    /**
     * 构建租户条件 (泛型版本，支持LambdaQueryWrapper)
     * 注意：对于LambdaQueryWrapper，需要具体实体类型才能使用方法引用
     */
    @SuppressWarnings("unchecked")
    public static <T> Optional<Consumer<T>> buildTenantConditionGeneric() {
        // 检查是否启用多租户
        if (TenantHelper.isEnable()) {
            String tenantId = TenantHelper.getTenantId();
            if (StrUtil.isNotBlank(tenantId)) {
                // 返回一个可以应用于不同wrapper类型的consumer
                return Optional.of(obj -> {
                    if (obj instanceof QueryWrapper) {
                        ((QueryWrapper<?>) obj).eq("tenant_id", tenantId);
                    }
                    // LambdaQueryWrapper需要具体实体类型才能使用方法引用
                    // 此处仅处理QueryWrapper，LambdaQueryWrapper需单独处理
                });
            }
        }
        return Optional.empty();
    }

    /**
     * 获取当前会话的剩余有效期（单位：秒）
     *
     * @return 剩余有效期，-1代表永久，-2代表已过期
     */
    public static long getTokenTimeout() {
        // 模拟实现
        return 3600L; // 模拟返回1小时
    }

    /**
     * 获取当前会话的剩余活跃有效期（单位：秒）
     * 在无操作的一段时间后过期
     *
     * @return 剩余活跃有效期
     */
    public static long getTokenActiveTimeout() {
        // 模拟实现
        return 1800L; // 模拟返回30分钟
    }

    /**
     * 续签当前会话
     * 如果当前token已经过期，则抛出异常
     */
    public static void renewTimeout(long timeout) {
        // 模拟实现
    }

    /**
     * 根据角色ID清除该角色关联的所有在线用户的登录状态（踢出在线用户）
     *
     * <p>
     * 先判断角色是否绑定用户，若无绑定则直接返回
     * 然后遍历当前所有在线Token，查找拥有该角色的用户并强制登出
     * 注意：在线用户量过大时，操作可能导致 Redis 阻塞，需谨慎调用
     * </p>
     *
     * @param roleId 角色ID
     */
    public static void cleanOnlineUserByRole(Long roleId) {
        // 模拟实现，实际应用中需要集成 SaToken 的在线用户管理功能
        // 例如遍历在线用户并踢出拥有指定角色的用户
        System.out.println("清理角色ID为 " + roleId + " 的在线用户");
    }
}