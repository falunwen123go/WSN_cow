package com.wsn.cow.service;

import com.wsn.cow.entity.DeviceControlLog;
import com.wsn.cow.entity.DeviceInfo;
import com.wsn.cow.entity.SensorData;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 设备控制服务接口
 */
public interface DeviceControlService {
    
    /**
     * 根据氨气浓度自动控制风机
     * @param data 传感器数据
     * @return 是否执行了控制操作
     */
    boolean checkAndControlByNH3(SensorData data);
    
    /**
     * 根据硫化氢浓度自动控制风机
     * @param data 传感器数据
     * @return 是否执行了控制操作
     */
    boolean checkAndControlByH2S(SensorData data);
    
    /**
     * 根据温度自动控制卷帘/供热设备
     * @param data 传感器数据
     * @return 是否执行了控制操作
     */
    boolean checkAndControlByTemperature(SensorData data);
    
    /**
     * 手动控制设备
     * @param deviceId 设备ID
     * @param action 控制动作：START-启动，STOP-停止
     * @param operator 操作人
     * @return 控制是否成功
     */
    boolean manualControl(String deviceId, String action, String operator);
    
    /**
     * 切换设备控制模式
     * @param deviceId 设备ID
     * @param autoMode 0-手动，1-自动
     * @return 是否成功
     */
    boolean switchAutoMode(String deviceId, Integer autoMode);
    
    /**
     * 获取所有设备列表
     */
    List<DeviceInfo> getAllDevices();
    
    /**
     * 根据设备ID查询设备信息
     */
    DeviceInfo getDeviceById(String deviceId);
    
    /**
     * 根据类型查询设备
     */
    List<DeviceInfo> getDevicesByType(String deviceType);
    
    /**
     * 查询运行中的设备数量
     */
    int getRunningDeviceCount();
    
    /**
     * 分页查询控制日志
     */
    Map<String, Object> getControlLogs(String deviceId, String controlType, 
                                      Date startTime, Date endTime, 
                                      int page, int pageSize);
    
    /**
     * 获取设备最新控制日志
     */
    DeviceControlLog getLatestLog(String deviceId);
    
    /**
     * 执行设备控制并记录日志
     * @param deviceId 设备ID
     * @param action 控制动作
     * @param controlType 控制方式：MANUAL-手动，AUTO-自动
     * @param operator 操作人
     * @param remark 备注
     * @return 控制是否成功
     */
    boolean executeControl(String deviceId, String action, String controlType, 
                          String operator, String remark);
}
