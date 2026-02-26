package com.github.lisuiheng.astra.sys.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.util.ObjectUtil;
import com.github.lisuiheng.astra.common.constant.Constants;
import com.github.lisuiheng.astra.common.domain.model.LoginUser;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.common.utils.MessageUtils;
import com.github.lisuiheng.astra.common.utils.ServletUtils;
import com.github.lisuiheng.astra.common.utils.SpringUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.common.log.event.LogininforEvent;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import com.github.lisuiheng.astra.sys.mapper.SysUserMapper;
import com.github.lisuiheng.astra.sys.service.ISysPermissionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.zhyd.oauth.model.AuthUser;
import org.springframework.stereotype.Service;

/**
 * 系统登录服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysLoginService {

    private final SysUserMapper userMapper;
    private final ISysPermissionService permissionService;

    /**
     * 检查租户
     *
     * @param tenantId 租户ID
     */
    public void checkTenant(String tenantId) {
        // 简化实现
    }

    /**
     * 退出登录
     */
    public void logout() {
        try {
            LoginUser loginUser = LoginHelper.getLoginUser();
            if (ObjectUtil.isNull(loginUser)) {
                return;
            }
            recordLogininfor(loginUser.getTenantId(), loginUser.getUsername(), Constants.LOGOUT_SUCCESS, MessageUtils.message("user.logout.success"));
        } catch (Exception ignored) {
        } finally {
            try {
                StpUtil.logout();
            } catch (Exception ignored) {
            }
        }
    }

    /**
     * 社交注册
     *
     * @param authUserData 认证用户数据
     */
    public void socialRegister(AuthUser authUserData) {
        // 简化实现
    }

    /**
     * 构建登录用户
     */
    public LoginUser buildLoginUser(SysUserVo user) {
        LoginUser loginUser = new LoginUser();
        Long userId = user.getUserId();
        loginUser.setTenantId(user.getTenantId());
        loginUser.setUserId(userId);
        loginUser.setDeptId(user.getDeptId());
        loginUser.setUsername(user.getUserName());
        loginUser.setNickname(user.getNickName());
        loginUser.setUserType(user.getUserType());
        loginUser.setMenuPermission(permissionService.getMenuPermission(userId));
        loginUser.setRolePermission(permissionService.getRolePermission(userId));
        return loginUser;
    }

    /**
     * 执行登录
     */
    public void login(LoginUser loginUser, String clientId, String deviceType) {
        SaLoginParameter model = new SaLoginParameter();
        model.setDeviceType(deviceType);
        model.setTimeout(60 * 60 * 24L); // 默认24小时过期
        model.setActiveTimeout(60 * 60 * 24L); // 默认24小时活跃过期
        model.setExtra(LoginHelper.CLIENT_KEY, clientId);

        LoginHelper.login(loginUser, model);
    }
    
    /**
     * 记录登录信息
     *
     * @param tenantId 租户ID
     * @param username 用户名
     * @param status   状态
     * @param message  消息内容
     */
    public void recordLogininfor(String tenantId, String username, String status, String message) {
        LogininforEvent logininforEvent = new LogininforEvent();
        logininforEvent.setTenantId(tenantId);
        logininforEvent.setUsername(username);
        logininforEvent.setStatus(status);
        logininforEvent.setMessage(message);
        logininforEvent.setRequest(ServletUtils.getRequest());
        SpringUtils.context().publishEvent(logininforEvent);
    }
}