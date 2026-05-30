# Xclub - 安卓便捷工具 App 设计文档

## 概述

Xclub 是一款面向个人用户的安卓便捷工具 App，集成内嵌网页、记账、知识库笔记和效率工具四大核心功能。采用 Kotlin + Jetpack Compose 原生开发，单体模块化架构，Telegram/X 风格 UI，支持本地加密存储、手动备份和可选的自建云同步。

## 项目架构

### 模块结构

```
xclub/
├── app/                          # 主应用壳，Navigation + 底部导航栏
├── core/
│   ├── core-ui/                  # 共享 UI 组件、主题、Compose 基础组件
│   ├── core-data/                # Room 数据库、Repository 接口、数据模型
│   ├── core-sync/                # 云同步引擎（自建服务器通信）
│   └── core-common/              # 工具类、扩展函数、常量
├── feature/
│   ├── feature-web/              # 内嵌网页模块
│   ├── feature-finance/          # 记账模块
│   ├── feature-notes/            # 笔记模块
│   └── feature-todo/             # 效率工具模块
└── sync-server/                  # Docker 部署的云同步服务端
```

### 依赖规则

```
app → feature-* → core-*
feature-* 之间不可互相依赖（通过 core-data 的 Repository 间接共享数据）
core-* 之间：core-ui 和 core-sync 依赖 core-common，但不互相依赖
```

### 技术栈

| 层面 | 技术 | 理由 |
|------|------|------|
| UI | Jetpack Compose + Material 3 | 现代化声明式 UI，Telegram/X 风格 |
| 导航 | Compose Navigation | 模块间导航，类型安全 |
| 数据库 | Room + SQLCipher | 本地加密持久化，类型安全查询 |
| 依赖注入 | Hilt | Google 官方推荐，模块化友好 |
| 网络 | Retrofit + OkHttp | 云同步 API 通信 |
| 序列化 | Kotlinx Serialization | 备份导入/导出、API 数据 |
| 异步 | Kotlin Coroutines + Flow | 响应式数据流 |
| WebView | Chromium WebView + 自定义 Client | 内嵌网页渲染 |
| 云同步服务端 | Ktor + SQLite/Docker | 轻量 Kotlin 服务端，一键部署 |
| 反向代理 | Caddy | 自动 HTTPS，配置简单 |

### 版本兼容

| 配置 | 值 | 理由 |
|------|------|------|
| targetSdk | 36 (Android 16) | 适配最新系统行为和 API |
| minSdk | 33 (Android 13) | 覆盖主流设备，无需兼容过老版本 |
| compileSdk | 36 | 使用最新 SDK 编译 |

向下兼容策略：使用 Jetpack 库自动处理大部分版本差异；对 Android 16 新特性做运行时版本检查，旧版本走降级逻辑。

## 功能模块设计

### feature-web — 内嵌网页

**核心定位：网页快捷入口，不是浏览器**

**功能：**
- 管理一组常用网页书签，点击即打开查看
- 每个书签：标题、URL、图标（自动获取 favicon）、分组
- WebView 沉浸式展示网页内容
- 仅保留最少操作：返回上一页（手势或简单按钮）、刷新

**数据模型：**

```kotlin
@Entity(tableName = "web_bookmarks")
data class WebBookmark(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val url: String,
    val faviconUrl: String? = null,
    val group: String? = null,
    val sortOrder: Int = 0,
    val createdAt: Instant,
    val updatedAt: Instant
)
```

**UI：**
- 首页：卡片式展示已配置的网页入口（类似 Telegram 聊天列表风格）
- 点击卡片 → 沉浸式 WebView 全屏展示网页内容
- 顶部仅显示网页标题 + 返回按钮，无地址栏
- 长按卡片弹出操作菜单（编辑/删除）
- 支持分组展示（如"工作"、"工具"）

**不做的事：** 多标签页、地址栏输入、页面内搜索、桌面模式切换、浏览历史、广告拦截

### feature-finance — 记账

**核心定位：以发薪日为周期的轻量记账 + 存钱计划**

**功能：**

1. **收支记录** — 金额、分类（餐饮/交通/购物/...）、备注、日期；支持自定义分类（图标+颜色）；支出/收入两种类型

2. **灵活记账周期** — 三种模式：
   - 自然月：每月1号到月末
   - 发薪日周期：以发薪日为起点（如15号→下月14号）
   - 自定义偏移：在以上任一模式基础上，前后偏移 N 天

   偏移逻辑示例：
   | 模式 | 基准周期 | 偏移 | 实际周期 |
   |------|---------|------|---------|
   | 自然月 | 1号~31号 | +3天 | 1号~次月3号 |
   | 发薪日 | 15号~14号 | -2天 | 13号~12号 |
   | 自然月 | 1号~31号 | -2天+3天 | 上月29号~次月3号 |

3. **存钱计划** — 创建存钱目标（名称、目标金额、截止日期）；支持手动存入和自定义周期自动扣款；自动扣款规则：每 N 天/每周/每月；进度展示

4. **统计概览** — 周期内分类支出饼图；最近几个周期支出趋势折线图；Top 支出分类排行

**数据模型：**

```kotlin
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: TransactionType,
    val amount: BigDecimal,
    val categoryId: Long,
    val note: String? = null,
    val date: LocalDate,
    val createdAt: Instant
)

enum class TransactionType { INCOME, EXPENSE }

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val icon: String,
    val color: Long,
    val type: TransactionType,
    val isDefault: Boolean = false
)

@Entity(tableName = "saving_plans")
data class SavingPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val targetAmount: BigDecimal,
    val currentAmount: BigDecimal,
    val deadline: LocalDate? = null,
    val autoDeductAmount: BigDecimal? = null,
    val autoDeductPeriod: DeductPeriod? = null,
    val autoDeductCustomDays: Int? = null,
    val createdAt: Instant
)

enum class DeductPeriod { DAILY, WEEKLY, MONTHLY, CUSTOM_DAYS }

@Entity(tableName = "saving_records")
data class SavingRecord(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val planId: Long,
    val amount: BigDecimal,
    val isAuto: Boolean,
    val date: LocalDate,
    val createdAt: Instant
)

@Entity(tableName = "billing_cycles")
data class BillingCycle(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val mode: CycleMode,
    val payday: Int? = null,
    val startOffset: Int = 0,
    val endOffset: Int = 0,
    val isDefault: Boolean = false
)

enum class CycleMode { NATURAL_MONTH, PAYDAY }

@Entity(tableName = "user_configs")
data class UserConfig(
    @PrimaryKey val key: String,
    val value: String
)
```

**UI：**
- 首页：当前周期概览卡片（收入/支出/结余）+ 今日记录列表
- 底部浮动按钮：快速记一笔
- 记账页面：大数字键盘输入金额，滑动选分类
- 存钱计划页：卡片式展示各计划进度
- 统计页：简洁图表 + 分类排行
- 周期概览顶部显示当前周期范围，左右滑动切换周期

### feature-notes — 笔记

**核心定位：知识库型笔记，支持结构化内容与双向链接**

**功能：**

1. **笔记编辑** — 每条笔记：标题、正文（Markdown）、备注/标签；编辑模式与预览模式切换；支持插入图片（本地存储）

2. **双向链接** — 使用 `[[笔记名]]` 语法引用其他笔记；自动在目标笔记的"反向链接"区域显示引用来源；点击链接直接跳转

3. **知识图谱** — 可视化展示笔记间链接关系；节点=笔记，边=链接关系；点击节点跳转；孤立笔记显示在图谱边缘

4. **组织与搜索** — 标签系统（每条笔记可打多个标签）；全文搜索（标题+正文+备注）；按标签筛选、按时间排序

**数据模型：**

```kotlin
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val content: String,
    val remark: String? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

@Entity(tableName = "note_tags")
data class NoteTag(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String
)

@Entity(tableName = "note_tag_relations", primaryKeys = ["noteId", "tagId"])
data class NoteTagRelation(
    val noteId: Long,
    val tagId: Long
)

@Entity(tableName = "note_links", primaryKeys = ["sourceNoteId", "targetNoteId"])
data class NoteLink(
    val sourceNoteId: Long,
    val targetNoteId: Long
)
```

**UI：**
- 首页：笔记列表 + 顶部搜索栏 + 标签筛选 chips
- 列表项：标题 + 内容摘要 + 标签 + 更新时间
- 编辑页：顶部标题输入 + 正文编辑区 + 底部工具栏（格式/链接/图片/预览）
- 笔记详情页底部：反向链接列表
- 知识图谱页：全屏 Canvas 绘制节点关系图，支持缩放和拖拽

### feature-todo — 效率工具

**核心定位：轻量待办与提醒，配合其他模块使用**

**功能：**

1. **待办事项** — 标题、备注、优先级（高/中/低）、截止日期；完成状态切换；支持重复待办（每天/每周/每月）

2. **提醒** — 为待办设置提醒时间；系统通知推送，点击直接打开对应待办；支持多个提醒时间

3. **快捷入口** — 从记账模块快速创建"记账提醒"；从笔记模块快速创建关联笔记的待办；模块间通过共享 deeplink 跳转

**数据模型：**

```kotlin
@Entity(tableName = "todos")
data class Todo(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val note: String? = null,
    val priority: Priority,
    val dueDate: LocalDate? = null,
    val isCompleted: Boolean = false,
    val repeatRule: RepeatRule? = null,
    val linkedNoteId: Long? = null,
    val createdAt: Instant,
    val updatedAt: Instant
)

enum class Priority { HIGH, MEDIUM, LOW }
enum class RepeatRule { DAILY, WEEKLY, MONTHLY }

@Entity(tableName = "reminders")
data class Reminder(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val todoId: Long,
    val remindAt: Instant,
    val isTriggered: Boolean = false
)
```

**UI：**
- 首页：今日待办 + 即将到期 + 已完成三段式列表
- 顶部日期选择器
- 滑动待办项快速完成/删除（类似 Telegram 消息滑动操作）
- 新建待办底部弹窗

## 数据层与云同步

### 本地存储

Room 数据库统一管理所有模块数据，启用 SQLCipher 加密。

**加密策略：**
- 数据库加密密钥派生自用户密码 + 设备唯一标识，存储在 EncryptedSharedPreferences
- 敏感字段（记账金额、笔记内容）额外使用 AES-256-GCM 加密
- 首次启动引导用户设置加密密码

**备份与恢复：**
- 导出：数据库序列化为 JSON 文件（Kotlinx Serialization），使用用户密码加密
- 导入：解密并解析 JSON 写入数据库，支持增量合并（按 id 去重，冲突取较新版本）
- 文件存储到 Downloads 目录或用户选择目录
- 支持自动定期备份（可配置周期）

**数据迁移：**
- Room Migration 机制处理数据库升级
- 每个版本变更编写 Migration 类
- 开发阶段使用 fallbackToDestructiveMigration

### 云同步

**同步架构：**

```
App (本地 Room) ←→ HTTPS ←→ Sync Server (Ktor + SQLite)
```

**同步策略：增量同步 + 最后写入胜出**
- 每条记录维护 updatedAt 时间戳和 syncVersion 版本号
- 客户端上传：发送本地 syncVersion 之后变更的记录
- 服务端返回：服务端 syncVersion 之后变更的记录
- 冲突处理：updatedAt 更新的记录胜出

**认证：**
- 用户配置服务器地址 + Token（首次配对时生成）
- Token 存储在本地 EncryptedSharedPreferences
- 所有请求携带 Token，服务端验证

**传输安全：**
- 强制 HTTPS
- 服务端自动生成自签名证书
- 客户端支持配置证书指纹校验
- 同步数据传输前额外 AES 加密

**同步配置：**
- 支持仅 Wi-Fi 下自动同步
- 按模块分批同步（笔记数据分页传输）

### sync-server — Docker 部署

**默认端口：5555，支持自定义**

**目录结构：**

```
sync-server/
├── Dockerfile
├── docker-compose.yml
├── Caddyfile
├── deploy.sh
├── src/
│   ├── Application.kt
│   ├── routes/
│   │   ├── AuthRoutes.kt
│   │   ├── SyncRoutes.kt
│   │   └── UpdateRoutes.kt
│   └── database/
│       └── DatabaseFactory.kt
└── README.md
```

**部署脚本（deploy.sh）交互式流程：**

1. 检测 Docker 环境，未安装则提示安装命令
2. 交互式输入：
   - 端口号（默认 5555，直接回车使用默认值）
   - 是否有域名（无域名则使用 IP 直接访问）
   - 如有域名：输入域名，自动配置 Caddy 反向代理 + Let's Encrypt SSL
   - 是否使用 Cloudflare（是 → 选择 Tunnel 或 DNS 代理模式）
   - 数据存储路径（默认 ./data）
3. 显示配置摘要，确认后开始部署
4. 实时显示部署进度（拉取镜像 → 创建容器 → 健康检查）
5. 部署完成显示访问地址和状态

**一键修复/删除：**

```bash
bash deploy.sh --repair    # 修复
bash deploy.sh --uninstall # 删除
```

`--repair` 逻辑：检测容器运行状态并尝试重启；检测端口占用并提示更换；检测数据卷完整性，损坏则从最近备份恢复；检测网络连通性，修复防火墙规则

`--uninstall` 逻辑：停止并删除容器；询问是否删除数据卷（默认保留）；清理相关镜像；显示卸载结果

**域名与 Cloudflare 适配：**

Caddy 作为反向代理，自动 HTTPS：

```
# 有域名
sync.example.com {
    reverse_proxy localhost:5555
}

# 无域名
:5555 {
    reverse_proxy localhost:5555
    tls internal
}
```

Cloudflare 接入方式 A（推荐）：Cloudflare Tunnel
- 部署脚本自动安装 cloudflared
- 使用 Tunnel Token 连接 Cloudflare 边缘网络
- 无需开放端口，无需公网 IP

```yaml
cloudflared:
  image: cloudflare/cloudflared:latest
  command: tunnel run
  environment:
    - TUNNEL_TOKEN=${CLOUDFLARE_TUNNEL_TOKEN}
```

Cloudflare 接入方式 B：DNS 代理模式
- 用户在 Cloudflare DNS 添加 A 记录指向服务器 IP
- 开启代理（橙色云朵），Cloudflare 自动提供 SSL
- 服务端 Caddy 监听 80 端口即可

**客户端适配：**
- App 设置页配置服务器地址支持：IP:端口、域名、Cloudflare Tunnel 域名
- 自动检测 HTTPS 可用性，优先 HTTPS
- Cloudflare 代理下 WebSocket 超时默认 100 秒，同步逻辑需处理重连

## 应用内更新

**APK 自更新流程：**

1. App 启动时检查更新（向自建服务器或 GitHub Release 请求版本信息）
2. 比对本地 versionCode 与服务器最新 versionCode
3. 如有新版本，弹出更新对话框（显示 changelog）
4. 用户确认后，后台下载新 APK
5. 下载完成，调用系统安装器安装
   - Android 8+ 需要 REQUEST_INSTALL_PACKAGES 权限（首次需用户授权）
   - 通过 FileProvider 提供 APK 文件给系统安装器
6. 安装完成后自动重启进入新版本

**版本信息 JSON：**

```json
{
  "versionCode": 12,
  "versionName": "1.2.0",
  "changelog": "修复...",
  "downloadUrl": "https://..."
}
```

**安全：** APK 下载使用 HTTPS；可选对 APK 做签名校验；Wi-Fi 下自动检查更新，移动网络仅提示

## UI 主题与设计语言

### 设计风格

Telegram/X 融合：简洁克制，信息密度高但不拥挤；圆角卡片 + 微妙阴影；流畅过渡动画；手势操作优先

### 主题系统

- 三种模式：浅色 / 深色 / 跟随系统
- Android 12+ 使用 Material 3 Dynamic Color（取自壁纸）
- 旧版本使用预设主题色
- 支持用户自定义主题色（从预设色板选择）

### 字体

- 中文：系统默认（思源黑体）
- 英文/数字：系统默认 Sans Serif
- 记账金额：等宽字体（Monospace）

### 通用 UI 规范

| 元素 | 规范 |
|------|------|
| 圆角 | 12dp（卡片）、8dp（按钮）、16dp（弹窗） |
| 间距 | 8dp 基准网格，内容区 16dp 水平边距 |
| 列表项高度 | 56dp（紧凑）/ 72dp（舒适） |
| FAB | 右下角，仅主页显示 |
| 弹窗 | 底部 Sheet 优先（符合单手操作习惯） |
| 滑动手势 | 左滑删除/完成，右滑归档/标记 |
| 长按 | 弹出上下文菜单 |
| 过渡动画 | 300ms 标准时长，EaseInOut 曲线 |

### 导航结构

```
底部导航栏（4 Tab）
├── 网页 → 书签列表 → WebView 沉浸页
├── 记账 → 周期概览 → 记账/存钱/统计 子页
├── 笔记 → 笔记列表 → 编辑/图谱 子页
└── 工具 → 待办列表 → 新建/提醒 子页

侧滑抽屉（从左侧滑出）
├── 设置
├── 数据备份与恢复
├── 云同步配置
├── 检查更新
└── 关于
```
