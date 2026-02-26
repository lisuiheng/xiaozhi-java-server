package com.github.lisuiheng.astra.common.constant;

/**
 * 通用常量信息
 */
public class Constants {

    /**
     * UTF-8 字符集
     */
    public static final String UTF8 = "UTF-8";

    /**
     * GBK 字符集
     */
    public static final String GBK = "GBK";

    /**
     * www主域
     */
    public static final String WWW = "www.";

    /**
     * http请求
     */
    public static final String HTTP = "http://";

    /**
     * https请求
     */
    public static final String HTTPS = "https://";

    /**
     * 通用成功标识
     */
    public static final int SUCCESS = 200;

    /**
     * 通用失败标识
     */
    public static final int FAIL = 500;

    /**
     * 登录成功
     */
    public static final String LOGIN_SUCCESS = "Success";

    /**
     * 注销成功
     */
    public static final String LOGOUT_SUCCESS = "Logout";

    /**
     * 登录失败
     */
    public static final String LOGIN_FAIL = "Error";

    /**
     * 验证码 redis key
     */
    public static final String CAPTCHA_CODE_KEY = "captcha_codes:";

    /**
     * 验证码有效期（分钟）
     */
    public static final Integer CAPTCHA_EXPIRATION = 2;

    /**
     * 令牌前缀
     */
    public static final String TOKEN_PREFIX = "Bearer ";

    /**
     * 令牌前缀
     */
    public static final String LOGIN_USER_KEY = "login_user_key";

    /**
     * 用户ID
     */
    public static final String JWT_USERID_KEY = "userid";

    /**
     * 用户名称
     */
    public static final String JWT_USERNAME_KEY = "sub";

    /**
     * 用户头像
     */
    public static final String JWT_AVATAR_KEY = "avatar";

    /**
     * 创建时间
     */
    public static final String JWT_CREATED_KEY = "created";

    /**
     * 用户权限
     */
    public static final String JWT_AUTHORITIES_KEY = "authorities";

    /**
     * 资源映射路径 前缀
     */
    public static final String RESOURCE_PREFIX = "/profile";

    /**
     * RMI 远程方法调用
     */
    public static final String LOOKUP_RMI = "rmi:";

    /**
     * LDAP 远程方法调用
     */
    public static final String LOOKUP_LDAP = "ldap:";

    /**
     * LDAPS 远程方法调用
     */
    public static final String LOOKUP_LDAPS = "ldaps:";

    /**
     * 定时任务白名单配置（仅允许访问的包名，如其他需要可以自行添加）
     */
    public static final String[] JOB_WHITELIST_STR = { "com.github.lisuiheng" };

    /**
     * 定时任务违规的字符
     */
    public static final String[] JOB_ERROR_STR = { "java.net.URL", "javax.naming.InitialContext", "org.yaml.snakeyaml",
            "org.springframework", "org.apache", "com.github.lisuiheng.astra.common.core.utils", "com.github.lisuiheng.astra.common.util" };

    /**
     * 超级管理员ID
     */
    public static final Long SUPER_ADMIN_ID = 1L;

    /**
     * 超级管理员角色 key
     */
    public static final String SUPER_ADMIN_ROLE_KEY = "superadmin";

    /**
     * 租户管理员角色 key
     */
    public static final String TENANT_ADMIN_ROLE_KEY = "admin";

    /**
     * 根节点标识
     */
    public static final Long TOP_PARENT_ID = 0L;
}