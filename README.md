# Astra Server

Astra Server 是一个基于 Spring Boot 构建的现代化 AI 服务后端系统，集成了多种 AI 服务（如通义千问）、向量数据库、语音处理等功能。

## 功能特性

- 🤖 AI 集成：支持阿里云通义千问等 AI 服务
- 🧠 向量存储：集成 Qdrant 向量数据库进行知识库管理
- 🔐 安全认证：基于 Sa-Token 的完整认证体系
- 💬 语音处理：支持 ASR（语音识别）和 TTS（文本转语音）功能
- 🗂️ 知识库：支持文档上传和语义检索
- 🌐 多租户：支持多租户架构
- 📊 WebSocket：实时通信支持

## 技术栈

- **基础框架**: Spring Boot 3.5+
- **语言**: Java 17
- **数据库**: PostgreSQL
- **缓存**: Redis
- **AI**: Spring AI Alibaba (DashScope)
- **向量库**: Qdrant
- **认证**: Sa-Token
- **ORM**: MyBatis-Plus
- **工具库**: Hutool

## 环境要求

- Java 17+
- Maven 3.8+
- PostgreSQL 12+
- Redis 6+
- Qdrant 向量数据库

## 安装部署

### 1. 克隆项目

```bash
git clone <your-repo-url>
cd astra-server
```

### 2. 创建配置文件

复制示例配置文件并根据实际情况修改：

```bash
cp application-example.yml application-local.yml
```

编辑 `application-local.yml` 文件，填入你的实际配置信息。

或者使用环境变量进行配置（推荐）：

```bash
# 数据库配置
export DB_URL=jdbc:postgresql://localhost:5432/astra
export DB_USERNAME=astra
export DB_PASSWORD=your_password

# Redis 配置
export REDIS_HOST=localhost
export REDIS_PORT=6379
export REDIS_PASSWORD=your_redis_password

# AI 服务配置
export DASHSCOPE_API_KEY=your_dashscope_api_key
export OPENAI_API_KEY=your_openai_api_key

# 字节跳动 TTS 服务配置
export BYTEDANCE_APP_ID=your_app_id
export BYTEDANCE_ACCESS_TOKEN=your_access_token

# JWT 密钥（生产环境请务必更改）
export JWT_SECRET=your_secure_jwt_secret
export SA_TOKEN_SECRET=your_secure_sa_token_secret
```

### 3. 构建项目

```bash
mvn clean install -DskipTests
```

### 4. 运行应用

```bash
# 方式一：使用 Maven
mvn spring-boot:run

# 方式二：打包后运行
java -jar target/astra-server.jar

# 方式三：指定配置文件
java -jar target/astra-server.jar --spring.profiles.active=local
```

## 配置说明

### 数据库配置

- 默认使用 PostgreSQL，请确保数据库已创建并配置正确
- 表结构会通过 MyBatis-Plus 自动创建

### Redis 配置

- 用于缓存和会话管理
- 配置连接池参数以优化性能

### AI 服务配置

- 需要申请阿里云 DashScope API Key
- 支持多种模型类型：qwen-plus, text-embedding-v4 等

### 向量数据库配置

- 默认使用 Qdrant，需先启动 Qdrant 服务
- 也可以切换为 Milvus 等其他向量数据库

## API 文档

项目集成了 Swagger，启动后可通过以下地址访问 API 文档：
- http://localhost:8001/swagger-ui.html

## 安全注意事项

1. **生产环境必需更改默认密钥**：
   - JWT 密钥
   - Sa-Token 密钥
   - 加密公私钥对

2. **环境变量优先**：
   - 敏感信息应通过环境变量配置
   - 避免将密码等信息写入配置文件

3. **HTTPS 部署**：
   - 生产环境建议启用 HTTPS

## 开发贡献

欢迎提交 Issue 和 Pull Request 来改进项目！

## 许可证

本项目采用 [LICENSE_TYPE] 许可证，详见 [LICENSE](./LICENSE) 文件。

---

如有问题请提交 Issue 或联系项目维护者。