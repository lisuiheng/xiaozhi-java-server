package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import com.github.lisuiheng.astra.common.core.constant.CacheConstants;
import com.github.lisuiheng.astra.common.mybatis.core.page.TableDataInfo;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.idempotent.annotation.RepeatSubmit;
import com.github.lisuiheng.astra.common.log.annotation.Log;
import com.github.lisuiheng.astra.common.log.enums.BusinessType;
import com.github.lisuiheng.astra.common.redis.utils.RedisUtils;
import com.github.lisuiheng.astra.common.utils.StreamUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.common.web.core.BaseController;
import com.github.lisuiheng.astra.sys.domain.dto.UserOnlineDTO;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserOnline;
import com.github.lisuiheng.astra.sys.service.ISysUserOnlineService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 在线用户监控
 *
 * @author Lion Li
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/monitor/online")
public class SysUserOnlineController extends BaseController {

    private final ISysUserOnlineService sysUserOnlineService;

    /**
     * 获取在线用户监控列表
     *
     * @param ipaddr   IP地址
     * @param userName 用户名
     */
    @SaCheckPermission("monitor:online:list")
    @GetMapping("/list")
    public TableDataInfo<SysUserOnline> list(String ipaddr, String userName) {
        // 获取所有未过期的 token
        Collection<String> keys = RedisUtils.keys(CacheConstants.ONLINE_TOKEN_KEY + "*");
        List<UserOnlineDTO> userOnlineDTOList = new ArrayList<>();
        for (String key : keys) {
            String token = StringUtils.substringAfterLast(key, ":");
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                continue;
            }
            Object cacheObject = RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token);
            if (cacheObject instanceof UserOnlineDTO) {
                userOnlineDTOList.add((UserOnlineDTO) cacheObject);
            }
        }
        if (StringUtils.isNotEmpty(ipaddr) && StringUtils.isNotEmpty(userName)) {
            userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
                    StringUtils.equals(ipaddr, userOnline.getIpaddr()) &&
                            StringUtils.equals(userName, userOnline.getUserName())
            );
        } else if (StringUtils.isNotEmpty(ipaddr)) {
            userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
                    StringUtils.equals(ipaddr, userOnline.getIpaddr())
            );
        } else if (StringUtils.isNotEmpty(userName)) {
            userOnlineDTOList = StreamUtils.filter(userOnlineDTOList, userOnline ->
                    StringUtils.equals(userName, userOnline.getUserName())
            );
        }
        Collections.reverse(userOnlineDTOList);
        userOnlineDTOList.removeAll(Collections.singleton(null));
        List<SysUserOnline> userOnlineList = BeanUtil.copyToList(userOnlineDTOList, SysUserOnline.class);
        return TableDataInfo.build(userOnlineList);
    }

    /**
     * 强退用户
     *
     * @param tokenId token值
     */
    @SaCheckPermission("monitor:online:forceLogout")
    @Log(title = "在线用户", businessType = BusinessType.FORCE)
    @RepeatSubmit()
    @DeleteMapping("/{tokenId}")
    public R<Void> forceLogout(@PathVariable String tokenId) {
        sysUserOnlineService.forceLogout(tokenId);
        return R.ok();
    }

    /**
     * 获取当前用户登录在线设备
     */
    @GetMapping()
    public TableDataInfo<SysUserOnline> getInfo() {
        List<SysUserOnline> userOnlineList = sysUserOnlineService.getCurrentUserOnlineDevices();
        return TableDataInfo.build(userOnlineList);
    }

    /**
     * 强退当前在线设备
     *
     * @param tokenId token值
     */
    @Log(title = "在线设备", businessType = BusinessType.FORCE)
    @RepeatSubmit()
    @DeleteMapping("/myself/{tokenId}")
    public R<Void> remove(@PathVariable("tokenId") String tokenId) {
        try {
            sysUserOnlineService.forceLogoutMyself(tokenId);
        } catch (NotLoginException ignored) {
        }
        return R.ok();
    }
}