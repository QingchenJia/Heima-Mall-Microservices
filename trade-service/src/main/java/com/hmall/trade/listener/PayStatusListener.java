package com.hmall.trade.listener;

import cn.hutool.core.util.ObjectUtil;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PayStatusListener {
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
            exchange = @Exchange("pay.direct"),
            key = "pay.success"))
    public void paySuccessListener(Long orderId) {
        // 根据订单ID获取订单信息
        Order order = orderService.getById(orderId);

        // 检查订单是否为空或是否已经支付成功
        if (ObjectUtil.isNull(order) || order.getStatus() != 1) {
            // 如果订单为空或已支付成功，则直接返回，不进行后续处理
            return;
        }

        // 标记订单为支付成功
        orderService.markOrderPaySuccess(orderId);
    }
}
