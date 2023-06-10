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
 * 日期2023/4/14 10:35
 * 秒杀订单新增订单监听
 */
@Component
@Log4j2
public class SeckillOrderAddListener {

    @Resource
    private SeckillOrderService seckillOrderService;

    @RabbitListener(queues = "seckill_order_queue")
    public void seckillOrderAddListener(Channel channel, Message message) {
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //消息的唯一标识
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息内容
        String userRecodeString = new String(message.getBody());
        try {
            //秒杀真实下单业务逻辑
            seckillOrderService.seckillOrderRealAddOrder(userRecodeString);
            //处理成功确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //秒杀下单失败 记录信息 丢掉消息
                log.error("秒杀下单失败 排队信息为" + userRecodeString);
                channel.basicReject(deliveryTag, false);
            }catch(Exception e1){
                log.error("秒杀下单并且拒收出现异常 排队信息为" + userRecodeString+
                                "异常信息为"+e1);
            }
        }
    }

}
