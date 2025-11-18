package com.wsn.cow.mapper;

import com.wsn.cow.entity.DeviceControlLog;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 设备控制日志Mapper接口
 */
public interface DeviceControlLogMapper {
    
    /**
     * 插入控制日志
     */
    int insert(DeviceControlLog log);
    
    /**
     * 根据ID查询
     */
    DeviceControlLog selectById(Long id);
    
    /**
     * 根据设备ID查询最新日志
     */
    DeviceControlLog selectLatestByDeviceId(String deviceId);
    
    /**
     * 分页查询控制日志
     */
    List<DeviceControlLog> selectPage(@Param("deviceId") String deviceId,
                                     @Param("controlType") String controlType,
                                     @Param("startTime") Date startTime,
                                     @Param("endTime") Date endTime,
                                     @Param("offset") int offset,
                                     @Param("limit") int limit);
    
    /**
     * 查询总数
     */
    int countByCondition(@Param("deviceId") String deviceId,
                        @Param("controlType") String controlType,
                        @Param("startTime") Date startTime,
                        @Param("endTime") Date endTime);
    
    /**
     * 删除指定时间之前的日志
     */
    int deleteBeforeTime(Date time);
}
