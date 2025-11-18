package com.wsn.cow.entity;

import lombok.Data;
import java.util.Date;

/**
 * 节点信息实体类
 */
@Data
public class NodeInfo {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 节点名称
     */
    private String nodeName;
    
    /**
     * 安装位置
     */
    private String location;
    
    /**
     * 节点状态：0-离线，1-在线，2-故障
     */
    private Integer status;
    
    /**
     * 电池电量（%）
     */
    private Integer batteryLevel;
    
    /**
     * 信号强度
     */
    private Integer signalStrength;
    
    /**
     * 最后通信时间
     */
    private Date lastCommTime;
    
    /**
     * 安装日期
     */
    private Date installDate;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 最后在线时间
     */
    private Date lastOnlineTime;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新时间
     */
    private Date updateTime;
}
