package com.atguigu.gmall.payment.config;

import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author Weirdo
 * 日期2023/4/12 14:14
 * 配置支付结果通知的消息队列
 */
@Configuration
public class PaymentRabbitConfig {

    /**
     * 支付结果通知交换机
     *
     * @return
     */
    @Bean("paymentExchange")
    public Exchange paymentExchange() {
        return ExchangeBuilder.directExchange("payment_exchange").build();
    }

    /**
     * 普通订单支付结果接收队列
     *
     * @return
     */
    @Bean("normalOrderPaymentQueue")
    public Queue normalOrderPaymentQueue() {
        return QueueBuilder.durable("normal_order_payment_queue").build();
    }

    /**
     * 秒杀订单支付结果接收队列
     *
     * @return
     */
    @Bean("seckillOrderPaymentQueue")
    public Queue seckillOrderPaymentQueue() {
        return QueueBuilder.durable("seckill_order_payment_queue").build();
    }

    /**
     * 普通订单支付结果接收队列和支付结果通知交换机绑定
     *
     * @param paymentExchange
     * @param normalOrderPaymentQueue
     * @return
     */
    @Bean("normalOrderPaymentBinding")
    public Binding normalOrderPaymentBinding(@Qualifier("paymentExchange") Exchange paymentExchange,
                                             @Qualifier("normalOrderPaymentQueue") Queue normalOrderPaymentQueue) {
        return BindingBuilder.bind(normalOrderPaymentQueue()).to(paymentExchange()).with("pay.normal.order").noargs();
    }

    /**
     * 秒杀订单支付结果接收队列和支付结果通知交换机绑定
     *
     * @param paymentExchange
     * @param seckillOrderPaymentQueue
     * @return
     */
    @Bean("seckillOrderPaymentBinding")
    public Binding seckillOrderPaymentBinding(@Qualifier("paymentExchange") Exchange paymentExchange,
                                              @Qualifier("seckillOrderPaymentQueue") Queue seckillOrderPaymentQueue) {
        return BindingBuilder.bind(seckillOrderPaymentQueue).to(paymentExchange).with("pay.seckill.order").noargs();
    }
}
