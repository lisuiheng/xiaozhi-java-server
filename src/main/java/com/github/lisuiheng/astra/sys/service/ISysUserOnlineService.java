package com.github.lisuiheng.astra.sys.service;

import com.github.lisuiheng.astra.sys.domain.dto.UserOnlineDTO;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserOnline;

import java.util.List;

/**
 * @author Lion Li
 */

public interface ISysUserOnlineService {

    /**
     * 分页查询在线用户
     *
     * @param ipaddr   IP地址
     * @param userName 用户名
     * @return 在线用户列表
     */
    List<SysUserOnline> selectOnlineList(String ipaddr, String userName);

    /**
     * 强退用户
     *
     * @param tokenId token值
     */
    void forceLogout(String tokenId);

    /**
     * 获取当前用户登录在线设备
     *
     * @return 在线设备列表
     */
    List<SysUserOnline> getCurrentUserOnlineDevices();

    /**
     * 强退当前用户的特定设备
     *
     * @param tokenId token值
     */
    void forceLogoutMyself(String tokenId);
}