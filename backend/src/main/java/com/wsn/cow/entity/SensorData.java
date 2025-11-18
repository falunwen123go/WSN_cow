package com.wsn.cow.entity;

import lombok.Data;
import java.util.Date;

/**
 * 传感器数据实体类
 */
@Data
public class SensorData {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 温度（摄氏度）
     */
    private Double temperature;
    
    /**
     * 湿度（%RH）
     */
    private Double humidity;
    
    /**
     * 氨气浓度（ppm）
     */
    private Double nh3Concentration;
    
    /**
     * 硫化氢浓度（ppm）
     */
    private Double h2sConcentration;
    
    /**
     * 数据状态:0-正常,1-异常
     */
    private Integer dataStatus;
    
    /**
     * 数据采集时间
     */
    private Date collectTime;
    
    /**
     * 数据创建时间(接收时间)
     */
    private Date createTime;
}
