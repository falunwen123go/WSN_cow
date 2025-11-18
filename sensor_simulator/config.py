"""
配置管理模块
读取和管理 config.yaml 配置文件
"""

import yaml
import os
from pathlib import Path


class Config:
    """配置管理类"""
    
    def __init__(self, config_file='config.yaml'):
        """
        初始化配置
        
        Args:
            config_file: 配置文件路径
        """
        self.config_file = config_file
        self.config_path = Path(__file__).parent / config_file
        self.config = self._load_config()
    
    def _load_config(self):
        """
        加载配置文件
        
        Returns:
            配置字典
        """
        try:
            if not self.config_path.exists():
                raise FileNotFoundError(f"配置文件不存在: {self.config_path}")
            
            with open(self.config_path, 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
            
            return config
        except Exception as e:
            print(f"加载配置文件失败: {e}")
            return self._get_default_config()
    
    def _get_default_config(self):
        """
        获取默认配置
        
        Returns:
            默认配置字典
        """
        return {
            'network': {
                'coordinator_ip': 'localhost',
                'coordinator_port': 8888,
                'protocol': 'socket'
            },
            'nodes': [
                {'node_id': 'NODE_001', 'location': '牛棚1号区域', 'enabled': True, 'data_interval': 60}
            ],
            'sensors': {
                'temperature': {'range': [0, 50], 'normal_range': [15, 28], 'accuracy': 2.0, 'unit': '℃'},
                'humidity': {'range': [20, 95], 'normal_range': [50, 75], 'accuracy': 5.0, 'unit': '%RH'},
                'nh3': {'range': [0, 100], 'normal_range': [5, 15], 'alarm_level_1': 25, 'alarm_level_2': 40, 'accuracy': 1.0, 'unit': 'ppm'},
                'h2s': {'range': [0, 50], 'normal_range': [0, 5], 'alarm_level_1': 10, 'alarm_level_2': 20, 'accuracy': 0.5, 'unit': 'ppm'}
            },
            'data_generation': {
                'mode': 'normal',
                'abnormal_probability': 0.1,
                'noise_level': 0.05,
                'trend_enabled': True
            },
            'zigbee': {
                'pan_id': '0x1234',
                'channel': 11,
                'coordinator_address': '0x0000',
                'frame_header': '7E',
                'checksum_enabled': True
            },
            'logging': {
                'level': 'INFO',
                'file': 'logs/simulator.log',
                'console': True
            }
        }
    
    def get(self, key, default=None):
        """
        获取配置项
        
        Args:
            key: 配置键（支持点号分隔的多级键）
            default: 默认值
            
        Returns:
            配置值
        """
        keys = key.split('.')
        value = self.config
        
        for k in keys:
            if isinstance(value, dict) and k in value:
                value = value[k]
            else:
                return default
        
        return value
    
    def get_network_config(self):
        """获取网络配置"""
        return self.get('network', {})
    
    def get_nodes_config(self):
        """获取节点配置"""
        return self.get('nodes', [])
    
    def get_sensor_config(self, sensor_type):
        """
        获取传感器配置
        
        Args:
            sensor_type: 传感器类型（temperature, humidity, nh3, h2s）
            
        Returns:
            传感器配置字典
        """
        return self.get(f'sensors.{sensor_type}', {})
    
    def get_data_generation_config(self):
        """获取数据生成配置"""
        return self.get('data_generation', {})
    
    def get_zigbee_config(self):
        """获取ZigBee配置"""
        return self.get('zigbee', {})
    
    def get_logging_config(self):
        """获取日志配置"""
        return self.get('logging', {})


# 全局配置实例
config = Config()


if __name__ == '__main__':
    # 测试配置读取
    print("网络配置:", config.get_network_config())
    print("节点配置:", config.get_nodes_config())
    print("温度传感器配置:", config.get_sensor_config('temperature'))
    print("数据生成配置:", config.get_data_generation_config())
