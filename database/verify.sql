-- 验证数据库和表是否创建成功
USE wsn_cow_monitor;

-- 显示所有表
SHOW TABLES;

-- 查看各表的记录数
SELECT 'node_info' AS table_name, COUNT(*) AS count FROM node_info
UNION ALL
SELECT 'device_info', COUNT(*) FROM device_info
UNION ALL
SELECT 'system_config', COUNT(*) FROM system_config
UNION ALL
SELECT 'user', COUNT(*) FROM user;

-- 查看节点信息
SELECT * FROM node_info;

-- 查看设备信息
SELECT * FROM device_info;

-- 查看部分系统配置
SELECT * FROM system_config LIMIT 5;
