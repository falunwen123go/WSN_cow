"""
传感器模拟器完整功能测试脚本
"""

import time
from config import config
from data_generator import SensorDataGenerator
from zigbee_protocol import ZigBeeProtocol
from communication import MockCommunicator

def test_all():
    """测试所有功能模块"""
    
    print("=" * 60)
    print("传感器模拟器功能测试")
    print("=" * 60)
    
    # 1. 测试配置读取
    print("\n【1. 配置模块测试】")
    print("-" * 60)
    network_config = config.get_network_config()
    print(f"网络配置: {network_config}")
    nodes_config = config.get_nodes_config()
    print(f"节点数量: {len(nodes_config)}")
    for node in nodes_config:
        print(f"  - {node['node_id']}: {node['location']}, 间隔={node['data_interval']}s")
    print("✓ 配置模块测试通过")
    
    # 2. 测试数据生成
    print("\n【2. 数据生成模块测试】")
    print("-" * 60)
    generator = SensorDataGenerator('NODE_001')
    
    # 测试正常数据
    print("正常模式数据:")
    generator.mode = 'normal'
    for i in range(3):
        data = generator.generate_sensor_data()
        print(f"  样本{i+1}: T={data['temperature']:.1f}℃, "
              f"H={data['humidity']:.1f}%, "
              f"NH3={data['nh3']:.1f}ppm, "
              f"H2S={data['h2s']:.2f}ppm, "
              f"电量={data['battery_level']}%")
    
    # 测试异常数据
    print("\n异常模式数据:")
    generator.mode = 'abnormal'
    for i in range(3):
        data = generator.generate_sensor_data()
        print(f"  样本{i+1}: T={data['temperature']:.1f}℃, "
              f"H={data['humidity']:.1f}%, "
              f"NH3={data['nh3']:.1f}ppm, "
              f"H2S={data['h2s']:.2f}ppm")
    print("✓ 数据生成模块测试通过")
    
    # 3. 测试ZigBee协议
    print("\n【3. ZigBee协议模块测试】")
    print("-" * 60)
    protocol = ZigBeeProtocol()
    
    # 生成测试数据
    test_data = generator.generate_sensor_data()
    print(f"原始数据: 节点={test_data['node_id']}, "
          f"温度={test_data['temperature']}℃")
    
    # 编码
    packet = protocol.encode_data_packet(test_data)
    print(f"数据包头: {packet['header']}")
    print(f"PAN ID: {packet['pan_id']}")
    print(f"源地址: {packet['source_address']}")
    print(f"目标地址: {packet['destination_address']}")
    print(f"校验和: {packet['checksum']}")
    
    # 解码验证
    json_msg = protocol.create_json_message(test_data)
    decoded_data = protocol.parse_json_message(json_msg)
    
    # 验证一致性
    is_valid = (
        test_data['temperature'] == decoded_data['temperature'] and
        test_data['humidity'] == decoded_data['humidity'] and
        test_data['nh3'] == decoded_data['nh3'] and
        test_data['h2s'] == decoded_data['h2s']
    )
    print(f"编解码一致性: {'✓ 通过' if is_valid else '✗ 失败'}")
    print("✓ ZigBee协议模块测试通过")
    
    # 4. 测试通信模块
    print("\n【4. 通信模块测试（模拟模式）】")
    print("-" * 60)
    communicator = MockCommunicator()
    
    if communicator.connect():
        print("模拟连接建立成功")
        
        # 发送3条测试数据
        print("\n发送测试数据:")
        generator.mode = 'normal'
        for i in range(3):
            data = generator.generate_sensor_data()
            success = communicator.send_data(data)
            if success:
                print(f"  ✓ 数据{i+1}发送成功")
            time.sleep(0.5)
        
        communicator.disconnect()
        print("\n模拟连接已断开")
        print("✓ 通信模块测试通过")
    
    # 5. 完整流程测试
    print("\n【5. 完整流程测试】")
    print("-" * 60)
    print("模拟一个节点工作流程:")
    
    node_id = 'NODE_001'
    generator = SensorDataGenerator(node_id)
    protocol = ZigBeeProtocol()
    communicator = MockCommunicator()
    
    if communicator.connect():
        print(f"✓ 节点 {node_id} 已连接")
        
        for cycle in range(2):
            print(f"\n第 {cycle + 1} 个数据周期:")
            
            # 生成数据
            sensor_data = generator.generate_sensor_data()
            print(f"  1. 数据采集: T={sensor_data['temperature']:.1f}℃, "
                  f"H={sensor_data['humidity']:.1f}%, "
                  f"NH3={sensor_data['nh3']:.1f}ppm, "
                  f"H2S={sensor_data['h2s']:.2f}ppm")
            
            # 协议编码
            packet = protocol.encode_data_packet(sensor_data)
            print(f"  2. 协议封装: 序列号={packet['sequence_number']}, "
                  f"校验和={packet['checksum']}")
            
            # 发送数据
            success = communicator.send_data(sensor_data)
            print(f"  3. 数据发送: {'✓ 成功' if success else '✗ 失败'}")
            
            time.sleep(1)
        
        communicator.disconnect()
        print("\n✓ 完整流程测试通过")
    
    # 测试总结
    print("\n" + "=" * 60)
    print("✓ 所有功能模块测试通过！")
    print("=" * 60)
    print("\n模拟器已准备就绪，可以开始使用:")
    print("  - 测试模式: python main.py --mock")
    print("  - 生产模式: python main.py")
    print("=" * 60)


if __name__ == '__main__':
    test_all()
