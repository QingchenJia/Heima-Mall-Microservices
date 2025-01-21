package com.hmall.common.config;

import com.hmall.common.interceptor.UserInfoInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@ConditionalOnClass(DispatcherServlet.class)
public class WebMvcConfig implements WebMvcConfigurer {
    /**
     * 添加拦截器配置
     * <p>
     * 该方法用于向应用程序中添加拦截器，以在请求处理之前或之后执行特定逻辑
     * 在本例中，我们添加了一个用户信息拦截器，它将应用于所有请求路径
     *
     * @param registry InterceptorRegistry实例，用于注册拦截器
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册一个用户信息拦截器，并指定其拦截的路径模式
        registry.addInterceptor(new UserInfoInterceptor())
                .addPathPatterns("/**");
    }
}
