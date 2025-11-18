-- =====================================================
-- 修复数据库表结构
-- 执行时间: 2025-11-18
-- 说明: 添加NodeInfo和SystemConfig表缺失的字段
-- =====================================================

USE wsn_cow_monitor;

-- 1. 为node_info表添加缺失的字段
ALTER TABLE node_info
ADD COLUMN battery_level INT COMMENT '电池电量(%)' AFTER status,
ADD COLUMN signal_strength INT COMMENT '信号强度' AFTER battery_level,
ADD COLUMN last_comm_time DATETIME COMMENT '最后通信时间' AFTER signal_strength,
ADD COLUMN install_date DATE COMMENT '安装日期' AFTER last_comm_time,
ADD COLUMN remark VARCHAR(200) COMMENT '备注' AFTER install_date;

-- 2. 为system_config表添加缺失的字段
ALTER TABLE system_config
ADD COLUMN config_type VARCHAR(20) COMMENT '配置类型' AFTER config_key,
ADD COLUMN create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间' AFTER description;

-- 3. 验证表结构
DESC node_info;
DESC system_config;
