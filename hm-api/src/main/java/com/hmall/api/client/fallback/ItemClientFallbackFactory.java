package com.hmall.api.client.fallback;

import com.hmall.api.client.ItemClient;
import com.hmall.api.dto.ItemDTO;
import com.hmall.api.dto.OrderDetailDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;

@Component
@Slf4j
public class ItemClientFallbackFactory implements FallbackFactory<ItemClient> {
    /**
     * 重写创建ItemClient的方法，用于处理特定的异常情况
     * 当调用ItemClient的实现类方法时，如果因特定异常导致方法调用失败，将使用此方法创建的实例
     *
     * @param cause 异常原因，用于记录日志
     * @return ItemClient的实现类实例，用于处理异常情况下的方法调用
     */
    @Override
    public ItemClient create(Throwable cause) {
        return new ItemClient() {
            /**
             * 根据多个ID查询商品信息，当调用失败时记录错误日志并返回null
             *
             * @param ids 商品ID集合
             * @return 查询到的商品信息列表，如果调用失败则为null
             */
            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                log.error("调用item-service失败", cause);
                return null;
            }

            /**
             * 扣减商品库存，当调用失败时记录错误日志
             *
             * @param items 订单详情列表，包含需要扣减库存的商品信息
             */
            @Override
            public void deductStock(List<OrderDetailDTO> items) {
                log.error("调用item-service失败", cause);
            }
        };
    }
}
