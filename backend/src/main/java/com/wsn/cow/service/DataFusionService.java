package com.wsn.cow.service;

import com.wsn.cow.entity.SensorData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据融合分析服务
 * 实现基于多因子的智能阈值计算和异常检测
 */
@Service
public class DataFusionService {
    
    private static final Logger logger = LoggerFactory.getLogger(DataFusionService.class);
    
    @Autowired
    private SystemConfigService configService;
    
    /**
     * 分析传感器数据并计算综合评分
     * @param sensorData 传感器数据
     * @return 综合分析结果
     */
    public Map<String, Object> analyzeSensorData(SensorData sensorData) {
        Map<String, Object> result = new HashMap<>();
        
        // 1. 计算THI(温湿度指数)
        Double thi = sensorData.calculateTHI();
        result.put("thi", thi);
        result.put("thiLevel", getTHILevel(thi));
        
        // 2. 计算AQI(空气质量指数)
        Double aqi = sensorData.calculateAQI();
        result.put("aqi", aqi);
        result.put("aqiLevel", getAQILevel(aqi));
        
        // 3. 计算综合环境评分 (0-100分)
        double score = calculateEnvironmentScore(thi, aqi);
        result.put("environmentScore", score);
        result.put("scoreLevel", getScoreLevel(score));
        
        // 4. 预测产奶量影响
        double thiLoss = SensorData.getTHILoss(thi);
        double aqiLoss = SensorData.getAQILoss(aqi);
        double totalLoss = (thiLoss + aqiLoss - thiLoss * aqiLoss) * 100;
        result.put("milkYieldLoss", String.format("%.1f%%", totalLoss));
        
        // 5. 智能建议
        result.put("suggestions", generateSuggestions(thi, aqi, score));
        
        // 6. 动态阈值调整
        result.put("dynamicThresholds", calculateDynamicThresholds(thi, aqi));
        
        logger.info("数据融合分析完成: nodeId={}, THI={}, AQI={}, score={}", 
                   sensorData.getNodeId(), thi, aqi, score);
        
        return result;
    }
    
    /**
     * 计算综合环境评分 (0-100分)
     */
    private double calculateEnvironmentScore(Double thi, Double aqi) {
        if (thi == null || aqi == null) {
            return 50.0;
        }
        
        // THI评分 (60分权重)
        double thiScore;
        if (thi < 68) {
            thiScore = 60.0;
        } else if (thi < 72) {
            thiScore = 60.0 - (thi - 68) * 2.5; // 60-50
        } else if (thi < 79) {
            thiScore = 50.0 - (thi - 72) * 2.86; // 50-30
        } else {
            thiScore = Math.max(30.0 - (thi - 79) * 3.0, 0); // 30-0
        }
        
        // AQI评分 (40分权重)
        double aqiScore;
        if (aqi < 0.3) {
            aqiScore = 40.0;
        } else if (aqi < 0.6) {
            aqiScore = 40.0 - (aqi - 0.3) * 33.33; // 40-30
        } else {
            aqiScore = Math.max(30.0 - (aqi - 0.6) * 75.0, 0); // 30-0
        }
        
        return thiScore + aqiScore;
    }
    
    /**
     * 获取THI等级描述
     */
    private String getTHILevel(Double thi) {
        if (thi == null) return "未知";
        if (thi < 68) return "舒适";
        if (thi < 72) return "轻度应激";
        if (thi < 79) return "中度应激";
        return "严重应激";
    }
    
    /**
     * 获取AQI等级描述
     */
    private String getAQILevel(Double aqi) {
        if (aqi == null) return "未知";
        if (aqi < 0.3) return "优";
        if (aqi < 0.6) return "良";
        return "差";
    }
    
    /**
     * 获取环境评分等级
     */
    private String getScoreLevel(double score) {
        if (score >= 85) return "优秀";
        if (score >= 70) return "良好";
        if (score >= 50) return "一般";
        if (score >= 30) return "较差";
        return "恶劣";
    }
    
    /**
     * 生成智能建议
     */
    private String generateSuggestions(Double thi, Double aqi, double score) {
        StringBuilder suggestions = new StringBuilder();
        
        if (thi != null && thi >= 72) {
            suggestions.append("温度过高,建议开启降温设备; ");
        } else if (thi != null && thi < 60) {
            suggestions.append("温度偏低,建议开启加热设备; ");
        }
        
        if (aqi != null && aqi >= 0.6) {
            suggestions.append("空气质量差,建议开启通风设备; ");
        }
        
        if (score < 50) {
            suggestions.append("环境综合评分较低,建议立即采取措施改善; ");
        }
        
        if (suggestions.length() == 0) {
            suggestions.append("环境状况良好,无需特殊处理");
        }
        
        return suggestions.toString();
    }
    
    /**
     * 计算动态阈值
     * 根据当前环境状况自适应调整告警阈值
     */
    public Map<String, Double> calculateDynamicThresholds(Double thi, Double aqi) {
        Map<String, Double> thresholds = new HashMap<>();
        
        // 获取基础阈值
        double baseNH3 = getConfigValue("threshold.nh3", 25.0);
        double baseH2S = getConfigValue("threshold.h2s", 10.0);
        double baseTemp = getConfigValue("threshold.temperature", 28.0);
        double baseHumi = getConfigValue("threshold.humidity", 75.0);
        
        // 根据THI动态调整温度阈值
        if (thi != null) {
            if (thi >= 72) {
                // 已经处于应激状态,降低温度阈值(更严格)
                baseTemp = baseTemp - 2.0;
            } else if (thi < 65) {
                // 环境舒适,可以适当放宽阈值
                baseTemp = baseTemp + 1.0;
            }
        }
        
        // 根据AQI动态调整气体阈值
        if (aqi != null) {
            if (aqi >= 0.6) {
                // 空气质量已经很差,降低气体阈值(更严格)
                baseNH3 = baseNH3 * 0.8;
                baseH2S = baseH2S * 0.8;
            } else if (aqi < 0.3) {
                // 空气质量好,可以适当放宽
                baseNH3 = baseNH3 * 1.1;
                baseH2S = baseH2S * 1.1;
            }
        }
        
        thresholds.put("nh3", baseNH3);
        thresholds.put("h2s", baseH2S);
        thresholds.put("temperature", baseTemp);
        thresholds.put("humidity", baseHumi);
        
        return thresholds;
    }
    
    /**
     * 智能异常检测
     * @param sensorData 传感器数据
     * @return 是否异常
     */
    public boolean detectAnomaly(SensorData sensorData) {
        Double thi = sensorData.calculateTHI();
        Double aqi = sensorData.calculateAQI();
        
        // 获取动态阈值
        Map<String, Double> thresholds = calculateDynamicThresholds(thi, aqi);
        
        // 检测各项指标
        boolean tempAnomaly = sensorData.getTemperature() > thresholds.get("temperature");
        boolean humiAnomaly = sensorData.getHumidity() > thresholds.get("humidity");
        boolean nh3Anomaly = sensorData.getNh3Concentration() > thresholds.get("nh3");
        boolean h2sAnomaly = sensorData.getH2sConcentration() > thresholds.get("h2s");
        
        // 多因素综合判断
        int anomalyCount = 0;
        if (tempAnomaly) anomalyCount++;
        if (humiAnomaly) anomalyCount++;
        if (nh3Anomaly) anomalyCount++;
        if (h2sAnomaly) anomalyCount++;
        
        // 至少有一项超标,或者THI/AQI严重超标
        boolean result = anomalyCount >= 1 || 
                        (thi != null && thi >= 79) || 
                        (aqi != null && aqi >= 0.8);
        
        if (result) {
            logger.warn("检测到异常数据: nodeId={}, anomalyCount={}, THI={}, AQI={}", 
                       sensorData.getNodeId(), anomalyCount, thi, aqi);
        }
        
        return result;
    }
    
    /**
     * 批量分析多个节点数据并生成报告
     */
    public Map<String, Object> generateFusionReport(List<SensorData> dataList) {
        Map<String, Object> report = new HashMap<>();
        
        double avgTHI = 0;
        double avgAQI = 0;
        double avgScore = 0;
        int normalCount = 0;
        int abnormalCount = 0;
        
        for (SensorData data : dataList) {
            Double thi = data.calculateTHI();
            Double aqi = data.calculateAQI();
            
            if (thi != null && aqi != null) {
                avgTHI += thi;
                avgAQI += aqi;
                avgScore += calculateEnvironmentScore(thi, aqi);
                
                if (detectAnomaly(data)) {
                    abnormalCount++;
                } else {
                    normalCount++;
                }
            }
        }
        
        int validCount = normalCount + abnormalCount;
        if (validCount > 0) {
            avgTHI /= validCount;
            avgAQI /= validCount;
            avgScore /= validCount;
        }
        
        report.put("totalNodes", dataList.size());
        report.put("validNodes", validCount);
        report.put("normalCount", normalCount);
        report.put("abnormalCount", abnormalCount);
        report.put("avgTHI", avgTHI);
        report.put("avgAQI", avgAQI);
        report.put("avgScore", avgScore);
        report.put("overallLevel", getScoreLevel(avgScore));
        
        return report;
    }
    
    /**
     * 获取配置值
     */
    private double getConfigValue(String key, double defaultValue) {
        try {
            String value = configService.getConfigValue(key);
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch (Exception e) {
            logger.warn("获取配置失败: key={}, 使用默认值: {}", key, defaultValue);
            return defaultValue;
        }
    }
}
