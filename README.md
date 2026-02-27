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
- 🔁 双向流式交互：支持实时语音输入和流式音频输出

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

### 双向流式交互配置

- 支持实时语音输入流处理
- 支持流式音频输出（TTS）
- 集成字节跳动火山引擎等主流 TTS 服务
- 支持多种音频格式的实时编解码
- 低延迟的流式处理架构，优化用户体验


## 开发贡献

欢迎提交 Issue 和 Pull Request 来改进项目！

## 许可证

本项目采用 [LICENSE_TYPE] 许可证，详见 [LICENSE](./LICENSE) 文件。

---

如有问题请提交 Issue 或联系项目维护者。