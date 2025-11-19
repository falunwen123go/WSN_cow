-- ============================================
-- 添加产奶量字段到传感器数据表
-- ============================================

USE wsn_cow_monitor;

-- 添加产奶量字段
ALTER TABLE sensor_data 
ADD COLUMN milk_yield DECIMAL(6,2) NULL COMMENT '产奶量(kg/天)' 
AFTER h2s_concentration;

-- 添加索引以提高查询性能
ALTER TABLE sensor_data 
ADD INDEX idx_milk_yield (milk_yield);

-- 验证字段是否添加成功
DESCRIBE sensor_data;

-- 为现有数据填充示例产奶量数据 (基于环境参数计算)
UPDATE sensor_data 
SET milk_yield = CASE 
    -- 根据温度和湿度计算产奶量
    WHEN temperature BETWEEN 15 AND 25 AND humidity BETWEEN 50 AND 70 AND nh3_concentration < 20 AND h2s_concentration < 10 
        THEN 28.0 + (RAND() * 7.0)  -- 良好环境: 28-35 kg
    WHEN temperature BETWEEN 10 AND 30 AND humidity BETWEEN 40 AND 80 AND nh3_concentration < 30 AND h2s_concentration < 15 
        THEN 22.0 + (RAND() * 6.0)  -- 一般环境: 22-28 kg
    ELSE 18.0 + (RAND() * 4.0)      -- 较差环境: 18-22 kg
END
WHERE milk_yield IS NULL;

-- 显示更新结果
SELECT 
    node_id,
    temperature,
    humidity,
    nh3_concentration,
    h2s_concentration,
    milk_yield,
    collect_time
FROM sensor_data
ORDER BY collect_time DESC
LIMIT 10;
