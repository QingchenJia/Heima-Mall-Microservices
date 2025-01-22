package com.hmall.api.config;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.ObjectUtil;
import com.hmall.common.utils.UserContext;
import feign.Logger;
import feign.RequestInterceptor;
import feign.RequestTemplate;
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

    /**
     * 配置用户信息请求拦截器
     * <p>
     * 该方法的作用是创建并配置一个RequestInterceptor实例，用于在发送请求前
     * 将当前用户信息（用户ID）添加到请求头部，以便后端服务可以获取到用户信息
     *
     * @return RequestInterceptor 实例，用于拦截请求并添加用户信息到请求头部
     */
    @Bean
    public RequestInterceptor userInfoRequestInterceptor() {
        return new RequestInterceptor() {
            /**
             * 拦截请求并添加用户信息到请求头部
             * <p>
             * 此方法在请求发送前被调用，它的主要作用是获取当前用户ID，并将其
             * 作为请求头部的一部分添加到请求中，以便后端服务可以利用这些信息
             *
             * @param requestTemplate 请求模板，用于定制请求
             */
            @Override
            public void apply(RequestTemplate requestTemplate) {
                // 获取当前用户ID
                Long userId = UserContext.getUser();
                // 如果用户ID不为空，则将其添加到请求头部
                if (ObjectUtil.isNotEmpty(userId)) {
                    requestTemplate.header("userInfo", Convert.toStr(userId));
                }
            }
        };
    }
}
