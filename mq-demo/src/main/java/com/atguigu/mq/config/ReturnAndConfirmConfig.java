package com.atguigu.mq.config;

import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

/**
 * @author Weirdo
 * 日期2023/4/10 20:24
 * 可靠性投递的配置类
 */
@Component
public class ReturnAndConfirmConfig implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init() {
        rabbitTemplate.setReturnCallback(this::returnedMessage);
        rabbitTemplate.setConfirmCallback(this::confirm);
    }
    /**
     * confirm模式: 消息是否抵达交换机--->无论是否抵达都会触发--高版本删除
     * @param correlationData
     * @param b
     * @param s
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        if(b){
            System.out.println("为true的时候:" + s);
        }else{
            System.out.println("将消息存储到redis/日志/mysql/重发");
            System.out.println("为false的时候:" + s);
        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        System.out.println("只要触发,就将消息记录到mysql/redis/日志中去/重发");
        byte[] body = message.getBody();
        System.out.println("消息内容为: " + new String(body));
        System.out.println("参数i为: " + i);
        System.out.println("参数s为: " + s);
        System.out.println("参数s1为: " + s1);
        System.out.println("参数s2为: " + s2);
    }
}
