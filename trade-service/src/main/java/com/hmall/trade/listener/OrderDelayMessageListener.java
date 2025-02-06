package com.hmall.trade.listener;

import cn.hutool.core.util.ObjectUtil;
import com.hmall.api.client.PayClient;
import com.hmall.api.dto.PayOrderDTO;
import com.hmall.trade.constant.MqConstant;
import com.hmall.trade.domain.po.Order;
import com.hmall.trade.service.IOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderDelayMessageListener {
    private final PayClient payClient;

    private final IOrderService orderService;

    /**
     * 监听延迟队列中的消息，处理订单支付状态
     * 该方法主要目的是检查订单在延迟队列中一段时间后是否已经完成支付
     * 如果订单未支付，则取消订单；如果已支付，则标记订单支付成功
     *
     * @param orderId 订单ID，用于查询和更新订单状态
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConstant.DELAY_QUEUE_NAME, durable = "true"),
            exchange = @Exchange(value = MqConstant.DELAY_EXCHANGE_NAME, delayed = "true"),
            key = MqConstant.DELAY_ROUTING_KEY))
    public void delayListener(Long orderId) {
        // 根据订单ID获取订单信息
        Order order = orderService.getById(orderId);

        // 检查订单是否存在且状态为待支付（状态码为1）
        if (ObjectUtil.isNull(order) || order.getStatus() != 1) {
            // 如果订单不存在或状态不是待支付，则直接返回，不进行后续处理
            return;
        }

        // 调用支付系统接口，查询订单的支付状态
        PayOrderDTO payOrderDTO = payClient.queryPayOrderByBizOrderNo(orderId);

        // 检查支付订单是否存在且状态为已支付（状态码为3）
        if (ObjectUtil.isNull(payOrderDTO) || payOrderDTO.getStatus() != 3) {
            // 如果支付订单不存在或状态不是已支付，则取消订单
            orderService.cancelOrder(orderId);
            return;
        }

        // 标记订单支付成功
        orderService.markOrderPaySuccess(orderId);
    }
}
