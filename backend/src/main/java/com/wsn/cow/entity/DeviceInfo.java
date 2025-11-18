package com.wsn.cow.entity;

import lombok.Data;
import java.util.Date;

/**
 * 设备信息实体类
 */
@Data
public class DeviceInfo {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 设备ID
     */
    private String deviceId;
    
    /**
     * 设备名称
     */
    private String deviceName;
    
    /**
     * 设备类型：FAN-风机，CURTAIN-卷帘，HEATING-加热，SPRAY-喷淋
     */
    private String deviceType;
    
    /**
     * 设备状态：0-关闭，1-运行
     */
    private Integer status;
    
    /**
     * 自动模式：0-手动，1-自动
     */
    private Integer autoMode;
    
    /**
     * 控制模式
     */
    private Integer controlMode;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
