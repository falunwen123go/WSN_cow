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
- **Spring Boot 2.7.18** - 应用框架
- **MyBatis** - ORM框架
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

# 如果遇到Schema不匹配,执行补丁脚本
mysql -u root -p < alter_tables.sql
```

### 2. 后端启动 ✅

```bash
# 进入后端目录
cd backend

# 编译项目
mvn clean compile

# 启动Spring Boot应用
mvn spring-boot:run

# 或打包后运行
mvn clean package -DskipTests
java -jar target/cow-monitor-backend-1.0.0.jar
```

**服务访问**:
- HTTP API: http://localhost:9090
- Socket服务: 自动启动在8888端口
- 启动时间: ~2秒

**API测试示例**:
```powershell
# 获取在线节点数
Invoke-RestMethod http://localhost:9090/api/node/online/count

# 获取最新传感器数据
Invoke-RestMethod http://localhost:9090/api/sensor/latest

# 获取告警列表
Invoke-RestMethod http://localhost:9090/api/alarm/list
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

**基础地址**: `http://localhost:9090/api`  
**统一返回格式**:
```json
{
  "code": 200,
  "message": "操作成功",
  "data": {...},
  "timestamp": 1731912345678
}
```

### 传感器数据接口 (✅ 已测试)
- `GET /api/sensor/latest` - 获取所有节点最新数据
- `GET /api/sensor/data/{nodeId}` - 获取指定节点最新数据
- `GET /api/sensor/history` - 分页查询历史数据
- `GET /api/sensor/statistics` - 查询数据统计信息

### 告警管理接口 (✅ 已测试)
- `GET /api/alarm/list` - 分页查询告警列表
- `GET /api/alarm/{id}` - 获取告警详情
- `GET /api/alarm/unhandled/count` - 获取未处理告警数量
- `POST /api/alarm` - 创建告警记录
- `PUT /api/alarm/handle/{id}` - 处理告警
- `DELETE /api/alarm/{id}` - 删除告警记录
- `GET /api/alarm/statistics` - 查询告警统计信息

### 节点管理接口 (✅ 已测试)
- `GET /api/node/list` - 获取节点列表
- `GET /api/node/{nodeId}` - 获取节点详情
- `GET /api/node/online/count` - 获取在线节点数
- `GET /api/node/topology` - 获取网络拓扑
- `POST /api/node` - 添加节点
- `PUT /api/node/{nodeId}` - 更新节点
- `DELETE /api/node/{nodeId}` - 删除节点

### 系统配置接口 (✅ 已测试)
- `GET /api/config/list` - 获取所有配置
- `GET /api/config/{key}` - 获取指定配置
- `POST /api/config` - 创建配置
- `PUT /api/config/{key}` - 更新配置
- `POST /api/config/refresh` - 刷新配置缓存

**更多详情**: 参见 `Day4_工作完成报告.md`

## 开发进度

### ✅ Day 1-2: 环境搭建与数据库设计
- [x] JDK 21、MySQL 8.0、Maven 3.9+环境配置
- [x] 数据库设计（7张核心表）
- [x] init.sql初始化脚本编写
- [x] Maven项目框架搭建
- [x] Git仓库初始化

### ✅ Day 3: 数据接收与持久化
- [x] Socket数据接收服务（端口8888）
- [x] 传感器数据解析与存储
- [x] 告警规则引擎
- [x] MyBatis Mapper实现
- [x] Service业务逻辑层
- [x] 功能测试与验证

### ✅ Day 4: Spring Boot迁移与API开发
- [x] **Spring Boot 2.7.18迁移**（从Spring Framework 5.3.31）
- [x] 23个RESTful API端点开发
- [x] 统一返回格式(Result<T>)
- [x] 全局异常处理
- [x] CORS跨域配置
- [x] **18个API完整测试（100%通过）**
- [x] 数据库Schema修复
- [x] 文档编写

**API统计**:
- 传感器数据: 5个 ✅
- 告警管理: 7个 ✅
- 节点管理: 7个 ✅
- 系统配置: 5个 ✅

### 🚧 Day 5-6: 前端开发（进行中）
- [ ] Vue 3 + Element Plus项目初始化
- [ ] 实时监控大屏
- [ ] 数据可视化(ECharts)
- [ ] 告警管理界面
- [ ] 节点管理界面
- [ ] 系统配置界面

### 📅 Day 7-8: Python传感器模拟器
- [ ] 传感器数据生成算法
- [ ] Socket客户端实现
- [ ] 多节点模拟
- [ ] 数据格式验证

### 📅 Day 9-10: 联调与优化
- [ ] 前后端联调
- [ ] 性能测试与优化
- [ ] 文档完善
- [ ] 系统交付

## 配置说明

### 数据库配置
编辑 `backend/src/main/resources/application.properties`：

```properties
# 数据库配置
spring.datasource.url=jdbc:mysql://localhost:3306/wsn_cow_monitor
spring.datasource.username=root
spring.datasource.password=your_password
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# HikariCP连接池
spring.datasource.hikari.maximum-pool-size=20
spring.datasource.hikari.minimum-idle=5

# MyBatis配置
mybatis.mapper-locations=classpath:mapper/*.xml
mybatis.type-aliases-package=com.wsn.cow.entity

# 服务器端口
server.port=9090
```

### Socket服务配置
Socket数据接收服务自动启动在8888端口，可在数据库`system_config`表中修改:
```sql
UPDATE system_config SET config_value='8888' WHERE config_key='serial.port';
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

**最后更新**: 2025-11-18  
**当前版本**: v1.0.0  
**开发状态**: Day 4完成，后端API开发完毕 ✅
