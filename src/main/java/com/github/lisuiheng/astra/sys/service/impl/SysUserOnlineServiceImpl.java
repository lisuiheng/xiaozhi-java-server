package com.github.lisuiheng.astra.sys.service.impl;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.github.lisuiheng.astra.common.core.constant.CacheConstants;
import com.github.lisuiheng.astra.sys.domain.dto.UserOnlineDTO;
import com.github.lisuiheng.astra.sys.domain.vo.SysUserOnline;
import com.github.lisuiheng.astra.sys.service.ISysUserOnlineService;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.common.utils.StreamUtils;
import com.github.lisuiheng.astra.common.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Lion Li
 */

@Service
@RequiredArgsConstructor
public class SysUserOnlineServiceImpl implements ISysUserOnlineService {

    @Override
    public List<SysUserOnline> selectOnlineList(String ipaddr, String userName) {
        // 获取所有未过期的 token
        Collection<String> keys = RedisUtils.keys(CacheConstants.ONLINE_TOKEN_KEY + "*");
        List<UserOnlineDTO> userOnlineDTOList = new ArrayList<>();
        for (String key : keys) {
            String token = StringUtils.substringAfterLast(key, ":");
            // 如果已经过期则跳过
            if (StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) < -1) {
                continue;
            }
            UserOnlineDTO userOnlineDTO = RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token, UserOnlineDTO.class);
            if (userOnlineDTO != null) {
                userOnlineDTOList.add(userOnlineDTO);
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
        return userOnlineList;
    }

    @Override
    public void forceLogout(String tokenId) {
        try {
            StpUtil.kickoutByTokenValue(tokenId);
        } catch (NotLoginException ignored) {
        }
    }

    @Override
    public List<SysUserOnline> getCurrentUserOnlineDevices() {
        // 获取指定账号 id 的 token 集合
        List<String> tokenIds = StpUtil.getTokenValueListByLoginId(StpUtil.getLoginIdAsString());
        List<UserOnlineDTO> userOnlineDTOList = tokenIds.stream()
            .filter(token -> StpUtil.stpLogic.getTokenActiveTimeoutByToken(token) >= -1)
            .map(token -> RedisUtils.getCacheObject(CacheConstants.ONLINE_TOKEN_KEY + token, UserOnlineDTO.class))
            .filter(obj -> obj != null)
            .collect(Collectors.toList());
        //复制和处理 SysUserOnline 对象列表
        Collections.reverse(userOnlineDTOList);
        userOnlineDTOList.removeAll(Collections.singleton(null));
        List<SysUserOnline> userOnlineList = BeanUtil.copyToList(userOnlineDTOList, SysUserOnline.class);
        return userOnlineList;
    }

    @Override
    public void forceLogoutMyself(String tokenId) {
        try {
            // 获取指定账号 id 的 token 集合
            List<String> keys = StpUtil.getTokenValueListByLoginId(StpUtil.getLoginIdAsString());
            keys.stream()
                .filter(key -> key.equals(tokenId))
                .findFirst()
                .ifPresent(key -> StpUtil.kickoutByTokenValue(tokenId));
        } catch (NotLoginException ignored) {
        }
    }
}