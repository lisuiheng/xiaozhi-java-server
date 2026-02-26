package com.github.lisuiheng.astra.sys.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.github.lisuiheng.astra.common.constant.Constants;
import com.github.lisuiheng.astra.common.domain.model.PasswordLoginBody;
import com.github.lisuiheng.astra.common.json.utils.JsonUtils;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.common.utils.MessageUtils;
import com.github.lisuiheng.astra.common.utils.ValidatorUtils;
import com.github.lisuiheng.astra.sys.domain.entity.SysUser;
import com.github.lisuiheng.astra.sys.domain.vo.LoginVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysClientVo;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserVo;
import com.github.lisuiheng.astra.sys.mapper.SysUserMapper;
import com.github.lisuiheng.astra.sys.service.IAuthStrategy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 密码认证策略
 *
 * @author Michelle.Chung
 */
@Slf4j
@Service("passwordauthstrategy")
@RequiredArgsConstructor
public class PasswordAuthStrategy implements IAuthStrategy {

    private final SysLoginService loginService;
    private final SysUserMapper userMapper;

    @Override
    public LoginVo login(String body, SysClientVo client) {
        PasswordLoginBody loginBody = JsonUtils.parseObject(body, PasswordLoginBody.class);
        ValidatorUtils.validate(loginBody);
        String username = loginBody.getUsername();
        String password = loginBody.getPassword();

        // 加载用户信息
        SysUser userEntity = loadUserByUsername(username);
        
        // 验证密码
        if (!BCrypt.checkpw(password, userEntity.getPassword())) {
            log.info("登录用户：{} 密码错误.", username);
            // 记录登录失败信息
            loginService.recordLogininfor(userEntity.getTenantId(), username, Constants.LOGIN_FAIL, MessageUtils.message("user.password.error"));
            throw new RuntimeException("用户名或密码错误");
        }

        // 将实体类转换为VO类
        SysUserVo user = convertToVo(userEntity);
        
        // 构建登录用户
        com.github.lisuiheng.astra.common.domain.model.LoginUser loginUser = loginService.buildLoginUser(user);
        loginUser.setClientKey(client.getClientKey());
        loginUser.setDeviceType(client.getDeviceType());

        // 执行登录
        loginService.login(loginUser, client.getClientId(), client.getDeviceType());
        
        // 记录登录成功信息
        loginService.recordLogininfor(user.getTenantId(), user.getUserName(), Constants.LOGIN_SUCCESS, MessageUtils.message("user.login.success"));

        // 构建返回结果
        LoginVo loginVo = new LoginVo();
        loginVo.setAccessToken(LoginHelper.getAccessToken());
        loginVo.setExpireIn(LoginHelper.getExpireIn());
        loginVo.setClientId(client.getClientId());
        return loginVo;
    }

    private SysUser loadUserByUsername(String username) {
        SysUser user = userMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUserName, username));
        if (user == null) {
            log.info("登录用户：{} 不存在.", username);
            // 记录登录失败信息
            loginService.recordLogininfor(null, username, Constants.LOGIN_FAIL, MessageUtils.message("user.not.exists"));
            throw new RuntimeException("用户不存在");
        } else if ("1".equals(user.getStatus())) {
            log.info("登录用户：{} 已被停用.", username);
            // 记录登录失败信息
            loginService.recordLogininfor(user.getTenantId(), username, Constants.LOGIN_FAIL, MessageUtils.message("user.blocked"));
            throw new RuntimeException("用户已被停用");
        }
        return user;
    }
    
    private SysUserVo convertToVo(SysUser entity) {
        SysUserVo vo = new SysUserVo();
        vo.setUserId(entity.getUserId());
        vo.setDeptId(entity.getDeptId());
        vo.setUserName(entity.getUserName());
        vo.setNickName(entity.getNickName());
        vo.setEmail(entity.getEmail());
        vo.setPhonenumber(entity.getPhonenumber());
        vo.setSex(entity.getSex());
        vo.setAvatar(entity.getAvatar());
        vo.setPassword(entity.getPassword());
        vo.setStatus(entity.getStatus());
        vo.setDelFlag(entity.getDelFlag());
        vo.setUserType(entity.getUserType());
        vo.setTenantId(entity.getTenantId());
        return vo;
    }
}