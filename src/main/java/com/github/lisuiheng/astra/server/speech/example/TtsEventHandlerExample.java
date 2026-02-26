package com.github.lisuiheng.astra.server.speech.example;

import com.github.lisuiheng.astra.server.speech.OkHttpTtsWebSocketHandler;
import com.github.lisuiheng.astra.server.speech.model.dto.TtsEvent;
import com.github.lisuiheng.astra.server.speech.protocol.EventType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * TTS 事件处理器使用示例
 */
public class TtsEventHandlerExample {

    public static void main(String[] args) {
        // 示例使用方式
        String endpoint = "ws://example.com/tts"; // 替换为实际的TTS服务端点
        Map<String, String> headers = new HashMap<>();
        // 添加必要的请求头

        // 创建处理器
        OkHttpTtsWebSocketHandler handler = new OkHttpTtsWebSocketHandler(endpoint, headers);

        // 设置事件回调（使用JavaBean）
        handler.setOnEvent(event -> {
            switch (event.getEventType()) {
                case CONNECTION_STARTED:
                    System.out.println("连接已建立: " + event.getTimestamp());
                    break;
                case SESSION_STARTED:
                    System.out.println("会话开始: " + event.getSessionId());
                    break;
                case TTS_RESPONSE:
                    System.out.println("收到音频数据");
                    break;
                case SESSION_FINISHED:
                    System.out.println("会话结束");
                    break;
                case SESSION_FAILED:
                    System.out.println("会话失败: " + event.getErrorMessage());
                    break;
                case CONNECTION_FINISHED:
                    System.out.println("连接结束");
                    break;
                default:
                    System.out.println("收到事件: " + event.getEventType() + ", 会话ID: " + event.getSessionId());
                    break;
            }

            // 也可以直接访问所有属性
            System.out.println("事件详情: " + event);
        });

        // 设置音频数据回调
        handler.setOnAudioData(audioData -> {
            System.out.println("收到音频数据: " + audioData.length + " bytes");
        });

        // 设置错误回调
        handler.setOnError(error -> {
            System.err.println("发生错误: " + error.getMessage());
        });

        // 设置关闭回调
        handler.setOnClose(reason -> {
            System.out.println("连接已关闭: " + reason);
        });

        // 连接
        handler.connect().thenAccept(connected -> {
            if (connected) {
                System.out.println("连接成功");

                // 开始会话
                Map<String, Object> config = new HashMap<>();
                config.put("voice_type", "default");
                config.put("audio_format", "pcm");

                handler.startSession(config).thenAccept(sessionId -> {
                    System.out.println("会话开始: " + sessionId);

                    // 发送文本
                    handler.sendText(sessionId, "你好，世界！", null).thenRun(() -> {
                        System.out.println("文本已发送");
                    });

                    // 结束会话
                    CompletableFuture<byte[]> audioFuture = handler.finishSession(sessionId);
                    audioFuture.thenAccept(audioData -> {
                        System.out.println("会话结束，收到音频数据: " + audioData.length + " bytes");
                    });
                });
            } else {
                System.out.println("连接失败");
            }
        });
    }
}