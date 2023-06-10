package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Weirdo
 * 日期2023/4/14 18:05
 * 秒杀订单死信队列配置类
 */
@Configuration
public class SeckillOrderDelayRabbitConfig {

    /**
     * 正常交换机
     * @return
     */
    @Bean("seckillOrderNormalExchange")
    public Exchange seckillOrderNormalExchange(){
        return ExchangeBuilder.directExchange("seckill_order_normal_exchange").build();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean("seckillOrderDeadQueue")
    public Queue seckillOrderDeadQueue(){
        return QueueBuilder.durable("seckill_order_dead_queue")
                .withArgument("x-dead-letter-exchange","seckill_order_dead_exchange")
                .withArgument("x-dead-letter-routing-key", "seckill.order.normal")
                .build();
    }
    /**
     * 死信队列和正常交换机绑定
     * @return
     */
    @Bean("seckillDeadBinding")
    public Binding seckillDeadBinding(@Qualifier("seckillOrderNormalExchange") Exchange seckillOrderNormalExchange,
                                      @Qualifier("seckillOrderDeadQueue") Queue seckillOrderDeadQueue){
        return BindingBuilder.bind(seckillOrderDeadQueue).to(seckillOrderNormalExchange).with("seckill.order.dead").noargs();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean("seckillOrderDeadExchange")
    public Exchange seckillOrderDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_order_dead_exchange").build();
    }

    /**
     * 正常队列
     * @return
     */
    @Bean("seckillOrderNormalQueue")
    public Queue seckillOrderNormalQueue(){
        return QueueBuilder.durable("seckill_order_normal_queue").build();
    }

    /**
     * 死信交换机和正常队列绑定
     * @param seckillOrderDeadExchange
     * @param seckillOrderNormalQueue
     * @return
     */
    @Bean("seckillOrderNormalBinding")
    public Binding seckillOrderNormalBinding(@Qualifier("seckillOrderDeadExchange") Exchange seckillOrderDeadExchange,
                                             @Qualifier("seckillOrderNormalQueue") Queue seckillOrderNormalQueue){
        return BindingBuilder.bind(seckillOrderNormalQueue).to(seckillOrderDeadExchange).with("seckill.order.normal").noargs();
    }
}
