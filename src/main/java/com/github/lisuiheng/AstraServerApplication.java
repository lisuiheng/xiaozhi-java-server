package com.github.lisuiheng;

import com.github.lisuiheng.astra.server.ai.config.RedisChatMemoryRepositoryAutoConfiguration;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;

@EnableAsync
@EnableScheduling
@SpringBootApplication
@EnableConfigurationProperties
@Import({RedisChatMemoryRepositoryAutoConfiguration.class})
@ComponentScan(basePackages = {
        "com.github.lisuiheng.astra.server.ai",
        "com.github.lisuiheng.astra.server.user",
        "com.github.lisuiheng.astra.server.server",
        "com.github.lisuiheng.astra.sys",
        "com.github.lisuiheng.astra.common",
        "com.github.lisuiheng.astra.server.speech",
        "com.github.lisuiheng.astra.server.asr",
        "com.github.lisuiheng.astra.server.tts",
        "com.github.lisuiheng.astra.server.config"  // 添加验证码配置包扫描
})
@MapperScan({
        "com.github.lisuiheng.astra.server.ai.mapper",
        "com.github.lisuiheng.astra.server.user.mapper",
        "com.github.lisuiheng.astra.server.server.mapper",
        "com.github.lisuiheng.astra.sys.mapper"
})
public class AstraServerApplication extends SpringBootServletInitializer {

    public static void main(String[] args) {
        SpringApplication.run(AstraServerApplication.class, args);
    }

}