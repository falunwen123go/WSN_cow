"""
传感器数据生成模块
模拟真实的传感器数据，支持正常模式和异常模式
"""

import random
import math
import time
from datetime import datetime
from config import config


class SensorDataGenerator:
    """传感器数据生成器"""
    
    def __init__(self, node_id):
        """
        初始化数据生成器
        
        Args:
            node_id: 节点ID
        """
        self.node_id = node_id
        self.data_gen_config = config.get_data_generation_config()
        self.mode = self.data_gen_config.get('mode', 'normal')
        self.abnormal_prob = self.data_gen_config.get('abnormal_probability', 0.1)
        self.noise_level = self.data_gen_config.get('noise_level', 0.05)
        self.trend_enabled = self.data_gen_config.get('trend_enabled', True)
        
        # 初始化传感器配置
        self.temp_config = config.get_sensor_config('temperature')
        self.hum_config = config.get_sensor_config('humidity')
        self.nh3_config = config.get_sensor_config('nh3')
        self.h2s_config = config.get_sensor_config('h2s')
        
        # 趋势变化参数
        self.time_offset = random.uniform(0, 2 * math.pi)  # 随机时间偏移
        self.trend_period = 3600  # 趋势周期（秒），1小时
    
    def generate_sensor_data(self):
        """
        生成一组完整的传感器数据
        
        Returns:
            包含所有传感器读数的字典
        """
        # 判断是否生成异常数据
        is_abnormal = False
        if self.mode == 'abnormal':
            is_abnormal = True
        elif self.mode == 'mixed':
            is_abnormal = random.random() < self.abnormal_prob
        
        # 生成各传感器数据
        temperature = self._generate_temperature(is_abnormal)
        humidity = self._generate_humidity(is_abnormal)
        nh3 = self._generate_nh3(is_abnormal)
        h2s = self._generate_h2s(is_abnormal)
        
        # 基于环境参数计算产奶量
        milk_yield = self._calculate_milk_yield(temperature, humidity, nh3, h2s)
        
        # 构建数据包
        data = {
            'node_id': self.node_id,
            'timestamp': datetime.now().strftime('%Y-%m-%d %H:%M:%S'),
            'temperature': round(temperature, 1),
            'humidity': round(humidity, 1),
            'nh3': round(nh3, 1),
            'h2s': round(h2s, 2),
            'milk_yield': round(milk_yield, 2),
            'battery_level': self._generate_battery_level(),
            'signal_strength': random.randint(60, 100)
        }
        
        return data
    
    def _generate_temperature(self, is_abnormal=False):
        """
        生成温度数据
        
        Args:
            is_abnormal: 是否生成异常数据
            
        Returns:
            温度值（℃）
        """
        if is_abnormal:
            # 异常温度：极低或极高
            if random.random() < 0.5:
                base_value = random.uniform(0, 10)  # 过低
            else:
                base_value = random.uniform(35, 45)  # 过高
        else:
            # 正常温度
            normal_range = self.temp_config.get('normal_range', [15, 28])
            center = sum(normal_range) / 2
            amplitude = (normal_range[1] - normal_range[0]) / 2
            
            # 添加趋势变化（模拟昼夜温差）
            if self.trend_enabled:
                trend = amplitude * 0.5 * math.sin(2 * math.pi * time.time() / self.trend_period + self.time_offset)
            else:
                trend = 0
            
            base_value = center + trend
        
        # 添加随机噪声
        noise = random.gauss(0, self.temp_config.get('accuracy', 2.0) * self.noise_level)
        value = base_value + noise
        
        # 限制在传感器量程内
        sensor_range = self.temp_config.get('range', [0, 50])
        value = max(sensor_range[0], min(sensor_range[1], value))
        
        return value
    
    def _generate_humidity(self, is_abnormal=False):
        """
        生成湿度数据
        
        Args:
            is_abnormal: 是否生成异常数据
            
        Returns:
            湿度值（%RH）
        """
        if is_abnormal:
            # 异常湿度：过低或过高
            if random.random() < 0.5:
                base_value = random.uniform(20, 40)  # 过低
            else:
                base_value = random.uniform(85, 95)  # 过高
        else:
            # 正常湿度
            normal_range = self.hum_config.get('normal_range', [50, 75])
            center = sum(normal_range) / 2
            amplitude = (normal_range[1] - normal_range[0]) / 2
            
            # 添加趋势变化（与温度相反的关系）
            if self.trend_enabled:
                trend = -amplitude * 0.4 * math.sin(2 * math.pi * time.time() / self.trend_period + self.time_offset)
            else:
                trend = 0
            
            base_value = center + trend
        
        # 添加随机噪声
        noise = random.gauss(0, self.hum_config.get('accuracy', 5.0) * self.noise_level)
        value = base_value + noise
        
        # 限制在传感器量程内
        sensor_range = self.hum_config.get('range', [20, 95])
        value = max(sensor_range[0], min(sensor_range[1], value))
        
        return value
    
    def _generate_nh3(self, is_abnormal=False):
        """
        生成氨气浓度数据
        
        Args:
            is_abnormal: 是否生成异常数据
            
        Returns:
            氨气浓度值（ppm）
        """
        if is_abnormal:
            # 异常氨气：超过警告阈值
            alarm_level_1 = self.nh3_config.get('alarm_level_1', 25)
            alarm_level_2 = self.nh3_config.get('alarm_level_2', 40)
            
            if random.random() < 0.3:
                # 30%概率达到二级警告
                base_value = random.uniform(alarm_level_2, alarm_level_2 + 15)
            else:
                # 70%概率达到一级警告
                base_value = random.uniform(alarm_level_1, alarm_level_2)
        else:
            # 正常氨气浓度
            normal_range = self.nh3_config.get('normal_range', [5, 15])
            base_value = random.uniform(normal_range[0], normal_range[1])
        
        # 添加随机噪声
        noise = random.gauss(0, self.nh3_config.get('accuracy', 1.0) * self.noise_level)
        value = base_value + noise
        
        # 限制在传感器量程内
        sensor_range = self.nh3_config.get('range', [0, 100])
        value = max(sensor_range[0], min(sensor_range[1], value))
        
        return value
    
    def _generate_h2s(self, is_abnormal=False):
        """
        生成硫化氢浓度数据
        
        Args:
            is_abnormal: 是否生成异常数据
            
        Returns:
            硫化氢浓度值（ppm）
        """
        if is_abnormal:
            # 异常硫化氢：超过警告阈值
            alarm_level_1 = self.h2s_config.get('alarm_level_1', 10)
            alarm_level_2 = self.h2s_config.get('alarm_level_2', 20)
            
            if random.random() < 0.3:
                # 30%概率达到二级警告
                base_value = random.uniform(alarm_level_2, alarm_level_2 + 8)
            else:
                # 70%概率达到一级警告
                base_value = random.uniform(alarm_level_1, alarm_level_2)
        else:
            # 正常硫化氢浓度
            normal_range = self.h2s_config.get('normal_range', [0, 5])
            base_value = random.uniform(normal_range[0], normal_range[1])
        
        # 添加随机噪声
        noise = random.gauss(0, self.h2s_config.get('accuracy', 0.5) * self.noise_level)
        value = base_value + noise
        
        # 限制在传感器量程内
        sensor_range = self.h2s_config.get('range', [0, 50])
        value = max(sensor_range[0], min(sensor_range[1], value))
        
        return value
    
    def _generate_battery_level(self):
        """
        生成电池电量
        
        Returns:
            电池电量（%）
        """
        # 模拟电池缓慢放电
        base_level = 100 - (time.time() % 86400) / 86400 * 5  # 每天减少5%
        noise = random.uniform(-2, 2)
        level = base_level + noise
        
        return max(0, min(100, round(level)))
    
    def _calculate_thi(self, temperature, humidity):
        """
        计算温湿度指数(THI - Temperature-Humidity Index)
        THI = (1.8*T + 32) - [(0.55 - 0.0055*RH) * (1.8*T - 26)]
        
        Args:
            temperature: 温度(℃)
            humidity: 湿度(%)
            
        Returns:
            THI值
        """
        t = temperature
        rh = humidity
        thi = (1.8 * t + 32) - ((0.55 - 0.0055 * rh) * (1.8 * t - 26))
        return thi
    
    def _calculate_aqi(self, nh3, h2s):
        """
        计算空气质量指数(AQI - Air Quality Index)
        AQI = 0.6 * (NH3/50) + 0.4 * (H2S/20)
        
        Args:
            nh3: 氨气浓度(ppm)
            h2s: 硫化氢浓度(ppm)
            
        Returns:
            AQI值(0-1)
        """
        nh3_ratio = min(nh3 / 50.0, 1.0)
        h2s_ratio = min(h2s / 20.0, 1.0)
        aqi = 0.6 * nh3_ratio + 0.4 * h2s_ratio
        return aqi
    
    def _calculate_milk_yield(self, temperature, humidity, nh3, h2s):
        """
        基于环境参数预测产奶量
        
        Args:
            temperature: 温度(℃)
            humidity: 湿度(%)
            nh3: 氨气浓度(ppm)
            h2s: 硫化氢浓度(ppm)
            
        Returns:
            产奶量(kg/天)
        """
        # 基础产奶量 (kg/天)
        base_milk = random.uniform(25.0, 30.0)
        
        # 计算THI和AQI
        thi = self._calculate_thi(temperature, humidity)
        aqi = self._calculate_aqi(nh3, h2s)
        
        # 根据THI计算产奶量损失
        if thi < 68:
            thi_loss = 0.0  # 无应激
        elif thi < 72:
            thi_loss = 0.05 + (thi - 68) * 0.0125  # 5%-10%
        elif thi < 79:
            thi_loss = 0.10 + (thi - 72) * 0.0143  # 10%-20%
        else:
            thi_loss = min(0.20 + (thi - 79) * 0.02, 0.40)  # 20%-40%
        
        # 根据AQI计算产奶量损失
        if aqi < 0.3:
            aqi_loss = 0.0  # 良好
        elif aqi < 0.6:
            aqi_loss = 0.05 + (aqi - 0.3) * 0.333  # 5%-15%
        else:
            aqi_loss = min(0.15 + (aqi - 0.6) * 0.375, 0.30)  # 15%-30%
        
        # 计算最终产奶量
        random_factor = random.uniform(-0.05, 0.05)  # ±5%随机波动
        milk_yield = base_milk * (1 - thi_loss) * (1 - aqi_loss) * (1 + random_factor)
        
        return max(15.0, milk_yield)  # 最低15kg/天


if __name__ == '__main__':
    # 测试数据生成器
    generator = SensorDataGenerator('NODE_001')
    
    print("=== 正常模式数据 ===")
    generator.mode = 'normal'
    for i in range(5):
        data = generator.generate_sensor_data()
        print(f"样本 {i+1}: 温度={data['temperature']}℃, 湿度={data['humidity']}%, "
              f"NH3={data['nh3']}ppm, H2S={data['h2s']}ppm, 产奶量={data['milk_yield']}kg")
    
    print("\n=== 异常模式数据 ===")
    generator.mode = 'abnormal'
    for i in range(5):
        data = generator.generate_sensor_data()
        print(f"样本 {i+1}: 温度={data['temperature']}℃, 湿度={data['humidity']}%, "
              f"NH3={data['nh3']}ppm, H2S={data['h2s']}ppm, 产奶量={data['milk_yield']}kg")
