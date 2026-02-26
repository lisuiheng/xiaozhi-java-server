package com.github.lisuiheng.astra.server.user.filter;

import com.github.lisuiheng.astra.common.util.JwtUtil;
import com.github.lisuiheng.astra.server.user.context.UserContextHolder;
import com.github.lisuiheng.astra.server.user.model.entity.User;
import com.github.lisuiheng.astra.server.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null && JwtUtil.verifyToken(token) && !JwtUtil.isTokenExpired(token)) {
            String userId = JwtUtil.getUserIdFromToken(token);
            if (userId != null) {
                try {
                    User user = userService.getById(userId);
                    if (user != null) {
                        UserContextHolder.setCurrentUser(user);
                    }
                } catch (Exception e) {
                    log.error("获取用户信息失败: userId={}", userId, e);
                }
            }
        }
        
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContextHolder.clear();
        }
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}