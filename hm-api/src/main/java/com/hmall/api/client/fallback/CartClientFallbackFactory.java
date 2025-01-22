package com.hmall.api.client.fallback;

import com.hmall.api.client.CartClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
@Slf4j
public class CartClientFallbackFactory implements FallbackFactory<CartClient> {
    /**
     * 重写创建CartClient的方法
     * 本方法用于在特定异常发生时，创建一个具有特定行为的CartClient实例
     *
     * @param cause 异常原因，用于记录调用cart-service失败的具体原因
     * @return 返回一个实现了CartClient接口的匿名内部类实例
     */
    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {
            /**
             * 实现removeByItemIds方法
             * 当调用此方法时，记录调用cart-service失败的错误信息
             *
             * @param itemIds 要移除的商品ID集合，此处未使用，因为调用失败，实际操作未执行
             */
            @Override
            public void removeByItemIds(Collection<Long> itemIds) {
                log.error("调用cart-service失败", cause);
            }
        };
    }
}
