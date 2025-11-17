package com.wsn.cow.common;

/**
 * 系统常量类
 */
public class Constants {
    
    // ==================== 告警相关常量 ====================
    /** 告警类型：温度 */
    public static final String ALARM_TYPE_TEMPERATURE = "temperature";
    
    /** 告警类型：湿度 */
    public static final String ALARM_TYPE_HUMIDITY = "humidity";
    
    /** 告警类型：氨气 */
    public static final String ALARM_TYPE_NH3 = "nh3";
    
    /** 告警类型：硫化氢 */
    public static final String ALARM_TYPE_H2S = "h2s";
    
    /** 告警级别：警告 */
    public static final Integer ALARM_LEVEL_WARNING = 1;
    
    /** 告警级别：严重 */
    public static final Integer ALARM_LEVEL_CRITICAL = 2;
    
    /** 告警级别：紧急 */
    public static final Integer ALARM_LEVEL_URGENT = 3;
    
    /** 告警状态：未处理 */
    public static final Integer ALARM_STATUS_UNHANDLED = 0;
    
    /** 告警状态：已处理 */
    public static final Integer ALARM_STATUS_HANDLED = 1;
    
    // ==================== 设备相关常量 ====================
    /** 设备类型：风机 */
    public static final String DEVICE_TYPE_FAN = "fan";
    
    /** 设备类型：卷帘 */
    public static final String DEVICE_TYPE_CURTAIN = "curtain";
    
    /** 设备类型：供热系统 */
    public static final String DEVICE_TYPE_HEATING = "heating";
    
    /** 设备状态：关闭 */
    public static final Integer DEVICE_STATUS_OFF = 0;
    
    /** 设备状态：开启 */
    public static final Integer DEVICE_STATUS_ON = 1;
    
    /** 控制模式：手动 */
    public static final Integer CONTROL_MODE_MANUAL = 0;
    
    /** 控制模式：自动 */
    public static final Integer CONTROL_MODE_AUTO = 1;
    
    /** 控制动作：开启 */
    public static final String CONTROL_ACTION_ON = "on";
    
    /** 控制动作：关闭 */
    public static final String CONTROL_ACTION_OFF = "off";
    
    // ==================== 节点相关常量 ====================
    /** 节点状态：离线 */
    public static final Integer NODE_STATUS_OFFLINE = 0;
    
    /** 节点状态：在线 */
    public static final Integer NODE_STATUS_ONLINE = 1;
    
    /** 节点离线超时时间(秒) */
    public static final Integer NODE_OFFLINE_TIMEOUT = 300;
    
    // ==================== 数据相关常量 ====================
    /** 数据状态：正常 */
    public static final Integer DATA_STATUS_NORMAL = 0;
    
    /** 数据状态：异常 */
    public static final Integer DATA_STATUS_ABNORMAL = 1;
    
    // ==================== 用户相关常量 ====================
    /** 用户角色：管理员 */
    public static final String USER_ROLE_ADMIN = "admin";
    
    /** 用户角色：普通用户 */
    public static final String USER_ROLE_USER = "user";
    
    /** 用户状态：禁用 */
    public static final Integer USER_STATUS_DISABLED = 0;
    
    /** 用户状态：启用 */
    public static final Integer USER_STATUS_ENABLED = 1;
    
    // ==================== 系统配置键 ====================
    /** 配置键：数据采集周期 */
    public static final String CONFIG_DATA_COLLECT_INTERVAL = "system.data.collect.interval";
    
    /** 配置键：告警去重间隔 */
    public static final String CONFIG_ALARM_DEDUPLICATE_INTERVAL = "system.alarm.deduplicate.interval";
    
    /** 配置键：节点离线超时 */
    public static final String CONFIG_NODE_OFFLINE_TIMEOUT = "system.node.offline.timeout";
    
    // ==================== WebSocket主题 ====================
    /** WebSocket主题：实时数据 */
    public static final String WS_TOPIC_DATA = "/topic/data";
    
    /** WebSocket主题：告警 */
    public static final String WS_TOPIC_ALARM = "/topic/alarm";
    
    /** WebSocket主题：设备状态 */
    public static final String WS_TOPIC_DEVICE = "/topic/device";
    
    // ==================== 默认值 ====================
    /** 默认分页大小 */
    public static final Integer DEFAULT_PAGE_SIZE = 10;
    
    /** 最大分页大小 */
    public static final Integer MAX_PAGE_SIZE = 100;
}
