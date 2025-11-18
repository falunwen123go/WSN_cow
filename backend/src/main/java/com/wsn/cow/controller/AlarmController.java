package com.wsn.cow.controller;

import com.wsn.cow.common.Result;
import com.wsn.cow.common.PageResult;
import com.wsn.cow.entity.AlarmRecord;
import com.wsn.cow.service.AlarmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

/**
 * 告警管理控制器
 */
@RestController
@RequestMapping("/api/alarm")
public class AlarmController extends BaseController {
    
    @Autowired
    private AlarmService alarmService;
    
    /**
     * 获取告警列表（分页）
     * GET /api/alarm/list?nodeId=xxx&alarmType=xxx&alarmLevel=xxx&handleStatus=xxx&startTime=xxx&endTime=xxx&pageNum=1&pageSize=10
     */
    @GetMapping("/list")
    public Result<PageResult<AlarmRecord>> getAlarmList(
            @RequestParam(required = false) String nodeId,
            @RequestParam(required = false) String alarmType,
            @RequestParam(required = false) Integer alarmLevel,
            @RequestParam(required = false) Integer handleStatus,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        
        int page = getPageNum(pageNum);
        int size = getPageSize(pageSize);
        
        logger.info("查询告警列表: nodeId={}, alarmType={}, alarmLevel={}, handleStatus={}, page={}, size={}", 
                   nodeId, alarmType, alarmLevel, handleStatus, page, size);
        
        PageResult<AlarmRecord> result = alarmService.queryAlarmPage(
                nodeId, alarmType, alarmLevel, handleStatus, startTime, endTime, page, size);
        
        return Result.success(result);
    }
    
    /**
     * 获取未处理告警数量
     * GET /api/alarm/unhandled/count
     */
    @GetMapping("/unhandled/count")
    public Result<Integer> getUnhandledCount() {
        logger.info("查询未处理告警数量");
        int count = alarmService.countUnhandled();
        return Result.success(count);
    }
    
    /**
     * 获取告警详情
     * GET /api/alarm/{id}
     */
    @GetMapping("/{id}")
    public Result<AlarmRecord> getAlarmDetail(@PathVariable Long id) {
        logger.info("查询告警详情: id={}", id);
        AlarmRecord alarm = alarmService.getAlarmById(id);
        if (alarm == null) {
            return Result.notFound("告警记录不存在");
        }
        return Result.success(alarm);
    }
    
    /**
     * 创建告警记录
     * POST /api/alarm
     */
    @PostMapping
    public Result<String> createAlarm(@RequestBody AlarmRecord alarm) {
        logger.info("创建告警记录: {}", alarm);
        
        if (alarm.getNodeId() == null || alarm.getAlarmType() == null) {
            return Result.badRequest("节点ID和告警类型不能为空");
        }
        
        alarmService.addAlarm(alarm);
        return Result.success("告警记录创建成功", "告警记录创建成功");
    }
    
    /**
     * 删除告警记录
     * DELETE /api/alarm/{id}
     */
    @DeleteMapping("/{id}")
    public Result<String> deleteAlarm(@PathVariable Long id) {
        logger.info("删除告警记录: id={}", id);
        
        AlarmRecord existing = alarmService.getAlarmById(id);
        if (existing == null) {
            return Result.notFound("告警记录不存在");
        }
        
        alarmService.deleteAlarm(id);
        return Result.success("告警记录删除成功", "告警记录删除成功");
    }
    
    /**
     * 获取未处理告警列表
     * GET /api/alarm/unhandled
     */
    @GetMapping("/unhandled")
    public Result<PageResult<AlarmRecord>> getUnhandledAlarms(
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        
        int page = getPageNum(pageNum);
        int size = getPageSize(pageSize);
        
        logger.info("查询未处理告警列表: page={}, size={}", page, size);
        
        PageResult<AlarmRecord> result = alarmService.queryAlarmPage(
                null, null, null, 0, null, null, page, size);
        
        return Result.success(result);
    }
    
    /**
     * 处理告警
     * PUT /api/alarm/handle/{id}
     */
    @PutMapping("/handle/{id}")
    public Result<String> handleAlarm(
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "1") Integer handleStatus,
            @RequestParam(required = false) String handleRemark) {
        
        logger.info("处理告警: id={}, status={}, remark={}", id, handleStatus, handleRemark);
        
        alarmService.updateHandleStatus(id, handleStatus, handleRemark);
        return Result.success("告警处理成功", "告警处理成功");
    }
    
    /**
     * 批量处理告警
     * PUT /api/alarm/handle/batch
     */
    @PutMapping("/handle/batch")
    public Result<String> batchHandleAlarms(
            @RequestParam String ids,
            @RequestParam(required = false, defaultValue = "1") Integer handleStatus,
            @RequestParam(required = false) String handleRemark) {
        
        logger.info("批量处理告警: ids={}, status={}", ids, handleStatus);
        
        String[] idArray = ids.split(",");
        for (String idStr : idArray) {
            try {
                Long id = Long.parseLong(idStr.trim());
                alarmService.updateHandleStatus(id, handleStatus, handleRemark);
            } catch (NumberFormatException e) {
                logger.warn("无效的告警ID: {}", idStr);
            }
        }
        
        return Result.success("批量处理成功", "批量处理成功");
    }
    
    /**
     * 获取告警统计信息
     * GET /api/alarm/statistics?startTime=xxx&endTime=xxx
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        
        logger.info("查询告警统计: startTime={}, endTime={}", startTime, endTime);
        
        Map<String, Object> statistics = alarmService.queryStatistics(startTime, endTime);
        return Result.success(statistics);
    }
    
    /**
     * 删除历史告警
     * DELETE /api/alarm/history?beforeTime=xxx
     */
    @DeleteMapping("/history")
    public Result<Integer> deleteHistory(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date beforeTime) {
        
        logger.info("删除历史告警: beforeTime={}", beforeTime);
        
        int count = alarmService.deleteBeforeTime(beforeTime);
        return Result.success("删除了 " + count + " 条历史告警", count);
    }
}
