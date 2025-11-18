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
     * 安装位置
     */
    private String location;
    
    /**
     * 设备状态：0-关闭，1-开启，2-故障
     */
    private Integer status;
    
    /**
     * 自动控制模式：0-手动，1-自动
     */
    private Integer autoMode;
    
    /**
     * 安装日期
     */
    private Date installDate;
    
    /**
     * 最后操作时间
     */
    private Date lastOpTime;
    
    /**
     * 备注
     */
    private String remark;
}
