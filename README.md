# 基于无线传感器网络的智能牛棚环境监测系统

## 项目简介

本项目是一个基于ZigBee无线传感器网络的智能牛棚环境监测系统，通过模拟传感器数据采集、无线传输、数据处理和智能控制，实现对牛棚环境的实时监测和自动化管理。

### 核心功能

- 📡 **无线传感器网络**：模拟CC2530 + DHT11 + NH₃ + H₂S传感器数据采集
- 🔄 **ZigBee通信**：模拟终端节点 → 协调器 → PC串口的数据传输
- 💾 **数据持久化**：MySQL数据库实时存储环境数据
- 🚨 **智能告警**：氨气/硫化氢超标自动告警
- 🎛️ **联动控制**：自动触发风机/卷帘/供热系统
- 📊 **实时监控**：Vue 3 + ECharts可视化展示

## 技术架构

### 后端技术栈
- **Java 21** - 开发语言
- **SSM框架** - Spring + Spring MVC + MyBatis
- **MySQL 8.0+** - 数据库
- **HikariCP** - 数据库连接池
- **WebSocket** - 实时数据推送
- **Maven 3.6+** - 项目管理

### 前端技术栈
- **Vue 3** - 渐进式JavaScript框架
- **Element Plus** - UI组件库
- **ECharts 5** - 数据可视化
- **Axios** - HTTP请求
- **Pinia** - 状态管理

### 模拟器技术
- **Python 3** - 传感器数据模拟
- **PySerial** - 串口通信模拟
- **NumPy** - 数值计算

## 项目结构

```
WSN_cow/
├── backend/                    # 后端项目
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wsn/cow/
│   │   │   │   ├── controller/    # 控制器层
│   │   │   │   ├── service/       # 业务逻辑层
│   │   │   │   ├── mapper/        # 数据访问层
│   │   │   │   ├── entity/        # 实体类
│   │   │   │   ├── dto/           # 数据传输对象
│   │   │   │   ├── vo/            # 视图对象
│   │   │   │   ├── common/        # 公共类
│   │   │   │   ├── config/        # 配置类
│   │   │   │   └── util/          # 工具类
│   │   │   └── resources/
│   │   │       ├── mapper/        # MyBatis XML
│   │   │       ├── application.yml
│   │   │       └── logback.xml
│   │   └── test/                  # 测试代码
│   └── pom.xml                    # Maven配置
├── frontend/                   # 前端项目（待创建）
├── sensor_simulator/          # Python传感器模拟器（待创建）
├── database/                  # 数据库脚本
│   ├── init.sql              # 初始化脚本
│   └── verify.sql            # 验证脚本
├── 需求分析文档.md
├── 两周开发任务计划.md
└── README.md
```

## 环境要求

- **JDK**: 21+
- **Maven**: 3.6+
- **MySQL**: 8.0+
- **Node.js**: 最新LTS版本
- **Python**: 3.x

## 快速开始

### 1. 数据库初始化

```bash
# 执行数据库初始化脚本
cd database
mysql -u root -p < init.sql
```

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 编译项目
mvn clean compile

# 打包项目
mvn clean package

# 运行（待添加Spring Boot支持）
```

### 3. 前端启动（待开发）

```bash
cd frontend
npm install
npm run dev
```

### 4. 模拟器启动（待开发）

```bash
cd sensor_simulator
pip install -r requirements.txt
python main.py
```

## 数据库表结构

| 表名 | 说明 |
|-----|------|
| sensor_data | 传感器数据表 |
| node_info | 节点信息表 |
| alarm_record | 告警记录表 |
| device_info | 设备信息表 |
| device_control_log | 设备控制日志表 |
| system_config | 系统配置表 |
| user | 用户表 |

## API接口文档

### 传感器数据接口
- `GET /api/sensor/latest` - 获取所有节点最新数据
- `GET /api/sensor/history` - 获取历史数据
- `GET /api/sensor/data/{nodeId}` - 获取指定节点数据

### 节点管理接口
- `GET /api/node/list` - 获取节点列表
- `POST /api/node` - 添加节点
- `PUT /api/node/{nodeId}` - 更新节点
- `DELETE /api/node/{nodeId}` - 删除节点

### 告警管理接口
- `GET /api/alarm/list` - 获取告警列表
- `GET /api/alarm/unhandled` - 获取未处理告警
- `PUT /api/alarm/handle/{alarmId}` - 处理告警

### 设备控制接口
- `GET /api/device/list` - 获取设备列表
- `POST /api/device/control` - 控制设备
- `GET /api/device/log` - 获取控制日志

## 开发进度

### ✅ 已完成
- [x] 环境搭建（JDK 21、MySQL 8.0、Maven、Node.js、Python）
- [x] 数据库设计与初始化（7张核心表）
- [x] 后端Maven项目框架搭建
- [x] Spring + MyBatis配置
- [x] 公共类（Result、PageResult、Constants）
- [x] 日志配置（Logback）
- [x] Git仓库初始化

### 🚧 进行中
- [ ] Python传感器模拟器开发
- [ ] 后端数据接收与处理模块
- [ ] 告警模块开发
- [ ] 设备控制模块开发

### 📅 待开发
- [ ] 前端Vue 3项目初始化
- [ ] 实时监控页面
- [ ] 历史数据查询与可视化
- [ ] 告警管理页面
- [ ] 设备控制页面
- [ ] 系统配置页面

## 配置说明

### 数据库配置
编辑 `backend/src/main/resources/application.yml`：

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/wsn_cow_monitor
  username: root
  password: your_password
```

### 串口/Socket配置

```yaml
serial:
  type: socket  # socket 或 serial
  socket:
    port: 8888
```

## 告警阈值配置

| 参数 | 警告阈值 | 严重阈值 | 单位 |
|-----|---------|---------|------|
| 氨气浓度 | 25 | 40 | ppm |
| 硫化氢浓度 | 10 | 20 | ppm |
| 温度（低） | 5 | 0 | °C |
| 温度（高） | 35 | 40 | °C |
| 湿度（低） | 30 | - | % |
| 湿度（高） | 85 | - | % |

## 开发团队

- **开发周期**: 2周（2025-11-18 ~ 2025-11-29）
- **项目类型**: 模拟仿真系统
- **文档版本**: v1.0

## 许可证

本项目仅供学习和研究使用。

## 联系方式

如有问题，请查阅项目文档或提交Issue。

---

**最后更新**: 2025-11-17
