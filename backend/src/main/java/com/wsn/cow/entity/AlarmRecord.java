package com.wsn.cow.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import java.util.Date;

/**
 * 告警记录实体类
 */
@Data
public class AlarmRecord {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 告警类型：TEMP-温度，HUMI-湿度，NH3-氨气，H2S-硫化氢
     */
    private String alarmType;
    
    /**
     * 告警级别：1-一般，2-严重，3-紧急
     */
    private Integer alarmLevel;
    
    /**
     * 当前值
     */
    private Double currentValue;
    
    /**
     * 阈值
     */
    private Double threshold;
    
    /**
     * 告警时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date alarmTime;
    
    /**
     * 处理状态：0-未处理，1-已处理
     */
    private Integer handleStatus;
    
    /**
     * 处理时间
     */
    private Date handleTime;
    
    /**
     * 处理备注
     */
    private String handleRemark;
}
