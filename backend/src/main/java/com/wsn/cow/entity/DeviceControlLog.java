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
    private String action;
    
    /**
     * 控制方式：MANUAL-手动，AUTO-自动
     */
    private String controlType;
    
    /**
     * 操作前状态
     */
    private Integer beforeStatus;
    
    /**
     * 操作后状态
     */
    private Integer afterStatus;
    
    /**
     * 操作人
     */
    private String operator;
    
    /**
     * 操作时间
     */
    private Date operateTime;
    
    /**
     * 备注
     */
    private String remark;
}
