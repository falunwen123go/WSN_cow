package com.wsn.cow.service.impl;

import com.wsn.cow.entity.SystemConfig;
import com.wsn.cow.mapper.SystemConfigMapper;
import com.wsn.cow.service.SystemConfigService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 系统配置服务实现类
 */
@Service
public class SystemConfigServiceImpl implements SystemConfigService {
    
    private static final Logger logger = LoggerFactory.getLogger(SystemConfigServiceImpl.class);
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    @Override
    public List<SystemConfig> queryAllConfigs() {
        return systemConfigMapper.selectAll();
    }
    
    @Override
    public String getConfigValue(String key) {
        SystemConfig config = systemConfigMapper.selectByKey(key);
        return config != null ? config.getConfigValue() : null;
    }
    
    @Override
    @Transactional
    public void updateConfig(String key, String value) {
        SystemConfig config = systemConfigMapper.selectByKey(key);
        if (config != null) {
            config.setConfigValue(value);
            config.setUpdateTime(new Date());
            systemConfigMapper.update(config);
            logger.info("更新配置成功: key={}, value={}", key, value);
        } else {
            logger.warn("配置不存在: key={}", key);
        }
    }
    
    @Override
    @Transactional
    public void addConfig(SystemConfig config) {
        config.setUpdateTime(new Date());
        systemConfigMapper.insert(config);
        logger.info("添加配置成功: key={}", config.getConfigKey());
    }
    
    @Override
    @Transactional
    public void deleteConfig(String key) {
        systemConfigMapper.deleteByKey(key);
        logger.info("删除配置成功: key={}", key);
    }
    
    @Override
    public void refreshCache() {
        // 刷新配置缓存(如果有缓存的话)
        logger.info("刷新配置缓存");
        // 这里可以添加缓存刷新逻辑
    }
}
