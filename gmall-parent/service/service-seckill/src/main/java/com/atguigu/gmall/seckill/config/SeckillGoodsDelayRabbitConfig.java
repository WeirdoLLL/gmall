package com.atguigu.gmall.seckill.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Weirdo
 * 日期2023/4/14 18:05
 * 秒杀商品库存同步的延迟消息队列配置
 */
@Configuration
public class SeckillGoodsDelayRabbitConfig {


    /**
     * 正常交换机
     * @return
     */
    @Bean("seckillGoodsNormalExchange")
    public Exchange seckillGoodsNormalExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_normal_exchange").build();
    }

    /**
     * 死信队列
     * @return
     */
    @Bean("seckillGoodsDeadQueue")
    public Queue seckillGoodsDeadQueue(){
        return QueueBuilder
                .durable("seckill_goods_dead_queue")
                .withArgument("x-dead-letter-exchange", "seckill_goods_dead_exchange")
                .withArgument("x-dead-letter-routing-key", "seckill.goods.normal")
                .build();
    }

    /**
     * 死信队列和正常交换机绑定
     * @param seckillGoodsDeadQueue
     * @param seckillGoodsNormalExchange
     * @return
     */
    @Bean
    public Binding seckillGoodsDeadBinding(@Qualifier("seckillGoodsDeadQueue") Queue seckillGoodsDeadQueue,
                                           @Qualifier("seckillGoodsNormalExchange") Exchange seckillGoodsNormalExchange){
        return BindingBuilder.bind(seckillGoodsDeadQueue).to(seckillGoodsNormalExchange).with("seckill.goods.dead").noargs();
    }

    /**
     * 死信交换机
     * @return
     */
    @Bean("seckillGoodsDeadExchange")
    public Exchange seckillGoodsDeadExchange(){
        return ExchangeBuilder.directExchange("seckill_goods_dead_exchange").build();
    }

    /**
     * 正常队列
     * @return
     */
    @Bean("seckillGoodsNormalQueue")
    public Queue seckillGoodsNormalQueue(){
        return QueueBuilder.durable("seckill_goods_normal_queue").build();
    }

    /**
     * 死信交换机和正常队列绑定
     * @param seckillGoodsNormalQueue
     * @param seckillGoodsDeadExchange
     * @return
     */
    @Bean
    public Binding seckillGoodsNormalBinding(@Qualifier("seckillGoodsNormalQueue") Queue seckillGoodsNormalQueue,
                                            @Qualifier("seckillGoodsDeadExchange") Exchange seckillGoodsDeadExchange){
        return BindingBuilder.bind(seckillGoodsNormalQueue).to(seckillGoodsDeadExchange).with("seckill.goods.normal").noargs();
    }
}
