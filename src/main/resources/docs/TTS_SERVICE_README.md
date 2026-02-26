# TTS服务使用说明

## 概述

本项目实现了基于字节跳动语音合成API的TTS（Text-to-Speech）服务，支持流式文本输入和流式语音输出。服务提供了REST API和WebSocket两种接口方式。

本实现基于OkHttp3 WebSocket客户端，相比传统的Java WebSocket实现具有更好的稳定性、连接管理和错误处理能力。

## 功能特性

1. **文本转语音**: 将文本转换为高质量的语音音频
2. **多种音频格式**: 支持MP3、OGG Opus、PCM、WAV等多种音频格式
3. **情感控制**: 支持不同情感的语音合成（高兴、悲伤、生气等）
4. **声音复刻**: 支持自定义声音ID的声音复刻功能
5. **流式处理**: 支持流式文本输入和流式音频输出
6. **REST API**: 提供HTTP接口方便集成
7. **WebSocket**: 提供实时双向通信接口
8. **异步处理**: 基于CompletableFuture的全异步处理
9. **连接管理**: 自动重连、心跳保活等机制
10. **资源清理**: 完善的资源管理和清理机制

## 项目结构

```
speech/
├── OkHttpTtsWebSocketHandler.java  # 基于OkHttp的WebSocket处理器
├── OkHttpTtsConfig.java            # TTS配置类
├── OkHttpTtsClient.java            # 高级TTS客户端
├── service/                        # 业务服务层
│   └── TtsService.java             # Spring服务封装
├── controller/                     # REST控制器
│   └── TtsController.java          # REST API控制器
├── websocket/                      # WebSocket处理器
│   └── TtsWebSocketHandler.java    # WebSocket处理器
├── config/                         # 配置类
│   ├── TtsProperties.java          # 配置属性
│   └── TtsWebSocketConfig.java     # WebSocket配置
├── protocol/                       # 协议层（已废弃）
│   └── ...                         # 旧的协议实现
└── example/                        # 使用示例
   ├── OkHttpTtsExample.java       # OkHttp使用示例
   ├── DirectTtsClientExample.java # 直接使用TtsProperties示例
   ├── TtsPropertiesUsageExample.java # TtsProperties详细使用示例
   ├── FixedTtsExample.java        # 修复后的使用示例
   └── StreamingTtsWithLLM.java    # 与大模型集成示例
```

## 配置说明

在`application-dev.yml`中配置TTS服务参数：

```yaml
tts:
  app-id: your_app_id
  access-token: your_access_token
  default-resource-id: seed-tts-2.0
  cloning-resource-id: seed-icl-2.0
  endpoint: wss://openspeech.bytedance.com/api/v3/tts/bidirection
  default-speaker: S_Baidu_chixiao_1
  default-model: seed-tts-1.1
  default-format: mp3
  default-sample-rate: 24000
```

## REST API接口

### 1. 文本合成语音

**Endpoint**: `POST /api/tts/synthesize`

**Request Body**:
```json
{
  "text": "要合成的文本内容",
  "format": "mp3",
  "speaker": "S_Baidu_chixiao_1",
  "model": "seed-tts-1.1",
  "sampleRate": 24000,
  "emotion": "happy",
  "emotionScale": 4,
  "speechRate": 0,
  "loudnessRate": 0
}
```

**Response**: 音频二进制数据

### 2. 健康检查

**Endpoint**: `GET /api/tts/health`

**Response**:
```json
{
  "status": "UP",
  "initialized": true
}
```

## WebSocket接口

**Endpoint**: `/ws/tts`

### 消息类型

1. **连接建立后**: 服务器发送连接成功消息
```json
{
  "type": "CONNECTED",
  "sessionId": "session_id"
}
```

2. **文本合成请求**:
```json
{
  "type": "SYNTHESIZE",
  "text": "要合成的文本内容",
  "format": "mp3",
  "speaker": "S_Baidu_chixiao_1"
}
```

3. **流式合成开始**:
```json
{
  "type": "STREAM_START",
  "streamId": "stream_id"
}
```

4. **流式数据发送**:
```json
{
  "type": "STREAM_DATA",
  "streamId": "stream_id",
  "text": "流式文本内容"
}
```

5. **流式合成结束**:
```json
{
  "type": "STREAM_END",
  "streamId": "stream_id"
}
```

## 使用示例

### 1. REST API调用示例

```bash
curl -X POST http://localhost:8001/api/tts/synthesize \
  -H "Content-Type: application/json" \
  -d '{
    "text": "你好，世界！",
    "format": "mp3",
    "speaker": "S_Baidu_chixiao_1"
  }' \
  --output output.mp3
```

### 2. 直接使用OkHttpTtsClient示例

```java
// 方式一：使用显式参数创建客户端
OkHttpTtsClient client = new OkHttpTtsClient(appId, accessToken, resourceId);

// 方式二：使用TtsProperties创建客户端（新增功能）
TtsProperties ttsProperties = new TtsProperties();
ttsProperties.setAppId("your-app-id");
ttsProperties.setAccessToken("your-access-token");
ttsProperties.setDefaultResourceId("seed-tts-2.0");
OkHttpTtsClient client = new OkHttpTtsClient(ttsProperties);

// 初始化连接
client.initialize().join();

// 配置TTS参数
OkHttpTtsConfig config = OkHttpTtsConfig.builder()
    .speaker("S_Baidu_chixiao_1")
    .audioFormat("mp3")
    .sampleRate(24000)
    .build();

// 合成文本
String text = "你好，这是一个TTS测试。";
CompletableFuture<byte[]> future = client.synthesize(text, config);
byte[] audioData = future.join();

// 保存音频文件
Files.write(Paths.get("output.mp3"), audioData);

// 关闭客户端
client.shutdown().join();
```

### 3. 流式处理示例

```java
// 创建客户端
OkHttpTtsClient client = new OkHttpTtsClient(appId, accessToken, resourceId);
client.initialize().join();

// 配置TTS参数
OkHttpTtsConfig config = OkHttpTtsConfig.builder()
    .speaker("S_Baidu_chixiao_1")
    .audioFormat("mp3")
    .sampleRate(24000)
    .build();

// 开始流式会话
CompletableFuture<OkHttpTtsClient.StreamingContext> sessionFuture = 
    client.startStreamingSession(config);
OkHttpTtsClient.StreamingContext context = sessionFuture.join();

// 流式发送文本
String[] texts = {"你好", "世界", "这是一个流式TTS测试"};
for (String text : texts) {
    client.streamText(context, text).join();
    Thread.sleep(500); // 模拟延迟
}

// 结束会话并获取完整音频
CompletableFuture<byte[]> audioFuture = client.finishStreamingSession(context);
byte[] completeAudio = audioFuture.join();

// 保存音频文件
Files.write(Paths.get("streaming_output.mp3"), completeAudio);

// 关闭客户端
client.shutdown().join();
```

### 4. 使用TtsProperties示例

项目提供了多个使用TtsProperties的示例：

1. **DirectTtsClientExample.java** - 直接使用TtsProperties创建客户端
2. **TtsPropertiesUsageExample.java** - 详细演示TtsProperties的使用方法
3. **OkHttpTtsExample.java** - 综合示例，包含多种使用方式
4. **FixedTtsExample.java** - 修复后的使用示例，展示了正确的连接流程

运行示例：
```bash
# 运行DirectTtsClientExample示例
mvn exec:java -Dexec.mainClass="com.github.lisuiheng.astra.server.speech.example.DirectTtsClientExample"

# 运行TtsPropertiesUsageExample示例
mvn exec:java -Dexec.mainClass="com.github.lisuiheng.astra.server.speech.example.TtsPropertiesUsageExample"

# 运行OkHttpTtsExample示例
mvn exec:java -Dexec.mainClass="com.github.lisuiheng.astra.server.speech.example.OkHttpTtsExample"

# 运行修复后的FixedTtsExample示例
mvn exec:java -Dexec.mainClass="com.github.lisuiheng.astra.server.speech.example.FixedTtsExample"
```

### 5. 高级功能示例

项目还提供了高级功能示例，展示如何使用声音复刻、情感控制和并发会话等特性：

```
# 运行高级功能示例
mvn exec:java -Dexec.mainClass="com.github.lisuiheng.astra.server.speech.example.OkHttpTtsAdvancedExample" \
    -DappId="your-app-id" \
    -DaccessToken="your-access-token"
```

高级示例包括：
1. **声音复刻**: 使用自定义声音ID进行个性化语音合成
2. **情感控制**: 控制语音的情感表达（高兴、悲伤、生气等）
3. **并发会话**: 同时处理多个TTS会话以提高吞吐量

## 高级功能

### 1. 情感控制

通过设置`emotion`和`emotionScale`参数来控制语音情感：

```json
{
  "text": "我很高兴！",
  "emotion": "happy",
  "emotionScale": 5
}
```

### 2. 声音复刻

使用自定义声音ID进行声音复刻：

```json
{
  "text": "这是我的声音。",
  "speaker": "your_custom_voice_id",
  "format": "wav",
  "sampleRate": 48000
}
```

### 3. 高级参数配置

```json
{
  "text": "高级配置示例",
  "speechRate": 20,
  "loudnessRate": 10
}
```

## 技术优势

### 基于 OkHttp3 的优势：
1. **更稳定的连接管理**：OkHttp 提供自动重连、连接池管理
2. **更好的线程模型**：内置的 Dispatcher 管理线程池
3. **完善的超时控制**：连接、读取、写入超时分别配置
4. **心跳保活**：支持 WebSocket ping/pong 保活机制
5. **更简洁的 API**：回调式的异步处理，代码更清晰

### 核心功能：
1. **完全异步**：所有操作返回 CompletableFuture
2. **会话管理**：支持多个并发会话
3. **流式处理**：真正的边收边播
4. **自动重连**：连接断开时自动重连
5. **资源清理**：完善的资源管理和清理机制

## 注意事项

1. 确保配置了正确的`app-id`和`access-token`
2. 不同的音色可能需要不同的`resource-id`
3. MP3和OGG格式建议明确设置`bitRate`参数
4. 流式处理时，确保按顺序发送消息
5. 音频数据通过WebSocket传输时为二进制格式

## 故障排除

1. **连接失败**: 检查网络连接和endpoint配置
2. **认证失败**: 检查app-id和access-token是否正确
3. **音频质量差**: 检查采样率和比特率设置
4. **无音频输出**: 检查音频格式和播放器兼容性
5. **连接中断**: 查看日志中的重连信息