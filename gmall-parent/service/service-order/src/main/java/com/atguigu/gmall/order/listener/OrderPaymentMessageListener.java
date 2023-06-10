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
 * 日期2023/4/12 14:31
 * 订单支付结果消息接受的消费者
 */
@Component
@Log4j2
public class OrderPaymentMessageListener {

    @Resource
    private OrderService orderService;

    /**
     * 监听普通订单的支付消息 修改订单状态为已支付
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "normal_order_payment_queue")
    public void orderPaymentMessage(Channel channel, Message message){
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取传递表示
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息内容
        String resultString = new String(message.getBody());
        try{
            //修改订单支付状态 TODO
            orderService.updateOrder(resultString);
            //确认消息
            channel.basicAck(deliveryTag,false);
        }catch(Exception e){
            try {
                //修改订单状态出现异常
                if (messageProperties.getRedelivered()) {
                    //处理消息 第二次失败记录到日志
                    log.error("修改订单的支付结果两次都失败,订单支付结果报文为:" + resultString);
                    //处理消息 第二次失败 直接删除
                    channel.basicReject(deliveryTag, false);
                } else {
                    //处理消息 第一次失败可以放回消息队列 requeue=true 重新排队
                    channel.basicReject(deliveryTag, true);
                }
            }catch(Exception e1){
                log.error("修改订单的支付结果的消费者出现异常,订单支付结果报文为:" +
                        resultString + ",拒绝消息出现异常,异常的内容为:" + e.getMessage());

            }
        }

    }
}
