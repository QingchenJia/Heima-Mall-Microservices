package com.hmall.api.config;

import feign.Logger;
import org.springframework.context.annotation.Bean;

public class DefaultFeignConfig {
    /**
     * 配置Feign客户端的日志记录级别
     * <p>
     * 此方法定义了一个Spring的@Bean注解，用于配置Feign客户端的日志级别
     * 选择Logger.Level.FULL作为日志级别，意味着将记录所有调用细节，这对于调试和跟踪API调用非常有用
     *
     * @return Logger.Level 返回配置的日志记录级别，此处为FULL
     */
    @Bean
    public Logger.Level feignLogLevel() {
        return Logger.Level.FULL;
    }
}
