package com.atguigu.mq.config;

import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Weirdo
 * 日期2023/4/10 19:27
 */
@Configuration
public class DemoRabbitConfig {
    /**
     * 创建交换机
     *
     * @return
     */
    @Bean
    public Exchange demoExchange() {
        return ExchangeBuilder.directExchange("demo_exchange").build();
    }

    /**
     * 创建队列
     * @return
     */
    @Bean
   public Queue demoQueue(){
        return QueueBuilder.durable("demo_queue").build();
   }

    /**
     * 创建队列1
     * @return
     */
    @Bean
   public Queue demoQueue1(){
        return QueueBuilder.durable("demo_queue1").build();
   }

    /**
     * 绑定队列
     * @param demoExchange
     * @param demoQueue
     * @return
     */
    @Bean
   public Binding demoBinding(@Qualifier("demoExchange")Exchange demoExchange,
                              @Qualifier("demoQueue")Queue demoQueue){
       return BindingBuilder.bind(demoQueue).to(demoExchange).with("user.add").noargs();
   }

    /**
     * 绑定队列1
     * @param demoExchange
     * @param demoQueue1
     * @return
     */
   @Bean
   public Binding demoBinding1(@Qualifier("demoExchange") Exchange demoExchange,
                               @Qualifier("demoQueue1") Queue demoQueue1){
        return BindingBuilder.bind(demoQueue1).to(demoExchange).with("user.delete").noargs();
   }

}
