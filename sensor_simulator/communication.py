"""
通信模块
实现Socket通信，向后端发送传感器数据
"""

import socket
import json
import time
import logging
from config import config
from zigbee_protocol import ZigBeeProtocol


class SocketCommunicator:
    """Socket通信类"""
    
    def __init__(self):
        """初始化Socket通信"""
        self.network_config = config.get_network_config()
        self.host = self.network_config.get('coordinator_ip', 'localhost')
        self.port = self.network_config.get('coordinator_port', 8888)
        self.protocol = ZigBeeProtocol()
        self.socket = None
        self.connected = False
        
        # 配置日志
        logging_config = config.get_logging_config()
        logging.basicConfig(
            level=getattr(logging, logging_config.get('level', 'INFO')),
            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
        )
        self.logger = logging.getLogger('SocketCommunicator')
    
    def connect(self, retry_count=3, retry_interval=5):
        """
        连接到后端服务器
        
        Args:
            retry_count: 重试次数
            retry_interval: 重试间隔（秒）
            
        Returns:
            是否连接成功
        """
        for attempt in range(retry_count):
            try:
                self.logger.info(f"正在连接到服务器 {self.host}:{self.port}... (尝试 {attempt + 1}/{retry_count})")
                
                self.socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
                self.socket.settimeout(10)
                self.socket.connect((self.host, self.port))
                
                self.connected = True
                self.logger.info("连接成功！")
                return True
                
            except Exception as e:
                self.logger.warning(f"连接失败: {e}")
                if attempt < retry_count - 1:
                    self.logger.info(f"等待 {retry_interval} 秒后重试...")
                    time.sleep(retry_interval)
                else:
                    self.logger.error("达到最大重试次数，连接失败")
        
        return False
    
    def send_data(self, sensor_data):
        """
        发送传感器数据
        
        Args:
            sensor_data: 传感器数据字典
            
        Returns:
            是否发送成功
        """
        if not self.connected:
            self.logger.error("未连接到服务器")
            return False
        
        try:
            # 编码为ZigBee协议JSON消息
            json_message = self.protocol.create_json_message(sensor_data)
            
            # 添加消息结束符
            message = json_message + '\n'
            
            # 发送数据
            self.socket.sendall(message.encode('utf-8'))
            
            self.logger.info(f"数据发送成功: 节点={sensor_data['node_id']}, "
                           f"温度={sensor_data['temperature']}℃, "
                           f"湿度={sensor_data['humidity']}%")
            
            return True
            
        except Exception as e:
            self.logger.error(f"数据发送失败: {e}")
            self.connected = False
            return False
    
    def receive_response(self, timeout=5):
        """
        接收服务器响应
        
        Args:
            timeout: 超时时间（秒）
            
        Returns:
            响应数据字典，失败返回None
        """
        if not self.connected:
            self.logger.error("未连接到服务器")
            return None
        
        try:
            self.socket.settimeout(timeout)
            response = self.socket.recv(4096).decode('utf-8')
            
            if response:
                response_data = json.loads(response)
                self.logger.debug(f"收到响应: {response_data}")
                return response_data
            
        except socket.timeout:
            self.logger.debug("接收响应超时（可能服务器无响应）")
        except Exception as e:
            self.logger.error(f"接收响应失败: {e}")
            self.connected = False
        
        return None
    
    def disconnect(self):
        """断开连接"""
        if self.socket:
            try:
                self.socket.close()
                self.logger.info("连接已断开")
            except Exception as e:
                self.logger.error(f"断开连接失败: {e}")
            finally:
                self.connected = False
                self.socket = None
    
    def is_connected(self):
        """
        检查连接状态
        
        Returns:
            是否已连接
        """
        return self.connected
    
    def reconnect(self):
        """重新连接"""
        self.logger.info("正在重新连接...")
        self.disconnect()
        return self.connect()


class MockCommunicator:
    """模拟通信类（用于测试）"""
    
    def __init__(self):
        """初始化模拟通信"""
        self.logger = logging.getLogger('MockCommunicator')
        self.connected = True
    
    def connect(self, retry_count=3, retry_interval=5):
        """模拟连接"""
        self.logger.info("模拟连接成功（测试模式）")
        self.connected = True
        return True
    
    def send_data(self, sensor_data):
        """模拟发送数据"""
        self.logger.info(f"[模拟发送] 节点={sensor_data['node_id']}, "
                        f"温度={sensor_data['temperature']}℃, "
                        f"湿度={sensor_data['humidity']}%, "
                        f"NH3={sensor_data['nh3']}ppm, "
                        f"H2S={sensor_data['h2s']}ppm")
        return True
    
    def receive_response(self, timeout=5):
        """模拟接收响应"""
        return {'status': 'success', 'message': 'Data received (mock)'}
    
    def disconnect(self):
        """模拟断开连接"""
        self.logger.info("模拟断开连接（测试模式）")
        self.connected = False
    
    def is_connected(self):
        """检查连接状态"""
        return self.connected
    
    def reconnect(self):
        """模拟重新连接"""
        return self.connect()


if __name__ == '__main__':
    # 测试通信模块
    from data_generator import SensorDataGenerator
    
    # 使用模拟通信器测试
    print("=== 使用模拟通信器测试 ===")
    mock_comm = MockCommunicator()
    mock_comm.connect()
    
    generator = SensorDataGenerator('NODE_001')
    for i in range(3):
        data = generator.generate_sensor_data()
        mock_comm.send_data(data)
        time.sleep(1)
    
    mock_comm.disconnect()
    
    # 如果需要测试真实Socket连接，取消下面的注释
    # print("\n=== 使用真实Socket连接测试 ===")
    # real_comm = SocketCommunicator()
    # if real_comm.connect():
    #     for i in range(3):
    #         data = generator.generate_sensor_data()
    #         real_comm.send_data(data)
    #         time.sleep(1)
    #     real_comm.disconnect()
