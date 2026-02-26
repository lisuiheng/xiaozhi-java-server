package com.github.lisuiheng.astra.server.user.context;

import com.github.lisuiheng.astra.server.user.model.entity.User;

/**
 * 用户上下文持有器
 */
public class UserContextHolder {

    private static final ThreadLocal<User> userContext = new ThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void setCurrentUser(User user) {
        userContext.set(user);
    }

    /**
     * 获取当前用户
     */
    public static User getCurrentUser() {
        return userContext.get();
    }

    /**
     * 获取当前用户ID
     */
    public static String getCurrentUserId() {
        User user = userContext.get();
        return user != null ? user.getId() : null;
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {
        User user = userContext.get();
        return user != null ? user.getUsername() : null;
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        userContext.remove();
    }
}