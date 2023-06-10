package com.atguigu.gmall.product.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Weirdo
 * 日期2023/4/11 18:52
 * 商品数据库数据和es数据同步的消息队列配置
 */
@Configuration
public class ProductRabbitConfig {

    /**
     * 创建交换机
     * @return
     */
    @Bean
    public Exchange productExchange(){
        return ExchangeBuilder.directExchange("product_exchange").build();
    }

    /**
     * 上架队列
     * @return
     */
    @Bean("skuUpperQueue")
    public Queue skuUpperQueue(){
        return QueueBuilder.durable("sku_upper_queue").build();
    }

    /**
     * 上架队列绑定
     * @param productExchange
     * @param skuUpperQueue
     * @return
     */
    @Bean
    public Binding skuUpperBinding(@Qualifier("productExchange") Exchange productExchange,
                                   @Qualifier("skuUpperQueue") Queue skuUpperQueue){
        return BindingBuilder.bind(skuUpperQueue).to(productExchange).with("sku.upper").noargs();
    }

    /**
     * 下架队列
     * @return
     */
    @Bean("skuDownQueue")
    public Queue skuDownQueue(){
        return QueueBuilder.durable("sku_down_queue").build();
    }

    /**
     * 下架队列绑定
     * @param productExchange
     * @param skuDownQueue
     * @return
     */
    @Bean
    public Binding skuDownBinding(@Qualifier("productExchange") Exchange productExchange,
                                  @Qualifier("skuDownQueue") Queue skuDownQueue){
        return BindingBuilder.bind(skuDownQueue).to(productExchange).with("sku.down").noargs();
    }
}
