# 宠物日常护理与健康追踪助手

> 课题一：宠物日常护理与健康追踪助手 —— 基于 uni-app + Spring Boot 的全栈小程序应用

## 一、项目介绍

本项目是一个宠物生命周期管理、健康数据记录与自动提醒的小程序应用，帮助宠物主人便捷地记录和追踪宠物的日常护理数据（体重、体温、精神状态），管理护理日志（喂食/洗澡/疫苗/驱虫），并自动生成月度健康报告。

### 核心功能

| 模块 | 功能说明 |
|------|----------|
| 首页仪表盘 | 宠物切换、体重/体温变化曲线（ECharts）、今日待办提醒（增删改查） |
| 护理日志页 | 记录喂食/洗澡/疫苗/驱虫，时间线倒序展示，标签筛选 |
| 健康报告页 | 月度报告（体重变化趋势、疫苗接种完成率、异常记录统计、健康评分） |
| 新增宠物页 | 录入宠物信息（名称、种类、品种、性别、出生日期） |

### 项目亮点

- **ECharts 图表绘制**：体重/体温变化曲线，含均值标线、渐变区域填充、自定义 tooltip
- **Spring @Scheduled 定时任务**：每天 6:00 自动扫描过期提醒并标记为"已过期"
- **JPA 聚合统计**：基于 Spring Data JPA 查询近 30 天健康数据，计算体重平均值与波动率
- **全局状态管理**：自研轻量级 petStore 实现多页面宠物切换联动
- **智能健康评分**：多维度扣分算法（体重波动率/异常次数/疫苗完成率）

## 二、技术栈

### 前端

| 技术 | 版本 | 用途 |
|------|------|------|
| uni-app | - | 跨端框架 |
| Vue 3 | - | 前端框架 |
| ECharts | 5.4.3 | 图表绘制 |
| uni-app 内置组件 | - | UI 组件（picker、scroll-view 等） |

### 后端

| 技术 | 版本 | 用途 |
|------|------|------|
| Spring Boot | 2.7.18 | 后端框架 |
| Spring Data JPA | - | ORM 与数据访问 |
| MySQL | 8.x | 关系型数据库 |
| Lombok | - | 简化 Java POJO |
| Maven | - | 依赖管理 |

### 开发工具

| 工具 | 用途 |
|------|------|
| Trae IDE | AI 辅助编程主环境 |
| HBuilderX / VS Code | uni-app 前端开发 |
| Navicat | MySQL 数据库管理 |
| Postman | API 接口测试 |

## 三、项目结构

```
pet-care-frontend/
├── frontend/                          # 前端（uni-app + Vue3）
│   ├── pages/
│   │   ├── index/index.vue            # 首页仪表盘
│   │   ├── care-log/index.vue         # 护理日志页
│   │   ├── health-report/index.vue    # 健康报告页
│   │   └── pet-add/index.vue          # 新增宠物页
│   ├── static/                        # tabBar 图标资源
│   ├── utils/
│   │   ├── api.js                     # API 请求封装
│   │   └── petStore.js                # 全局宠物状态管理
│   ├── App.vue                        # 应用入口（含 ECharts 预加载）
│   ├── main.js                        # Vue3 入口
│   ├── manifest.json                  # 应用配置
│   ├── pages.json                     # 路由与 tabBar 配置
│   └── uni.scss                       # 全局样式变量
│
├── backend/                           # 后端（Spring Boot）
│   ├── src/main/java/com/petcare/
│   │   ├── PetCareApplication.java    # 启动类（@EnableScheduling）
│   │   ├── config/
│   │   │   ├── DataInitializer.java   # 数据初始化器
│   │   │   └── WebConfig.java         # CORS 配置
│   │   ├── controller/
│   │   │   ├── PetController.java     # 核心 API（健康记录/统计/待办）
│   │   │   └── PetInfoController.java # 宠物与待办 CRUD
│   │   ├── service/
│   │   │   ├── HealthRecordService.java
│   │   │   ├── StatisticsService.java # 月度统计（平均值/波动率）
│   │   │   └── ReminderService.java
│   │   ├── repository/
│   │   │   ├── HealthRecordRepository.java
│   │   │   ├── PetRepository.java
│   │   │   └── ReminderRepository.java
│   │   ├── entity/
│   │   │   ├── Pet.java
│   │   │   ├── HealthRecord.java
│   │   │   └── Reminder.java
│   │   ├── dto/
│   │   │   ├── ApiResult.java
│   │   │   ├── HealthRecordRequest.java
│   │   │   ├── HealthRecordResponse.java
│   │   │   ├── MonthlyStatisticsResponse.java
│   │   │   └── ReminderResponse.java
│   │   ├── enums/
│   │   │   ├── CareType.java          # 喂食/洗澡/疫苗/驱虫
│   │   │   └── MentalState.java       # 活跃/正常/安静/生病
│   │   └── scheduler/
│   │       └── ReminderScheduler.java # @Scheduled 定时任务
│   ├── src/main/resources/
│   │   └── application.yml            # 配置文件
│   └── pom.xml
│
├── README.md                          # 本文档
├── prompt_log.md                      # AI Prompt 日志
└── 个人实训总结报告.md                # 个人总结
```

## 四、安装与运行指南

### 环境要求

- JDK 11+
- MySQL 8.x
- Maven 3.6+（或使用项目自带 mvnw）
- Node.js 16+（前端编译）
- HBuilderX（推荐，用于运行 uni-app）

### 4.1 数据库准备

1. 登录 MySQL，创建数据库：

```sql
CREATE DATABASE pet_care DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 表结构由 JPA 的 `hibernate.ddl-auto=update` 自动创建，无需手动建表。
3. 初始测试数据由 `DataInitializer.java` 在首次启动时自动插入。

### 4.2 后端启动

```bash
cd backend
# 修改 src/main/resources/application.yml 中的数据库密码（默认 123456）
./mvnw spring-boot:run
# 或使用本地 Maven
mvn spring-boot:run
```

后端启动成功后访问：`http://localhost:8080/api/pet/list`

### 4.3 前端启动

1. 使用 HBuilderX 打开 `frontend` 目录
2. 点击「运行」→「运行到浏览器」→ 选择 Chrome
3. 或执行命令：

```bash
cd frontend
npm install
npm run dev:h5
```

4. 浏览器访问前端页面，确保后端已启动

### 4.4 配置说明

如需修改数据库连接，编辑 `backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/pet_care
    username: root
    password: 你的密码
```

前端 API 地址默认为 `http://localhost:8080/api/pet`，如需修改请编辑 `frontend/utils/api.js` 中的 `BASE_URL`。

## 五、API 文档

基础地址：`http://localhost:8080/api/pet`

统一响应格式：

```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

### 5.1 新增健康日志

```
POST /api/pet/health-record
```

**请求体：**

```json
{
  "petId": 1,
  "careType": "FEEDING",
  "weight": 4.5,
  "temperature": 38.5,
  "mentalState": "NORMAL",
  "note": "今天食欲良好",
  "recordDate": "2026-07-18"
}
```

**字段说明：**

| 字段 | 类型 | 必填 | 说明 |
|------|------|------|------|
| petId | Long | 是 | 宠物 ID |
| careType | String | 是 | 护理类型：FEEDING/BATH/VACCINE/DEWORMING |
| weight | Double | 否 | 体重，校验范围 0-100 |
| temperature | Double | 否 | 体温，校验范围 30-45 |
| mentalState | String | 否 | 精神状态：ACTIVE/NORMAL/QUIET/ILL |
| note | String | 否 | 备注 |
| recordDate | String | 是 | 记录日期，格式 yyyy-MM-dd |

### 5.2 获取月度统计数据

```
GET /api/pet/statistics/monthly?petId=1
```

**响应：**

```json
{
  "code": 200,
  "data": {
    "dates": ["2026-06-19", "2026-06-20"],
    "weights": [4.2, 4.3],
    "temperatures": [38.5, 38.6],
    "avgWeight": 4.25,
    "weightVolatility": 2.35,
    "abnormalCount": 1,
    "totalRecords": 10,
    "vaccineCompletionRate": 100
  }
}
```

### 5.3 获取今日待办事项

```
GET /api/pet/reminders/today?petId=1
```

**响应：**

```json
{
  "code": 200,
  "data": [
    {
      "id": 1,
      "title": "今天该喂驱虫药",
      "reminderDate": "2026-07-18",
      "completed": false,
      "expired": false,
      "careTypeName": "驱虫"
    }
  ]
}
```

### 5.4 获取健康记录列表

```
GET /api/pet/health-records?petId=1&careType=FEEDING
```

careType 为可选参数，不传则返回所有类型。

### 5.5 宠物管理

```
GET  /api/pet/list              # 获取宠物列表
POST /api/pet/add               # 新增宠物
```

### 5.6 待办事项管理

```
POST   /api/pet/reminder/add            # 新增待办
PUT    /api/pet/reminder/complete/{id}  # 标记完成
PUT    /api/pet/reminder/update/{id}    # 更新待办
DELETE /api/pet/reminder/{id}           # 删除待办
```

## 六、数据库表结构

### pet 表（宠物）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 自增主键 |
| name | VARCHAR | 宠物名称 |
| species | VARCHAR | 物种 |
| breed | VARCHAR | 品种 |
| birth_date | DATE | 出生日期 |
| gender | VARCHAR | 性别 |
| image_url | VARCHAR | 头像 URL |

### health_record 表（健康记录）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 自增主键 |
| pet_id | BIGINT (FK) | 关联 pet.id |
| care_type | VARCHAR | 护理类型枚举 |
| weight | DOUBLE | 体重 |
| temperature | DOUBLE | 体温 |
| mental_state | VARCHAR | 精神状态枚举 |
| note | VARCHAR | 备注 |
| record_date | DATE | 记录日期 |

### reminder 表（待办提醒）

| 字段 | 类型 | 说明 |
|------|------|------|
| id | BIGINT (PK) | 自增主键 |
| pet_id | BIGINT (FK) | 关联 pet.id |
| title | VARCHAR | 待办标题 |
| reminder_date | DATE | 提醒日期 |
| completed | BOOLEAN | 是否完成 |
| expired | BOOLEAN | 是否过期 |
| care_type | VARCHAR | 护理类型枚举 |

## 七、定时任务说明

系统内置 `@Scheduled` 定时任务（[ReminderScheduler.java](backend/src/main/java/com/petcare/scheduler/ReminderScheduler.java)）：

- **执行时间**：每天早上 6:00（Cron: `0 0 6 * * ?`）
- **功能**：扫描所有未完成且提醒日期早于今天的待办，自动标记为"已过期"
- **目的**：确保过期疫苗/护理提醒能及时反馈给用户

## 八、AI 辅助开发说明

本项目使用 Trae IDE 进行 AI 辅助编程，详细的 Prompt 日志见 [prompt_log.md](prompt_log.md)。

AI 主要参与了：
1. 项目脚手架搭建与目录结构规划
2. ECharts 图表配置（折线图、均值标线、渐变填充）
3. Spring Boot 后端 API 开发（Controller/Service/Repository）
4. Bug 修复（NPE、宠物切换联动、待办关联等）
5. 代码审查与优化建议
