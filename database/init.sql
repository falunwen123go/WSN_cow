-- =====================================================
-- 智能牛棚环境监测系统 - 数据库初始化脚本
-- 创建日期: 2025-11-17
-- 数据库: MySQL 8.0+
-- =====================================================

-- 创建数据库
DROP DATABASE IF EXISTS wsn_cow_monitor;
CREATE DATABASE wsn_cow_monitor DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE wsn_cow_monitor;

-- =====================================================
-- 1. 传感器数据表 (sensor_data)
-- =====================================================
DROP TABLE IF EXISTS sensor_data;
CREATE TABLE sensor_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    node_id VARCHAR(20) NOT NULL COMMENT '节点ID',
    temperature DECIMAL(5,2) NOT NULL COMMENT '温度值(°C)',
    humidity DECIMAL(5,2) NOT NULL COMMENT '湿度值(%)',
    nh3_concentration DECIMAL(6,2) NOT NULL COMMENT '氨气浓度(ppm)',
    h2s_concentration DECIMAL(6,2) NOT NULL COMMENT '硫化氢浓度(ppm)',
    data_status TINYINT DEFAULT 0 COMMENT '数据状态(0正常,1异常)',
    collect_time DATETIME NOT NULL COMMENT '采集时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '记录创建时间',
    INDEX idx_node_id (node_id),
    INDEX idx_collect_time (collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='传感器数据表';

-- =====================================================
-- 2. 节点信息表 (node_info)
-- =====================================================
DROP TABLE IF EXISTS node_info;
CREATE TABLE node_info (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    node_id VARCHAR(20) UNIQUE NOT NULL COMMENT '节点ID',
    node_name VARCHAR(50) NOT NULL COMMENT '节点名称',
    location VARCHAR(100) COMMENT '节点位置',
    status TINYINT DEFAULT 1 COMMENT '节点状态(0离线,1在线)',
    last_online_time DATETIME COMMENT '最后在线时间',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='节点信息表';

-- =====================================================
-- 3. 告警记录表 (alarm_record)
-- =====================================================
DROP TABLE IF EXISTS alarm_record;
CREATE TABLE alarm_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    node_id VARCHAR(20) NOT NULL COMMENT '节点ID',
    alarm_type VARCHAR(20) NOT NULL COMMENT '告警类型(temperature/humidity/nh3/h2s)',
    alarm_level TINYINT NOT NULL COMMENT '告警级别(1警告,2严重,3紧急)',
    current_value DECIMAL(6,2) NOT NULL COMMENT '当前值',
    threshold_value DECIMAL(6,2) NOT NULL COMMENT '阈值',
    alarm_time DATETIME NOT NULL COMMENT '告警时间',
    status TINYINT DEFAULT 0 COMMENT '处理状态(0未处理,1已处理)',
    handle_time DATETIME COMMENT '处理时间',
    remark VARCHAR(200) COMMENT '备注',
    INDEX idx_node_id (node_id),
    INDEX idx_alarm_time (alarm_time),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='告警记录表';

-- =====================================================
-- 4. 设备信息表 (device_info)
-- =====================================================
DROP TABLE IF EXISTS device_info;
CREATE TABLE device_info (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    device_id VARCHAR(20) UNIQUE NOT NULL COMMENT '设备ID',
    device_name VARCHAR(50) NOT NULL COMMENT '设备名称',
    device_type VARCHAR(20) NOT NULL COMMENT '设备类型(fan/curtain/heating)',
    status TINYINT DEFAULT 0 COMMENT '设备状态(0关闭,1开启)',
    control_mode TINYINT DEFAULT 1 COMMENT '控制模式(0手动,1自动)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备信息表';

-- =====================================================
-- 5. 设备控制日志表 (device_control_log)
-- =====================================================
DROP TABLE IF EXISTS device_control_log;
CREATE TABLE device_control_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    device_id VARCHAR(20) NOT NULL COMMENT '设备ID',
    control_action VARCHAR(10) NOT NULL COMMENT '控制动作(on/off)',
    control_mode TINYINT NOT NULL COMMENT '控制模式(0手动,1自动)',
    operator VARCHAR(50) COMMENT '操作者',
    control_time DATETIME NOT NULL COMMENT '控制时间',
    reason VARCHAR(200) COMMENT '原因/触发条件',
    INDEX idx_device_id (device_id),
    INDEX idx_control_time (control_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='设备控制日志表';

-- =====================================================
-- 6. 系统配置表 (system_config)
-- =====================================================
DROP TABLE IF EXISTS system_config;
CREATE TABLE system_config (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    config_key VARCHAR(50) UNIQUE NOT NULL COMMENT '配置键',
    config_value VARCHAR(200) NOT NULL COMMENT '配置值',
    description VARCHAR(200) COMMENT '描述',
    update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='系统配置表';

-- =====================================================
-- 7. 用户表 (user) - 可选
-- =====================================================
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    id INT AUTO_INCREMENT PRIMARY KEY COMMENT '主键ID',
    username VARCHAR(50) UNIQUE NOT NULL COMMENT '用户名',
    password VARCHAR(100) NOT NULL COMMENT '密码(加密)',
    real_name VARCHAR(50) COMMENT '真实姓名',
    role VARCHAR(20) DEFAULT 'user' COMMENT '角色(admin/user)',
    status TINYINT DEFAULT 1 COMMENT '状态(0禁用,1启用)',
    create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    last_login_time DATETIME COMMENT '最后登录时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- =====================================================
-- 初始化数据
-- =====================================================

-- 插入节点信息
INSERT INTO node_info (node_id, node_name, location, status, last_online_time) VALUES
('NODE_001', '1号传感器节点', '牛棚东区A栏', 1, NOW()),
('NODE_002', '2号传感器节点', '牛棚中区B栏', 1, NOW()),
('NODE_003', '3号传感器节点', '牛棚西区C栏', 1, NOW());

-- 插入设备信息
INSERT INTO device_info (device_id, device_name, device_type, status, control_mode) VALUES
('FAN_001', '1号排风扇', 'fan', 0, 1),
('CURTAIN_001', '1号卷帘', 'curtain', 0, 1),
('HEATING_001', '1号供热系统', 'heating', 0, 1);

-- 插入系统配置 - 告警阈值
INSERT INTO system_config (config_key, config_value, description) VALUES
-- 氨气告警阈值
('alarm.nh3.warning', '25', '氨气警告阈值(ppm)'),
('alarm.nh3.critical', '40', '氨气严重告警阈值(ppm)'),

-- 硫化氢告警阈值
('alarm.h2s.warning', '10', '硫化氢警告阈值(ppm)'),
('alarm.h2s.critical', '20', '硫化氢严重告警阈值(ppm)'),

-- 温度告警阈值
('alarm.temp.low.warning', '5', '温度低警告阈值(°C)'),
('alarm.temp.low.critical', '0', '温度低严重告警阈值(°C)'),
('alarm.temp.high.warning', '35', '温度高警告阈值(°C)'),
('alarm.temp.high.critical', '40', '温度高严重告警阈值(°C)'),

-- 湿度告警阈值
('alarm.humidity.low', '30', '湿度低告警阈值(%)'),
('alarm.humidity.high', '85', '湿度高告警阈值(%)'),

-- 系统配置
('system.data.collect.interval', '30', '数据采集周期(秒)'),
('system.node.offline.timeout', '300', '节点离线超时时间(秒)'),
('system.alarm.deduplicate.interval', '600', '告警去重间隔(秒)'),

-- 串口配置
('serial.port', '8888', 'Socket通信端口'),
('serial.baudrate', '115200', '波特率');

-- 插入默认管理员用户 (密码: admin123，使用简单加密)
INSERT INTO user (username, password, real_name, role) VALUES
('admin', '$2a$10$EhL8V/VX8EW1k5RjPvKOZ.YPxH1JCJ8U6xZ0X3K9Y2L8M4N6O7P8Q', '系统管理员', 'admin');

-- =====================================================
-- 查询验证
-- =====================================================
SELECT '数据库创建完成！' AS message;
SELECT '节点信息：' AS info;
SELECT * FROM node_info;
SELECT '设备信息：' AS info;
SELECT * FROM device_info;
SELECT '系统配置：' AS info;
SELECT * FROM system_config;

-- =====================================================
-- 脚本结束
-- =====================================================
