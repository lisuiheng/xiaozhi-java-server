package com.github.lisuiheng.astra.server.speech.example;

import com.github.lisuiheng.astra.server.speech.OkHttpTtsClient;
import com.github.lisuiheng.astra.server.speech.OkHttpTtsConfig;
import com.github.lisuiheng.astra.server.speech.config.TtsProperties;
import lombok.extern.slf4j.Slf4j;

import java.io.FileOutputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * OkHttp TTS 使用示例
 * 展示如何使用TtsProperties配置TTS客户端
 */
@Slf4j
public class OkHttpTtsExample {

    public static void main(String[] args) throws Exception {
        
        // 方式2: 使用TtsProperties（推荐方式）
        exampleWithTtsProperties();
    }

    /**
     * 示例: 使用TtsProperties配置TTS客户端
     */
    private static void exampleWithTtsProperties() throws Exception {
        log.info("=== Example with TtsProperties ===");
        
        // 创建TtsProperties实例并配置
        TtsProperties ttsProperties = new TtsProperties();
        ttsProperties.setAppId(System.getProperty("appId", "8500930862"));
        ttsProperties.setAccessToken(System.getProperty("accessToken", "pO8keqQzua-AMIaQGFcIjB0K3jcdJKlp"));
        ttsProperties.setDefaultResourceId("volc.service_type.10029");
        ttsProperties.setDefaultSpeaker("zh_female_wanwanxiaohe_moon_bigtts");
        ttsProperties.setDefaultFormat("mp3");
        ttsProperties.setDefaultSampleRate(24000);

        // 使用TtsProperties创建客户端
        OkHttpTtsClient client = new OkHttpTtsClient(ttsProperties);

        try {
            // 执行示例
            runBasicExamples(client);
        } finally {
            // 关闭客户端
            client.shutdown().join();
        }
    }

    /**
     * 运行基本示例
     */
    private static void runBasicExamples(OkHttpTtsClient client) {
        try {
            // 示例2: 同步合成
            exampleSynchronousSynthesis(client);

            // 示例3: 流式合成
//            exampleStreamingSynthesis(client);

        } catch (Exception e) {
            log.error("Error in examples", e);
        }
    }


    /**
     * 示例2: 同步合成
     */
    private static void exampleSynchronousSynthesis(OkHttpTtsClient client) {
        log.info("=== Example 2: Synchronous Synthesis ===");

        // 配置TTS参数
        OkHttpTtsConfig config = OkHttpTtsConfig.builder()
            .speaker("zh_female_wanwanxiaohe_moon_bigtts")
            .audioFormat("mp3")
            .sampleRate(24000)
            .build();

        String text = "如是我闻，一时，佛在舍卫国祗树给孤独园，与大比丘众千二百五十人俱。尔时，世尊食时，著衣持钵，\n" +
                "入舍卫大城乞食。于其城中，次第乞已，还至本处。饭食讫，收衣钵，洗足已，敷座而坐。";

        CompletableFuture<byte[]> synthesisFuture = client.synthesize(text, config);
        synthesisFuture.thenAccept(audio -> {
            try (FileOutputStream fos = new FileOutputStream("synchronous_output.mp3")) {
                fos.write(audio);
                log.info("Synchronous synthesis completed. Audio size: {} bytes", audio.length);
            } catch (Exception e) {
                log.error("Failed to save audio", e);
            }
        }).join();
    }

    /**
     * 示例3: 流式合成
     */
    private static void exampleStreamingSynthesis(OkHttpTtsClient client) {
        log.info("=== Example 3: Streaming Synthesis ===");

        OkHttpTtsConfig config = OkHttpTtsConfig.builder()
            .speaker("zh_female_wanwanxiaohe_moon_bigtts")
            .audioFormat("mp3")
            .sampleRate(24000)
            .build();

        String longText = "这是一个流式合成示例。文本会被分割成小块进行流式处理。" +
                         "这样可以在接收文本的同时输出语音，实现真正的流式体验。" +
                         "特别适合与大模型集成，实现实时的语音对话。";

        // 开始流式会话
        client.startStreamingSession(config)
            .thenAccept(context -> {
                log.info("Streaming session started: {}", context.getSessionId());

                try {
                    // 模拟流式输入
                    String[] sentences = longText.split("[。！？]");
                    AtomicInteger sentenceCount = new AtomicInteger(0);

                    for (String sentence : sentences) {
                        if (sentence.trim().isEmpty()) continue;

                        log.info("Streaming sentence {}: {}",
                                 sentenceCount.incrementAndGet(), sentence);

                        // 流式发送文本
                        client.streamText(context, sentence).join();

                        // 模拟处理延迟
                        Thread.sleep(200);
                    }

                    // 结束会话并获取音频
                    client.finishStreamingSession(context)
                        .thenAccept(audio -> {
                            try (FileOutputStream fos = new FileOutputStream("streaming_output.mp3")) {
                                fos.write(audio);
                                log.info("Streaming synthesis completed. Audio size: {} bytes", audio.length);
                            } catch (Exception e) {
                                log.error("Failed to save audio", e);
                            }
                        }).join();

                } catch (Exception e) {
                    log.error("Streaming synthesis failed", e);
                }
            }).join();
    }

    /**
     * 示例4: 与大模型集成
     */
    private static void exampleLLMIntegration(OkHttpTtsClient client) {
        log.info("=== Example 4: LLM Integration ===");

        OkHttpTtsConfig config = OkHttpTtsConfig.builder()
            .speaker("zh_female_wanwanxiaohe_moon_bigtts")
            .audioFormat("ogg_opus")
            .sampleRate(48000)
            .build();

        // 创建流式会话
        OkHttpTtsClient.StreamingSession session = client.createStreamingSession(config);

        // 启动音频处理线程
        ExecutorService audioProcessor = Executors.newSingleThreadExecutor();
        audioProcessor.submit(() -> {
            while (session.isActive()) {
                try {
                    Thread.sleep(100);

                    // 实时处理音频块
                    session.getAudioChunks().forEach(chunk -> {
                        // 这里可以实时播放或处理音频
                        log.debug("Real-time audio chunk: {} bytes", chunk.length);
                    });

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }
        });

        try {
            // 开始会话
            session.start().join();
            log.info("LLM integration session started: {}", session.getSessionId());

            // 模拟从大模型获取的流式响应
            String[] llmResponses = {
                "你好，我是AI助手。",
                "我可以帮助你处理各种任务。",
                "请问有什么可以帮您的吗？",
                "我可以回答问题、提供建议、协助写作等等。"
            };

            // 流式发送大模型响应
            for (String response : llmResponses) {
                log.info("LLM Response: {}", response);
                session.sendText(response).join();

                // 模拟大模型思考时间
                Thread.sleep(500);
            }

            // 结束会话并获取完整音频
            byte[] completeAudio = session.finish().join();

            try (FileOutputStream fos = new FileOutputStream("llm_integration.opus")) {
                fos.write(completeAudio);
                log.info("LLM integration completed. Audio size: {} bytes", completeAudio.length);
            }

        } catch (Exception e) {
            log.error("LLM integration failed", e);
        } finally {
            audioProcessor.shutdownNow();
        }
    }
}