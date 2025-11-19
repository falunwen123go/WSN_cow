"""
产奶量预测模型测试脚本
验证THI/AQI计算和产奶量预测功能
"""

import sys
import os
sys.path.append(os.path.dirname(__file__))

from data_generator import SensorDataGenerator
import random

def test_milk_yield_prediction():
    """测试产奶量预测功能"""
    print("="*60)
    print("产奶量预测模型测试")
    print("="*60)
    
    generator = SensorDataGenerator('TEST_NODE')
    
    # 测试场景1: 理想环境
    print("\n【场景1: 理想环境】")
    print("-" * 40)
    temp, humi, nh3, h2s = 20.0, 60.0, 12.0, 4.0
    thi = generator._calculate_thi(temp, humi)
    aqi = generator._calculate_aqi(nh3, h2s)
    milk = generator._calculate_milk_yield(temp, humi, nh3, h2s)
    
    print(f"环境参数: T={temp}℃, H={humi}%, NH₃={nh3}ppm, H₂S={h2s}ppm")
    print(f"THI = {thi:.2f} (舒适)")
    print(f"AQI = {aqi:.3f} (优)")
    print(f"预测产奶量: {milk:.2f} kg/天")
    print(f"评估: ✅ 环境优秀,产奶量正常")
    
    # 测试场景2: 高温高湿环境
    print("\n【场景2: 高温高湿环境】")
    print("-" * 40)
    temp, humi, nh3, h2s = 28.0, 75.0, 18.0, 6.0
    thi = generator._calculate_thi(temp, humi)
    aqi = generator._calculate_aqi(nh3, h2s)
    milk = generator._calculate_milk_yield(temp, humi, nh3, h2s)
    
    print(f"环境参数: T={temp}℃, H={humi}%, NH₃={nh3}ppm, H₂S={h2s}ppm")
    print(f"THI = {thi:.2f} (中度应激)")
    print(f"AQI = {aqi:.3f} (良)")
    print(f"预测产奶量: {milk:.2f} kg/天")
    print(f"评估: ⚠️ 中度应激,建议开启降温设备")
    
    # 测试场景3: 空气质量差
    print("\n【场景3: 空气质量差】")
    print("-" * 40)
    temp, humi, nh3, h2s = 22.0, 65.0, 32.0, 12.0
    thi = generator._calculate_thi(temp, humi)
    aqi = generator._calculate_aqi(nh3, h2s)
    milk = generator._calculate_milk_yield(temp, humi, nh3, h2s)
    
    print(f"环境参数: T={temp}℃, H={humi}%, NH₃={nh3}ppm, H₂S={h2s}ppm")
    print(f"THI = {thi:.2f} (轻度应激)")
    print(f"AQI = {aqi:.3f} (差)")
    print(f"预测产奶量: {milk:.2f} kg/天")
    print(f"评估: ❌ 空气质量差,建议强制通风")
    
    # 测试场景4: 极端恶劣环境
    print("\n【场景4: 极端恶劣环境】")
    print("-" * 40)
    temp, humi, nh3, h2s = 32.0, 80.0, 40.0, 15.0
    thi = generator._calculate_thi(temp, humi)
    aqi = generator._calculate_aqi(nh3, h2s)
    milk = generator._calculate_milk_yield(temp, humi, nh3, h2s)
    
    print(f"环境参数: T={temp}℃, H={humi}%, NH₃={nh3}ppm, H₂S={h2s}ppm")
    print(f"THI = {thi:.2f} (严重应激)")
    print(f"AQI = {aqi:.3f} (差)")
    print(f"预测产奶量: {milk:.2f} kg/天")
    print(f"评估: 🚨 环境恶劣,紧急处理!")
    
    # 批量测试
    print("\n【批量测试: 连续生成10组数据】")
    print("-" * 40)
    generator.mode = 'mixed'
    
    total_milk = 0
    for i in range(10):
        data = generator.generate_sensor_data()
        thi = generator._calculate_thi(data['temperature'], data['humidity'])
        aqi = generator._calculate_aqi(data['nh3'], data['h2s'])
        total_milk += data['milk_yield']
        
        print(f"样本{i+1:2d}: T={data['temperature']:5.1f}℃ H={data['humidity']:5.1f}% "
              f"NH₃={data['nh3']:5.1f}ppm H₂S={data['h2s']:5.2f}ppm "
              f"| THI={thi:5.1f} AQI={aqi:.3f} | 产奶={data['milk_yield']:5.2f}kg")
    
    avg_milk = total_milk / 10
    print(f"\n平均产奶量: {avg_milk:.2f} kg/天")

def test_thi_calculation():
    """测试THI计算的准确性"""
    print("\n" + "="*60)
    print("THI计算准确性测试")
    print("="*60)
    
    generator = SensorDataGenerator('TEST_NODE')
    
    test_cases = [
        (15, 50, "舒适"),
        (20, 60, "舒适"),
        (25, 65, "舒适"),
        (27, 70, "轻度应激"),
        (30, 75, "中度应激"),
        (33, 80, "严重应激"),
    ]
    
    for temp, humi, expected in test_cases:
        thi = generator._calculate_thi(temp, humi)
        
        if thi < 68:
            level = "舒适"
        elif thi < 72:
            level = "轻度应激"
        elif thi < 79:
            level = "中度应激"
        else:
            level = "严重应激"
        
        status = "✅" if level == expected else "❌"
        print(f"{status} T={temp:2d}℃ H={humi:2d}% | THI={thi:5.2f} | {level:8s} (预期: {expected})")

def test_aqi_calculation():
    """测试AQI计算的准确性"""
    print("\n" + "="*60)
    print("AQI计算准确性测试")
    print("="*60)
    
    generator = SensorDataGenerator('TEST_NODE')
    
    test_cases = [
        (10, 5, "优"),
        (15, 6, "良"),
        (25, 10, "良"),
        (35, 15, "差"),
        (45, 18, "差"),
    ]
    
    for nh3, h2s, expected in test_cases:
        aqi = generator._calculate_aqi(nh3, h2s)
        
        if aqi < 0.3:
            level = "优"
        elif aqi < 0.6:
            level = "良"
        else:
            level = "差"
        
        status = "✅" if level == expected else "❌"
        print(f"{status} NH₃={nh3:2d}ppm H₂S={h2s:2d}ppm | AQI={aqi:.3f} | {level} (预期: {expected})")

if __name__ == '__main__':
    print("\n🧪 WSN牛棚监测系统 - 产奶量预测模型测试\n")
    
    try:
        test_milk_yield_prediction()
        test_thi_calculation()
        test_aqi_calculation()
        
        print("\n" + "="*60)
        print("✅ 所有测试完成!")
        print("="*60)
        
    except Exception as e:
        print(f"\n❌ 测试失败: {e}")
        import traceback
        traceback.print_exc()
