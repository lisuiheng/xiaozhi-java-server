package com.github.lisuiheng.astra.sys.controller;

import cn.dev33.satoken.annotation.SaIgnore;
import cn.dev33.satoken.stp.StpUtil;
import com.github.lisuiheng.astra.common.domain.R;
import com.github.lisuiheng.astra.common.sse.core.SseEmitterManager;
import com.github.lisuiheng.astra.common.satoken.utils.LoginHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

/**
 * SSE 控制器
 *
 * @author 
 */
@RestController
@ConditionalOnProperty(value = "sse.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class SseController implements DisposableBean {

    private final SseEmitterManager sseEmitterManager;

    /**
     * 建立 SSE 连接
     */
    @GetMapping(value = "/resource/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        if (!StpUtil.isLogin()) {
            return null;
        }
        String tokenValue = StpUtil.getTokenValue();
        Long userId = LoginHelper.getUserId();
        return sseEmitterManager.connect(userId, tokenValue);
    }

    /**
     * 关闭 SSE 连接
     */
    @SaIgnore
    @GetMapping(value = "/resource/sse/close")
    public R<Void> close() {
        String tokenValue = StpUtil.getTokenValue();
        Long userId = LoginHelper.getUserId();
        sseEmitterManager.disconnect(userId, tokenValue);
        return R.ok();
    }

    /**
     * 清理资源
     */
    @Override
    public void destroy() throws Exception {
        // 销毁时不需要做什么 此方法避免无用操作报错
    }
}