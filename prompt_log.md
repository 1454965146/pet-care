# AI Prompt 日志

> 本文档记录项目开发过程中使用 Trae IDE（AI 辅助编程）的 Prompt 与 AI 原始输出，并标注每条 Prompt 对应解决的功能与文件。

---

## Prompt 1：项目脚手架搭建

**时间**：项目启动阶段

**Prompt**：

> 前端使用uniapp，后端使用java，实现下面项目：课题一：【宠物日常护理与健康追踪助手】
> 核心领域：宠物生命周期管理、健康数据记录、自动提醒。
> 小程序页面（3个独立视图）：
> 1. 首页仪表盘：展示宠物的最近一次体重/体温变化曲线（使用 ECharts 绘图）、待办提醒
> 2. 护理日志页：记录"喂食/洗澡/疫苗/驱虫"，支持按时间线倒序展示，并配有标签筛选
> 3. 健康报告页：自动生成月度报告（体重变化趋势、疫苗接种完成率、异常记录统计）
> Java 后端 API：
> - POST /api/pet/health-record - 新增健康日志（包含体重、体温、精神状态枚举，后端进行数据校验）
> - GET /api/pet/statistics/monthly - 返回近30天的体重数组和异常次数（基于 JPA 聚合查询，计算平均值和波动率）
> - GET /api/pet/reminders/today - 返回今日待办事项（利用 Spring @Scheduled 定时扫描数据库，自动将过期疫苗标记为"已过期"）

**AI 原始输出（摘要）**：

AI 生成了完整的项目脚手架，包括：
- 后端 Spring Boot 项目结构（controller / service / repository / entity / dto / enums）
- 前端 uni-app 项目结构（pages / utils / static）
- 三个核心 API 端点的初始实现
- ECharts 图表基础配置
- @Scheduled 定时任务初始实现

**对应文件**：
- `backend/src/main/java/com/petcare/` （整个后端包结构）
- `frontend/pages/index/index.vue`、`frontend/pages/care-log/index.vue`、`frontend/pages/health-report/index.vue`
- `backend/src/main/java/com/petcare/scheduler/ReminderScheduler.java`

---

## Prompt 2：项目优化（新增宠物页 + 健康报告布局 + MySQL 数据库）

**时间**：项目优化阶段

**Prompt**：

> 我对这个项目有几个优化意见：
> 1：项目里面前端代码中没有发现新增宠物的按钮或者页面；
> 2：健康报告页面布局需要得到优化，而且数值只需要保留小数点后两位就可以；
> 3：后端需要有数据库，使用mysql，请根据我的优化意见进行优化项目

**AI 原始输出（摘要）**：

AI 完成了三项优化：
1. 新增 `pages/pet-add/index.vue` 页面，包含宠物名称、种类（6种）、品种、性别、出生日期表单
2. 健康报告页布局重构，添加健康评分进度条、4 个统计卡片（2×2 网格）、所有数值使用 `toFixed(2)` 保留两位小数
3. 后端切换为 MySQL 数据库，修改 `application.yml` 配置，添加 `mysql-connector-j` 依赖

**对应文件**：
- `frontend/pages/pet-add/index.vue`（新建）
- `frontend/pages/health-report/index.vue`（布局优化）
- `backend/src/main/resources/application.yml`（MySQL 配置）
- `backend/pom.xml`（MySQL 依赖）

---

## Prompt 3：后端启动报错排查

**时间**：问题修复阶段

**Prompt**：

> 后端项目启动会产生报错信息，请你对pet-care-frontend项目文件夹里面的：报错输出.txt，进行分析，后端的mysql数据库我已经进行手动创建

**AI 原始输出（摘要）**：

AI 分析报错日志，发现是数据库连接密码错误（`Access denied for user 'root'@'localhost'`），指导修改 `application.yml` 中的密码。

**对应文件**：
- `backend/src/main/resources/application.yml`（密码修改）

---

## Prompt 4：前端功能问题修复（宠物切换 + 待办新增）

**时间**：问题修复阶段

**Prompt**：

> 项目前端部分有两个问题：
> 1：在首页，动物新增后没办法切换新增加的动物；
> 2：在首页的今日待办只有固定的文字没办法进行新增或者修改

**AI 原始输出（摘要）**：

AI 创建了全局状态管理 `petStore.js`，使用 Vue `reactive` 实现跨页面宠物切换联动：
```javascript
import { reactive } from 'vue'
export const petStore = reactive({
  currentPetId: null,
  currentPet: null,
  petList: []
})
export const setCurrentPet = (pet) => {
  petStore.currentPetId = pet.id
  petStore.currentPet = pet
  uni.$emit('petChanged', pet)
}
```
同时在首页添加了待办新增/编辑弹窗组件。

**对应文件**：
- `frontend/utils/petStore.js`（新建）
- `frontend/pages/index/index.vue`（宠物切换 + 待办弹窗）
- `frontend/utils/api.js`（新增待办 CRUD API）

---

## Prompt 5：待办 NPE 与护理日志不联动修复

**时间**：问题修复阶段

**Prompt**：

> 点击今日待办新增没办法新增新的待办，请查看pet-care-frontend文件夹下的error.txt文件，检查前端和后端控制台的输出，同时护理页面的数据没有因为我更换了动物而进行变更，护理记录不对

**AI 原始输出（摘要）**：

AI 发现后端 `addReminder` 方法中 `petId` 为 null 导致 NPE（`Cannot invoke "Pet.getId()" because "Reminder.getPet()" is null`），修复方案：
```java
@PostMapping("/reminder/add")
public ApiResult<Reminder> addReminder(@RequestBody Map<String, Object> body) {
    Long petId = Long.valueOf(body.get("petId").toString());
    Pet pet = petRepository.findById(petId)
            .orElseThrow(() -> new RuntimeException("宠物不存在"));
    Reminder reminder = new Reminder();
    reminder.setPet(pet);
    // ...
}
```
护理日志页改用 `petStore.currentPetId` 并监听 `petChanged` 事件刷新数据。

**对应文件**：
- `backend/src/main/java/com/petcare/controller/PetInfoController.java`（addReminder 修复）
- `frontend/pages/care-log/index.vue`（宠物联动）
- `frontend/pages/index/index.vue`（新增待办传递 petId）

---

## Prompt 6：健康趋势图无数据 + 待办不可用修复

**时间**：问题修复阶段

**Prompt**：

> 首页健康趋势没有数据显示，今日待办还是不能使用，检查error.txt的前端后端控制台输出查看问题

**AI 原始输出（摘要）**：

AI 发现两个问题：
1. `StatisticsService` 中查询所有 reminder 时包含 `pet` 为 null 的脏数据导致 NPE，改用 `findByPetIdAndCareType` 查询特定宠物疫苗提醒
2. ECharts renderjs 引入方式在 H5 下不稳定，改为动态 script 标签加载 CDN

```java
// 修复前（NPE）：
List<Reminder> vaccineReminders = reminderRepository.findAll().stream()
    .filter(r -> r.getPet().getId().equals(petId) && r.getCareType() == CareType.VACCINE)
    .collect(Collectors.toList());

// 修复后：
List<Reminder> vaccineReminders = reminderRepository.findByPetIdAndCareType(petId, CareType.VACCINE);
```

**对应文件**：
- `backend/src/main/java/com/petcare/service/StatisticsService.java`（NPE 修复）
- `backend/src/main/java/com/petcare/repository/ReminderRepository.java`（新增查询方法）
- `frontend/pages/index/index.vue`（ECharts 加载方式）
- `frontend/pages/health-report/index.vue`（ECharts 加载方式）

---

## Prompt 7：健康报告 ECharts 图表优化

**时间**：优化阶段

**Prompt**：

> 优化健康报告里面体重变化趋势的eacher图，加载数据过慢，而且显示不出图表

**AI 原始输出（摘要）**：

AI 发现图表不显示的根因是使用了 `<canvas>` 标签（ECharts 需要 `<div>` 容器），并优化加载速度：
1. 将 `<canvas>` 改为 `<view>`（渲染为 div）
2. 在 `App.vue` 的 `onLaunch` 中预加载 ECharts CDN
3. 添加 `waitForECharts()` 轮询等待机制（50 次重试 + 兜底加载）
4. 修复 API 参数传递：`data: { petId }` 改为 URL 查询参数 `?petId=`

**对应文件**：
- `frontend/App.vue`（ECharts 预加载）
- `frontend/pages/health-report/index.vue`（容器修复 + 加载优化）
- `frontend/pages/index/index.vue`（同步修复）
- `frontend/utils/api.js`（API 参数修复）

---

## Prompt 8：项目完成度评估

**时间**：评估阶段

**Prompt**：

> 项目可以正常运行，请你进行评估一下该项目的完成情况和作为一个课程的小项目设计是否可以完成课程要求

**AI 原始输出（摘要）**：

AI 全面检查前后端代码，输出评估报告：
- 核心要求完成率约 90%
- 3 个页面视图全部实现
- 3 个核心 API 端点全部实现
- @Scheduled 定时任务和 ECharts 绘图均已具备
- 指出可优化点：JPA 聚合查询、Bean Validation、全局异常处理

**对应文件**：无（评估报告）

---

## Prompt 9：考核提交文档生成

**时间**：提交阶段

**Prompt**：

> 请你阅读pet-care-frontend文件夹下面的AI辅助编程与工程化实训（居家自学版）考核方案.docx文件，根据里面的要求输出这个项目最后需要提交的文件资料，视频和截图除外

**AI 原始输出（摘要）**：

AI 解析 docx 考核方案，生成以下提交文档：
- `README.md`（项目介绍、技术栈、安装运行指南、API 文档）
- `prompt_log.md`（本文件，AI Prompt 日志）
- `个人实训总结报告.md`（个人总结）

**对应文件**：
- `README.md`
- `prompt_log.md`
- `个人实训总结报告.md`

---

## AI Code Review 记录

> 以下为利用 AI 进行代码审查的记录，建议配合截图提交

### 审查 Prompt

> 请对这个宠物护理项目进行全面的代码审查，检查代码质量、架构规范、潜在bug，并给出优化建议

### AI 审查结论与优化建议

**1. 架构层面**：
- `PetInfoController` 直接注入 Repository 跳过 Service 层，违反分层架构规范，建议抽取 `PetService` 和 `ReminderService` 承载业务逻辑
- 缺少全局异常处理（`@ControllerAdvice`），建议新增 `GlobalExceptionHandler` 统一处理 RuntimeException、参数校验异常

**2. 数据访问层面**：
- `StatisticsService` 的统计计算使用 Java Stream 内存计算，建议改用 JPA `@Query` 聚合查询（`AVG()`、`STDDEV()`）提升性能
- `HealthRecordRepository` 虽 import 了 `@Query` 但未实际使用

**3. 数据校验**：
- 未引入 `spring-boot-starter-validation`，建议在 DTO 上添加 `@NotNull`、`@Min`、`@Max` 注解，Controller 添加 `@Valid`

**4. 前端层面**：
- ECharts 通过 `document.createElement('script')` 加载，仅适用于 H5，建议条件编译支持小程序端
- `api.js` 中 `BASE_URL` 硬编码 `localhost:8080`，建议改为环境变量配置
- `petStore` 无本地持久化，重启后状态丢失，建议使用 `uni.setStorageSync` 持久化 `currentPetId`

**5. 工程化建议**：
- 建议增加单元测试（JUnit + MockMvc）
- 建议配置 CI/CD（GitHub Actions）
- 建议前端增加下拉刷新和分页加载
