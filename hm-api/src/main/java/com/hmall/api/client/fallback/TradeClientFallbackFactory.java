package com.hmall.api.client.fallback;

import com.hmall.api.client.TradeClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TradeClientFallbackFactory implements FallbackFactory<TradeClient> {
    /**
     * 重写创建TradeClient的方法
     * 本方法用于处理当调用TradeService失败时的情况
     *
     * @param cause 异常原因，用于记录导致调用失败的原因
     * @return 返回一个实现了TradeClient接口的匿名类实例，用于处理订单支付成功的标记
     */
    @Override
    public TradeClient create(Throwable cause) {
        return new TradeClient() {
            /**
             * 标记订单支付成功的方法
             * 当调用trade-service失败时，通过本方法记录错误日志
             *
             * @param orderId 订单ID，用于标识特定的订单
             */
            @Override
            public void markOrderPaySuccess(Long orderId) {
                log.error("调用trade-service失败", cause);
            }
        };
    }
}
