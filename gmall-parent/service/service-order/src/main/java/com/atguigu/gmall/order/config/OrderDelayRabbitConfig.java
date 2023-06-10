package com.atguigu.gmall.order.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Weirdo
 * 日期2023/4/11 1:04
 * 发送订单延迟消息的配置
 */
@Configuration
public class OrderDelayRabbitConfig {

    /**
     * 创建普通交换机
     * @return
     */
    @Bean("orderNormalExchange")
    public Exchange orderNormalExchange(){
        return ExchangeBuilder.directExchange("order_normal_exchange").build();
    }

    /**
     * 创建死信队列
     * @return
     */
    @Bean("orderDeadQueue")
    public Queue orderDeadQueue(){
        return QueueBuilder
                .durable("order_dead_queue")
                .withArgument("x-dead-letter-exchange","order_dead_exchange")
                .withArgument("x-dead-letter-routing-key","order.normal")
                .build();
    }

    /**
     * 正常交换机绑定死信队列
     * @param orderNormalExchange
     * @param orderDeadQueue
     * @return
     */
    @Bean
    public Binding orderDeadBinding(@Qualifier("orderNormalExchange") Exchange orderNormalExchange,
                               @Qualifier("orderDeadQueue") Queue orderDeadQueue){
        return BindingBuilder.bind(orderDeadQueue).to(orderNormalExchange).with("order.dead").noargs();
    }
    /**
     * 创建死信交换机
     * @return
     */
    @Bean("orderDeadExchange")
    public Exchange orderDeadExchange(){
        return ExchangeBuilder.directExchange("order_dead_exchange").build();
    }

    /**
     * 创建普通队列
     * @return
     */
    @Bean("orderNormalQueue")
    public Queue orderNormalQueue(){
        return QueueBuilder.durable("order_normal_queue").build();
    }

    /**
     * 私信交换机绑定正常队列
     * @param orderDeadExchange
     * @param orderNormalQueue
     * @return
     */
    @Bean
    public Binding orderNormalBinding(@Qualifier("orderDeadExchange") Exchange orderDeadExchange,
                               @Qualifier("orderNormalQueue") Queue orderNormalQueue){
        return BindingBuilder.bind(orderNormalQueue).to(orderDeadExchange).with("order.normal").noargs();
    }

}
