package com.atguigu.gmall.seckill.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;

/**
 * @author Weirdo
 * 日期2023/4/17 10:22
 * 注册拦截器
 */
@Configuration
public class SeckillInterceptorConfig extends WebMvcConfigurationSupport {

    @Resource
    private SeckillInterceptor seckillInterceptor;

    /**
     * 将拦截器注册到核心控制器中去
     * @param registry
     */
    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(seckillInterceptor).addPathPatterns("/**");
        super.addInterceptors(registry);
    }
}
