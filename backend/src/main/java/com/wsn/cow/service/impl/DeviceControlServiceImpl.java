package com.wsn.cow.service.impl;

import com.wsn.cow.common.PageResult;
import com.wsn.cow.entity.DeviceControlLog;
import com.wsn.cow.entity.DeviceInfo;
import com.wsn.cow.entity.SensorData;
import com.wsn.cow.entity.SystemConfig;
import com.wsn.cow.mapper.DeviceControlLogMapper;
import com.wsn.cow.mapper.DeviceInfoMapper;
import com.wsn.cow.mapper.SystemConfigMapper;
import com.wsn.cow.service.DeviceControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 设备控制服务实现类
 */
@Service
public class DeviceControlServiceImpl implements DeviceControlService {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceControlServiceImpl.class);
    
    @Autowired
    private DeviceInfoMapper deviceInfoMapper;
    
    @Autowired
    private DeviceControlLogMapper deviceControlLogMapper;
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    // 设备类型常量
    private static final String DEVICE_TYPE_FAN = "FAN";
    private static final String DEVICE_TYPE_CURTAIN = "CURTAIN";
    private static final String DEVICE_TYPE_HEATING = "HEATING";
    
    // 设备状态常量
    private static final int STATUS_OFF = 0;
    private static final int STATUS_ON = 1;
    private static final int STATUS_FAULT = 2;
    
    // 控制动作常量
    private static final String ACTION_START = "START";
    private static final String ACTION_STOP = "STOP";
    
    // 控制类型常量
    private static final String CONTROL_TYPE_MANUAL = "MANUAL";
    private static final String CONTROL_TYPE_AUTO = "AUTO";
    
    @Override
    public boolean checkAndControlByNH3(SensorData data) {
        if (data == null || data.getNh3Concentration() == null) {
            return false;
        }
        
        try {
            // 获取氨气阈值配置
            Double nh3Warning = getConfigValue("alarm.nh3.warning", 25.0);
            Double nh3Critical = getConfigValue("alarm.nh3.critical", 40.0);
            
            // 查询风机设备（自动模式）
            List<DeviceInfo> fans = deviceInfoMapper.selectByType(DEVICE_TYPE_FAN);
            boolean controlled = false;
            
            for (DeviceInfo fan : fans) {
                // 只控制自动模式的设备
                if (fan.getAutoMode() != null && fan.getAutoMode() == 1) {
                    // 超过严重阈值，启动风机
                    if (data.getNh3Concentration() >= nh3Critical && fan.getStatus() == STATUS_OFF) {
                        executeControl(fan.getDeviceId(), ACTION_START, CONTROL_TYPE_AUTO, 
                                     "System", "NH3浓度达到严重级别(" + data.getNh3Concentration() + "ppm)，自动启动风机");
                        controlled = true;
                        logger.info("NH3浓度严重超标，自动启动风机: {}", fan.getDeviceId());
                    }
                    // 超过警告阈值，启动风机
                    else if (data.getNh3Concentration() >= nh3Warning && fan.getStatus() == STATUS_OFF) {
                        executeControl(fan.getDeviceId(), ACTION_START, CONTROL_TYPE_AUTO, 
                                     "System", "NH3浓度达到警告级别(" + data.getNh3Concentration() + "ppm)，自动启动风机");
                        controlled = true;
                        logger.info("NH3浓度超标，自动启动风机: {}", fan.getDeviceId());
                    }
                    // 恢复正常，关闭风机（留有5ppm缓冲）
                    else if (data.getNh3Concentration() < (nh3Warning - 5) && fan.getStatus() == STATUS_ON) {
                        executeControl(fan.getDeviceId(), ACTION_STOP, CONTROL_TYPE_AUTO, 
                                     "System", "NH3浓度恢复正常(" + data.getNh3Concentration() + "ppm)，自动关闭风机");
                        controlled = true;
                        logger.info("NH3浓度恢复正常，自动关闭风机: {}", fan.getDeviceId());
                    }
                }
            }
            
            return controlled;
        } catch (Exception e) {
            logger.error("根据NH3浓度自动控制失败", e);
            return false;
        }
    }
    
    @Override
    public boolean checkAndControlByH2S(SensorData data) {
        if (data == null || data.getH2sConcentration() == null) {
            return false;
        }
        
        try {
            // 获取硫化氢阈值配置
            Double h2sWarning = getConfigValue("alarm.h2s.warning", 10.0);
            Double h2sCritical = getConfigValue("alarm.h2s.critical", 20.0);
            
            // 查询风机设备（自动模式）
            List<DeviceInfo> fans = deviceInfoMapper.selectByType(DEVICE_TYPE_FAN);
            boolean controlled = false;
            
            for (DeviceInfo fan : fans) {
                // 只控制自动模式的设备
                if (fan.getAutoMode() != null && fan.getAutoMode() == 1) {
                    // 超过严重阈值，启动风机
                    if (data.getH2sConcentration() >= h2sCritical && fan.getStatus() == STATUS_OFF) {
                        executeControl(fan.getDeviceId(), ACTION_START, CONTROL_TYPE_AUTO, 
                                     "System", "H2S浓度达到严重级别(" + data.getH2sConcentration() + "ppm)，自动启动风机");
                        controlled = true;
                        logger.info("H2S浓度严重超标，自动启动风机: {}", fan.getDeviceId());
                    }
                    // 超过警告阈值，启动风机
                    else if (data.getH2sConcentration() >= h2sWarning && fan.getStatus() == STATUS_OFF) {
                        executeControl(fan.getDeviceId(), ACTION_START, CONTROL_TYPE_AUTO, 
                                     "System", "H2S浓度达到警告级别(" + data.getH2sConcentration() + "ppm)，自动启动风机");
                        controlled = true;
                        logger.info("H2S浓度超标，自动启动风机: {}", fan.getDeviceId());
                    }
                    // 恢复正常，关闭风机（留有2ppm缓冲）
                    else if (data.getH2sConcentration() < (h2sWarning - 2) && fan.getStatus() == STATUS_ON) {
                        executeControl(fan.getDeviceId(), ACTION_STOP, CONTROL_TYPE_AUTO, 
                                     "System", "H2S浓度恢复正常(" + data.getH2sConcentration() + "ppm)，自动关闭风机");
                        controlled = true;
                        logger.info("H2S浓度恢复正常，自动关闭风机: {}", fan.getDeviceId());
                    }
                }
            }
            
            return controlled;
        } catch (Exception e) {
            logger.error("根据H2S浓度自动控制失败", e);
            return false;
        }
    }
    
    @Override
    public boolean checkAndControlByTemperature(SensorData data) {
        if (data == null || data.getTemperature() == null) {
            return false;
        }
        
        try {
            // 获取温度阈值配置
            Double tempLow = getConfigValue("alarm.temp.low.warning", 5.0);
            Double tempHigh = getConfigValue("alarm.temp.high.warning", 35.0);
            
            boolean controlled = false;
            
            // 温度过低，启动加热设备
            if (data.getTemperature() < tempLow) {
                List<DeviceInfo> heatings = deviceInfoMapper.selectByType(DEVICE_TYPE_HEATING);
                for (DeviceInfo heating : heatings) {
                    if (heating.getAutoMode() != null && heating.getAutoMode() == 1 
                        && heating.getStatus() == STATUS_OFF) {
                        executeControl(heating.getDeviceId(), ACTION_START, CONTROL_TYPE_AUTO, 
                                     "System", "温度过低(" + data.getTemperature() + "°C)，自动启动加热设备");
                        controlled = true;
                        logger.info("温度过低，自动启动加热设备: {}", heating.getDeviceId());
                    }
                }
            }
            // 温度恢复，关闭加热设备（留有2度缓冲）
            else if (data.getTemperature() > (tempLow + 2)) {
                List<DeviceInfo> heatings = deviceInfoMapper.selectByType(DEVICE_TYPE_HEATING);
                for (DeviceInfo heating : heatings) {
                    if (heating.getAutoMode() != null && heating.getAutoMode() == 1 
                        && heating.getStatus() == STATUS_ON) {
                        executeControl(heating.getDeviceId(), ACTION_STOP, CONTROL_TYPE_AUTO, 
                                     "System", "温度恢复正常(" + data.getTemperature() + "°C)，自动关闭加热设备");
                        controlled = true;
                        logger.info("温度恢复正常，自动关闭加热设备: {}", heating.getDeviceId());
                    }
                }
            }
            
            // 温度过高，打开卷帘通风
            if (data.getTemperature() > tempHigh) {
                List<DeviceInfo> curtains = deviceInfoMapper.selectByType(DEVICE_TYPE_CURTAIN);
                for (DeviceInfo curtain : curtains) {
                    if (curtain.getAutoMode() != null && curtain.getAutoMode() == 1 
                        && curtain.getStatus() == STATUS_OFF) {
                        executeControl(curtain.getDeviceId(), ACTION_START, CONTROL_TYPE_AUTO, 
                                     "System", "温度过高(" + data.getTemperature() + "°C)，自动打开卷帘");
                        controlled = true;
                        logger.info("温度过高，自动打开卷帘: {}", curtain.getDeviceId());
                    }
                }
            }
            // 温度恢复，关闭卷帘（留有2度缓冲）
            else if (data.getTemperature() < (tempHigh - 2)) {
                List<DeviceInfo> curtains = deviceInfoMapper.selectByType(DEVICE_TYPE_CURTAIN);
                for (DeviceInfo curtain : curtains) {
                    if (curtain.getAutoMode() != null && curtain.getAutoMode() == 1 
                        && curtain.getStatus() == STATUS_ON) {
                        executeControl(curtain.getDeviceId(), ACTION_STOP, CONTROL_TYPE_AUTO, 
                                     "System", "温度恢复正常(" + data.getTemperature() + "°C)，自动关闭卷帘");
                        controlled = true;
                        logger.info("温度恢复正常，自动关闭卷帘: {}", curtain.getDeviceId());
                    }
                }
            }
            
            return controlled;
        } catch (Exception e) {
            logger.error("根据温度自动控制失败", e);
            return false;
        }
    }
    
    @Override
    @Transactional
    public boolean manualControl(String deviceId, String action, String operator) {
        return executeControl(deviceId, action, CONTROL_TYPE_MANUAL, operator, "手动控制");
    }
    
    @Override
    @Transactional
    public boolean switchAutoMode(String deviceId, Integer autoMode) {
        try {
            int result = deviceInfoMapper.updateAutoMode(deviceId, autoMode);
            if (result > 0) {
                logger.info("切换设备控制模式成功: deviceId={}, autoMode={}", deviceId, autoMode);
                return true;
            }
            return false;
        } catch (Exception e) {
            logger.error("切换设备控制模式失败", e);
            return false;
        }
    }
    
    @Override
    public List<DeviceInfo> getAllDevices() {
        try {
            return deviceInfoMapper.selectAll();
        } catch (Exception e) {
            logger.error("查询设备列表失败", e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public DeviceInfo getDeviceById(String deviceId) {
        try {
            return deviceInfoMapper.selectByDeviceId(deviceId);
        } catch (Exception e) {
            logger.error("查询设备信息失败: deviceId={}", deviceId, e);
            return null;
        }
    }
    
    @Override
    public List<DeviceInfo> getDevicesByType(String deviceType) {
        try {
            return deviceInfoMapper.selectByType(deviceType);
        } catch (Exception e) {
            logger.error("根据类型查询设备失败: deviceType={}", deviceType, e);
            return new ArrayList<>();
        }
    }
    
    @Override
    public int getRunningDeviceCount() {
        try {
            List<DeviceInfo> devices = deviceInfoMapper.selectByStatus(STATUS_ON);
            return devices != null ? devices.size() : 0;
        } catch (Exception e) {
            logger.error("查询运行中设备数量失败", e);
            return 0;
        }
    }
    
    @Override
    public Map<String, Object> getControlLogs(String deviceId, String controlType, 
                                              Date startTime, Date endTime, 
                                              int page, int pageSize) {
        Map<String, Object> result = new HashMap<>();
        try {
            int offset = (page - 1) * pageSize;
            List<DeviceControlLog> logs = deviceControlLogMapper.selectPage(
                deviceId, controlType, startTime, endTime, offset, pageSize);
            int total = deviceControlLogMapper.countByCondition(
                deviceId, controlType, startTime, endTime);
            
            result.put("list", logs);
            result.put("total", total);
            result.put("page", page);
            result.put("pageSize", pageSize);
            result.put("totalPages", (total + pageSize - 1) / pageSize);
        } catch (Exception e) {
            logger.error("查询控制日志失败", e);
            result.put("list", new ArrayList<>());
            result.put("total", 0);
        }
        return result;
    }
    
    @Override
    public DeviceControlLog getLatestLog(String deviceId) {
        try {
            return deviceControlLogMapper.selectLatestByDeviceId(deviceId);
        } catch (Exception e) {
            logger.error("查询最新控制日志失败: deviceId={}", deviceId, e);
            return null;
        }
    }
    
    @Override
    @Transactional
    public boolean executeControl(String deviceId, String action, String controlType, 
                                  String operator, String remark) {
        try {
            // 查询设备信息
            DeviceInfo device = deviceInfoMapper.selectByDeviceId(deviceId);
            if (device == null) {
                logger.error("设备不存在: deviceId={}", deviceId);
                return false;
            }
            
            // 检查设备是否故障
            if (device.getStatus() == STATUS_FAULT) {
                logger.error("设备故障，无法控制: deviceId={}", deviceId);
                return false;
            }
            
            // 记录操作前状态
            Integer beforeStatus = device.getStatus();
            
            // 执行控制动作
            Integer afterStatus = beforeStatus;
            if (ACTION_START.equals(action)) {
                afterStatus = STATUS_ON;
            } else if (ACTION_STOP.equals(action)) {
                afterStatus = STATUS_OFF;
            }
            
            // 更新设备状态
            int updateResult = deviceInfoMapper.updateStatus(deviceId, afterStatus);
            if (updateResult <= 0) {
                logger.error("更新设备状态失败: deviceId={}", deviceId);
                return false;
            }
            
            // 记录控制日志
            DeviceControlLog log = new DeviceControlLog();
            log.setDeviceId(deviceId);
            log.setControlAction(action);
            log.setControlMode("MANUAL".equals(controlType) ? 0 : 1);
            log.setOperator(operator);
            log.setControlTime(new Date());
            log.setReason(remark);
            
            deviceControlLogMapper.insert(log);
            
            logger.info("设备控制成功: deviceId={}, action={}, controlType={}, operator={}", 
                       deviceId, action, controlType, operator);
            return true;
            
        } catch (Exception e) {
            logger.error("执行设备控制失败", e);
            return false;
        }
    }
    
    /**
     * 获取配置值
     */
    private Double getConfigValue(String key, Double defaultValue) {
        try {
            SystemConfig config = systemConfigMapper.selectByKey(key);
            if (config != null && config.getConfigValue() != null) {
                return Double.parseDouble(config.getConfigValue());
            }
        } catch (Exception e) {
            logger.warn("获取配置失败: key={}, 使用默认值: {}", key, defaultValue);
        }
        return defaultValue;
    }
}
