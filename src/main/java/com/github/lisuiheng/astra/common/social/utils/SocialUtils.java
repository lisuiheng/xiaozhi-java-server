package com.github.lisuiheng.astra.common.social.utils;

import com.github.lisuiheng.astra.sys.config.properties.SocialProperties;
import me.zhyd.oauth.config.AuthConfig;
import me.zhyd.oauth.exception.AuthException;
import me.zhyd.oauth.model.AuthCallback;
import me.zhyd.oauth.model.AuthResponse;
import me.zhyd.oauth.model.AuthUser;
import me.zhyd.oauth.request.*;

/**
 * 社交登录工具类
 */
public class SocialUtils {

    /**
     * 获取认证请求
     *
     * @param source 来源
     * @param socialProperties 社交登录配置
     * @return 认证请求
     */
    public static AuthRequest getAuthRequest(String source, SocialProperties socialProperties) {
        // 简化实现，实际中需要根据配置创建对应的AuthRequest
        AuthConfig config = AuthConfig.builder()
            .clientId(socialProperties.getType().get(source).getClientId())
            .clientSecret(socialProperties.getType().get(source).getClientSecret())
            .redirectUri(socialProperties.getType().get(source).getRedirectUri())
            .build();
        
        switch (source.toLowerCase()) {
            case "gitee":
                return new AuthGiteeRequest(config);
            case "github":
                return new AuthGithubRequest(config);
            default:
                throw new AuthException("不支持的登录类型: " + source);
        }
    }

    /**
     * 登录认证
     *
     * @param source 来源
     * @param code code
     * @param state state
     * @param socialProperties 社交登录配置
     * @return 认证响应
     */
    public static AuthResponse<AuthUser> loginAuth(String source, String code, String state, SocialProperties socialProperties) throws AuthException {
        AuthRequest authRequest = getAuthRequest(source, socialProperties);
        AuthCallback callback = new AuthCallback();
        callback.setCode(code);
        callback.setState(state);
        return authRequest.login(callback);
    }
}