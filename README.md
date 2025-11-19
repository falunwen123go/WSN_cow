# 基于无线传感器网络的智能牛棚环境监测系统

## 项目简介

本项目是一个基于Vue 3 + Spring Boot的智能牛棚环境监测系统，实现实时环境监测、智能告警和设备联动控制。

### ✨ 核心功能

- 📊 **实时监测** - 温度、湿度、NH₃、H₂S四大环境指标实时显示
- 📈 **数据可视化** - ECharts图表展示历史趋势，支持多维度数据分析
- 🚨 **智能告警** - 多级别告警系统，超标自动触发，支持告警处理
- 🎛️ **设备控制** - 风机、卷帘、供热、喷淋设备远程控制和自动模式
- 🗺️ **节点管理** - 传感器节点状态监控、电池电量、信号强度管理
- 📝 **历史查询** - 多条件查询历史数据，支持时间范围筛选和导出
- ⚙️ **系统配置** - 灵活的阈值配置和系统参数管理

## 🏗️ 技术架构

### 后端技术栈
- **Java 21** - 开发语言
- **Spring Boot 2.7.18** - 应用框架  
- **MyBatis 3.5.14** - ORM框架
- **MySQL 8.0+** - 关系型数据库
- **HikariCP** - 高性能数据库连接池
- **Maven 3.9+** - 项目构建工具

### 前端技术栈
- **Vue 3.3+** - 渐进式JavaScript框架
- **TypeScript 5** - 类型安全的JavaScript超集
- **Element Plus 2.4+** - Vue 3 UI组件库
- **ECharts 5.4+** - 强大的数据可视化库
- **Axios** - HTTP客户端
- **Pinia** - Vue 3状态管理
- **Vue Router 4** - 官方路由管理器
- **Vite 5** - 下一代前端构建工具
- **Day.js** - 轻量级日期处理库

### 数据库设计
- **7张核心数据表** - 传感器数据、节点信息、告警记录、设备信息、控制日志、系统配置、用户表
- **完整索引设计** - 优化查询性能
- **示例数据** - 30条传感器记录、12条告警记录、5条设备日志

## 📁 项目结构

```
WSN_cow/
├── backend/                           # 后端项目（Spring Boot）
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/wsn/cow/
│   │   │   │   ├── controller/       # REST API控制器（5个）
│   │   │   │   ├── service/          # 业务逻辑层（5个）
│   │   │   │   ├── mapper/           # MyBatis数据访问层（5个）
│   │   │   │   ├── entity/           # 实体类（7个）
│   │   │   │   ├── common/           # 统一返回、分页、异常处理
│   │   │   │   └── WsnCowApplication.java  # 启动类
│   │   │   └── resources/
│   │   │       ├── mapper/           # MyBatis XML映射文件
│   │   │       └── application.yml   # 配置文件
│   │   └── test/                     # 单元测试
│   └── pom.xml                       # Maven依赖配置
│
├── frontend/                          # 前端项目（Vue 3）
│   ├── src/
│   │   ├── views/                    # 页面组件（6个主要页面）
│   │   │   ├── DashboardView.vue    # 数据监控大屏 ✅
│   │   │   ├── NodesView.vue        # 节点管理 ✅  
│   │   │   ├── AlarmsView.vue       # 报警信息 ✅
│   │   │   ├── DevicesView.vue      # 设备控制 ✅
│   │   │   ├── HistoryView.vue      # 历史记录 ✅
│   │   │   └── SettingsView.vue     # 系统配置 ✅
│   │   ├── components/               # 公共组件（8个）
│   │   │   ├── Layout.vue           # 主布局
│   │   │   ├── DataCard.vue         # 数据卡片
│   │   │   ├── AlarmBadge.vue       # 告警徽章
│   │   │   ├── DeviceSwitch.vue     # 设备开关
│   │   │   ├── NodeStatus.vue       # 节点状态
│   │   │   └── ...
│   │   ├── api/                      # API接口封装（5个模块）
│   │   ├── types/                    # TypeScript类型定义
│   │   ├── utils/                    # 工具函数
│   │   ├── router/                   # 路由配置
│   │   └── App.vue                   # 根组件
│   ├── public/                       # 静态资源
│   ├── package.json                  # 依赖配置
│   └── vite.config.ts               # Vite配置
│
├── database/                          # 数据库脚本
│   ├── init.sql                      # 初始化脚本（建表+示例数据）
│   ├── 数据库真实结构文档.md          # 数据库设计文档
│   └── ...
│
├── docs/                              # 项目文档
│   ├── 后端接口完整文档.md            # 38个API接口文档
│   ├── 前端API修复报告.md             # API对接修复记录
│   ├── 前后端字段映射文档.md          # 数据结构映射关系
│   └── ...
│
├── README.md                          # 项目说明（本文件）
└── 运行指南.md                         # 快速启动指南
```

## 🎯 功能特性

### 1. 数据监控大屏
- ✅ 实时显示温度、湿度、NH₃、H₂S当前值
- ✅ 节点统计：总数、在线、离线状态
- ✅ 设备统计：总数、运行中、已停止、离线
- ✅ 温湿度趋势图（最近1小时，双Y轴）
- ✅ 气体浓度趋势图（最近1小时，实时更新）
- ✅ 最新报警消息列表（支持快速处理）

### 2. 节点管理
- ✅ 节点列表展示（3个传感器节点）
- ✅ 状态监控：在线/离线、电池电量、信号强度
- ✅ 节点信息：节点ID、名称、安装位置、通信时间
- ✅ 操作功能：详情查看、编辑、删除（美化按钮组）
- ✅ 筛选功能：按状态、关键词搜索

### 3. 报警管理  
- ✅ 报警列表（分页显示）
- ✅ 多维度筛选：报警级别、类型、处理状态、时间范围
- ✅ 报警统计：总数、未处理、严重、警告、一般
- ✅ 详细信息：节点ID、报警内容、当前值、阈值
- ✅ 批量操作：处理、删除报警记录

### 4. 设备控制
- ✅ 设备卡片展示：风机、卷帘、供热、喷淋（3台设备）
- ✅ 实时状态：运行/停止、自动/手动模式
- ✅ 远程控制：启动、停止设备
- ✅ 模式切换：自动模式/手动模式
- ✅ 控制日志：操作人、操作时间、操作原因（分页查询）
- ✅ 设备统计：总设备数、运行中、自动模式、离线

### 5. 历史记录
- ✅ 历史数据查询（分页）
- ✅ 多条件筛选：节点、时间范围
- ✅ 数据展示：温度、湿度、NH₃、H₂S、采集时间
- ✅ 图表可视化：选中数据生成趋势图
- ✅ 数据导出：支持CSV格式导出

### 6. 系统配置
- ✅ 阈值配置：温度、湿度、气体浓度告警阈值
- ✅ 系统参数：数据刷新频率、告警推送设置
- ✅ 参数管理：查看、编辑、保存配置
- ✅ 配置缓存：支持刷新配置缓存

## 📊 数据库设计

| 表名 | 说明 | 记录数 | 主要字段 |
|-----|------|-------|---------|
| sensor_data | 传感器数据表 | 30条示例 | 温度、湿度、NH₃、H₂S、采集时间 |
| node_info | 节点信息表 | 3个节点 | 节点ID、名称、位置、状态、电量 |
| alarm_info | 报警记录表 | 12条示例 | 报警类型、级别、状态、时间 |
| device_info | 设备信息表 | 3台设备 | 设备ID、类型、状态、控制模式 |
| device_control_log | 设备控制日志表 | 5条记录 | 操作动作、操作人、时间、原因 |
| system_config | 系统配置表 | 10项配置 | 配置键、值、描述 |
| user | 用户表 | 1个管理员 | 用户名、密码、角色 |

## 🚀 快速开始

### 环境要求
- JDK 21+
- Maven 3.9+  
- MySQL 8.0+
- Node.js 18+ (LTS)
- npm 9+ 或 pnpm

### 1. 数据库初始化

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库并导入数据
source d:/OneDrive/桌面/cursor/WSN_cow/database/init.sql
```

### 2. 后端启动

```bash
# 进入后端目录
cd backend

# 修改数据库配置（如需要）
# 编辑 src/main/resources/application.yml

# 启动Spring Boot应用
mvn spring-boot:run

# 或者打包后运行
mvn clean package -DskipTests
java -jar target/cow-monitor-backend-1.0.0.jar
```

**后端服务**: http://localhost:9090  
**API文档**: 参见 `后端接口完整文档.md`

### 3. 前端启动

```bash
# 进入前端目录  
cd frontend

# 安装依赖（首次运行）
npm install

# 启动开发服务器
npm run dev
```

**前端访问**: http://localhost:5173

### 4. 验证运行

**测试后端API**:
```powershell
# 获取节点列表
Invoke-RestMethod http://localhost:9090/api/node/list

# 获取最新传感器数据
Invoke-RestMethod http://localhost:9090/api/sensor/latest

# 获取告警列表
Invoke-RestMethod http://localhost:9090/api/alarm/list?pageNum=1&pageSize=10
```

**访问前端页面**:
1. 打开浏览器访问 http://localhost:5173
2. 查看数据监控大屏
3. 导航测试6个功能页面

## 📡 API接口

### 统一返回格式
```json
{
  "code": 200,
  "message": "操作成功",
  "data": { },
  "timestamp": 1700293847123
}
```

### 接口清单（38个）

#### 传感器数据 (5个)
- `GET /api/sensor/latest` - 获取所有节点最新数据
- `GET /api/sensor/data/{nodeId}` - 获取指定节点数据
- `GET /api/sensor/history` - 分页查询历史数据
- `GET /api/sensor/statistics` - 查询统计信息
- `POST /api/sensor` - 创建传感器数据

#### 节点管理 (7个)
- `GET /api/node/list` - 获取节点列表（支持分页）
- `GET /api/node/{nodeId}` - 获取节点详情
- `GET /api/node/online/count` - 获取在线节点数
- `GET /api/node/topology` - 获取网络拓扑
- `POST /api/node` - 添加节点
- `PUT /api/node/{nodeId}` - 更新节点
- `DELETE /api/node/{nodeId}` - 删除节点

#### 告警管理 (7个)
- `GET /api/alarm/list` - 分页查询告警列表
- `GET /api/alarm/{id}` - 获取告警详情
- `GET /api/alarm/unhandled/count` - 获取未处理告警数
- `POST /api/alarm` - 创建告警
- `PUT /api/alarm/handle/{id}` - 处理告警
- `DELETE /api/alarm/{id}` - 删除告警
- `GET /api/alarm/statistics` - 告警统计

#### 设备控制 (14个)
- `GET /api/device/list` - 获取设备列表
- `GET /api/device/{deviceId}` - 获取设备详情
- `POST /api/device/control` - 手动控制设备
- `PUT /api/device/{deviceId}/mode` - 切换控制模式
- `GET /api/device/log` - 查询控制日志（分页）
- `GET /api/device/running/count` - 获取运行中设备数
- 等...

#### 系统配置 (5个)
- `GET /api/config/list` - 获取所有配置
- `GET /api/config/{key}` - 获取指定配置
- `POST /api/config` - 创建配置
- `PUT /api/config/{key}` - 更新配置  
- `POST /api/config/refresh` - 刷新配置缓存

**详细文档**: 参见 `后端接口完整文档.md`（971行）

## 🛠️ 配置说明

### 后端配置 (application.yml)

```yaml
server:
  port: 9090

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/wsn_cow_monitor?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
    driver-class-name: com.mysql.cj.jdbc.Driver
    
mybatis:
  mapper-locations: classpath:mapper/*.xml
  type-aliases-package: com.wsn.cow.entity
  configuration:
    map-underscore-to-camel-case: true
```

### 前端配置 (vite.config.ts)

```typescript
export default defineConfig({
  server: {
    port: 5173,
    proxy: {
      '/api': {
        target: 'http://localhost:9090',
        changeOrigin: true
      }
    }
  }
})
```

## 🎨 界面展示

### 数据监控大屏
- 实时数据卡片：温度23.4°C、湿度65.3%、NH₃ 15.5ppm、H₂S 5.1ppm
- 节点统计卡片：3个节点（0在线、3离线）
- 设备统计卡片：3台设备（0运行、3停止）
- 温湿度趋势图：双Y轴折线图，实时更新
- 气体浓度趋势图：NH₃和H₂S双曲线
- 最新报警列表：级别徽章、处理状态、快速操作

### 节点管理
- 节点列表表格：节点ID、名称、位置、状态标签
- 电池电量进度条：颜色渐变（绿→黄→红）
- 信号强度标签：dBm值+颜色状态
- 操作按钮组：详情、编辑、删除（紧凑布局）

### 设备控制
- 设备卡片网格：3列布局，响应式设计
- 设备类型标签：风机、卷帘、供热、喷淋
- 状态指示：运行中（绿色）/已停止（灰色）
- 控制开关：自定义DeviceSwitch组件
- 控制日志表格：完整操作审计记录

## 📈 项目统计

- **总代码行数**: ~12,000行
- **后端**: 
  - Java文件: 25个
  - 接口数量: 38个
  - 单元测试: 10个
- **前端**:
  - Vue组件: 23个
  - 页面: 6个
  - 公共组件: 8个
  - API模块: 5个
- **数据库**:
  - 数据表: 7张
  - 索引: 15个
  - 示例数据: 61条

## ⚠️ 已知问题与解决方案

### 1. PageResult字段不匹配
**问题**: 后端返回`list`字段，前端期望`records`  
**解决**: ✅ 已修复，统一使用`list`字段

### 2. ECharts图表重叠
**问题**: 文字和图形重叠，时间显示相同  
**解决**: ✅ 已优化grid布局，使用历史数据API获取趋势

### 3. ElTag type属性警告  
**问题**: 空字符串导致验证失败
**解决**: ✅ 已修复，所有ElTag使用有效type值

### 4. 分页参数不一致
**问题**: 前端使用`page/size`，后端期望`pageNum/pageSize`  
**解决**: ✅ 已统一为`pageNum/pageSize`

## 🔧 常见问题

**Q: 后端启动失败，端口被占用？**  
A: 检查9090端口，使用 `netstat -ano | findstr :9090` 查找并杀死进程

**Q: 前端请求跨域错误？**  
A: 确保后端已启动，Vite已配置proxy代理

**Q: 数据库连接失败？**  
A: 检查MySQL服务状态，验证application.yml中的数据库配置

**Q: npm install安装失败？**  
A: 尝试使用国内镜像: `npm config set registry https://registry.npmmirror.com`

## 📚 相关文档

- [运行指南.md](./运行指南.md) - 详细的启动步骤
- [后端接口完整文档.md](./后端接口完整文档.md) - 38个API详细说明
- [前后端字段映射文档.md](./前后端字段映射文档.md) - 数据结构对照
- [前端API修复报告.md](./前端API修复报告.md) - API对接问题记录
- [数据库真实结构文档.md](./database/数据库真实结构文档.md) - 数据库设计

## 📝 开发进度

- ✅ **Day 1-3**: 数据库设计、后端基础开发
- ✅ **Day 4**: Spring Boot迁移、38个API开发
- ✅ **Day 5**: 设备控制API、数据库Schema修复
- ✅ **Day 6**: Vue 3前端初始化、布局、API封装
- ✅ **Day 7-10**: 前端页面开发、数据可视化、功能完善
- ✅ **Day 11**: 系统集成测试、Bug修复、文档完善
- 🎯 **Day 12-14**: 性能优化、部署准备、项目交付

## 👥 开发团队

- **项目周期**: 2周（2025-11-18 ~ 2025-11-29）
- **项目类型**: 智能监测系统
- **当前版本**: v1.0.0
- **开发状态**: ✅ 核心功能已完成

## 📄 许可证

本项目仅供学习和研究使用。

---

**最后更新**: 2025-11-19  
**文档版本**: v2.0  
**系统状态**: ✅ 生产就绪
