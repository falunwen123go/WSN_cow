package com.wsn.cow.controller;

import com.wsn.cow.common.Result;
import com.wsn.cow.entity.DeviceControlLog;
import com.wsn.cow.entity.DeviceInfo;
import com.wsn.cow.service.DeviceControlService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设备控制接口
 */
@RestController
@RequestMapping("/api/device")
@CrossOrigin(origins = "*")
public class DeviceController {
    
    private static final Logger logger = LoggerFactory.getLogger(DeviceController.class);
    
    @Autowired
    private DeviceControlService deviceControlService;
    
    /**
     * 获取设备列表
     */
    @GetMapping("/list")
    public Result<List<DeviceInfo>> getDeviceList(@RequestParam(required = false) String deviceType) {
        try {
            List<DeviceInfo> devices;
            if (deviceType != null && !deviceType.isEmpty()) {
                devices = deviceControlService.getDevicesByType(deviceType);
            } else {
                devices = deviceControlService.getAllDevices();
            }
            return Result.success(devices);
        } catch (Exception e) {
            logger.error("查询设备列表失败", e);
            return Result.error("查询设备列表失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取设备详情
     */
    @GetMapping("/{deviceId}")
    public Result<DeviceInfo> getDeviceDetail(@PathVariable String deviceId) {
        try {
            DeviceInfo device = deviceControlService.getDeviceById(deviceId);
            if (device == null) {
                return Result.error("设备不存在");
            }
            return Result.success(device);
        } catch (Exception e) {
            logger.error("查询设备详情失败: deviceId={}", deviceId, e);
            return Result.error("查询设备详情失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取运行中设备数量
     */
    @GetMapping("/running/count")
    public Result<Integer> getRunningCount() {
        try {
            int count = deviceControlService.getRunningDeviceCount();
            return Result.success(count);
        } catch (Exception e) {
            logger.error("查询运行中设备数量失败", e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
    
    /**
     * 手动控制设备
     */
    @PostMapping("/control")
    public Result<String> controlDevice(@RequestBody Map<String, String> params) {
        try {
            String deviceId = params.get("deviceId");
            String action = params.get("action");
            String operator = params.getOrDefault("operator", "Admin");
            
            if (deviceId == null || action == null) {
                return Result.error("参数不完整");
            }
            
            if (!"START".equals(action) && !"STOP".equals(action)) {
                return Result.error("控制动作无效，只支持START或STOP");
            }
            
            boolean success = deviceControlService.manualControl(deviceId, action, operator);
            if (success) {
                return Result.success("设备控制成功", null);
            } else {
                return Result.error("设备控制失败");
            }
        } catch (Exception e) {
            logger.error("控制设备失败", e);
            return Result.error("控制设备失败：" + e.getMessage());
        }
    }
    
    /**
     * 切换控制模式
     */
    @PutMapping("/{deviceId}/mode")
    public Result<String> switchMode(@PathVariable String deviceId, 
                                   @RequestBody Map<String, Integer> params) {
        try {
            Integer autoMode = params.get("autoMode");
            if (autoMode == null || (autoMode != 0 && autoMode != 1)) {
                return Result.error("控制模式参数错误，应为0(手动)或1(自动)");
            }
            
            boolean success = deviceControlService.switchAutoMode(deviceId, autoMode);
            if (success) {
                return Result.success("切换控制模式成功", null);
            } else {
                return Result.error("切换控制模式失败");
            }
        } catch (Exception e) {
            logger.error("切换控制模式失败: deviceId={}", deviceId, e);
            return Result.error("切换控制模式失败：" + e.getMessage());
        }
    }
    
    /**
     * 查询控制日志
     */
    @GetMapping("/log")
    public Result<Map<String, Object>> getControlLogs(
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String controlType,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize) {
        try {
            Map<String, Object> result = deviceControlService.getControlLogs(
                deviceId, controlType, startTime, endTime, page, pageSize);
            return Result.success(result);
        } catch (Exception e) {
            logger.error("查询控制日志失败", e);
            return Result.error("查询控制日志失败：" + e.getMessage());
        }
    }
    
    /**
     * 获取设备最新日志
     */
    @GetMapping("/{deviceId}/latest-log")
    public Result<DeviceControlLog> getLatestLog(@PathVariable String deviceId) {
        try {
            DeviceControlLog log = deviceControlService.getLatestLog(deviceId);
            return Result.success(log);
        } catch (Exception e) {
            logger.error("查询最新日志失败: deviceId={}", deviceId, e);
            return Result.error("查询失败：" + e.getMessage());
        }
    }
}
