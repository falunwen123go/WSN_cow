package com.wsn.cow.controller;

import com.wsn.cow.common.Result;
import com.wsn.cow.common.PageResult;
import com.wsn.cow.entity.SensorData;
import com.wsn.cow.service.SensorDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 传感器数据控制器
 */
@RestController
@RequestMapping("/api/sensor")
public class SensorDataController extends BaseController {
    
    @Autowired
    private SensorDataService sensorDataService;
    
    /**
     * 获取所有节点最新数据
     * GET /api/sensor/latest
     */
    @GetMapping("/latest")
    public Result<List<SensorData>> getLatestData() {
        logger.info("查询所有节点最新数据");
        List<SensorData> data = sensorDataService.queryLatestData();
        return Result.success(data);
    }
    
    /**
     * 获取指定节点最新数据
     * GET /api/sensor/data/{nodeId}
     */
    @GetMapping("/data/{nodeId}")
    public Result<SensorData> getNodeLatestData(@PathVariable String nodeId) {
        logger.info("查询节点最新数据: nodeId={}", nodeId);
        SensorData data = sensorDataService.queryLatestByNode(nodeId);
        if (data == null) {
            return Result.notFound("节点数据不存在");
        }
        return Result.success(data);
    }
    
    /**
     * 查询历史数据（分页）
     * GET /api/sensor/history?nodeId=xxx&startTime=xxx&endTime=xxx&pageNum=1&pageSize=10
     */
    @GetMapping("/history")
    public Result<PageResult<SensorData>> getHistoryData(
            @RequestParam(required = false) String nodeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date endTime,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        
        int page = getPageNum(pageNum);
        int size = getPageSize(pageSize);
        
        logger.info("查询历史数据: nodeId={}, startTime={}, endTime={}, page={}, size={}", 
                   nodeId, startTime, endTime, page, size);
        
        PageResult<SensorData> result = sensorDataService.queryHistoryData(
                nodeId, startTime, endTime, page, size);
        
        return Result.success(result);
    }
    
    /**
     * 获取数据统计信息
     * GET /api/sensor/statistics?nodeId=xxx&startTime=xxx&endTime=xxx
     * 日期格式支持: yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss
     */
    @GetMapping("/statistics")
    public Result<Map<String, Object>> getStatistics(
            @RequestParam(required = false) String nodeId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startTime,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endTime) {
        
        logger.info("查询数据统计: nodeId={}, startTime={}, endTime={}", nodeId, startTime, endTime);
        
        Map<String, Object> statistics = sensorDataService.queryStatistics(nodeId, startTime, endTime);
        return Result.success(statistics);
    }
}
