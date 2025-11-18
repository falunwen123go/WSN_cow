package com.wsn.cow.service.impl;

import com.wsn.cow.entity.AlarmRecord;
import com.wsn.cow.entity.SystemConfig;
import com.wsn.cow.mapper.AlarmRecordMapper;
import com.wsn.cow.mapper.SystemConfigMapper;
import com.wsn.cow.service.AlarmService;
import com.wsn.cow.common.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 告警服务实现类
 */
@Service
public class AlarmServiceImpl implements AlarmService {
    
    private static final Logger logger = LoggerFactory.getLogger(AlarmServiceImpl.class);
    
    @Autowired
    private AlarmRecordMapper alarmRecordMapper;
    
    @Autowired
    private SystemConfigMapper systemConfigMapper;
    
    // 缓存告警阈值配置
    private Map<String, Double> thresholdCache = new HashMap<>();
    
    @Override
    @Transactional
    public void checkAndCreateAlarm(String nodeId, String alarmType, Double currentValue) {
        if (currentValue == null) {
            return;
        }
        
        // 获取阈值配置
        Double threshold1 = getThreshold(alarmType, 1);
        Double threshold2 = getThreshold(alarmType, 2);
        
        if (threshold1 == null && threshold2 == null) {
            return;
        }
        
        // 判断告警级别
        Integer alarmLevel = null;
        Double threshold = null;
        
        if (threshold2 != null && isExceedThreshold(alarmType, currentValue, threshold2)) {
            alarmLevel = 2; // 二级告警（严重）
            threshold = threshold2;
        } else if (threshold1 != null && isExceedThreshold(alarmType, currentValue, threshold1)) {
            alarmLevel = 1; // 一级告警（一般）
            threshold = threshold1;
        }
        
        // 如果超过阈值，创建告警记录
        if (alarmLevel != null) {
            AlarmRecord alarm = new AlarmRecord();
            alarm.setNodeId(nodeId);
            alarm.setAlarmType(alarmType);
            alarm.setAlarmLevel(alarmLevel);
            alarm.setCurrentValue(currentValue);
            alarm.setThreshold(threshold);
            alarm.setAlarmTime(new Date());
            alarm.setHandleStatus(0); // 未处理
            
            alarmRecordMapper.insert(alarm);
            logger.warn("生成告警: nodeId={}, type={}, level={}, value={}, threshold={}", 
                       nodeId, alarmType, alarmLevel, currentValue, threshold);
        }
    }
    
    @Override
    public PageResult<AlarmRecord> queryAlarmPage(String nodeId, String alarmType, Integer alarmLevel, Integer handleStatus, 
                                                   Date startTime, Date endTime, 
                                                   int pageNum, int pageSize) {
        int offset = (pageNum - 1) * pageSize;
        List<AlarmRecord> list = alarmRecordMapper.selectPage(nodeId, alarmType, alarmLevel, handleStatus, 
                                                              startTime, endTime, offset, pageSize);
        int total = alarmRecordMapper.countByCondition(nodeId, alarmType, alarmLevel, handleStatus, 
                                                       startTime, endTime);
        
        return new PageResult<>(pageNum, pageSize, (long)total, list);
    }
    
    @Override
    public int getUnhandledCount() {
        return alarmRecordMapper.countUnhandled();
    }
    
    @Override
    @Transactional
    public void handleAlarm(Long id, String handler, String handleRemark) {
        alarmRecordMapper.updateHandleStatus(id, 2, handler, handleRemark);
        logger.info("处理告警: id={}, handler={}", id, handler);
    }
    
    @Override
    @Transactional
    public int cleanHistoryAlarms(Date beforeTime) {
        int count = alarmRecordMapper.deleteBeforeTime(beforeTime);
        logger.info("清理历史告警: count={}, beforeTime={}", count, beforeTime);
        return count;
    }
    
    @Override
    public int countUnhandled() {
        return alarmRecordMapper.countUnhandled();
    }
    
    @Override
    public AlarmRecord getAlarmById(Long id) {
        return alarmRecordMapper.selectById(id);
    }
    
    @Override
    @Transactional
    public void addAlarm(AlarmRecord alarm) {
        if (alarm.getAlarmTime() == null) {
            alarm.setAlarmTime(new Date());
        }
        if (alarm.getHandleStatus() == null) {
            alarm.setHandleStatus(0); // 默认未处理
        }
        alarmRecordMapper.insert(alarm);
        logger.info("添加告警记录: {}", alarm);
    }
    
    @Override
    @Transactional
    public void deleteAlarm(Long id) {
        alarmRecordMapper.deleteById(id);
        logger.info("删除告警记录: id={}", id);
    }
    
    @Override
    @Transactional
    public void updateHandleStatus(Long id, Integer handleStatus, String handleRemark) {
        alarmRecordMapper.updateHandleStatus(id, handleStatus, "system", handleRemark);
        logger.info("更新告警状态: id={}, status={}", id, handleStatus);
    }
    
    @Override
    public Map<String, Object> queryStatistics(Date startTime, Date endTime) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 总告警数
        int total = alarmRecordMapper.countByCondition(null, null, null, null, startTime, endTime);
        statistics.put("total", total);
        
        // 未处理告警数
        int unhandled = alarmRecordMapper.countByCondition(null, null, null, 0, startTime, endTime);
        statistics.put("unhandled", unhandled);
        
        // 已处理告警数
        int handled = alarmRecordMapper.countByCondition(null, null, null, 1, startTime, endTime);
        statistics.put("handled", handled);
        
        // 按告警类型统计
        Map<String, Integer> byType = new HashMap<>();
        for (String type : new String[]{"TEMP", "HUMI", "NH3", "H2S"}) {
            int count = alarmRecordMapper.countByCondition(null, type, null, null, startTime, endTime);
            byType.put(type, count);
        }
        statistics.put("byType", byType);
        
        // 按告警级别统计
        Map<String, Integer> byLevel = new HashMap<>();
        for (int level = 1; level <= 2; level++) {
            int count = alarmRecordMapper.countByCondition(null, null, level, null, startTime, endTime);
            byLevel.put("level" + level, count);
        }
        statistics.put("byLevel", byLevel);
        
        logger.info("查询告警统计: total={}, unhandled={}", total, unhandled);
        return statistics;
    }
    
    @Override
    @Transactional
    public int deleteBeforeTime(Date beforeTime) {
        int count = alarmRecordMapper.deleteBeforeTime(beforeTime);
        logger.info("删除历史告警: count={}, beforeTime={}", count, beforeTime);
        return count;
    }
    
    /**
     * 获取告警阈值
     */
    private Double getThreshold(String alarmType, int level) {
        String configKey = alarmType + "_THRESHOLD_" + level;
        
        // 先从缓存获取
        if (thresholdCache.containsKey(configKey)) {
            return thresholdCache.get(configKey);
        }
        
        // 从数据库获取
        SystemConfig config = systemConfigMapper.selectByKey(configKey);
        if (config != null) {
            try {
                Double threshold = Double.parseDouble(config.getConfigValue());
                thresholdCache.put(configKey, threshold);
                return threshold;
            } catch (NumberFormatException e) {
                logger.error("阈值配置格式错误: key={}, value={}", configKey, config.getConfigValue());
            }
        }
        
        return null;
    }
    
    /**
     * 判断是否超过阈值
     */
    private boolean isExceedThreshold(String alarmType, Double value, Double threshold) {
        switch (alarmType) {
            case "TEMP":
                // 温度: 过低或过高都告警
                return value < 5.0 || value > threshold;
            case "HUMI":
                // 湿度: 过低或过高都告警
                return value < 30.0 || value > threshold;
            case "NH3":
            case "H2S":
                // 气体浓度: 超过阈值告警
                return value > threshold;
            default:
                return false;
        }
    }
}
