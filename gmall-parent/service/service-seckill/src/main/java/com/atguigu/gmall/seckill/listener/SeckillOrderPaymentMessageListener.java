package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author Weirdo
 * 日期2023/4/17 14:28
 */
@Component
@Log4j2
public class SeckillOrderPaymentMessageListener {

    @Autowired
    private SeckillOrderService seckillOrderService;

    /**
     * 监听秒杀订单支付消息,修改秒杀订单的支付状态
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "seckill_order_payment_queue")
    public void seckillOrderPaymengMessageListener(Channel channel, Message message){
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息的内容
        String resultString = new String(message.getBody());
        try {
            //修改秒杀订单的支付状态
            seckillOrderService.updateSeckillOrder(resultString);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //只确认一次 第二次直接丢掉
                log.error("修改秒杀订单的支付结果失败,支付的报文为:" + resultString);
                //消息处理掉
                channel.basicReject(deliveryTag, false);
            }catch (Exception e1){
                log.error("修改秒杀订单的支付结果失败,支付的报文为:" + resultString + ",拒绝消息出现异常,异常的内容为:" + e.getMessage());
            }
        }
    }
}
