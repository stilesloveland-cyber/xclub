# Xclub

一个现代化的 Android 便捷工具 App，集成网页快捷入口、记账、知识库笔记、待办提醒和云同步功能。

## 功能特性

| 模块 | 功能 |
|------|------|
| 🌐 内嵌网页 | 沉浸式 WebView，支持配置多个网址快捷入口 |
| 💰 记账 | 收支记录、存钱计划、自定义周期扣款、发薪日记账、月账单偏移 |
| 📝 知识库笔记 | 双向链接 `[[笔记名]]`、标题/正文/备注、知识图谱 |
| ✅ 待办提醒 | 待办列表、提醒功能 |
| ☁️ 云同步 | 自建服务器 Docker 一键部署，支持 Cloudflare Tunnel |

## 技术栈

- **Android**: Kotlin + Jetpack Compose + Material 3
- **架构**: 单体模块化（multi-module）
- **本地存储**: Room + SQLCipher（AES-256-GCM 加密）
- **依赖注入**: Hilt
- **云同步**: Ktor 服务端 + SQLite + Docker + Caddy

## 下载安装

### APK 下载

CI 构建产物（每次 push 到 main 分支自动构建）：

- [xclub-debug-apk](https://github.com/stilesloveland-cyber/xclub/releases) — Debug 测试版

将 APK 传到手机，直接安装即可使用（无需云同步）。

## 云同步服务器部署

如果需要在多设备间同步数据，需要部署自建同步服务器。

### 方式一：Docker 一键部署（推荐）

#### 1. 下载服务器 JAR

CI 构建产物：
- [xclub-sync-server](https://github.com/stilesloveland-cyber/xclub/releases) — Sync Server JAR

下载后放入 `/sync-server/` 目录。

#### 2. 运行部署向导

```bash
cd sync-server
bash deploy.sh
```

部署向导会依次询问：
- **端口号**（默认 5555）
- **是否有域名**（有域名配合 Caddy 反向代理）
- **是否使用 Cloudflare Tunnel**（零配置内网穿透）
- **数据存储路径**（默认 `./data`）

#### 3. 访问服务

- 本机访问：`http://localhost:<端口>/health`
- 有域名：`https://你的域名/health`

### 方式二：手动 Docker 部署

```bash
cd sync-server

# 拉取镜像（需要先放入 JAR）
docker build -t xclub-sync-server .

# 启动服务
PORT=5555 docker compose up -d
```

### 环境变量

| 变量 | 默认值 | 说明 |
|------|--------|------|
| `PORT` | 5555 | 服务监听端口 |
| `DOMAIN` | （空） | 有域名时配置，Caddy 用于 HTTPS |
| `CLOUDFLARE_TUNNEL_TOKEN` | （空） | Cloudflare Tunnel Token |

### Cloudflare Tunnel（零配置内网穿透）

如果你没有公网 IP 但有 Cloudflare 账号：

```bash
# 1. 获取 Cloudflare Tunnel Token
#    在 https://dash.cloudflare.com 创建 Zero Trust Tunnels

# 2. 启动带 Tunnel 的服务
CLOUDFLARE_TUNNEL_TOKEN=你的token docker compose --profile cloudflare up -d
```

### 健康检查

```bash
curl http://localhost:5555/health
# 返回: ok
```

## 目录结构

```
sync-server/
├── build.gradle.kts      # Gradle 构建配置
├── Dockerfile            # Docker 镜像定义
├── docker-compose.yml    # Docker Compose 配置
├── Caddyfile             # Caddy 反向代理配置
├── deploy.sh             # 可视化部署向导
└── src/main/kotlin/      # Ktor 服务端源码
    ├── Application.kt    # 入口
    ├── routes/           # 路由（认证/同步/更新）
    ├── database/         # SQLite 数据库
    └── model/            # 数据模型
```

## API 端点

| 端点 | 方法 | 说明 |
|------|------|------|
| `/` | GET | 服务状态 |
| `/health` | GET | 健康检查 |
| `/api/auth` | POST | 用户注册/验证 |
| `/api/sync` | POST | 数据同步（需认证） |
| `/api/update/check` | GET | 检查应用更新 |
| `/api/update/download/<version>` | GET | 下载 APK 更新包 |

## 开发

### 环境要求

- JDK 17+
- Android SDK (API 33+)
- Gradle 8.10

### 构建

```bash
# 构建 APK
./gradlew assembleDebug

# 构建同步服务器
./gradlew :sync-server:build
```

### CI/CD

每次 push 到 `main` 分支自动触发：
1. 编译 Android Debug APK
2. 编译 Sync Server JAR
3. 上传构建产物到 GitHub Actions Artifacts

## 许可证

MIT
