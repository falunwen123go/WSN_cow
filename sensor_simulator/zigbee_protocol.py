"""
ZigBee协议模拟模块
模拟ZigBee网络通信和数据包格式
"""

import json
import struct
from config import config


class ZigBeeProtocol:
    """ZigBee协议模拟类"""
    
    def __init__(self):
        """初始化ZigBee协议"""
        self.zigbee_config = config.get_zigbee_config()
        self.pan_id = self.zigbee_config.get('pan_id', '0x1234')
        self.channel = self.zigbee_config.get('channel', 11)
        self.coordinator_addr = self.zigbee_config.get('coordinator_address', '0x0000')
        self.frame_header = self.zigbee_config.get('frame_header', '7E')
        self.checksum_enabled = self.zigbee_config.get('checksum_enabled', True)
    
    def encode_data_packet(self, sensor_data):
        """
        将传感器数据编码为ZigBee数据包
        
        Args:
            sensor_data: 传感器数据字典
            
        Returns:
            编码后的数据包（JSON格式）
        """
        # ZigBee数据包结构
        packet = {
            'header': self.frame_header,
            'pan_id': self.pan_id,
            'source_address': self._get_node_address(sensor_data['node_id']),
            'destination_address': self.coordinator_addr,
            'channel': self.channel,
            'sequence_number': self._generate_sequence_number(),
            'payload': {
                'node_id': sensor_data['node_id'],
                'timestamp': sensor_data['timestamp'],
                'data': {
                    'temperature': sensor_data['temperature'],
                    'humidity': sensor_data['humidity'],
                    'nh3': sensor_data['nh3'],
                    'h2s': sensor_data['h2s']
                },
                'status': {
                    'battery_level': sensor_data.get('battery_level', 100),
                    'signal_strength': sensor_data.get('signal_strength', 100)
                }
            }
        }
        
        # 计算校验和
        if self.checksum_enabled:
            packet['checksum'] = self._calculate_checksum(packet['payload'])
        
        return packet
    
    def decode_data_packet(self, packet):
        """
        解码ZigBee数据包
        
        Args:
            packet: ZigBee数据包
            
        Returns:
            解码后的传感器数据
        """
        try:
            # 验证数据包头
            if packet.get('header') != self.frame_header:
                raise ValueError("Invalid frame header")
            
            # 验证校验和
            if self.checksum_enabled:
                expected_checksum = self._calculate_checksum(packet['payload'])
                if packet.get('checksum') != expected_checksum:
                    raise ValueError("Checksum mismatch")
            
            # 提取传感器数据
            payload = packet['payload']
            sensor_data = {
                'node_id': payload['node_id'],
                'timestamp': payload['timestamp'],
                'temperature': payload['data']['temperature'],
                'humidity': payload['data']['humidity'],
                'nh3': payload['data']['nh3'],
                'h2s': payload['data']['h2s'],
                'battery_level': payload['status']['battery_level'],
                'signal_strength': payload['status']['signal_strength']
            }
            
            return sensor_data
        except Exception as e:
            print(f"数据包解码失败: {e}")
            return None
    
    def create_json_message(self, sensor_data):
        """
        创建JSON格式的消息（用于Socket通信）
        
        Args:
            sensor_data: 传感器数据字典
            
        Returns:
            JSON字符串
        """
        # 编码为ZigBee数据包
        packet = self.encode_data_packet(sensor_data)
        
        # 转换为JSON字符串
        json_message = json.dumps(packet, ensure_ascii=False)
        
        return json_message
    
    def parse_json_message(self, json_message):
        """
        解析JSON格式的消息
        
        Args:
            json_message: JSON字符串
            
        Returns:
            传感器数据字典
        """
        try:
            # 解析JSON
            packet = json.loads(json_message)
            
            # 解码数据包
            sensor_data = self.decode_data_packet(packet)
            
            return sensor_data
        except Exception as e:
            print(f"JSON消息解析失败: {e}")
            return None
    
    def _get_node_address(self, node_id):
        """
        根据节点ID获取ZigBee地址
        
        Args:
            node_id: 节点ID
            
        Returns:
            ZigBee地址（16位地址）
        """
        # 简单的地址映射
        node_map = {
            'NODE_001': '0x0001',
            'NODE_002': '0x0002',
            'NODE_003': '0x0003'
        }
        return node_map.get(node_id, '0x0001')
    
    def _generate_sequence_number(self):
        """
        生成序列号
        
        Returns:
            序列号
        """
        if not hasattr(self, '_seq_number'):
            self._seq_number = 0
        
        self._seq_number = (self._seq_number + 1) % 256
        return self._seq_number
    
    def _calculate_checksum(self, payload):
        """
        计算数据包校验和
        
        Args:
            payload: 数据负载
            
        Returns:
            校验和（16进制字符串）
        """
        # 将payload转换为字符串
        payload_str = json.dumps(payload, sort_keys=True)
        
        # 计算简单的8位校验和
        checksum = 0
        for char in payload_str:
            checksum = (checksum + ord(char)) & 0xFF
        
        # 取反加1（补码）
        checksum = (~checksum + 1) & 0xFF
        
        return f"0x{checksum:02X}"


if __name__ == '__main__':
    # 测试ZigBee协议
    protocol = ZigBeeProtocol()
    
    # 模拟传感器数据
    test_data = {
        'node_id': 'NODE_001',
        'timestamp': '2024-01-15 10:30:00',
        'temperature': 22.5,
        'humidity': 65.3,
        'nh3': 12.5,
        'h2s': 3.2,
        'battery_level': 85,
        'signal_strength': 75
    }
    
    print("=== 原始传感器数据 ===")
    print(json.dumps(test_data, indent=2, ensure_ascii=False))
    
    print("\n=== 编码为ZigBee数据包 ===")
    packet = protocol.encode_data_packet(test_data)
    print(json.dumps(packet, indent=2, ensure_ascii=False))
    
    print("\n=== 创建JSON消息 ===")
    json_msg = protocol.create_json_message(test_data)
    print(json_msg)
    
    print("\n=== 解析JSON消息 ===")
    decoded_data = protocol.parse_json_message(json_msg)
    print(json.dumps(decoded_data, indent=2, ensure_ascii=False))
    
    print("\n=== 验证数据一致性 ===")
    print(f"温度一致: {test_data['temperature'] == decoded_data['temperature']}")
    print(f"湿度一致: {test_data['humidity'] == decoded_data['humidity']}")
    print(f"NH3一致: {test_data['nh3'] == decoded_data['nh3']}")
    print(f"H2S一致: {test_data['h2s'] == decoded_data['h2s']}")
