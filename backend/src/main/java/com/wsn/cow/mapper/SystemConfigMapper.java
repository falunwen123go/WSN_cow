package com.wsn.cow.mapper;

import com.wsn.cow.entity.SystemConfig;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 系统配置Mapper接口
 */
public interface SystemConfigMapper {
    
    /**
     * 插入配置
     */
    int insert(SystemConfig config);
    
    /**
     * 根据ID查询
     */
    SystemConfig selectById(Long id);
    
    /**
     * 根据配置键查询
     */
    SystemConfig selectByKey(String configKey);
    
    /**
     * 根据配置类型查询列表
     */
    List<SystemConfig> selectByType(String configType);
    
    /**
     * 查询所有配置
     */
    List<SystemConfig> selectAll();
    
    /**
     * 更新配置值
     */
    int updateValue(@Param("configKey") String configKey, 
                   @Param("configValue") String configValue);
    
    /**
     * 更新配置
     */
    int updateById(SystemConfig config);
    
    /**
     * 删除配置
     */
    int deleteById(Long id);
    
    /**
     * 更新配置对象
     */
    int update(SystemConfig config);
    
    /**
     * 根据key删除配置
     */
    int deleteByKey(String configKey);
}
