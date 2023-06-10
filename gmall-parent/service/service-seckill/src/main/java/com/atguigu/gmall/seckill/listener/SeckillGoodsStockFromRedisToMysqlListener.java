package com.atguigu.gmall.seckill.listener;

import com.atguigu.gmall.seckill.service.SeckillGoodsService;
import com.rabbitmq.client.Channel;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author Weirdo
 * 日期2023/4/14 18:57
 * 监听商品库存消息队列
 * 商品库存信息 写入redis的同时 发消息到死信队列 等该时间段到了以后 处理商品库存信息
 */
@Component
@Log4j2
public class SeckillGoodsStockFromRedisToMysqlListener {
    @Resource
    private SeckillGoodsService seckillGoodsService;
    @RabbitListener(queues = "seckill_goods_normal_queue")
    public void seckillGoodsStockFromRedisToMysql(Channel channel, Message message){
        //获取消息属性
        MessageProperties messageProperties = message.getMessageProperties();
        //获取消息唯一表示
        long deliveryTag = messageProperties.getDeliveryTag();
        //获取消息内容
        String time = new String(message.getBody());
        try {
            //同步库存
            seckillGoodsService.updateSeckillStock(time);
            //确认消息
            channel.basicAck(deliveryTag,false);
        } catch (Exception e) {
            try {
                log.error("同步秒杀商品库存失败 消息内容为"+time+"异常信息为"+e);
                //处理消息  只确认一次  直接扔掉
                channel.basicReject(deliveryTag,false);
            } catch (Exception e1) {
                log.error("同步秒杀商品库存失败 且拒收失败 消息内容为"+time+"异常信息为"+e1);
            }
        }
    }
}
