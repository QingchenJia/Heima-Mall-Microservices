package com.hmall.trade.listener;

import com.hmall.trade.service.IOrderService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class RabbitMqListener {
    @Autowired
    private IOrderService orderService;

    /**
     * 监听支付成功的消息队列
     * 当接收到支付成功的消息时，该方法会被触发
     *
     * @param orderId 订单ID，用于标记支付成功的订单
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = "pay.success.queue", durable = "true"),
            exchange = @Exchange(value = "pay.direct"),
            key = "pay.success"))
    public void paySuccessListener(Long orderId) {
        orderService.markOrderPaySuccess(orderId);
    }
}
