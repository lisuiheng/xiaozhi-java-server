package com.github.lisuiheng.astra.server.user.interceptor;

import com.github.lisuiheng.astra.server.user.context.UserContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AuthenticationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取用户ID
        String userId = request.getHeader("X-User-Id");
        
        if (userId != null && !userId.isEmpty()) {
            // 在实际应用中，这里应该从数据库或缓存中获取用户信息
            // 为了简化，我们这里只是记录日志
            log.debug("Authenticated user: {}", userId);
        }
        
        return true;
    }
}