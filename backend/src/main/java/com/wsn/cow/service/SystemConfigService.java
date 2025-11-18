package com.wsn.cow.service;

import com.wsn.cow.entity.SystemConfig;
import java.util.List;

/**
 * 系统配置服务接口
 */
public interface SystemConfigService {
    
    /**
     * 查询所有配置
     */
    List<SystemConfig> queryAllConfigs();
    
    /**
     * 根据key获取配置值
     */
    String getConfigValue(String key);
    
    /**
     * 更新配置
     */
    void updateConfig(String key, String value);
    
    /**
     * 添加配置
     */
    void addConfig(SystemConfig config);
    
    /**
     * 删除配置
     */
    void deleteConfig(String key);
    
    /**
     * 刷新配置缓存
     */
    void refreshCache();
}
