package com.wsn.cow.service;

import com.wsn.cow.entity.AlarmRecord;
import com.wsn.cow.common.PageResult;
import java.util.Date;

/**
 * 告警服务接口
 */
public interface AlarmService {
    
    /**
     * 检查传感器数据并生成告警（如果超过阈值）
     */
    void checkAndCreateAlarm(String nodeId, String alarmType, Double currentValue);
    
    /**
     * 分页查询告警记录
     */
    PageResult<AlarmRecord> queryAlarmPage(String nodeId, String alarmType, Integer alarmLevel, 
                                           Integer handleStatus, Date startTime, Date endTime,
                                           int pageNum, int pageSize);
    
    /**
     * 获取未处理告警数量
     */
    int getUnhandledCount();
    
    /**
     * 处理告警
     */
    void handleAlarm(Long id, String handler, String handleRemark);
    
    /**
     * 清理历史告警
     */
    int cleanHistoryAlarms(Date beforeTime);
    
    /**
     * 统计未处理告警数量
     */
    int countUnhandled();
    
    /**
     * 根据ID获取告警记录
     */
    AlarmRecord getAlarmById(Long id);
    
    /**
     * 添加告警记录
     */
    void addAlarm(AlarmRecord alarm);
    
    /**
     * 删除告警记录
     */
    void deleteAlarm(Long id);
    
    /**
     * 更新告警处理状态
     */
    void updateHandleStatus(Long id, Integer handleStatus, String handleRemark);
    
    /**
     * 查询告警统计信息
     */
    java.util.Map<String, Object> queryStatistics(Date startTime, Date endTime);
    
    /**
     * 删除指定时间之前的告警
     */
    int deleteBeforeTime(Date beforeTime);
}
