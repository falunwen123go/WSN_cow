package com.wsn.cow.entity;

import lombok.Data;
import java.util.Date;

/**
 * 传感器数据实体类
 */
@Data
public class SensorData {
    
    /**
     * 主键ID
     */
    private Long id;
    
    /**
     * 节点ID
     */
    private String nodeId;
    
    /**
     * 温度（摄氏度）
     */
    private Double temperature;
    
    /**
     * 湿度（%RH）
     */
    private Double humidity;
    
    /**
     * 氨气浓度（ppm）
     */
    private Double nh3Concentration;
    
    /**
     * 硫化氢浓度（ppm）
     */
    private Double h2sConcentration;
    
    /**
     * 产奶量（kg/天）
     */
    private Double milkYield;
    
    /**
     * 数据状态:0-正常,1-异常
     */
    private Integer dataStatus;
    
    /**
     * 数据采集时间
     */
    private Date collectTime;
    
    /**
     * 数据创建时间(接收时间)
     */
    private Date createTime;
    
    /**
     * 计算温湿度指数(THI)
     * THI = (1.8*T + 32) - [(0.55 - 0.0055*RH) * (1.8*T - 26)]
     * @return THI值
     */
    public Double calculateTHI() {
        if (temperature == null || humidity == null) {
            return null;
        }
        double t = temperature;
        double rh = humidity;
        return (1.8 * t + 32) - ((0.55 - 0.0055 * rh) * (1.8 * t - 26));
    }
    
    /**
     * 计算空气质量指数(AQI)
     * AQI = 0.6 * (NH3/50) + 0.4 * (H2S/20)
     * @return AQI值 (0-1)
     */
    public Double calculateAQI() {
        if (nh3Concentration == null || h2sConcentration == null) {
            return null;
        }
        double nh3Ratio = Math.min(nh3Concentration / 50.0, 1.0);
        double h2sRatio = Math.min(h2sConcentration / 20.0, 1.0);
        return 0.6 * nh3Ratio + 0.4 * h2sRatio;
    }
    
    /**
     * 根据THI计算产奶量损失率
     * @param thi 温湿度指数
     * @return 损失率 (0-1)
     */
    public static double getTHILoss(double thi) {
        if (thi < 68) {
            return 0.0; // 无应激
        } else if (thi < 72) {
            return 0.05 + (thi - 68) * 0.0125; // 5%-10%
        } else if (thi < 79) {
            return 0.10 + (thi - 72) * 0.0143; // 10%-20%
        } else {
            return Math.min(0.20 + (thi - 79) * 0.02, 0.40); // 20%-40%
        }
    }
    
    /**
     * 根据AQI计算产奶量损失率
     * @param aqi 空气质量指数
     * @return 损失率 (0-1)
     */
    public static double getAQILoss(double aqi) {
        if (aqi < 0.3) {
            return 0.0; // 良好
        } else if (aqi < 0.6) {
            return 0.05 + (aqi - 0.3) * 0.333; // 5%-15%
        } else {
            return Math.min(0.15 + (aqi - 0.6) * 0.375, 0.30); // 15%-30%
        }
    }
    
    /**
     * 预测产奶量
     * @param baseMilk 基础产奶量 (kg/天)
     * @param randomFactor 随机因子 (-0.05 ~ 0.05)
     * @return 预测产奶量 (kg/天)
     */
    public Double predictMilkYield(double baseMilk, double randomFactor) {
        Double thi = calculateTHI();
        Double aqi = calculateAQI();
        
        if (thi == null || aqi == null) {
            return null;
        }
        
        double thiLoss = getTHILoss(thi);
        double aqiLoss = getAQILoss(aqi);
        
        return baseMilk * (1 - thiLoss) * (1 - aqiLoss) * (1 + randomFactor);
    }
}
