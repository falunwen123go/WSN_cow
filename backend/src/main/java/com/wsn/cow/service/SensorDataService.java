package com.wsn.cow.service;

import com.wsn.cow.entity.SensorData;
import com.wsn.cow.common.PageResult;
import java.util.Date;
import java.util.List;

/**
 * 传感器数据服务接口
 */
public interface SensorDataService {
    
    /**
     * 保存传感器数据（包含数据验证和告警判断）
     */
    void saveSensorData(SensorData sensorData);
    
    /**
     * 批量保存传感器数据
     */
    void batchSaveSensorData(List<SensorData> dataList);
    
    /**
     * 获取节点最新数据
     */
    SensorData getLatestData(String nodeId);
    
    /**
     * 根据条件分页查询传感器数据
     */
    PageResult<SensorData> querySensorDataPage(String nodeId, Date startTime, Date endTime, int pageNum, int pageSize);
    
    /**
     * 根据节点ID和时间范围查询数据列表
     */
    List<SensorData> querySensorDataList(String nodeId, Date startTime, Date endTime);
    
    /**
     * 清理历史数据（删除指定时间之前的数据）
     */
    int cleanHistoryData(Date beforeTime);
    
    /**
     * 查询所有节点最新数据
     */
    List<SensorData> queryLatestData();
    
    /**
     * 查询指定节点最新数据
     */
    SensorData queryLatestByNode(String nodeId);
    
    /**
     * 查询历史数据（分页）
     */
    PageResult<SensorData> queryHistoryData(String nodeId, Date startTime, Date endTime, int pageNum, int pageSize);
    
    /**
     * 查询数据统计信息
     */
    java.util.Map<String, Object> queryStatistics(String nodeId, Date startTime, Date endTime);
}
