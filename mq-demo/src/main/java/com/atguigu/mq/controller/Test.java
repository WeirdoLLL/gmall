package com.atguigu.mq.controller;

import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Weirdo
 * 日期2023/4/10 19:23
 */
@RestController
@RequestMapping(value = "/send")
public class Test {
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping
    public String send() {
        //发送消息
        rabbitTemplate.convertAndSend("demo_exchange",
                                    "user.add",
                                    "java0926第一条消息");

        return "成功";
        }
    }


