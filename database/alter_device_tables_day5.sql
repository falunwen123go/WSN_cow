-- Day 5 数据库Schema补丁 - 设备控制表字段添加
-- 执行时间: 2025-11-18

USE wsn_cow_monitor;

-- 1. 为device_info表添加auto_mode字段
ALTER TABLE device_info 
ADD COLUMN auto_mode INT DEFAULT 0 COMMENT '自动控制模式：0-手动，1-自动'
AFTER status;

-- 2. 为device_control_log表添加control_type字段  
ALTER TABLE device_control_log
ADD COLUMN control_type VARCHAR(20) DEFAULT 'MANUAL' COMMENT '控制方式：MANUAL-手动，AUTO-自动'
AFTER action;

-- 验证添加结果
SELECT 'device_info表结构:' as info;
DESCRIBE device_info;

SELECT 'device_control_log表结构:' as info;
DESCRIBE device_control_log;

-- 更新现有设备为手动模式
UPDATE device_info SET auto_mode = 0 WHERE auto_mode IS NULL;

SELECT '数据库Schema更新完成' as result;
