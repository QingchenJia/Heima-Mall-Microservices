package com.hmall.common.interceptor;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.hmall.common.utils.UserContext;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class UserInfoInterceptor implements HandlerInterceptor {
    /**
     * 在请求处理之前进行预处理
     *
     * @param request  HttpServletRequest对象，用于获取请求信息
     * @param response HttpServletResponse对象，用于设置响应信息
     * @param handler  处理请求的处理器对象
     * @return boolean    返回true表示继续执行下一个拦截器或处理器，返回false表示中断执行
     * <p>
     * 此方法主要用于检查请求头中是否包含用户信息，并在存在用户信息时设置当前用户上下文
     * 如果请求头中没有用户信息，方法直接返回true，允许请求继续处理
     * 如果请求头中包含用户信息，则将用户ID转换为长整型并设置到用户上下文中，然后返回true继续处理请求
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从请求头中获取用户信息
        String userId = request.getHeader("userInfo");
        // 检查用户信息是否为空，为空则直接返回true，继续执行下一个拦截器或处理器
        if (StrUtil.isBlank(userId)) {
            return true;
        }

        // 用户信息不为空时，将用户ID转换为长整型并设置到用户上下文中
        UserContext.setUser(Convert.toLong(userId));
        // 继续执行下一个拦截器或处理器
        return true;
    }

    /**
     * 在请求完成后执行的操作
     *
     * @param request  HTTP请求对象，用于获取请求相关的信息
     * @param response HTTP响应对象，用于获取响应相关的信息
     * @param handler  处理请求的处理器对象，可以是任何对象，取决于具体的实现
     * @param ex       在请求处理过程中发生的异常，如果没有异常，则为null
     * @throws Exception 根据具体实现，可能会抛出异常
     *                   <p>
     *                   此方法主要用于请求完成后的清理工作，特别是移除通过UserContext存储的用户信息
     *                   它确保了即使在发生异常的情况下，用户信息也能被正确移除，避免了潜在的信息泄露风险
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.removeUser();
    }
}
