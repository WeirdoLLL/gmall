package com.atguigu.gmall.order.listener;

import com.atguigu.gmall.order.service.OrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Weirdo
 * 日期2023/4/11 1:38
 * 监听延迟消息的消费者
 */
@Component
@Log4j2
public class OrderDelayMessageListener {

    @Resource
    private OrderService orderService;

    @RabbitListener(queues = "order_normal_queue")
    public void orderDelayMessage(Channel channel, Message message) {
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息的内容
        Long orderId = Long.parseLong(new String(message.getBody()));
        try {
            //取消超时确认
            orderService.cancelOrder(orderId);
            //这个false是批量新增  小于等于id100的全部消息容易把别人的确认了
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            try {
                //取消订单出现异常
                if (messageProperties.getRedelivered()) {
                    //若为第二次取消失败 则记录到日志
                    log.error("取消超时订单两次失败" + orderId);
                    //消息处理掉 false不放回队列
                    channel.basicReject(deliveryTag, false);
                } else {
                    //再给一次机会 true消息放回队列
                    channel.basicReject(deliveryTag, true);
                }
            } catch (Exception e1) {
                log.error("超时取消订单的消费者出现异常 超时取消的订单号为" + orderId);
                log.error("拒绝消息出异常 异常信息为" + orderId);
            }
        }
    }
}
