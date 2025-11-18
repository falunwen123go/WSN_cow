package com.wsn.cow.common;

import lombok.Data;
import java.io.Serializable;

/**
 * 统一返回结果类
 * @param <T> 数据类型
 */
@Data
public class Result<T> implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /** 状态码 */
    private Integer code;
    
    /** 返回消息 */
    private String message;
    
    /** 返回数据 */
    private T data;
    
    /** 时间戳 */
    private Long timestamp;
    
    public Result() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
    /**
     * 成功返回（无数据）
     */
    public static <T> Result<T> success() {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), null);
    }
    
    /**
     * 成功返回（带数据）
     */
    public static <T> Result<T> success(T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), ResultCode.SUCCESS.getMessage(), data);
    }
    
    /**
     * 成功返回（自定义消息和数据）
     */
    public static <T> Result<T> success(String message, T data) {
        return new Result<>(ResultCode.SUCCESS.getCode(), message, data);
    }
    
    /**
     * 失败返回（使用默认错误码）
     */
    public static <T> Result<T> error() {
        return new Result<>(ResultCode.ERROR.getCode(), ResultCode.ERROR.getMessage(), null);
    }
    
    /**
     * 失败返回（自定义消息）
     */
    public static <T> Result<T> error(String message) {
        return new Result<>(ResultCode.ERROR.getCode(), message, null);
    }
    
    /**
     * 失败返回（自定义错误码和消息）
     */
    public static <T> Result<T> error(Integer code, String message) {
        return new Result<>(code, message, null);
    }
    
    /**
     * 失败返回（使用ResultCode枚举）
     */
    public static <T> Result<T> error(ResultCode resultCode) {
        return new Result<>(resultCode.getCode(), resultCode.getMessage(), null);
    }
    
    /**
     * 资源未找到（404）
     */
    public static <T> Result<T> notFound(String message) {
        return new Result<>(ResultCode.NOT_FOUND.getCode(), message, null);
    }
    
    /**
     * 错误请求（400）
     */
    public static <T> Result<T> badRequest(String message) {
        return new Result<>(ResultCode.PARAM_ERROR.getCode(), message, null);
    }
}
