package com.atguigu.gmall.payment.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author Weirdo
 * 日期2023/4/12 11:54
 * 可靠性投递配置
 */
@Component
@Log4j2
public class PaymentReturnAndConfirmConfig implements RabbitTemplate.ReturnCallback,RabbitTemplate.ConfirmCallback {
    @Resource
    private RabbitTemplate rabbitTemplate;

    /**
     * 初始化配置
     */
    @PostConstruct
    public void init(){
        rabbitTemplate.setReturnCallback(this::returnedMessage);
        rabbitTemplate.setConfirmCallback(this::confirm);
    }

    /**
     * 消息是否抵达交换机触发
     * @param correlationData
     * @param b
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(!b){
            Message message = correlationData.getReturnedMessage();
            MessageProperties messageProperties = message.getMessageProperties();
            log.error("消息没有抵达交换机,消息的内容为:" + new String(message.getBody()));
        }
    }

    /**
     * 消息未抵达队列配置
     * @param message
     * @param i
     * @param s
     * @param s1
     * @param s2
     */
    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        log.error("消息抵达了交换机但是没有抵达队列,消息的内容为:" + new String(message.getBody()));
        log.error("消息抵达了交换机但是没有抵达队列,错误码内容为:" + i);
        log.error("消息抵达了交换机但是没有抵达队列,错误的内容为:" + s);
        log.error("消息抵达了交换机但是没有抵达队列,消息的交换机属性内容为:" + s1);
        log.error("消息抵达了交换机但是没有抵达队列,消息的routingkey属性内容为:" + s2);
    }
}
