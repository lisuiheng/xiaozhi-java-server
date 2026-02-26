package com.github.lisuiheng.astra.sys.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * @author Lion Li
 */

@Schema(description = "在线用户信息")
@Data
public class SysUserOnline {

    /**
     * 用户会话id
     */
    @Schema(description = "会话ID")
    private String tokenId;

    /**
     * 部门名称
     */
    @Schema(description = "部门名称")
    private String deptName;

    /**
     * 用户名称
     */
    @Schema(description = "用户名")
    private String userName;

    /**
     * 客户端
     */
    @Schema(description = "客户端")
    private String clientKey;

    /**
     * 设备类型
     */
    @Schema(description = "设备类型")
    private String deviceType;

    /**
     * 登录IP地址
     */
    @Schema(description = "IP地址")
    private String ipaddr;

    /**
     * 登录地址
     */
    @Schema(description = "登录地点")
    private String loginLocation;

    /**
     * 浏览器类型
     */
    @Schema(description = "浏览器")
    private String browser;

    /**
     * 操作系统
     */
    @Schema(description = "操作系统")
    private String os;

    /**
     * 登录时间
     */
    @Schema(description = "登录时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Long loginTime;
}