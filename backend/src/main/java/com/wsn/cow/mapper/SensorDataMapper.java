package com.wsn.cow.mapper;

import com.wsn.cow.entity.SensorData;
import org.apache.ibatis.annotations.Param;
import java.util.Date;
import java.util.List;

/**
 * 传感器数据Mapper接口
 */
public interface SensorDataMapper {
    
    /**
     * 插入传感器数据
     */
    int insert(SensorData sensorData);
    
    /**
     * 批量插入传感器数据
     */
    int batchInsert(@Param("list") List<SensorData> list);
    
    /**
     * 根据ID查询
     */
    SensorData selectById(Long id);
    
    /**
     * 根据节点ID查询最新数据
     */
    SensorData selectLatestByNodeId(String nodeId);
    
    /**
     * 根据节点ID和时间范围查询
     */
    List<SensorData> selectByNodeIdAndTime(@Param("nodeId") String nodeId, 
                                           @Param("startTime") Date startTime, 
                                           @Param("endTime") Date endTime);
    
    /**
     * 分页查询传感器数据
     */
    List<SensorData> selectPage(@Param("nodeId") String nodeId,
                                @Param("startTime") Date startTime,
                                @Param("endTime") Date endTime,
                                @Param("offset") int offset,
                                @Param("limit") int limit);
    
    /**
     * 查询总数
     */
    int countByCondition(@Param("nodeId") String nodeId,
                        @Param("startTime") Date startTime,
                        @Param("endTime") Date endTime);
    
    /**
     * 删除指定时间之前的数据
     */
    int deleteBeforeTime(Date time);
    
    /**
     * 查询所有节点的最新数据
     */
    List<SensorData> selectLatestByAllNodes();
    
    /**
     * 查询指定节点的最新数据
     */
    SensorData selectLatestByNode(String nodeId);
}
