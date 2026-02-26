package com.github.lisuiheng.astra.sys.listener;

import cn.hutool.http.useragent.UserAgent;
import cn.hutool.http.useragent.UserAgentUtil;
import com.github.lisuiheng.astra.common.constant.Constants;
import com.github.lisuiheng.astra.common.utils.ServletUtils;
import com.github.lisuiheng.astra.common.utils.StringUtils;
import com.github.lisuiheng.astra.common.utils.AddressUtils;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import com.github.lisuiheng.astra.sys.domain.bo.SysLogininforBo;
import com.github.lisuiheng.astra.sys.service.ISysClientService;
import com.github.lisuiheng.astra.sys.service.ISysLogininforService;
import com.github.lisuiheng.astra.common.log.event.LogininforEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * 系统访问日志情况信息 监听器
 *
 * @author Lion Li
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SysLogininforListener {

    private final ISysLogininforService logininforService;
    private final ISysClientService clientService;

    /**
     * 记录登录信息
     *
     * @param logininforEvent 登录事件
     */
    @Async
    @EventListener
    public void recordLogininfor(LogininforEvent logininforEvent) {
        String ip = ServletUtils.getClientIP(logininforEvent.getRequest());
        UserAgent userAgent = UserAgentUtil.parse(logininforEvent.getRequest().getHeader("User-Agent"));

        // 客户端信息
        String clientId = logininforEvent.getRequest().getHeader(LoginHelper.CLIENT_KEY);
        com.github.lisuiheng.astra.sys.domain.vo.SysClientVo client = null;
        if (StringUtils.isNotBlank(clientId)) {
            client = clientService.queryByClientId(clientId);
        }

        String address = AddressUtils.getRealAddressByIP(ip);
        StringBuilder s = new StringBuilder();
        s.append(getBlock(ip));
        s.append(address);
        s.append(getBlock(logininforEvent.getUsername()));
        s.append(getBlock(logininforEvent.getStatus()));
        s.append(getBlock(logininforEvent.getMessage()));
        // 打印信息到日志
        log.info(s.toString(), logininforEvent.getArgs());

        // 封装对象
        SysLogininforBo logininfor = new SysLogininforBo();
        logininfor.setTenantId(logininforEvent.getTenantId());
        logininfor.setUserName(logininforEvent.getUsername());
        if (client != null) {
            logininfor.setClientKey(client.getClientKey());
            logininfor.setDeviceType(client.getDeviceType());
        }
        logininfor.setIpaddr(ip);
        logininfor.setLoginLocation(address);
        logininfor.setBrowser(userAgent.getBrowser().getName());
        logininfor.setOs(userAgent.getOs().getName());
        logininfor.setMsg(logininforEvent.getMessage());
        // 设置登录时间
        logininfor.setLoginTime(new Date());
        // 日志状态
        if (StringUtils.equalsAny(logininforEvent.getStatus(), Constants.LOGIN_SUCCESS, Constants.LOGOUT_SUCCESS)) {
            logininfor.setStatus("0"); // 成功状态
        } else if (Constants.LOGIN_FAIL.equals(logininforEvent.getStatus())) {
            logininfor.setStatus("1"); // 失败状态
        }
        // 插入数据
        logininforService.insertLogininfor(logininfor);
    }

    private String getBlock(Object msg) {
        if (msg == null) {
            msg = "";
        }
        return "[" + msg.toString() + "]";
    }
}