package com.github.lisuiheng.astra.common.exception;

/**
 * 业务异常
 */
public final class ServiceException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * 错误提示
     */
    private String message;

    /**
     * 错误明细，内部调试错误
     */
    private String detailMessage;

    /**
     * 错误码
     */
    private Integer code;

    /**
     * 空构造方法，避免反序列化问题
     */
    public ServiceException() {
    }

    public ServiceException(String message) {
        this.message = message;
    }

    public ServiceException(String message, String detailMessage) {
        this.message = message;
        this.detailMessage = detailMessage;
    }

    public ServiceException(String message, Throwable cause) {
        super(cause);
        this.message = message;
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String message, Integer code) {
        this.message = message;
        this.code = code;
    }

    public ServiceException(String message, Integer code, Throwable cause) {
        super(cause);
        this.message = message;
        this.code = code;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public String getDetailMessage() {
        return detailMessage;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }
}