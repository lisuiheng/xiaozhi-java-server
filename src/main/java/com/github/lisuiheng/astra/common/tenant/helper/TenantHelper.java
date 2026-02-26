package com.github.lisuiheng.astra.common.tenant.helper;

import cn.hutool.core.convert.Convert;
import com.github.lisuiheng.astra.common.utils.SpringUtils;

import java.util.Stack;
import java.util.function.Supplier;

/**
 * 租户助手类
 */
public class TenantHelper {

    private static final ThreadLocal<String> TEMP_DYNAMIC_TENANT = new ThreadLocal<>();

    /**
     * 是否启用租户
     *
     * @return true/false
     */
    public static boolean isEnable() {
        return Convert.toBool(SpringUtils.getProperty("tenant.enable"), false);
    }

    /**
     * 在动态租户中执行
     *
     * @param handle 处理执行方法
     */
    public static <T> T dynamic(String tenantId, Supplier<T> handle) {
        setDynamic(tenantId);
        try {
            return handle.get();
        } finally {
            clearDynamic();
        }
    }

    /**
     * 设置动态租户
     *
     * @param tenantId 租户id
     */
    public static void setDynamic(String tenantId) {
        TEMP_DYNAMIC_TENANT.set(tenantId);
    }

    /**
     * 清除动态租户
     */
    public static void clearDynamic() {
        TEMP_DYNAMIC_TENANT.remove();
    }

    /**
     * 获取当前租户ID
     *
     * @return 租户ID
     */
    public static String getTenantId() {
        return TEMP_DYNAMIC_TENANT.get();
    }
}