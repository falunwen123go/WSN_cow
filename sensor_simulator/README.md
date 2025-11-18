# 传感器模拟器

## 简介

牛棚环境监测系统的传感器模拟器，用于模拟ZigBee传感器节点采集环境数据并通过网络发送到后端服务器。

## 功能特点

- **多节点模拟**: 支持同时模拟多个传感器节点
- **真实数据生成**: 基于实际传感器特性生成模拟数据
  - 温度传感器 (DHT11): 0-50℃, ±2℃精度
  - 湿度传感器: 20-95%RH, ±5%精度
  - 氨气传感器: 0-100ppm, ±1ppm精度
  - 硫化氢传感器: 0-50ppm, ±0.5ppm精度
- **数据模式**: 支持正常模式、异常模式和混合模式
- **ZigBee协议模拟**: 模拟ZigBee网络通信协议
- **Socket通信**: 通过Socket连接向后端发送数据
- **灵活配置**: YAML配置文件管理所有参数

## 项目结构

```
sensor_simulator/
├── __init__.py           # 包初始化文件
├── main.py              # 主程序入口
├── config.py            # 配置管理模块
├── config.yaml          # 配置文件
├── data_generator.py    # 数据生成模块
├── zigbee_protocol.py   # ZigBee协议模拟
├── communication.py     # 通信模块
├── requirements.txt     # 依赖包列表
├── README.md           # 说明文档
└── logs/               # 日志目录（自动创建）
```

## 安装依赖

```bash
pip install -r requirements.txt
```

## 配置说明

编辑 `config.yaml` 文件进行配置：

### 网络配置
```yaml
network:
  coordinator_ip: "localhost"  # 后端服务器地址
  coordinator_port: 8888       # 后端服务器端口
  protocol: "socket"           # 通信协议
```

### 节点配置
```yaml
nodes:
  - node_id: "NODE_001"        # 节点ID
    location: "牛棚1号区域"    # 节点位置
    enabled: true              # 是否启用
    data_interval: 60          # 数据发送间隔（秒）
```

### 传感器配置
```yaml
sensors:
  temperature:
    range: [0, 50]            # 量程范围
    normal_range: [15, 28]    # 正常范围
    accuracy: 2.0             # 精度
```

### 数据生成模式
```yaml
data_generation:
  mode: "normal"              # normal/abnormal/mixed
  abnormal_probability: 0.1   # 异常概率（混合模式）
  noise_level: 0.05           # 噪声水平
  trend_enabled: true         # 是否启用趋势变化
```

## 使用方法

### 测试模式运行（使用模拟通信器）
```bash
python main.py --mock
```

### 生产模式运行（连接真实后端）
```bash
python main.py
```

### 指定配置文件
```bash
python main.py --config custom_config.yaml
```

### 停止模拟器
按 `Ctrl+C` 或发送 `SIGTERM` 信号

## 模块说明

### 1. config.py - 配置管理
- 读取和解析 YAML 配置文件
- 提供配置访问接口
- 支持默认配置

### 2. data_generator.py - 数据生成器
- 根据传感器特性生成模拟数据
- 支持正常和异常数据模式
- 添加噪声和趋势变化
- 模拟昼夜温湿度变化

### 3. zigbee_protocol.py - ZigBee协议
- 封装ZigBee数据包格式
- 实现数据编码和解码
- 计算校验和
- 生成JSON消息

### 4. communication.py - 通信模块
- Socket客户端实现
- 自动重连机制
- 模拟通信器（用于测试）
- 日志记录

### 5. main.py - 主程序
- 多线程管理传感器节点
- 信号处理和优雅退出
- 日志系统配置
- 命令行参数解析

## 数据格式

### 传感器数据格式
```json
{
  "node_id": "NODE_001",
  "timestamp": "2024-01-15 10:30:00",
  "temperature": 22.5,
  "humidity": 65.3,
  "nh3": 12.5,
  "h2s": 3.2,
  "battery_level": 85,
  "signal_strength": 75
}
```

### ZigBee数据包格式
```json
{
  "header": "7E",
  "pan_id": "0x1234",
  "source_address": "0x0001",
  "destination_address": "0x0000",
  "channel": 11,
  "sequence_number": 1,
  "payload": {
    "node_id": "NODE_001",
    "timestamp": "2024-01-15 10:30:00",
    "data": {
      "temperature": 22.5,
      "humidity": 65.3,
      "nh3": 12.5,
      "h2s": 3.2
    },
    "status": {
      "battery_level": 85,
      "signal_strength": 75
    }
  },
  "checksum": "0xA5"
}
```

## 测试

### 测试配置读取
```bash
python config.py
```

### 测试数据生成
```bash
python data_generator.py
```

### 测试ZigBee协议
```bash
python zigbee_protocol.py
```

### 测试通信模块
```bash
python communication.py
```

## 日志

日志文件保存在 `logs/simulator.log`，包含：
- 节点启动/停止信息
- 数据发送记录
- 连接状态变化
- 错误和警告信息

## 注意事项

1. **后端服务器**: 确保后端服务器的Socket服务已启动并监听指定端口
2. **防火墙**: 如果连接失败，检查防火墙设置
3. **数据间隔**: 建议不要设置过小的数据发送间隔，避免网络拥塞
4. **测试模式**: 开发阶段建议使用 `--mock` 参数进行测试

## 未来扩展

- [ ] 支持串口通信（pyserial）
- [ ] 支持更多传感器类型
- [ ] 数据可视化界面
- [ ] 批量数据导入/导出
- [ ] 故障场景模拟
