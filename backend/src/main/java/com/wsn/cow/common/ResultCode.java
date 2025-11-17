package com.wsn.cow.common;

import lombok.Getter;

/**
 * 返回状态码枚举
 */
@Getter
public enum ResultCode {
    
    /** 成功 */
    SUCCESS(200, "操作成功"),
    
    /** 失败 */
    ERROR(500, "操作失败"),
    
    /** 参数错误 */
    PARAM_ERROR(400, "参数错误"),
    
    /** 未授权 */
    UNAUTHORIZED(401, "未授权"),
    
    /** 禁止访问 */
    FORBIDDEN(403, "禁止访问"),
    
    /** 资源不存在 */
    NOT_FOUND(404, "资源不存在"),
    
    /** 数据库操作失败 */
    DATABASE_ERROR(5001, "数据库操作失败"),
    
    /** 数据不存在 */
    DATA_NOT_EXIST(5002, "数据不存在"),
    
    /** 数据已存在 */
    DATA_ALREADY_EXIST(5003, "数据已存在"),
    
    /** 串口通信异常 */
    SERIAL_ERROR(6001, "串口通信异常"),
    
    /** 数据解析失败 */
    DATA_PARSE_ERROR(6002, "数据解析失败"),
    
    /** 告警生成失败 */
    ALARM_ERROR(7001, "告警生成失败"),
    
    /** 设备控制失败 */
    DEVICE_CONTROL_ERROR(8001, "设备控制失败");
    
    private final Integer code;
    private final String message;
    
    ResultCode(Integer code, String message) {
        this.code = code;
        this.message = message;
    }
}
