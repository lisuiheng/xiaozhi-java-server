package com.github.lisuiheng.astra.sys.domain.vo;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
/**
 * 登录验证信息
 *
 * @author Michelle.Chung
 */
@Data
public class LoginVo {
    /**
     * 授权令牌
     */
    @JsonProperty("access_token")
    private String accessToken;
    /**
     * 刷新令牌
     */
    @JsonProperty("refresh_token")
    private String refreshToken;
    /**
     * 授权令牌 access_token 的有效期
     */
    @JsonProperty("expire_in")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long expireIn;
    /**
     * 刷新令牌 refresh_token 的有效期
     */
    @JsonProperty("refresh_expire_in")
    @JsonSerialize(using = ToStringSerializer.class)
    private Long refreshExpireIn;
    /**
     * 应用id
     */
    @JsonProperty("client_id")
    private String clientId;
    /**
     * 令牌权限
     */
    private String scope;
    /**
     * 用户 openid
     */
    private String openid;
}