package com.wsn.cow.mapper;

import com.wsn.cow.entity.DeviceInfo;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/**
 * 设备信息Mapper接口
 */
public interface DeviceInfoMapper {
    
    /**
     * 插入设备信息
     */
    int insert(DeviceInfo deviceInfo);
    
    /**
     * 根据ID查询
     */
    DeviceInfo selectById(Long id);
    
    /**
     * 根据设备ID查询
     */
    DeviceInfo selectByDeviceId(String deviceId);
    
    /**
     * 查询所有设备
     */
    List<DeviceInfo> selectAll();
    
    /**
     * 根据设备类型查询
     */
    List<DeviceInfo> selectByType(String deviceType);
    
    /**
     * 根据状态查询设备列表
     */
    List<DeviceInfo> selectByStatus(Integer status);
    
    /**
     * 更新设备信息
     */
    int updateById(DeviceInfo deviceInfo);
    
    /**
     * 更新设备状态
     */
    int updateStatus(@Param("deviceId") String deviceId, 
                    @Param("status") Integer status);
    
    /**
     * 更新自动控制模式
     */
    int updateAutoMode(@Param("deviceId") String deviceId, 
                      @Param("autoMode") Integer autoMode);
    
    /**
     * 删除设备
     */
    int deleteById(Long id);
}
