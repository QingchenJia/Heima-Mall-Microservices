package com.hmall.gateway.filter;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.text.AntPathMatcher;
import cn.hutool.core.util.StrUtil;
import com.hmall.common.exception.UnauthorizedException;
import com.hmall.gateway.config.AuthProperties;
import com.hmall.gateway.utils.JwtTool;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    private final AuthProperties authProperties;

    private final JwtTool jwtTool;

    /**
     * 自定义过滤器，用于处理进入网关的请求
     * 主要功能包括：排除特定路径的请求进行认证、验证用户令牌、设置用户信息到请求头
     *
     * @param exchange 服务器Web交换对象，包含请求和响应
     * @param chain    过滤器链，用于执行下一个过滤器或最终的路由
     * @return Mono<Void> 表示异步处理完成
     */
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // 获取当前请求和响应对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        // 将请求路径转换为字符串
        String path = request.getPath().toString();

        // 遍历配置的排除路径，如果当前请求路径匹配到排除路径，则直接放行
        for (String excludePath : authProperties.getExcludePaths()) {
            if (antPathMatcher.match(excludePath, path)) {
                return chain.filter(exchange);
            }
        }

        // 从请求头中获取授权令牌
        String token = request.getHeaders().getFirst("authorization");

        // 如果令牌为空，则返回未授权状态码
        if (StrUtil.isBlank(token)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 初始化用户ID为null
        Long userId = null;

        try {
            // 解析令牌，获取用户ID
            userId = jwtTool.parseToken(token);
        } catch (UnauthorizedException e) {
            // 如果解析失败，说明令牌无效，返回未授权状态码
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        // 将用户ID转换为字符串形式
        String userInfo = Convert.toStr(userId);

        // 通过交换对象的mutate方法修改请求头，添加用户信息
        ServerWebExchange serverWebExchange = exchange.mutate()
                .request(builder -> builder.header("userInfo", userInfo))
                .build();

        // 继续处理下一个过滤器或路由
        return chain.filter(serverWebExchange);
    }

    /**
     * 获取当前对象的顺序值
     * <p>
     * 顺序值用于确定对象在某些操作中的执行顺序或显示顺序
     * 返回值为0表示默认顺序，具体数值的含义依赖于具体的业务逻辑和使用场景
     *
     * @return int 返回当前对象的顺序值，0表示默认顺序
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
