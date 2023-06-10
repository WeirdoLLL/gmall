package com.atguigu.gmall.list.listener;

import com.atguigu.gmall.list.service.GoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author Weirdo
 * 日期2023/4/11 19:01
 * 监听商品上下架消息的消费者: 同步es数据
 */
@Component
@Log4j2
public class SkuUpOrDownMessageListener {

    @Resource
    private GoodsService goodsService;
    @RabbitListener(queues = "sku_upper_queue")
    public void skuUpMessage(Channel channel, Message message){
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息的内容
        Long skuId = Long.parseLong(new String(message.getBody()));

        try {
            //上架
            goodsService.addSkuIntoEs(skuId);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //取消订单出现异常
                if(messageProperties.getRedelivered()){
                    //若为第二次取消都失败,则记录到日志
                    log.error("商品上架失败,商品id为:" + skuId);
                    //消息处理掉
                    channel.basicReject(deliveryTag, false);
                }else{
                    //再给一次机会
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("商品上架的消费者出现异常,商品id为:" +
                        skuId + ",异常的内容为:" + e.getMessage());
            }
        }
    }

    /**
     * 商品下架消费者
     * @param channel
     * @param message
     */
    @RabbitListener(queues = "sku_down_queue")
    public void skuDownMessage(Channel channel, Message message){
        //获取消息的属性
        MessageProperties messageProperties = message.getMessageProperties();
        //消息的编号
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息的内容
        Long skuId = Long.parseLong(new String(message.getBody()));
        try {
            //上架
            goodsService.removeGoods(skuId);
            //确认消息
            channel.basicAck(deliveryTag, false);
        }catch (Exception e){
            try {
                //取消订单出现异常
                if(messageProperties.getRedelivered()){
                    //若为第二次取消都失败,则记录到日志
                    log.error("商品下架失败,商品id为:" + skuId);
                    //消息处理掉
                    channel.basicReject(deliveryTag, false);
                }else{
                    //再给一次机会
                    channel.basicReject(deliveryTag, true);
                }
            }catch (Exception e1){
                log.error("商品下架的消费者出现异常,商品id为:" +
                        skuId + ",异常的内容为:" + e.getMessage());
            }
        }
    }

}
