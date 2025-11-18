-- =====================================================
-- 第7天示例数据插入脚本
-- 执行时间: 2025-11-18
-- 说明: 小规模演示数据，用于前端功能测试
-- =====================================================

USE wsn_cow_monitor;

-- 1. 更新节点信息（添加电池、信号强度等数据）
UPDATE node_info SET 
    battery_level = 85,
    signal_strength = 78,
    last_comm_time = NOW() - INTERVAL 5 MINUTE,
    install_date = '2025-11-01',
    remark = '东区A栏监测节点'
WHERE node_id = 'NODE_001';

UPDATE node_info SET 
    battery_level = 92,
    signal_strength = 85,
    last_comm_time = NOW() - INTERVAL 3 MINUTE,
    install_date = '2025-11-01',
    remark = '中区B栏监测节点'
WHERE node_id = 'NODE_002';

UPDATE node_info SET 
    battery_level = 68,
    signal_strength = 62,
    last_comm_time = NOW() - INTERVAL 8 MINUTE,
    install_date = '2025-11-01',
    remark = '西区C栏监测节点'
WHERE node_id = 'NODE_003';

-- 2. 插入传感器数据（最近24小时，每2小时一条记录）
INSERT INTO sensor_data (node_id, temperature, humidity, nh3_concentration, h2s_concentration, data_status, collect_time, create_time) VALUES
-- NODE_001 数据
('NODE_001', 23.5, 65.0, 15.2, 5.1, 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('NODE_001', 24.2, 63.5, 18.5, 6.2, 0, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('NODE_001', 25.1, 62.0, 22.3, 7.5, 0, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),
('NODE_001', 26.8, 60.5, 28.6, 8.9, 0, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),
('NODE_001', 28.2, 58.0, 35.2, 11.2, 0, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR),
('NODE_001', 27.5, 59.5, 32.1, 10.3, 0, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 11 HOUR),
('NODE_001', 25.8, 61.0, 26.5, 8.5, 0, NOW() - INTERVAL 13 HOUR, NOW() - INTERVAL 13 HOUR),
('NODE_001', 24.3, 64.0, 20.8, 6.8, 0, NOW() - INTERVAL 15 HOUR, NOW() - INTERVAL 15 HOUR),
('NODE_001', 22.9, 66.5, 16.3, 5.5, 0, NOW() - INTERVAL 17 HOUR, NOW() - INTERVAL 17 HOUR),
('NODE_001', 21.5, 68.0, 14.1, 4.8, 0, NOW() - INTERVAL 19 HOUR, NOW() - INTERVAL 19 HOUR),
('NODE_001', 20.8, 70.0, 12.5, 4.2, 0, NOW() - INTERVAL 21 HOUR, NOW() - INTERVAL 21 HOUR),
('NODE_001', 20.2, 71.5, 11.8, 3.9, 0, NOW() - INTERVAL 23 HOUR, NOW() - INTERVAL 23 HOUR),

-- NODE_002 数据
('NODE_002', 22.8, 67.0, 14.5, 4.8, 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('NODE_002', 23.5, 65.5, 17.2, 5.9, 0, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('NODE_002', 24.8, 63.0, 21.8, 7.2, 0, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),
('NODE_002', 26.2, 61.5, 27.5, 8.5, 0, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),
('NODE_002', 27.8, 59.0, 33.8, 10.8, 0, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR),
('NODE_002', 26.9, 60.5, 30.2, 9.6, 0, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 11 HOUR),
('NODE_002', 25.2, 62.0, 24.5, 7.8, 0, NOW() - INTERVAL 13 HOUR, NOW() - INTERVAL 13 HOUR),
('NODE_002', 23.8, 65.0, 19.3, 6.3, 0, NOW() - INTERVAL 15 HOUR, NOW() - INTERVAL 15 HOUR),
('NODE_002', 22.3, 67.5, 15.8, 5.2, 0, NOW() - INTERVAL 17 HOUR, NOW() - INTERVAL 17 HOUR),
('NODE_002', 21.2, 69.0, 13.5, 4.5, 0, NOW() - INTERVAL 19 HOUR, NOW() - INTERVAL 19 HOUR),
('NODE_002', 20.5, 70.5, 12.1, 4.0, 0, NOW() - INTERVAL 21 HOUR, NOW() - INTERVAL 21 HOUR),
('NODE_002', 19.9, 72.0, 11.2, 3.7, 0, NOW() - INTERVAL 23 HOUR, NOW() - INTERVAL 23 HOUR),

-- NODE_003 数据
('NODE_003', 24.0, 64.0, 16.8, 5.5, 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('NODE_003', 25.2, 62.0, 20.5, 6.8, 0, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('NODE_003', 26.5, 60.0, 25.2, 8.2, 0, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),
('NODE_003', 28.5, 57.5, 32.8, 10.5, 0, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),
('NODE_003', 30.2, 55.0, 42.5, 13.8, 0, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR),
('NODE_003', 29.1, 56.5, 38.2, 12.1, 0, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 11 HOUR),
('NODE_003', 27.2, 59.0, 28.8, 9.2, 0, NOW() - INTERVAL 13 HOUR, NOW() - INTERVAL 13 HOUR),
('NODE_003', 25.5, 61.5, 22.5, 7.3, 0, NOW() - INTERVAL 15 HOUR, NOW() - INTERVAL 15 HOUR),
('NODE_003', 23.8, 64.0, 17.8, 5.9, 0, NOW() - INTERVAL 17 HOUR, NOW() - INTERVAL 17 HOUR),
('NODE_003', 22.5, 66.5, 14.8, 4.9, 0, NOW() - INTERVAL 19 HOUR, NOW() - INTERVAL 19 HOUR),
('NODE_003', 21.8, 68.0, 13.2, 4.3, 0, NOW() - INTERVAL 21 HOUR, NOW() - INTERVAL 21 HOUR),
('NODE_003', 21.0, 69.5, 12.3, 4.0, 0, NOW() - INTERVAL 23 HOUR, NOW() - INTERVAL 23 HOUR);

-- 3. 插入告警记录（包含已处理和未处理的告警）
INSERT INTO alarm_record (node_id, alarm_type, alarm_level, current_value, threshold_value, alarm_time, status, handle_time, remark) VALUES
-- 已处理的告警
('NODE_001', 'NH3', 2, 42.5, 40.0, NOW() - INTERVAL 8 HOUR, 1, NOW() - INTERVAL 7 HOUR, '氨气浓度严重超标，已启动排风扇'),
('NODE_002', 'TEMP', 1, 36.2, 35.0, NOW() - INTERVAL 6 HOUR, 1, NOW() - INTERVAL 5 HOUR, '温度轻微超标，已调整卷帘'),
('NODE_003', 'H2S', 2, 21.5, 20.0, NOW() - INTERVAL 5 HOUR, 1, NOW() - INTERVAL 4 HOUR, '硫化氢浓度超标，已启动通风系统'),

-- 未处理的告警
('NODE_003', 'NH3', 3, 45.8, 40.0, NOW() - INTERVAL 2 HOUR, 0, NULL, NULL),
('NODE_001', 'TEMP', 2, 37.5, 35.0, NOW() - INTERVAL 1 HOUR, 0, NULL, NULL),
('NODE_002', 'HUMI', 1, 52.0, 50.0, NOW() - INTERVAL 30 MINUTE, 0, NULL, NULL);

-- 4. 插入设备控制日志
INSERT INTO device_control_log (device_id, control_action, control_mode, operator, control_time, reason) VALUES
-- 自动控制记录
('FAN_001', 'START', 1, 'System', NOW() - INTERVAL 8 HOUR, '氨气浓度42.5ppm超过阈值40.0ppm，自动启动排风扇'),
('CURTAIN_001', 'ADJUST', 1, 'System', NOW() - INTERVAL 6 HOUR, '温度36.2℃超过阈值35.0℃，自动调整卷帘降温'),
('FAN_001', 'STOP', 1, 'System', NOW() - INTERVAL 3 HOUR, '氨气浓度降至安全范围，自动关闭排风扇'),

-- 手动控制记录
('HEATING_001', 'START', 0, 'admin', NOW() - INTERVAL 5 HOUR, '手动启动供热系统，预防夜间温度过低'),
('FAN_001', 'START', 0, 'admin', NOW() - INTERVAL 2 HOUR, '手动启动排风扇，加强通风'),
('CURTAIN_001', 'ADJUST', 0, 'admin', NOW() - INTERVAL 1 HOUR, '手动调整卷帘开度至50%');

-- 5. 验证数据插入
SELECT '=== 节点信息 ===' as info;
SELECT node_id, node_name, battery_level, signal_strength, last_comm_time FROM node_info;

SELECT '=== 传感器数据统计 ===' as info;
SELECT node_id, COUNT(*) as count, 
       MAX(collect_time) as latest_time,
       AVG(temperature) as avg_temp,
       AVG(humidity) as avg_humi
FROM sensor_data 
GROUP BY node_id;

SELECT '=== 告警记录统计 ===' as info;
SELECT alarm_type, alarm_level, 
       SUM(CASE WHEN status = 0 THEN 1 ELSE 0 END) as unhandled,
       SUM(CASE WHEN status = 1 THEN 1 ELSE 0 END) as handled
FROM alarm_record 
GROUP BY alarm_type, alarm_level;

SELECT '=== 设备控制日志统计 ===' as info;
SELECT device_id, control_mode, COUNT(*) as count
FROM device_control_log
GROUP BY device_id, control_mode;

SELECT '示例数据插入完成！' as result;
