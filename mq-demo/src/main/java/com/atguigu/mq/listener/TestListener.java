package com.atguigu.mq.listener;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

/**
 * @author Weirdo
 * 日期2023/4/10 19:53
 * 消费者
 */
@Component
public class TestListener {
    @RabbitListener(queues = "demo_queue")
    public void getMessage(Channel channel, Message message){
        //获取消息的内容
        byte[] body = message.getBody();
        System.out.println("收到的消息为"+new String(body));
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息的标号: 这个消费者一共消费了多少条消息的计数
        long deliveryTag = messageProperties.getDeliveryTag();
        System.out.println("消息的传递标签为:"+deliveryTag);
        try {
            int i = 1/0;
            /**
             * 1.消息的编号
             * 2.是否批量确认
             */
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            try{
                //判断此消息是否被拒绝过
                if (messageProperties.getRedelivered()){
                    //记录到日志 mysql redis
                    //拒绝过 第二次被消费了
                    channel.basicReject(deliveryTag,false);
                }else{
                    //再来一次
                    channel.basicReject(deliveryTag, false);
                }
            }catch(Exception e1){
                System.out.println("拒绝消息失败");
            }

        }
    }
}
