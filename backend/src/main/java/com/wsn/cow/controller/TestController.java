package com.wsn.cow.controller;

import com.wsn.cow.common.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统测试控制器
 */
@RestController
@RequestMapping("/api/test")
public class TestController {
    
    /**
     * 健康检查接口
     */
    @GetMapping("/health")
    public Result<Map<String, Object>> health() {
        Map<String, Object> data = new HashMap<>();
        data.put("status", "UP");
        data.put("application", "WSN Cow Monitor System");
        data.put("version", "1.0.0");
        data.put("timestamp", System.currentTimeMillis());
        
        return Result.success("系统运行正常", data);
    }
    
    /**
     * 数据库连接测试
     */
    @GetMapping("/db")
    public Result<String> testDatabase() {
        // 这里后续会添加数据库查询测试
        return Result.success("数据库连接正常");
    }
}
