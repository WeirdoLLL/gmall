package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillOrderService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Weirdo
 * 日期2023/4/15 10:30
 * 秒杀订单超时订单消息队列监听
 */
@Component
@Log4j2
public class SeckillOrderTimeoutListener {

    @Resource
    private SeckillOrderService seckillOrderService;
    @RabbitListener(queues = "seckill_order_normal_queue")
    public void seckillOrderTimeOut(Channel channel, Message message){

        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息唯一表示
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息内容
        String username = new String(message.getBody());

        try {
            //超时取消订单
            seckillOrderService.cancelSeckillOrder(username);
            //处理完成 确认消息
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            try {
                log.error("超时取消秒杀订单失败 用户名为"+username+"异常信息为"+e);
                //只处理一次 不放回消息队列 丢掉消息
                channel.basicReject(deliveryTag,false);
            } catch (Exception e1) {
                log.error("超时取消秒杀订单拒收消息失败 用户名为"+username+"异常信息为"+e1);
            }
        }
    }
}
