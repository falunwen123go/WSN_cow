package com.wsn.cow.entity;

import lombok.Data;
import java.util.Date;

/**
 * 系统配置实体类
 */
@Data
public class SystemConfig {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 配置键
     */
    private String configKey;
    
    /**
     * 配置值
     */
    private String configValue;
    
    /**
     * 配置说明
     */
    private String description;
    
    /**
     * 配置类型：ALARM-告警阈值，SYSTEM-系统配置，DEVICE-设备配置
     */
    private String configType;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
