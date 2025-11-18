package com.wsn.cow.entity;

import lombok.Data;
import java.util.Date;

/**
 * 设备控制日志实体类
 */
@Data
public class DeviceControlLog {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 控制动作：START-启动，STOP-停止，ADJUST-调节
     */
    private String controlAction;
    
    /**
     * 控制模式:0-手动,1-自动
     */
    private Integer controlMode;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 控制时间
     */
    private Date controlTime;
    
    /**
     * 原因/备注
     */
    private String reason;
}
