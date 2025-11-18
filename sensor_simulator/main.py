"""
传感器模拟器主程序
模拟多个传感器节点向后端发送数据
"""

import time
import threading
import logging
import signal
import sys
from pathlib import Path
from config import config
from data_generator import SensorDataGenerator
from communication import SocketCommunicator, MockCommunicator


class SensorNode:
    """传感器节点类"""
    
    def __init__(self, node_config, use_mock=False):
        """
        初始化传感器节点
        
        Args:
            node_config: 节点配置字典
            use_mock: 是否使用模拟通信器
        """
        self.node_id = node_config.get('node_id')
        self.location = node_config.get('location')
        self.enabled = node_config.get('enabled', True)
        self.data_interval = node_config.get('data_interval', 60)
        
        # 创建数据生成器
        self.data_generator = SensorDataGenerator(self.node_id)
        
        # 创建通信器
        if use_mock:
            self.communicator = MockCommunicator()
        else:
            self.communicator = SocketCommunicator()
        
        # 控制标志
        self.running = False
        self.thread = None
        
        # 配置日志
        self.logger = logging.getLogger(f'SensorNode-{self.node_id}')
    
    def start(self):
        """启动传感器节点"""
        if not self.enabled:
            self.logger.info(f"节点 {self.node_id} 未启用，跳过启动")
            return
        
        if self.running:
            self.logger.warning(f"节点 {self.node_id} 已在运行中")
            return
        
        self.running = True
        self.thread = threading.Thread(target=self._run, daemon=True)
        self.thread.start()
        self.logger.info(f"节点 {self.node_id} ({self.location}) 已启动")
    
    def stop(self):
        """停止传感器节点"""
        if not self.running:
            return
        
        self.running = False
        if self.thread:
            self.thread.join(timeout=5)
        
        self.communicator.disconnect()
        self.logger.info(f"节点 {self.node_id} 已停止")
    
    def _run(self):
        """节点运行主循环"""
        # 连接到服务器
        if not self.communicator.connect():
            self.logger.error(f"节点 {self.node_id} 连接失败，停止运行")
            self.running = False
            return
        
        self.logger.info(f"节点 {self.node_id} 开始发送数据，间隔 {self.data_interval} 秒")
        
        while self.running:
            try:
                # 生成传感器数据
                sensor_data = self.data_generator.generate_sensor_data()
                
                # 发送数据
                success = self.communicator.send_data(sensor_data)
                
                if not success:
                    # 尝试重新连接
                    self.logger.warning(f"节点 {self.node_id} 发送失败，尝试重新连接...")
                    if not self.communicator.reconnect():
                        self.logger.error(f"节点 {self.node_id} 重连失败，停止运行")
                        break
                
                # 等待下一次发送
                time.sleep(self.data_interval)
                
            except Exception as e:
                self.logger.error(f"节点 {self.node_id} 运行异常: {e}")
                time.sleep(5)  # 异常后等待5秒再重试


class SensorSimulator:
    """传感器模拟器主程序"""
    
    def __init__(self, use_mock=False):
        """
        初始化模拟器
        
        Args:
            use_mock: 是否使用模拟通信器（测试模式）
        """
        self.use_mock = use_mock
        self.nodes = []
        
        # 配置日志
        self._setup_logging()
        self.logger = logging.getLogger('SensorSimulator')
        
        # 创建日志目录
        log_dir = Path(__file__).parent / 'logs'
        log_dir.mkdir(exist_ok=True)
        
        # 注册信号处理
        signal.signal(signal.SIGINT, self._signal_handler)
        signal.signal(signal.SIGTERM, self._signal_handler)
    
    def _setup_logging(self):
        """配置日志系统"""
        logging_config = config.get_logging_config()
        log_level = getattr(logging, logging_config.get('level', 'INFO'))
        
        # 配置根日志记录器
        logging.basicConfig(
            level=log_level,
            format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
            handlers=[
                logging.StreamHandler()  # 控制台输出
            ]
        )
        
        # 如果配置了日志文件，添加文件处理器
        if logging_config.get('file'):
            log_file = Path(__file__).parent / logging_config['file']
            log_file.parent.mkdir(exist_ok=True)
            file_handler = logging.FileHandler(log_file, encoding='utf-8')
            file_handler.setFormatter(
                logging.Formatter('%(asctime)s - %(name)s - %(levelname)s - %(message)s')
            )
            logging.getLogger().addHandler(file_handler)
    
    def _signal_handler(self, signum, frame):
        """信号处理函数"""
        self.logger.info(f"收到信号 {signum}，正在停止模拟器...")
        self.stop()
        sys.exit(0)
    
    def initialize(self):
        """初始化传感器节点"""
        nodes_config = config.get_nodes_config()
        
        self.logger.info(f"正在初始化 {len(nodes_config)} 个传感器节点...")
        
        for node_config in nodes_config:
            node = SensorNode(node_config, self.use_mock)
            self.nodes.append(node)
            self.logger.info(f"节点 {node.node_id} ({node.location}) 初始化完成")
    
    def start(self):
        """启动所有传感器节点"""
        self.logger.info("=== 传感器模拟器启动 ===")
        
        if self.use_mock:
            self.logger.info("运行模式: 测试模式（模拟通信）")
        else:
            self.logger.info("运行模式: 生产模式（真实Socket通信）")
        
        # 启动所有节点
        for node in self.nodes:
            node.start()
        
        self.logger.info(f"{len(self.nodes)} 个传感器节点已启动，按 Ctrl+C 停止")
        
        # 保持主线程运行
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            self.logger.info("收到停止信号...")
            self.stop()
    
    def stop(self):
        """停止所有传感器节点"""
        self.logger.info("正在停止所有传感器节点...")
        
        for node in self.nodes:
            node.stop()
        
        self.logger.info("=== 传感器模拟器已停止 ===")


def main():
    """主函数"""
    import argparse
    
    # 解析命令行参数
    parser = argparse.ArgumentParser(description='牛棚环境监测系统传感器模拟器')
    parser.add_argument('--mock', action='store_true', help='使用模拟通信器（测试模式）')
    parser.add_argument('--config', type=str, default='config.yaml', help='配置文件路径')
    args = parser.parse_args()
    
    # 创建并启动模拟器
    simulator = SensorSimulator(use_mock=args.mock)
    simulator.initialize()
    simulator.start()


if __name__ == '__main__':
    main()
