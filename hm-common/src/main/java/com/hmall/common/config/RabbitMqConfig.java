package com.hmall.common.config;

import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(MessageConverter.class)
public class RabbitMqConfig {
    /**
     * 配置并返回一个消息转换器，用于将消息转换为JSON格式或从JSON格式转换消息
     * 此处使用Jackson2JsonMessageConverter来实现消息的序列化和反序列化
     *
     * @return MessageConverter 实现类的实例，用于消息的JSON格式转换
     */
    @Bean
    public MessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
