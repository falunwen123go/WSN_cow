package com.wsn.cow.controller;

import com.wsn.cow.common.Result;
import com.wsn.cow.entity.SystemConfig;
import com.wsn.cow.service.SystemConfigService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 系统配置控制器
 */
@RestController
@RequestMapping("/api/config")
public class SystemConfigController extends BaseController {
    
    @Autowired
    private SystemConfigService systemConfigService;
    
    /**
     * 获取所有配置
     * GET /api/config/list
     */
    @GetMapping("/list")
    public Result<List<SystemConfig>> getConfigList() {
        logger.info("查询系统配置列表");
        List<SystemConfig> list = systemConfigService.queryAllConfigs();
        return Result.success(list);
    }
    
    /**
     * 获取配置值
     * GET /api/config/{key}
     */
    @GetMapping("/{key}")
    public Result<String> getConfigValue(@PathVariable String key) {
        logger.info("查询配置值: key={}", key);
        String value = systemConfigService.getConfigValue(key);
        if (value == null) {
            return Result.notFound("配置项不存在");
        }
        return Result.success(value);
    }
    
    /**
     * 创建配置项
     * POST /api/config
     */
    @PostMapping
    public Result<String> createConfig(@RequestBody SystemConfig config) {
        logger.info("创建配置: key={}, value={}", config.getConfigKey(), config.getConfigValue());
        
        // 检查配置是否已存在
        String existing = systemConfigService.getConfigValue(config.getConfigKey());
        if (existing != null) {
            return Result.error("配置项已存在");
        }
        
        systemConfigService.addConfig(config);
        return Result.success("配置创建成功", "配置创建成功");
    }
    
    /**
     * 刷新配置缓存
     * POST /api/config/refresh
     */
    @PostMapping("/refresh")
    public Result<String> refreshCache() {
        logger.info("刷新配置缓存");
        systemConfigService.refreshCache();
        return Result.success("缓存刷新成功", "缓存刷新成功");
    }
    
    /**
     * 更新配置项
     * PUT /api/config/{key}
     */
    @PutMapping("/{key}")
    public Result<String> updateConfig(@PathVariable String key, @RequestParam String value) {
        logger.info("更新配置: key={}, value={}", key, value);
        systemConfigService.updateConfig(key, value);
        return Result.success("配置更新成功", "配置更新成功");
    }
    
    /**
     * 批量更新配置
     * PUT /api/config/batch
     */
    @PutMapping("/batch")
    public Result<String> batchUpdateConfig(@RequestBody Map<String, String> configs) {
        logger.info("批量更新配置: count={}", configs.size());
        
        for (Map.Entry<String, String> entry : configs.entrySet()) {
            systemConfigService.updateConfig(entry.getKey(), entry.getValue());
        }
        
        return Result.success("配置更新成功", "配置更新成功");
    }
    
    /**
     * 获取告警阈值配置
     * GET /api/config/alarm/thresholds
     */
    @GetMapping("/alarm/thresholds")
    public Result<Map<String, Object>> getAlarmThresholds() {
        logger.info("查询告警阈值配置");
        
        Map<String, Object> thresholds = new java.util.HashMap<>();
        thresholds.put("TEMP_THRESHOLD_1", systemConfigService.getConfigValue("TEMP_THRESHOLD_1"));
        thresholds.put("TEMP_THRESHOLD_2", systemConfigService.getConfigValue("TEMP_THRESHOLD_2"));
        thresholds.put("HUMI_THRESHOLD_1", systemConfigService.getConfigValue("HUMI_THRESHOLD_1"));
        thresholds.put("HUMI_THRESHOLD_2", systemConfigService.getConfigValue("HUMI_THRESHOLD_2"));
        thresholds.put("NH3_THRESHOLD_1", systemConfigService.getConfigValue("NH3_THRESHOLD_1"));
        thresholds.put("NH3_THRESHOLD_2", systemConfigService.getConfigValue("NH3_THRESHOLD_2"));
        thresholds.put("H2S_THRESHOLD_1", systemConfigService.getConfigValue("H2S_THRESHOLD_1"));
        thresholds.put("H2S_THRESHOLD_2", systemConfigService.getConfigValue("H2S_THRESHOLD_2"));
        
        return Result.success(thresholds);
    }
}
