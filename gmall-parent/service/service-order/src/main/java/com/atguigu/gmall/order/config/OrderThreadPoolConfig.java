package com.atguigu.gmall.order.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 包名:com.atguigu.gmall.Item.config
 *
 * @author Weirdo
 * 日期2023/3/29 0:29
 * 自定义线程池
 */
@Configuration
public class OrderThreadPoolConfig {

    /**
     * 自定义线程池
     * @return
     */
    @Bean
    public ThreadPoolExecutor threadPoolExecutor(){
        return new ThreadPoolExecutor(12,
                24,10, TimeUnit.SECONDS,new ArrayBlockingQueue<>(10000));
    }
}
