package com.wsn.cow.util;

import java.util.regex.Pattern;

/**
 * 数据校验工具类
 */
public class ValidationUtils {
    
    /**
     * 节点ID格式：NODE_XXX
     */
    private static final Pattern NODE_ID_PATTERN = Pattern.compile("^NODE_\\d{3}$");
    
    /**
     * 设备ID格式：XXX_XXX
     */
    private static final Pattern DEVICE_ID_PATTERN = Pattern.compile("^[A-Z]+_\\d{3}$");
    
    /**
     * 校验节点ID格式
     */
    public static boolean isValidNodeId(String nodeId) {
        return nodeId != null && NODE_ID_PATTERN.matcher(nodeId).matches();
    }
    
    /**
     * 校验设备ID格式
     */
    public static boolean isValidDeviceId(String deviceId) {
        return deviceId != null && DEVICE_ID_PATTERN.matcher(deviceId).matches();
    }
    
    /**
     * 校验温度值范围（0-50℃）
     */
    public static boolean isValidTemperature(Double temperature) {
        return temperature != null && temperature >= 0 && temperature <= 50;
    }
    
    /**
     * 校验湿度值范围（20-95%）
     */
    public static boolean isValidHumidity(Double humidity) {
        return humidity != null && humidity >= 20 && humidity <= 95;
    }
    
    /**
     * 校验氨气浓度范围（0-100ppm）
     */
    public static boolean isValidNH3(Double nh3) {
        return nh3 != null && nh3 >= 0 && nh3 <= 100;
    }
    
    /**
     * 校验硫化氢浓度范围（0-50ppm）
     */
    public static boolean isValidH2S(Double h2s) {
        return h2s != null && h2s >= 0 && h2s <= 50;
    }
    
    /**
     * 校验电池电量范围（0-100%）
     */
    public static boolean isValidBatteryLevel(Integer batteryLevel) {
        return batteryLevel != null && batteryLevel >= 0 && batteryLevel <= 100;
    }
    
    /**
     * 校验信号强度范围（0-100）
     */
    public static boolean isValidSignalStrength(Integer signalStrength) {
        return signalStrength != null && signalStrength >= 0 && signalStrength <= 100;
    }
    
    /**
     * 校验字符串是否为空
     */
    public static boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }
    
    /**
     * 校验字符串是否非空
     */
    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }
}
