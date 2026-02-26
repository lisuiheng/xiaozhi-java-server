# Astra Server 项目结构说明

## 项目概述

Astra Server 是一个基于 Spring Boot 的 AI 服务器应用程序，提供了 AI 聊天、向量存储、语音处理等功能。

## 技术栈

- Spring Boot 2.6.15
- Java 11
- Maven 构建工具
- MyBatis-Plus 数据库访问
- Redis 缓存
- RabbitMQ 消息队列
- WebSocket 实时通信

## 目录结构

```
astra-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/github/lisuiheng/astra/server/
│   │   │       ├── AstraServerApplication.java (主启动类)
│   │   │       ├── ai/ (AI相关模块)
│   │   │       │   ├── controller/
│   │   │       │   │   ├── ChromaController.java
│   │   │       │   │   └── KnowledgeBaseController.java
│   │   │       │   ├── mapper/
│   │   │       │   │   └── KnowledgeDocMapper.java
│   │   │       │   ├── model/
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── AddDocumentRequest.java
│   │   │       │   │   │   ├── ChromaQueryRequest.java
│   │   │       │   │   │   ├── ChromaQueryResponse.java
│   │   │       │   │   │   ├── MemoryQuery.java
│   │   │       │   │   │   ├── MemoryRecord.java
│   │   │       │   │   │   └── QueryRequest.java
│   │   │       │   │   └── entity/
│   │   │       │   │       └── KnowledgeDoc.java
│   │   │       │   └── service/
│   │   │       │       ├── AIChatService.java
│   │   │       │       ├── ChromaService.java
│   │   │       │       ├── KnowledgeDocService.java
│   │   │       │       └── VectorMemoryService.java
│   │   │       ├── server/ (核心服务模块)
│   │   │       │   ├── config/
│   │   │       │   │   ├── BailianClientConfig.java
│   │   │       │   │   ├── ChatClientConfig.java
│   │   │       │   │   ├── MilvusConfig.java
│   │   │       │   │   ├── SecurityConfig.java
│   │   │       │   │   ├── VectorStoreConfig.java
│   │   │       │   │   ├── VoiceWebSocketHandler.java
│   │   │       │   │   ├── AsrWebSocketConfig.java
│   │   │       │   │   └── TtsWebSocketConfig.java
│   │   │       │   ├── controller/
│   │   │       │   │   └── ChatController.java
│   │   │       │   ├── enums/
│   │   │       │   │   └── SessionStatus.java
│   │   │       │   ├── handler/
│   │   │       │   │   ├── JsonbTypeHandler.java
│   │   │       │   │   └── MyMetaObjectHandler.java
│   │   │       │   ├── interceptor/
│   │   │       │   │   └── VoiceHttpSessionHandshakeInterceptor.java
│   │   │       │   ├── model/
│   │   │       │   │   ├── dto/
│   │   │       │   │   │   ├── ChatRequest.java
│   │   │       │   │   │   ├── ChatResponse.java
│   │   │       │   │   │   └── VoiceStreamRequest.java
│   │   │       │   │   └── entity/
│   │   │       │   │       ├── Conversation.java
│   │   │       │   │       └── SessionState.java
│   │   │       │   └── util/
│   │   │       │       └── OpusCodec.java
│   │   │       └── voice/ (语音处理模块)
│   │   │           ├── handler/
│   │   │           │   └── AsrWebSocketHandler.java
│   │   │           ├── model/
│   │   │           │   ├── dto/
│   │   │           │   │   ├── AudioParams.java
│   │   │           │   │   ├── inbound/
│   │   │           │   │   │   ├── ErrorInbound.java
│   │   │           │   │   │   ├── HelloInbound.java
│   │   │           │   │   │   ├── InboundMessage.java
│   │   │           │   │   │   ├── IotInbound.java
│   │   │           │   │   │   ├── ListenInbound.java
│   │   │           │   │   │   ├── SttInbound.java
│   │   │           │   │   │   └── TtsInbound.java
│   │   │           │   │   └── outbound/
│   │   │           │   │       ├── ErrorOutbound.java
│   │   │           │   │       ├── HelloOutbound.java
│   │   │           │   │       ├── IotAckOutbound.java
│   │   │           │   │       ├── OutboundMessage.java
│   │   │           │   │       ├── TtsStartOutbound.java
│   │   │           │   │       └── TtsStopOutbound.java
│   │   │           └── service/
│   │   │               ├── AsrService.java
│   │   │               ├── AsrWebSocketSessionManager.java
│   │   │               ├── TtsService.java
│   │   │               ├── UnifiedSessionManager.java
│   │   │               ├── UserMemoryService.java
│   │   │               └── VoiceService.java
│   │   └── common/
│   │       └── util/
│   │           └── StringUtils.java
│   └── resources/
│       ├── application.yml
│       ├── application-dev.yml
│       ├── config.properties
│       ├── logback-spring.xml
│       ├── data/
│       │   └── code.txt
│       └── docs/
│           └── PROJECT_STRUCTURE.md (本文档)
│   └── test/
│       └── java/
│           └── com/github/lisuiheng/
│               └── AiApplicationTest.java
└── pom.xml
```

## 模块说明

### AI 模块 (ai/)
该模块负责处理 AI 相关功能：
- 向量数据库操作 (Chroma)
- 知识库管理
- AI 聊天服务

### 核心服务模块 (server/)
该模块提供核心服务功能：
- WebSocket 配置和处理器
- 安全配置
- 聊天控制器
- 会话状态管理

### 语音处理模块 (voice/)
该模块负责语音相关的处理：
- ASR (自动语音识别)
- TTS (文本转语音)
- WebSocket 会话管理
- 音频参数处理

## 配置文件

- `application.yml`: 主配置文件
- `application-dev.yml`: 开发环境配置
- `config.properties`: 自定义配置属性
- `logback-spring.xml`: 日志配置

## 构建与运行

项目使用 Maven 进行构建，Java 版本要求为 11。

```bash
# 构建项目
mvn clean package

# 运行项目
mvn spring-boot:run
```