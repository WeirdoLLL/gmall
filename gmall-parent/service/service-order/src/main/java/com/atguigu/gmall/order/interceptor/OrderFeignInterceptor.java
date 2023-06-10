package com.atguigu.gmall.order.interceptor;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Enumeration;

/**
 * @author Weirdo
 * 日期2023/4/9 23:16
 * feign的拦截器
 */
@Component
public class OrderFeignInterceptor implements RequestInterceptor {
    /**
     * 拦截所有feign调用发生真实调用前一刻做的事 增强
     *
     * @param requestTemplate
     */
    @Override
    public void apply(RequestTemplate requestTemplate) {
        //获取原Request对象
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        //只有非等于空的时候,才获取原request的请求头数据-->有request的时候-->用户请求触发的时候
        if (requestAttributes != null) {
            //获取原request对象
            HttpServletRequest request = requestAttributes.getRequest();
            //原request的请求头中所有的数据到原封不动的放入新的这个request中去
            Enumeration<String> headerNames = request.getHeaderNames();
            //遍历获取存储
            while (headerNames.hasMoreElements()) {
                //获取每个元素
                String name = headerNames.nextElement();
                //存储
                requestTemplate.header(name, request.getHeader(name));
            }
        }
    }

}
