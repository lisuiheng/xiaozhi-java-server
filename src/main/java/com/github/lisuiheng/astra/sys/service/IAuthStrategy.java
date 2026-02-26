package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.common.utils.SpringUtils;
import com.github.lisuiheng.astra.common.exception.ServiceException;
import com.github.lisuiheng.astra.sys.domain.vo.SysClientVo;
import com.github.lisuiheng.astra.sys.domain.vo.LoginVo;

/**
 * 认证策略接口
 */
public interface IAuthStrategy {

    String BASE_NAME = "AuthStrategy";

    /**
     * 登录
     *
     * @param body 登录信息
     * @param client 客户端信息
     * @return 登录结果
     */
    LoginVo login(String body, SysClientVo client);

    /**
     * 登录
     *
     * @param body 登录信息
     * @param client 客户端信息
     * @param grantType 授权类型
     * @return 登录结果
     */
    static LoginVo login(String body, SysClientVo client, String grantType) {
        String beanName = grantType + BASE_NAME;
        if (!SpringUtils.containsBean(beanName.toLowerCase())) {
            throw new ServiceException("授权类型不正确: " + grantType);
        }
        IAuthStrategy instance = SpringUtils.getBean(beanName.toLowerCase());
        return instance.login(body, client);
    }
}