package com.github.lisuiheng.astra.common.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

/**
 * 响应信息主体
 */
@Data
@NoArgsConstructor
@Schema(description = "统一响应结果")
public class R<T> implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(description = "响应码")
    private int code;

    @Schema(description = "响应消息")
    private String msg;

    @Schema(description = "响应数据")
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public R(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public R(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    // 成功的静态工厂方法
    public static <T> R<T> ok() {
        return new R<>(200, "操作成功");
    }

    public static <T> R<T> ok(T data) {
        return new R<>(200, "操作成功", data);
    }

    public static <T> R<T> ok(String msg) {
        return new R<>(200, msg);
    }

    public static <T> R<T> ok(String msg, T data) {
        return new R<>(200, msg, data);
    }

    public static <T> R<T> ok(int code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> ok(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    // 失败的静态工厂方法
    public static <T> R<T> fail() {
        return new R<>(500, "操作失败");
    }

    public static <T> R<T> fail(String msg) {
        return new R<>(500, msg);
    }

    public static <T> R<T> fail(int code, String msg) {
        return new R<>(code, msg);
    }

    public static <T> R<T> fail(T data) {
        return new R<>(500, "操作失败", data);
    }

    public static <T> R<T> fail(String msg, T data) {
        return new R<>(500, msg, data);
    }

    public static <T> R<T> fail(int code, String msg, T data) {
        return new R<>(code, msg, data);
    }

    // 警告的静态工厂方法
    public static <T> R<T> warn(String msg) {
        return new R<>(301, msg);
    }

    // 判断方法
    public boolean isSuccess() {
        return this.code == 200;
    }

    /**
     * 判断数据是否为成功响应
     */
    @JsonIgnore
    public static <T> Boolean isOk(T data) {
        if (data instanceof R) {
            R<?> r = (R<?>) data;
            return r.getCode() == 200;
        }
        return true;
    }

    /**
     * 判断数据是否为失败响应
     */
    @JsonIgnore
    public static <T> Boolean isError(T data) {
        if (data instanceof R) {
            R<?> r = (R<?>) data;
            return r.getCode() != 200;
        }
        return false;
    }
}