package com.atguigu.gmall.seckill.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 自定义线程池
 */
@Configuration
public class SeckillOrderThreadPoolConfig {

    /**
     * 自定义线程池bean
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(12,
                24,
                10,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(80000));
    }
}
