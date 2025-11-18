package com.wsn.cow.service.impl;

import com.wsn.cow.entity.SensorData;
import com.wsn.cow.mapper.SensorDataMapper;
import com.wsn.cow.service.SensorDataService;
import com.wsn.cow.service.AlarmService;
import com.wsn.cow.service.NodeInfoService;
import com.wsn.cow.common.PageResult;
import com.wsn.cow.util.ValidationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * 传感器数据服务实现类
 */
@Service
public class SensorDataServiceImpl implements SensorDataService {
    
    private static final Logger logger = LoggerFactory.getLogger(SensorDataServiceImpl.class);
    
    @Autowired
    private SensorDataMapper sensorDataMapper;
    
    @Autowired
    private AlarmService alarmService;
    
    @Autowired
    private NodeInfoService nodeInfoService;
    
    @Override
    @Transactional
    public void saveSensorData(SensorData sensorData) {
        // 数据校验
        if (!validateSensorData(sensorData)) {
            logger.warn("传感器数据校验失败: {}", sensorData);
            return;
        }
        
        // 设置接收时间
        if (sensorData.getReceiveTime() == null) {
            sensorData.setReceiveTime(new Date());
        }
        
        // 保存到数据库
        sensorDataMapper.insert(sensorData);
        logger.info("保存传感器数据成功: nodeId={}, temp={}, humi={}", 
                   sensorData.getNodeId(), sensorData.getTemperature(), sensorData.getHumidity());
        
        // 告警检测
        checkAlarms(sensorData);
    }
    
    @Override
    @Transactional
    public void batchSaveSensorData(List<SensorData> dataList) {
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        
        Date receiveTime = new Date();
        for (SensorData data : dataList) {
            if (data.getReceiveTime() == null) {
                data.setReceiveTime(receiveTime);
            }
        }
        
        sensorDataMapper.batchInsert(dataList);
        logger.info("批量保存传感器数据成功: count={}", dataList.size());
        
        // 逐条处理告警检测
        for (SensorData data : dataList) {
            checkAlarms(data);
        }
    }
    
    @Override
    public SensorData getLatestData(String nodeId) {
        return sensorDataMapper.selectLatestByNodeId(nodeId);
    }
    
    @Override
    public PageResult<SensorData> querySensorDataPage(String nodeId, Date startTime, Date endTime, 
                                                       int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<SensorData> list = sensorDataMapper.selectPage(nodeId, startTime, endTime, offset, pageSize);
        int total = sensorDataMapper.countByCondition(nodeId, startTime, endTime);
        
        return new PageResult<>(pageNum, pageSize, (long)total, list);
    }
    
    @Override
    public List<SensorData> querySensorDataList(String nodeId, Date startTime, Date endTime) {
        return sensorDataMapper.selectByNodeIdAndTime(nodeId, startTime, endTime);
    }
    
    @Override
    @Transactional
    public int cleanHistoryData(Date beforeTime) {
        int count = sensorDataMapper.deleteBeforeTime(beforeTime);
        logger.info("清理历史数据: count={}, beforeTime={}", count, beforeTime);
        return count;
    }
    
    /**
     * 验证传感器数据
     */
    private boolean validateSensorData(SensorData data) {
        if (data == null) {
            return false;
        }
        
        // 校验节点ID
        if (!ValidationUtils.isValidNodeId(data.getNodeId())) {
            logger.warn("节点ID格式错误: {}", data.getNodeId());
            return false;
        }
        
        // 校验温度
        if (!ValidationUtils.isValidTemperature(data.getTemperature())) {
            logger.warn("温度值超出范围: {}", data.getTemperature());
            return false;
        }
        
        // 校验湿度
        if (!ValidationUtils.isValidHumidity(data.getHumidity())) {
            logger.warn("湿度值超出范围: {}", data.getHumidity());
            return false;
        }
        
        // 校验氨气
        if (!ValidationUtils.isValidNH3(data.getNh3Concentration())) {
            logger.warn("氨气浓度超出范围: {}", data.getNh3Concentration());
            return false;
        }
        
        // 校验硫化氢
        if (!ValidationUtils.isValidH2S(data.getH2sConcentration())) {
            logger.warn("硫化氢浓度超出范围: {}", data.getH2sConcentration());
            return false;
        }
        
        return true;
    }
    
    /**
     * 检查告警
     */
    private void checkAlarms(SensorData data) {
        String nodeId = data.getNodeId();
        
        // 检查温度告警
        alarmService.checkAndCreateAlarm(nodeId, "TEMP", data.getTemperature());
        
        // 检查湿度告警
        alarmService.checkAndCreateAlarm(nodeId, "HUMI", data.getHumidity());
        
        // 检查氨气告警
        alarmService.checkAndCreateAlarm(nodeId, "NH3", data.getNh3Concentration());
        
        // 检查硫化氢告警
        alarmService.checkAndCreateAlarm(nodeId, "H2S", data.getH2sConcentration());
    }
    
    @Override
    public List<SensorData> queryLatestData() {
        return sensorDataMapper.selectLatestByAllNodes();
    }
    
    @Override
    public SensorData queryLatestByNode(String nodeId) {
        return sensorDataMapper.selectLatestByNode(nodeId);
    }
    
    @Override
    public PageResult<SensorData> queryHistoryData(String nodeId, Date startTime, Date endTime, int pageNum, int pageSize) {
        return querySensorDataPage(nodeId, startTime, endTime, pageNum, pageSize);
    }
    
    @Override
    public java.util.Map<String, Object> queryStatistics(String nodeId, Date startTime, Date endTime) {
        List<SensorData> dataList = querySensorDataList(nodeId, startTime, endTime);
        
        if (dataList.isEmpty()) {
            return java.util.Collections.emptyMap();
        }
        
        // 计算统计信息
        java.util.Map<String, Object> stats = new java.util.HashMap<>();
        stats.put("count", dataList.size());
        
        // 温度统计
        double tempSum = 0, tempMax = Double.MIN_VALUE, tempMin = Double.MAX_VALUE;
        double humiSum = 0, humiMax = Double.MIN_VALUE, humiMin = Double.MAX_VALUE;
        double nh3Sum = 0, nh3Max = Double.MIN_VALUE, nh3Min = Double.MAX_VALUE;
        double h2sSum = 0, h2sMax = Double.MIN_VALUE, h2sMin = Double.MAX_VALUE;
        
        for (SensorData data : dataList) {
            // 温度
            tempSum += data.getTemperature();
            tempMax = Math.max(tempMax, data.getTemperature());
            tempMin = Math.min(tempMin, data.getTemperature());
            
            // 湿度
            humiSum += data.getHumidity();
            humiMax = Math.max(humiMax, data.getHumidity());
            humiMin = Math.min(humiMin, data.getHumidity());
            
            // 氨气
            nh3Sum += data.getNh3Concentration();
            nh3Max = Math.max(nh3Max, data.getNh3Concentration());
            nh3Min = Math.min(nh3Min, data.getNh3Concentration());
            
            // 硫化氢
            h2sSum += data.getH2sConcentration();
            h2sMax = Math.max(h2sMax, data.getH2sConcentration());
            h2sMin = Math.min(h2sMin, data.getH2sConcentration());
        }
        
        int count = dataList.size();
        stats.put("temperature", java.util.Map.of(
            "avg", Math.round(tempSum / count * 10) / 10.0,
            "max", tempMax,
            "min", tempMin
        ));
        stats.put("humidity", java.util.Map.of(
            "avg", Math.round(humiSum / count * 10) / 10.0,
            "max", humiMax,
            "min", humiMin
        ));
        stats.put("nh3", java.util.Map.of(
            "avg", Math.round(nh3Sum / count * 10) / 10.0,
            "max", nh3Max,
            "min", nh3Min
        ));
        stats.put("h2s", java.util.Map.of(
            "avg", Math.round(h2sSum / count * 100) / 100.0,
            "max", h2sMax,
            "min", h2sMin
        ));
        
        return stats;
    }
}

