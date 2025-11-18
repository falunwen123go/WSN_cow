"""
传感器模拟器快速演示脚本
在测试模式下运行10秒
"""

import time
import sys
from main import SensorSimulator

def quick_demo():
    """快速演示"""
    print("=" * 60)
    print("传感器模拟器 - 快速演示（10秒）")
    print("=" * 60)
    
    # 创建模拟器（测试模式）
    simulator = SensorSimulator(use_mock=True)
    
    # 初始化节点
    simulator.initialize()
    
    # 启动节点（在后台线程运行）
    print("\n启动所有传感器节点...")
    for node in simulator.nodes:
        node.start()
    
    # 运行10秒
    print("模拟器运行中... (10秒演示)")
    time.sleep(10)
    
    # 停止
    print("\n停止所有传感器节点...")
    simulator.stop()
    
    print("\n" + "=" * 60)
    print("✓ 演示完成！")
    print("=" * 60)


if __name__ == '__main__':
    try:
        quick_demo()
    except KeyboardInterrupt:
        print("\n用户中断演示")
        sys.exit(0)
