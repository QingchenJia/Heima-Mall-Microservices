package com.hmall.api.client.fallback;

import com.hmall.api.client.UserClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<UserClient> {
    /**
     * 重写创建UserClient的方法
     * 此方法在发生错误时被调用，用于生成一个能够处理错误的UserClient实例
     *
     * @param cause 触发错误的原因，用于在日志中记录
     * @return 返回一个匿名内部类实例，该实例重写了UserClient的deductMoney方法
     */
    @Override
    public UserClient create(Throwable cause) {
        return new UserClient() {
            /**
             * 重写deductMoney方法
             * 当调用此方法时，不会执行实际的扣款逻辑，而是记录错误日志
             *
             * @param pw 密码参数，未使用
             * @param amount 金额参数，未使用
             */
            @Override
            public void deductMoney(String pw, Integer amount) {
                // 记录调用user-service失败的错误日志，并附带错误原因
                log.error("调用user-service失败", cause);
            }
        };
    }
}
