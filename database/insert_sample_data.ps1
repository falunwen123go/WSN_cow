# 插入示例数据的PowerShell脚本

$mysqlCmd = "mysql -u root -proot wsn_cow_monitor"

# 插入传感器数据 - NODE_001
Write-Host "插入NODE_001传感器数据..."
$sql = @"
INSERT INTO sensor_data (node_id, temperature, humidity, nh3_concentration, h2s_concentration, data_status, collect_time, create_time) VALUES
('NODE_001', 23.5, 65.0, 15.2, 5.1, 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('NODE_001', 24.2, 63.5, 18.5, 6.2, 0, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('NODE_001', 25.1, 62.0, 22.3, 7.5, 0, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),
('NODE_001', 26.8, 60.5, 28.6, 8.9, 0, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),
('NODE_001', 28.2, 58.0, 35.2, 11.2, 0, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR),
('NODE_001', 27.5, 59.5, 32.1, 10.3, 0, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 11 HOUR),
('NODE_001', 25.8, 61.0, 26.5, 8.5, 0, NOW() - INTERVAL 13 HOUR, NOW() - INTERVAL 13 HOUR),
('NODE_001', 24.3, 64.0, 20.8, 6.8, 0, NOW() - INTERVAL 15 HOUR, NOW() - INTERVAL 15 HOUR);
"@
Invoke-Expression "$mysqlCmd -e `"$sql`""

# 插入传感器数据 - NODE_002
Write-Host "插入NODE_002传感器数据..."
$sql = @"
INSERT INTO sensor_data (node_id, temperature, humidity, nh3_concentration, h2s_concentration, data_status, collect_time, create_time) VALUES
('NODE_002', 22.8, 67.0, 14.5, 4.8, 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('NODE_002', 23.5, 65.5, 17.2, 5.9, 0, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('NODE_002', 24.8, 63.0, 21.8, 7.2, 0, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),
('NODE_002', 26.2, 61.5, 27.5, 8.5, 0, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),
('NODE_002', 27.8, 59.0, 33.8, 10.8, 0, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR),
('NODE_002', 26.9, 60.5, 30.2, 9.6, 0, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 11 HOUR),
('NODE_002', 25.2, 62.0, 24.5, 7.8, 0, NOW() - INTERVAL 13 HOUR, NOW() - INTERVAL 13 HOUR),
('NODE_002', 23.8, 65.0, 19.3, 6.3, 0, NOW() - INTERVAL 15 HOUR, NOW() - INTERVAL 15 HOUR);
"@
Invoke-Expression "$mysqlCmd -e `"$sql`""

# 插入传感器数据 - NODE_003
Write-Host "插入NODE_003传感器数据..."
$sql = @"
INSERT INTO sensor_data (node_id, temperature, humidity, nh3_concentration, h2s_concentration, data_status, collect_time, create_time) VALUES
('NODE_003', 24.0, 64.0, 16.8, 5.5, 0, NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 1 HOUR),
('NODE_003', 25.2, 62.0, 20.5, 6.8, 0, NOW() - INTERVAL 3 HOUR, NOW() - INTERVAL 3 HOUR),
('NODE_003', 26.5, 60.0, 25.2, 8.2, 0, NOW() - INTERVAL 5 HOUR, NOW() - INTERVAL 5 HOUR),
('NODE_003', 28.5, 57.5, 32.8, 10.5, 0, NOW() - INTERVAL 7 HOUR, NOW() - INTERVAL 7 HOUR),
('NODE_003', 30.2, 55.0, 42.5, 13.8, 0, NOW() - INTERVAL 9 HOUR, NOW() - INTERVAL 9 HOUR),
('NODE_003', 29.1, 56.5, 38.2, 12.1, 0, NOW() - INTERVAL 11 HOUR, NOW() - INTERVAL 11 HOUR),
('NODE_003', 27.2, 59.0, 28.8, 9.2, 0, NOW() - INTERVAL 13 HOUR, NOW() - INTERVAL 13 HOUR),
('NODE_003', 25.5, 61.5, 22.5, 7.3, 0, NOW() - INTERVAL 15 HOUR, NOW() - INTERVAL 15 HOUR);
"@
Invoke-Expression "$mysqlCmd -e `"$sql`""

# 插入告警记录
Write-Host "插入告警记录..."
$sql = @"
INSERT INTO alarm_record (node_id, alarm_type, alarm_level, current_value, threshold_value, alarm_time, status, handle_time, remark) VALUES
('NODE_001', 'NH3', 2, 42.5, 40.0, NOW() - INTERVAL 8 HOUR, 1, NOW() - INTERVAL 7 HOUR, '氨气浓度严重超标，已启动排风扇'),
('NODE_002', 'TEMP', 1, 36.2, 35.0, NOW() - INTERVAL 6 HOUR, 1, NOW() - INTERVAL 5 HOUR, '温度轻微超标，已调整卷帘'),
('NODE_003', 'H2S', 2, 21.5, 20.0, NOW() - INTERVAL 5 HOUR, 1, NOW() - INTERVAL 4 HOUR, '硫化氢浓度超标，已启动通风系统'),
('NODE_003', 'NH3', 3, 45.8, 40.0, NOW() - INTERVAL 2 HOUR, 0, NULL, NULL),
('NODE_001', 'TEMP', 2, 37.5, 35.0, NOW() - INTERVAL 1 HOUR, 0, NULL, NULL),
('NODE_002', 'HUMI', 1, 52.0, 50.0, NOW() - INTERVAL 30 MINUTE, 0, NULL, NULL);
"@
Invoke-Expression "$mysqlCmd -e `"$sql`""

# 插入设备控制日志
Write-Host "插入设备控制日志..."
$sql = @"
INSERT INTO device_control_log (device_id, control_action, control_mode, operator, control_time, reason) VALUES
('FAN_001', 'START', 1, 'System', NOW() - INTERVAL 8 HOUR, '氨气浓度42.5ppm超过阈值40.0ppm，自动启动排风扇'),
('CURTAIN_001', 'ADJUST', 1, 'System', NOW() - INTERVAL 6 HOUR, '温度36.2℃超过阈值35.0℃，自动调整卷帘降温'),
('FAN_001', 'STOP', 1, 'System', NOW() - INTERVAL 3 HOUR, '氨气浓度降至安全范围，自动关闭排风扇'),
('HEATING_001', 'START', 0, 'admin', NOW() - INTERVAL 5 HOUR, '手动启动供热系统，预防夜间温度过低'),
('FAN_001', 'START', 0, 'admin', NOW() - INTERVAL 2 HOUR, '手动启动排风扇，加强通风'),
('CURTAIN_001', 'ADJUST', 0, 'admin', NOW() - INTERVAL 1 HOUR, '手动调整卷帘开度至50%');
"@
Invoke-Expression "$mysqlCmd -e `"$sql`""

Write-Host "所有示例数据插入完成！" -ForegroundColor Green
